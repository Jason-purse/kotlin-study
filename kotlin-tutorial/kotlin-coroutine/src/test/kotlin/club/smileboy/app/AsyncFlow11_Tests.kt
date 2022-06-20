package club.smileboy.app

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis

/**
 * @author JASONJ
 * @date 2022/6/19
 * @time 20:14
 * @description 异步流 ....
 *
 * contents
 *  - flow
 *  - sequence / normal iterator
 *  - type evolve
 *  - flow is cold / lazy
 *  - flow is cancellable
 *  - flow builder
 *  - flow 中间操作符
 *  - 终态操作符
 *  - flow 是依次执行的
 *  - flow wrong emission with context
 *  - flowOn operator
 *  - buffering
 *  - conflation
 *  - process latest value
 *  - compose multiple flows
 *  - flattening flows
 *  - flow exceptions
 *  - everything is caught
 *  - 异常透明
 *  - flow completion
 *  - 启动流程
 *  - flow cancellation checks
 *
 **/
class AsyncFlow11_Tests {

    val logger: Logger = LoggerFactory.getLogger(AsyncFlow11_Tests::class.java);

    /**
     * 包含多个值的流
     */
    @Test
    fun multipleValues() {

        runBlocking {
            flow<Int> {
                repeat(100) {
                    emit(it)
                }
            }.collect {
                println(it)
            }
        }
    }

    // ------------------------------------- sequence ------------------------
    /**
     * cpu 暂停性计算 ...
     */
    @Test
    fun sequenceTests() {
        sequence<Int> {
            repeat(100) {
                TimeUnit.SECONDS.sleep(1)
                yield(it)
            }
        }
    }

    // ---------------------------------------- 类型演变 ----------------------------
    /**
     * 为了异步流,就像使用同步sequence<?> 一样,使用flow<?> 即可,只是它们的值是异步返回的 ..
     * 由于流是异步的,那么一般使用在协程中 ...
     * 它的大多数函数都是可暂停的 ...
     * flow 流构建器 内部是 可暂停的 ...
     */
    @Test
    fun flowUse() {
        fun simple(): Flow<Int> = flow { // flow builder
            for (i in 1..3) {
                delay(100) // pretend we are doing something useful here
                emit(i) // emit next value
            }
        }
    }

    // ------------------------------------ 流是冷的,惰性的 ------------------------
    @Test
    fun flowIsCold() {
        fun simple(): Flow<Int> = flow {
            println("Flow started")
            for (i in 1..3) {
                delay(100)
                emit(i)
            }
        }
        // invoke ,but action is not invoke ..
        simple().let {
            println("waiting for collec function invoke .... begin flow handle ...")
            runBlocking {
                // util collect invoke
                it.collect {
                    println(it)
                }

                println("again collect result was same ...")
                it.collect {
                    println(it)
                }
            }
        }

    }

    // ----------------------------------------- flow is cancellable -----------------------
    /**
     * 流正常的退出,而没有异常 ...
     * 流中可以运行暂停函数,那么就可以监听协程的状态变化,那么让流是可以停止的 ... (所以叫做结合 协程进行取消)
     */
    @Test
    fun flowIsCanncel() {
        fun simple(): Flow<Int> = flow {
            for (i in 1..3) {
                delay(100)
                println("Emitting $i")
                emit(i)
            }
        }
        runBlocking {
            simple().let {
                withTimeoutOrNull(60) {
                    it.collect {
                        println(it)
                    }
                }
            }
        }
    }


    // --------------------------------------------- flow builder --------------------------------
    /**
     * flow builder 用来创建流
     * flow {  } 通过一个action 创建流
     * flowOf(...ele) 创建一个具有指定个数的流
     * 任何一个集合 / 序列都能够转变为流
     */
    @Test
    fun flowBuilder() {
        flow {
            emit(1)
        }

        flowOf(1, 2, 3, 4, 5)

        (1..5).asFlow() //


    }

