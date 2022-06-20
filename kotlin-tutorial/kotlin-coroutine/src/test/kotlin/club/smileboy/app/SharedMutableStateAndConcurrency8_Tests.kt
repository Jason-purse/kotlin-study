package club.smileboy.app

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.system.measureTimeMillis

/**
 * @author FLJ
 * @date 2022/6/17
 * @time 16:25
 * @Description 可变状态和并发测试 ...
 *
 * 协程能够并发的执行(例如: 通过多线程派发器) ... 但是它带来了一些并发问题 ...
 * 主要问题就是访问共享可变状态 ... 在协程领域，这个问题的一些解决方案与多线程世界中的解决方案类似，但其他一些解决方案是独一无二的。
 *
 * Contents
 *  - 问题 - 27行
 *  -  同步条件 - 82行
 *  - 基于协程的状态更新 - 110行 ..
 *  - 互斥性修改共享给数据 -- 150行
 *  - Actors -- 191行 ..
 *
 *
 */
class SharedMutableStateAndConcurrency8_Tests {
    /**
     * 问题出现?
     * 使用 100个协程做相同的动作 1千次 ...
     * 我们需要测试它们的完成时间进行深度比较 ...
     *
     *
     */
    @Test
    fun problemTest() {
        val n = 100 // number of coroutines to  launch
        val k = 1000 // times an action is repeated by each coroutine

        runBlocking {
            val time = measureTimeMillis {
                coroutineScope {
                    repeat(n) {
                        repeat(k) {
                            launch {
                                println("1")
                            }
                        }
                    }
                }
            }

            println("Completed ${n * k} actions in $time ms")
        }
    }
    /**
     * 在多协程(多线程上下文) 改变可变数据 ...
     * 同样  假设我们需要计数
     *
     * 但是这是一个错误示例,不能够正确修改可变数据 ...
     * 例如它并不总是等于 理想值 ...
     *
     * Counter = 9593
     *
     * 因为它们没有同步 ...
     */
    @Test
    fun caculation() {
        var counter = 0
        runBlocking {
            withContext(Dispatchers.Default) {
                repeat(10000) {
                    launch {
                        counter++
                    }
                }
            }
            println("Counter = $counter")
        }
    }


    // --------------------------- 进行同步 ---------------------
    /**
     * 使用原子型的 数据类型,例如AtomicInteger ...的数据安全的方法 ...
     *
     * 这是一种快速解决简单问题的方式,在简单计数 / 队列 /标准结构 / 基础操作上 能够正确解决 ..
     * 但是，它不容易扩展到复杂状态或没有现成的线程安全实现的复杂操作。
     *
     * it does not easily scale to complex state or to complex operations that do not have ready-to-use thread-safe implementations.
     */
    @Test
    fun synchronoused() {
        runBlocking {
            val counter = AtomicInteger()

            withContext(Dispatchers.Default) {
                repeat(10000) {
                    launch {
                        counter.incrementAndGet()
                    }
                }
            }
            println("Counter = $counter")
        }
    }

    // ------------------------------------------ 使用基于协程的方式进行共享状态更新 ------------------------------
    // ---------------- 核心就是将数据装到一个线程中进行修改 ... 那么可见性问题已解决 ...
    // 但是这段代码执行的非常慢 ..
    // 因为进行细腻化线程管控 ...
    // 因为它虽然在不同的协程上运行,但是最终数据汇总通过 同一个线程中进行更新 ...
    @Test
    fun coroutineByMutableState() {
        var a = 0;
        runBlocking {
            var that = this;
            repeat(1000) {
                launch(Dispatchers.Default) {
                    withContext(that.coroutineContext) {
                        a ++
                    }
                }
            }
        }
        println("计算结果 $a")
    }

    // 你能够控制线程范围的粒度 .. 在不同的位置使用withContext ...
    // 但是它让所有的协程都运行在指定的上下文 ... 协程运行更慢 ...
    @Test
    fun coroutineWithContextChooseGrains() {
//        例如
        var a = 0;
        runBlocking {
            var that = this;
            withContext(that.coroutineContext) {
            repeat(1000) {
                    launch(Dispatchers.Default) {
                        a ++
                    }
                }
            }
        }
        println("计算结果 $a")
    }