    // ------------------------------------ 流中间操作符 -------------------------------
    /**
     *  中间操作符的作用就是将流进行转变,就像使用集合 / 序列 进行map一样  ..
     *  但是这里的变换更加的泛化 .. 根据上游返回下游 .. 并且这些操作符是冷的,并且这一类的操作符并不是一个暂停函数 ...(它能够快速工作,返回新变化的流的定义) ...
     *  记住它仅仅返回一个定义 ...(也就是说,所有冷的操作符都是惰性处理的,直到终态操作符使用) ...
     *
     *  基础的常见的操作符 有 map / filter ... 不同于序列 /集合的是在这些操作符内部能够调用暂停函数 ...(而集合/序列的操作符 无法调用暂停函数 ,因为它们是普通函数,阻塞式编程) ..
     *
     *  例如,一堆集合的结果通过map进行映射,就算一个请求时常非常的长,也能够通过暂停函数进行实现模拟此请求过程 ...
     *
     */
    @Test
    fun intermediateTests() {
        suspend fun performRequest(request: Int): String {
            delay(1000) // imitate long-running asynchronous work
            return "response $request"
        }

        runBlocking<Unit> {
            (1..3).asFlow() // a flow of requests
                // map 操作符本身不是暂停函数,但是它的函数体内部,是一个暂停函数快 ..
                .map { request ->
                    performRequest(request)
                }
                // 当没有调用 collect的时候,整个流的转变都不会开始  ...
                .collect { response -> println(response) }
        }
    }

    /**
     * 其次功能更加强大的是 transform ...
     * transform 可以说能够模拟其他任何中态操作符 ..
     * 在它的函数体中能够随意的弹出value 任意次 ...
     * 变换的过程由你定义 ..
     */
    @Test
    fun transformTests() {
        suspend fun performRequest(request: Int): String {
            delay(1000) // imitate long-running asynchronous work
            return "response $request"
        }
        runBlocking {
            (1..3).asFlow() // a flow of requests
                .transform { request ->
                    emit("Making request $request")
                    emit(performRequest(request))
                }
                .collect { response -> println(response) }
        }
    }

    /**
     * 其次你还能够通过 take 进行尺寸限制 ...
     * 用于在协程中退出都是通过抛出异常的方式,那么 关于资源释放的操作能够正确执行 ....
     *
     * 还记得协程中的finally块中 不能够干什么,或者能够强制让它干什么 ... 如果忘记可以查看 协程取消和超时进行温习 ...
     */
    @Test
    fun sizeLimitTests() {
        fun numbers(): Flow<Int> = flow {
            try {
                emit(1)
                emit(2)
                println("This line will not execute")
                emit(3)
            } finally {
                println("Finally in numbers")
            }
        }


        runBlocking<Unit> {
            numbers()
                .take(2) // take only the first two
                .collect { value -> println(value) }
        }
    }
// -------------------------------------------------- 终态操作符 -------------------------------
    /**
     *终态操作符的作用就是收集流,它是一个暂停函数 ..
     * 例如collect 就是一个最常见的终态操作符 ..
     * 但是还有一些其他简单的终态操作符 ... toList / toSet
     * 或者 first 去弹出单个值 ..
     * 或者reduce / fold 去归并一个流
     */
    @Test
    fun terminalOperatorTests() {
        runBlocking {
            val sum = (1..5).asFlow()
                .map { it * it } // squares of numbers from 1 to 5
//                .fold(0) {total,value -> total + value}
                .reduce { a, b -> a + b } // sum them (terminal operator)
            println(sum)
        }
    }

    // -------------------------------- flow is sequential ...
    /**
     *
     * 流是依次执行的 ...
     * 也就是上流没有执行完之前,下游无法执行 ...(但是这个是针对于每一个弹射的元素而言) ...
     *
     * 也就是当 终态操作符执行时 ,flow在协程中进行处理,每一个弹射的值将被所有的中间操作符处理完毕之后传递给终态操作符 ...
     * 如图所示,并不会等待所有的上游处理完毕之后再处理下游 ... 而是针对每一个弹射元素 ...
     */
    @Test
    fun flowIsSequential() {
        runBlocking {
            (1..5).asFlow()
                .filter {
                    println("Filter $it")
                    it % 2 == 0
                }
                .map {
                    println("Map $it")
                    "string $it"
                }.collect {
                    println("Collect $it")
                }
        }
    }

    // ---------------------------------------------- flow context --------------------------------------
    /**
     * 流的收集总是发生在调用协程的上下文中 ... 但是我们也可以指定流收集运行的协程的上下文
     *
     * 但是这仅仅只是说收集执行的协程上下文与流具体实现无关 ...
     * 流的属性叫做上下文保留 ....
     *
     *
     * 其次 许多流的构建器 例如 flow { ....   }  这个花括号中的代理对象是 FlowCollector ,它是相关的协程的collector ...
     * 那么这个花括号中执行代码的协程上下文是由这个收集器提供的  ..
     *
     * 那么默认情况下,它等用于使用调用协程的上下文
     *
     * 这对于上下文执行不敏感的情况来说,能够快速运行 / 且异步代码不会阻塞调用者 ..
     *
     */
    @Test
    fun flowContextTests() {
        fun simple(): Flow<Int> = flow {
            logger.info("Started simple flow")
            for (i in 1..3) {
                emit(i)
            }
        }

        runBlocking {
            simple().collect { value ->
                logger.info(value.toString()) // run in the specified context
            }
        }
    }

    // ------------------------------------- wrong emission withContext ---------------------
    /**
     * 一般来说, CPu 消耗计算的代码可能需要在Default上下文中运行,UI 更新可能在Main上下文中运行 ..
     * 但是flow {} 构建器(也就是FlowCollector 存在一个上下文保留属性,它不允许使用不同的上下文进行元素弹射 ...
     *
     * 那么说到这里,其实也就是想说,FlowCollector 是由终态操作符提供的,那么终态操作符的协程上下文就默认是
     * 例如流构建器代码中的异步代码块的上下文保留属性 ...
     *
     */
    @Test
    fun flowWrongEmissionWithContext() {
        fun simple(): Flow<Int> = flow {
            // The WRONG way to change context for CPU-consuming code in flow builder
            kotlinx.coroutines.withContext(Dispatchers.Default) {
                for (i in 1..3) {
                    Thread.sleep(100) // pretend we are computing it in CPU-consuming way
                    emit(i) // emit next value
                }
            }
        }

        runBlocking<Unit> {
            simple().collect { value -> println(value) }
        }
    }

    // ------------------------------------- flowOn ----------------------------------
    /**
     * 但是 流的上下文保留属性是能够通过flowOn操作符进行修改的 ..
     * 从而使 流能够在其他上下文中进行弹射 ...
     *
     * 以下例子就已经改变了流默认的依次执行的特性 ...  -- 一定要好好理解这个依次执行的特性 ...
     * 目前,flowOn 将改变了上游流的上下文 - 它和收集协程并发执行 ...
     *
     * 也就是说flowOn 可以改变上游流的上下文保留, 例如它改变了运行这个流应该在那一个协程上运行的派发器 .....
     */
    @Test
    fun flowOnOperator() {
        fun simple(): Flow<Int> = flow {
            for (i in 1..3) {
                Thread.sleep(100) // pretend we are computing it in CPU-consuming way
                logger.info("Emitting $i")
                emit(i) // emit next value
            }
        }.flowOn(Dispatchers.Default) // RIGHT way to change context for CPU-consuming code in flow builder

        runBlocking<Unit> {
            simple().collect { value ->
                logger.info("Collected $value")
            }
        }

    }

    // buffering
    /**
     * 在不同的协程中运行流的不同部分,对收集流的整体时间有所帮助 ...
     * 特别是长时间异步代码运行 ...
     * 例如消费者300毫秒消费,生产者 100毫秒产生 ...
     *
     * 例如这个例子中,流是依次处理的,那么必然存在等待 .... 因为消费者过慢,导致其他上游流的弹射非常慢 ...
     *
     * 那么通过buffered 也将改变流依次执行的特性 .. 那么  上游和下游的收集并发处理 ...
     *
     * 通过对比收集实现,我们能够发现,由于上游不再等待 下游收集,那么整体时间将缩短 200毫秒 ...
     *
     * 但是,请注意,buffer 操作符仅仅显式的请求缓存而不修改执行上下文 ...  对比flowOn(flowOn 使用相同的缓存机制,但是仅仅在它改变 CoroutineDispatcher 时) ...
     */
    @Test
    fun bufferingTests() {
        fun simple(): Flow<Int> = flow {
            for (i in 1..3) {
                delay(100) // pretend we are asynchronously waiting 100 ms
                logger.info("emiit $i")
                emit(i) // emit next value
            }
        }
        runBlocking<Unit> {
            val time = measureTimeMillis {
                simple()
                    // 仅仅通过buffer 来改变协程 运行simple flow 构建器中的代码
                    .buffer()
                    .collect { value ->
                        delay(300) // pretend we are processing it for 300 ms
                        logger.info("collect value $value")
                    }
            }
            println("Collected in $time ms")
        }
    }