    // ------------------------------------------------------ mutual exclusion  相互排除 --------------------
    /**
     * 相互排除的意思,排他性进行数据更新...
     *  将共享数据的修改设置为排他性条件 ...(并且保证绝不会并发) ..
     *  阻塞式的世界里面,通常使用 synchronized or ReentrantLock
     *  协程中使用互斥量 Mutex  ...  可以有lock / unlock 去划分一个部分 ..
     *  关键点就是它是协程世界的(是一个暂停函数 lock) 它不会阻塞线程 ...
     *
     *
     *  但是例如这样写,通常是一段 模板代码 ... 所以我们可以使用withLock扩展函数 代替这段模板代码 。。
     *  它等价于
     *  mutex.lock();
     *  try { ... } finally { mutex.unlock() }
     *
     *  这个控制是细腻度的,所以它付出了代价 ...
     *
     *  一定要注意互斥条件 。。
     *  这个例子中的锁定是细粒度的，所以它付出了代价。但是，对于某些绝对必须定期修改某些共享状态但没有限制该状态到自然线程的情况，这是一个不错的选择。
     *
     *  也就是自然线程的ThreadLocal 状态与它无关 ....
     */
    @Test
    fun mutexTest() {
        val mutex = Mutex()
        var counter = 0

        runBlocking {
            withContext(Dispatchers.Default) {
                launch {
                    // protect each increment with lock
                    mutex.withLock {
                        counter++
                    }
                }
            }
            println("Counter = $counter")
        }
    }

    // ---------------------------------  Actors ---------------------------------------
    /**
     * actor 由协程组合而成的实体
     * 这个状态是被控制并封装到协程中 ..  它通过管道和另一些协程进行通信 ...
     * 一个简单的actor 是一个函数, 对于具有复杂状态的actor 你应该设计为一个类 ....
     *
     * 这里有一个actor 协程构建器 能够非常方便的actor的邮箱地址的管道 放入这个scope中去接收并合并来自发送管道的数据到job的结果对象 ..
     * 因此这样的一个引用可以放置到actor中 作为它的句柄 ...(执行这些动作) ...
     *
     * 使用actor的第一步 定义消息的class(actor 将使用它进行消息处理) ...
     * Kotlin的密闭类 非常适合这样的目的 ... 我们能够定义CounterMsg 密闭类 的子类 为 IncCounter 用来计数 通过GetCounter 函数获取它的值 ..
     * 后者需要发送一个响应 ...
     * 一个 CompletableDeferred 通信原语，表示将来会知道（通信）的单个值，用于此目的。
     */


    /**
     * 测试 ... actor ...
     * 下面的代码中,我们通过actor 绑定了一个处理指定类型的管道,通过它 我们能够处理输入的数据,并进行归并 ...
     *
     *
     * 其实本质上 我们是将管道封装了 ....  封装为actor 将管道封装到 actor 身上,通过它的send 来在内部进行数据合并,这个例子非常具有参考价值 ...
     * actor 其实就是一块原子执行快 ...
     */
    // Message types for counterActor
    sealed class CounterMsg
    object IncCounter : CounterMsg() // one-way message to increment counter
    class GetCounter(val response: CompletableDeferred<Int>) : CounterMsg() // a request with reply


    fun CoroutineScope.counterActor() = actor<CounterMsg> {
        var counter = 0 // actor state
        // for in 可以在一个可迭代对象上进行遍历 ...
        // 没有数据的时候会阻塞 ...
        // 在没有调用这个actor 的close 会停掉 channel ... 于是结束 ...
        for (msg in channel) { // iterate over incoming messages
            when (msg) {
                is IncCounter -> counter++
                is GetCounter -> msg.response.complete(counter)
            }
        }
        println("遍历结束 ...")
    }

    /**
     * 需要注意 ,actor 本身是一个协程 ... 并且独立运行 ...
     * actor 可能能够修改对应对象的私有属性,来实现数据共享,它带来的好处就是避免了锁 ..
     * 仅仅只有对应的消息才会收到影响 ...
     *
     * Actor 比在负载下锁定更有效，因为在这种情况下它总是有工作要做，而且它根本不需要切换到不同的上下文。
     *
     * 最后 actor 是 produce 协程构建器的对偶形式 ..
     * actor 包含一个接收管道 / produce 包含一个生产管道 ...
     */
    @Test
    fun actorProducerTests() {
        runBlocking<Unit> {
            val counter = counterActor() // create the actor
            withContext(Dispatchers.Default) {
                launch {
                    // send 执行是异步的..
                    counter.send(IncCounter)
                    println("进行数据通信,即将发送数据到另外一个协程 ....,当改变成功之后,可以发送或者通过回调 通知给之前的协程 ...")
                }
            }
            // send a message to get a counter value from an actor
            val response = CompletableDeferred<Int>()
            counter.send(GetCounter(response))
            println("Counter = ${response.await()}")

            delay(3000)
            counter.close() // shutdown the actor
        }
    }
}