    // ----------------------------------- conflation -----------------------
    /**
     * 当一个流的部分结果更新 ,也许我们没必要处理每一个值 ...
     * 相关我们只需要处理最新值即可 ...
     * 那么我们可以通过conflation 合并flow(去跳过中间值,当一个collector 处理的非常慢的时候) ...
     * 例如还是生产者与消费者模型 ..
     *  生产者 100毫秒处理一次,而消费者  300毫秒处理一次 ..那么 消费者太慢,对于一些业务来说,例如频繁的进行数据接收 ... (gps 位置,丢失一两次的数据也不是很危险) ..
     *
     *  这样中间的值  弹设值 2 就被忽略了 ...
     */
    @Test
    fun conflationTests() {
        fun simple(): Flow<Int> = flow {
            for (i in 1..3) {
                delay(100) // pretend we are asynchronously waiting 100 ms
                logger.info("emiit $i")
                emit(i) // emit next value
            }
        }
        runBlocking {
            val time = measureTimeMillis {
                simple()
                    .conflate() // conflate emissions, don't process each one
                    .collect { value ->
                        delay(300) // pretend we are processing it for 300 ms
                        println(value)
                    }
            }
            println("Collected in $time ms")
        }
    }

    // ------------------------------------------------------ 处理最新值 -----------------------------------------
    /**
     * 处理最近值和处理最新值 其实本意上没有什么不同,但是 细想一下就可以有所区分 ..
     * 最近值(也就是永远排在第一个处理的值,对于conflation 来说 他会不断的刷新最近值)
     * 但是最新值(是我们每次产生的一个值叫做最新值,那么如果必须处理它,我们就必须规定消费者的收集机制 / 策略)
     * 那么处理最新值的策略,就是丢弃之前的处理(如果在最新值来到的时候,还没有处理完毕),重启消费者收集器处理最新值 ...
     *
     * 那么kotlin 提供了这一系列的 xxxLatest 操作,在原有 xxx操作符的基础上,能够处理最新值 ..
     *
     * 通过这个例子我们能够发现,与处理最近值的结果完全不同,仅仅只有最后一个弹射值被处理 ...
     */
    @Test
    fun processLatestTests() {
        fun simple(): Flow<Int> = flow {
            for (i in 1..3) {
                delay(100) // pretend we are asynchronously waiting 100 ms
                logger.info("emiit $i")
                emit(i) // emit next value
            }
        }

        runBlocking {
            val time = measureTimeMillis {
                simple()
                    .collectLatest { value -> // cancel & restart on the latest value
                        println("Collecting $value")
                        delay(300) // pretend we are processing it for 300 ms
                        println("Done $value")
                    }
            }
            println("Collected in $time ms")
        }

    }
    // 组合多个流
    /**
     * Zip Sequence.Zip能够进行序列压缩 ...(能够对等合并两个流的值) ..
     * 会丢弃不对等的流的数据 ....
     */
    @Test
    fun composeMultipleFlowsTests() {

        runBlocking {
            flow<Int> {
                emit(1)
                emit(2)
                emit(3)
                emit(4)
                emit(9)
                emit(10)
            }
                .zip(flow {
                    emit(5)
                    emit(6)
                    emit(7)
                    emit(8)

                }) { t1, t2 ->
                    t1 to t2
                }.collect {
                    println("${it.first} -> ${it.second}")
                }
        }

    }

    /**
     * combine 操作符进行 多个流的 "conflation 概念"进行合并 ..
     * 总是合并最近值,一旦一个流有数据弹出,那么将产生一次合并 ...
     *
     * 相比zip,(zip总是对等合并),combine 总是在新数据弹出时进行重新计算 ...(假设可以一起不断更新多个人的最新情况) ...
     *
     * 通过onEach 操作对每一个元素进行 额外的操作 ...
     */
    @Test
    fun combineMultiFlowsTests() {

        runBlocking {
            val nums = (1..3).asFlow().onEach { delay(300) } // numbers 1..3 every 300 ms
            val strs = flowOf("one", "two", "three").onEach { delay(400) } // strings every 400 ms
            val startTime = System.currentTimeMillis() // remember the start time
            nums.combine(strs) { a, b -> "$a -> $b" } // compose a single string with "zip"
                .collect { value -> // collect and print
                    println("$value at ${System.currentTimeMillis() - startTime} ms from start")
                }
        }


        // 我们来查看一下buffer 放在delay 前和后的区别  ... 首先肯定是buffer前的上游都是独立协程运行,后面的另一个协程运行 ...
        // 但是两个的本质 有着截然不同,原因就是它们运行的方式(协程) 不一样 ...

        // 一个是 OnEach和collect 依次处理 ..
        // 一个是 OnEach和 buffer 依次处理 .. / 然后一起和collect 并发处理
        runBlocking {
            // 这个的效果肯定是每隔300毫秒收集一个
            val number = (1..3).asFlow().onEach {
                delay(300)
                println("弹射 $it")
            }.buffer()
            val times = measureTimeMillis { number.collect { println("收集 value$it") } }
            println("onEach after consume times $times")

            // 对比两个发现没有什么不同 ... 但是运行所在协程有本质不同 ...
            val number1 = (1..3).asFlow().onEach {
                println("弹射1 $it")
            }.buffer().onEach {
                delay(300)
            }
            val times1 = measureTimeMillis { number1.collect { println("收集1 value$it") } }
            println("onEach before consume times $times1")
        }

    }

    // 扁平化流 ..
    /**
     * Flow<Flow<?>> 可以通过 flatten / flatMap 形式进行扁平化 ...(这两个操作符都是集合 /序列的操作符) ..
     * 那么对于流的异步特性,应该使用 flatMapConcat等家族的操作符 ...
     * 并且每一种操作符都有不同的特性:
     *
     *    - Concatenating mode
     *      这个模式包含了两种  flatMapConcat 操作符  /  flattenConcat 操作符 ..
     *      工作原理,内部流中的下一个元素收集之前,会等待当前元素处理完毕 ..(也就是这个流的元素处理是依次的)...
     */
    @Test
    fun flattenTests() {

        fun requestFlow(i: Int): Flow<String> = flow {
            emit("$i: First")
            delay(500) // wait 500 ms
            emit("$i: Second")
        }

        runBlocking {
            val startTime = System.currentTimeMillis() // remember the start time
            (1..3).asFlow().onEach { delay(100) } // a number every 100 ms
                // flatternConcat 1.7版本才有 ...
                .flatMapConcat { requestFlow(it) }
                .collect { value -> // collect and print
                    println("$value at ${System.currentTimeMillis() - startTime} ms from start")
                }
        }
    }

    /**
     * flatMapMerge
     * 这是另一种flatten 模式..
     * 它们会并发的收集所有进入的流 然后合并它们值到单个流 (所以它们的值弹射的非常快) ..
     * 这个 flatten 模式由 flatMapMerge 和 flattenMerge 操作符实现 ...
     * 它们同时接收可选的concurrency 参数 然后限制(同时用于收集的)并发流的个数(同时收集的并发流的个数,默认等价于DEFAULT_CONCURRENCY) ...
     *
     * flatMapMerge的 代码块调用是依次的,但是 收集是并发的 ... 它等价于先执行了 map(requestFlow(it))然后 在结果上 执行flattenMerge ..
     */
    @Test
    fun flatMapMergeTests() {

        fun requestFlow(i: Int): Flow<String> = flow {
            emit("$i: First")
            delay(500) // wait 500 ms
            emit("$i: Second")
        }

        runBlocking {
            val startTime = System.currentTimeMillis() // remember the start time
            (1..3).asFlow().onEach { delay(100) } // a number every 100 ms
                // 由于这个操作符会收集 action种产生的流 ..
                .flatMapMerge { requestFlow(it) }
                .collect { value -> // collect and print
                    println("$value at ${System.currentTimeMillis() - startTime} ms from start")
                }
        }
    }

    /**
     *  flatMapLatest 类似于collectLatest 操作符 ...
     *  它们的工作模式也相同,只不过是只要有新的流弹射,都会进行flatten
     *  同样  只要有新的数据弹射, 收集器重启(之前的流取消) ... 开启新的flatten ..
     *
     *  也就是说 flatMapLatest 会取消  block 块中的所有代码 。。
     */
    @Test
    fun flatMapLatest() {

        fun requestFlow(i: Int): Flow<String> = flow {
            emit("$i: First")
            delay(500) // wait 500 ms
            emit("$i: Second")
        }

        runBlocking {
            val startTime = System.currentTimeMillis() // remember the start time
            (1..3).asFlow().onEach { delay(100) } // a number every 100 ms
                .flatMapLatest { requestFlow(it) }
                .collect { value -> // collect and print
                    println("$value at ${System.currentTimeMillis() - startTime} ms from start")
                }
        }
    }

    // 流的异常 ....
    /**
     * 流的异常 ...  当发生异常的时候,flow的收集将会完成 ...
     * 那么我们如何处理异常 ...
     *  1. 收集器 try - catch
     *      通过使用kotlin的try/catch block 在collector上能够处理 收集过程中的异常 ...
     */
    @Test
    fun flowExceptions() {
        fun simple(): Flow<Int> = flow {
            for (i in 1..3) {
                println("Emitting $i")
                emit(i) // emit next value
            }
        }

        runBlocking<Unit> {
            try {
                simple().collect { value ->
                    println(value)
                    check(value <= 1) { "Collected $value" }
                }
            } catch (e: Throwable) {
                println("Caught $e")
            }
        }
    }

    // everything is caught
    /**
     * 一切事情都是可以caught的(不管是中态操作符 / 还是 终态操作符 / 收集器) ..
     */
    @Test
    fun everythingIsCaught() {
        fun simple(): Flow<String> =
            flow {
                for (i in 1..3) {
                    println("Emitting $i")
                    emit(i) // emit next value
                }
            }
                .map { value ->
                    check(value <= 1) { "Crashed on $value" }
                    "string $value"
                }

        runBlocking<Unit> {
            val simple = simple()
            try {
                simple.collect { value -> println(value) }
            } catch (e: Throwable) {
                println("Caught $e")
            }
        }
    }

    // 流对异常是透明的
    /**
     * 正是由于流的异常的透明性,让我们能够通过try-catch 捕捉流中所发生的异常 ..
     * 有些情况下,我们为了流能够正常处理,可以需要对某些异常进行映射,转变为业务处理 - 这维护了异常透明性 ...
     * 通过catch 操作符我们能够分析异常并响应他(依赖于什么样的异常被抛出) ...
     *  - 重抛异常
     *  - 异常能够转变为发射值(从catch 的内部通过emitter 进行数据弹射)
     *  - 异常能够被忽略,记录且能够被某些其他代码进行处理 ...
     *
     *  通过使用catch 操作符,我们就不需要使用try-catch了,但是 collect 除外 .. 因为catch 是中态操作符 ..(仅仅捕获上游异常) ..
     *
     *  如果我们想要捕捉 collect的异常,那么使用声明式异常捕捉,将collect 的核心动作切换为onEach 然后后续使用catch 兜住进行处理 ...
     */
    @Test
    fun flowToExceptionIsTransparency() {

        fun simple(): Flow<Int> =
            flow {
                for (i in 1..3) {
                    println("Emitting $i")
                    emit(i) // emit next value
                }
            }

        runBlocking {
            var flows: Flow<Int> = simple().map { it -> throw CancellationException("取消异常") }
                .catch { error ->
                    println("print error ${error.message}")
                };

            flows.collect {
                println(it)
            }
        }
    }

    /**
     * 声明式捕获 ...
     *
     * 请注意捕获之后,则默认流失败 ... 继续往下游流传递 ..
     * 对于流的依次性处理原理,那么发生错误的元素完成之后则流则关闭 ...
     */
    @Test
    fun declarativelyCatchTests() {
        runBlocking {
            flow<Int> {
                emit(1)
                emit(2)
                emit(3)
            }.onEach {
                if (it == 2) {
                    throw CancellationException("出现错误,2 无法被捕获!!!")
                }
                println("collect value $it")
            }.catch {
//                    error -> println("log error ${error.message}")
                    error ->
                emit(10)
                println("log error ${error.message}")
            }
                .collect()
        }

    }
// ---------------------------------- flow  completion ---------------------------------
    /**
     * 流的完成
     *         流可以正常 或者异常完成 , 但是你完成之后可以执行一些动作 ..
     *         有声明式以及命令式两种 ..
     *         1. try-finally ...
     *         2. onCompletion
     *          并且它可以接收可选的异常信息 ...(它无法处理异常,它是一种特殊的操作符 / 同 onEach 一样 ..) ... 但是它们够可以抛出异常 ..于是 catch 操作符能够声明式的捕捉它们
     *           切记 小心异常覆盖 ...
     *          上游的异常可以交给catch 进行声明式处理 ..
     */
    @Test
    fun flowCompletion() {
        runBlocking {
            flow {
                emit(1)
                emit(2)
                emit(3)
            }
                .onCompletion { error ->
                    println("处理完毕 ....")
                }
                .collect { println(it) }
        }

        runBlocking {
            fun simple(): Flow<Int> = flow {
                emit(1)
                throw RuntimeException()
            }
            simple()
                .onCompletion { cause ->
                    if (cause != null) {
                        println("Flow completed exceptionally")
                        //覆盖了上游的异常..
                        throw CancellationException("onCompletion error")
                    }

                }
                .catch { cause -> println("Caught exception,message: ${cause.message}") }
                .collect { value -> println(value) }
        }
    }

    // Launching flow
//    --------------------------------------------
    /**
     * 由于终态操作符会阻塞当前协程等待流完成,但是我们可以通过使用不同的协程启动流,来让下面的命令式代码立即执行 ...
     * 使用launchin 达到这样的效果
     *
     * 于是onEach { ... }.launchIn(scope) 代码块类似于 addEventListener ....
     * 但是不需要移除 ... 因为结构化并发退出,所有的执行都会取消(达到此目的) ..
     *
     * 注意到 launchIn 返回了Job ...能够取消相关的flow 收集协程(而无需取消整个scope或者join 等待它) ...
     */
    @Test
    fun launchingFlows() {
        // 对比事件驱动系统,我们也可以使用flow来代表来自相同来源的异步事件 ...
        // 然后事件驱动系统中,我们需要使用addEventListener  注册一块代码(针对感兴趣的事件,并编写后续处理代码) ...
        // 那么这里onEach 就能够做这个事情 ....
        // but 所有的流都是冷的,需要通过collect 启动流 ..
        runBlocking {
            // Imitate a flow of events
            fun events(): Flow<Int> = (1..3).asFlow().onEach { delay(100) }

            runBlocking<Unit> {
                events()
                    .onEach { event -> println("Event: $event") }
//                    .collect() // <--- Collecting the flow waits
                    // 通过launchIn使用新的协程进行流收集启动 ...
                    .launchIn(CoroutineScope(coroutineContext))
                println("Done")
            }
        }
    }

    // Flow cancellation check
    /**
     * 由于流弹射时会进行 ensureActive检查 .. 所以busy loop 也能够取消 ...
     *
     * 例如这个例子中,收集到3的时候,取消了协程,然后busy loop 也能够取消,emit 会检查 ...
     *
     * 然而有些flow 操作符没有额外的取消检查(因为性能原因) ..
     * 例如IntRange.asFlow 这些都没有协程取消检查 ..  那么为了这些能够取消 使用 边缘操作符OnEach 进行协程取消检查 ...
     *  .onEach { currentCoroutineContext().ensureActive() }
     *  或者 使用 cancellable 操作符开箱即用 .... 否则其他情况不需要关心协程取消(因为都包含了自动检查) ...
     */
    @Test
    fun flowCancellationChecks() {

        fun foo(): Flow<Int> = flow {
            for (i in 1..5) {
                println("Emitting $i")
                emit(i)
            }
        }
        runBlocking<Unit> {
            foo().collect { value ->
                if (value == 3) cancel()
                println(value)
            }
        }
    }

    // 流和响应式Streams
    /**
     * 例如 RxJava / project Reactor / 所以流的设计非常相似 ..
     *
     * 流的设计受Reactive streams的启发,但是流设计是为了尽可能简单 ...为了kotlin 友好的暂停以及尊重结构化并发 ...
     *
     * 如果没有响应式的先驱者和他们的巨大工作，实现这一目标是不可能的。您可以在 Reactive Streams 和 Kotlin Flows 文章中阅读完整的故事。
     * 虽然不同,但是概念上,Flow 是一个响应式流 并且它们能够转变为响应式(符合规范和 TCK)Publisher 并且相互转换 ...
     * 这些转换器都通过kotlinx.coroutines 开箱即用的设计并且能够在相关的响应式模块中发现 ..
     * kotlinx-coroutines-reactive(reactive streams) .. kotlinx-coroutines-reactor ...
     * 以及 kotlinx-coroutines-rx2 / kotlinx-coroutines-rx3
     * 它们都能够相互转换 ..
     *
     * 通过Reactor的Context集成以及暂停友好的方式和各种响应式实体工作 ...
     */
    @Test
    fun flowAndReactiveStreams() {
            // 目前为止,我只学过 reactor /rxjava
        // 本质上它们的方法大致相同,但是有些许不同..
    }
}