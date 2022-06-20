package club.smileboy.app

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.selects.select
import org.junit.jupiter.api.Test
import java.util.*

/**
 * @author FLJ
 * @date 2022/6/17
 * @time 19:13
 * @Description select 表达式
 *
 * 等同于java selector ....的概念 ...
 * 它是实验性的 .
 * 它会同时等待所有协程,当第一个协程可用的时候,选择第一个 ...
 *
 * Select expressions are an experimental feature of kotlinx.coroutines.
 * Their API is expected to evolve(演变) ... in the upcoming updates of the kotlinx.coroutines
 * library with potentially breaking changes.
 *
 *
 * contents
 *  - Selecting from channels
 *  - selecting on close(保护性的选择想要的东西,对于关闭的管道 宽容处理 ...) ...
 *  - selecting to send --- 147行 ..(选择性发送) ...
 *  - selecting to deferred value
 *
 */
class SelectExpression9_Tests {

    // ---------------------------------- 从管道中选择 --------------------------
    /**
     * 从管道中选择
     * 假设两个生产者都在 生产数据,但是每一个产生的数据我们都应该 消费,那么通过select 也是一种方式,我们也可以使用 另类 管道 进行消费 ...
     *
     * 本来select中管道的 onReceive 子句会导致select 抛出异常,但是我们可以通过  onReceiveCatching 在管道关闭的时候执行特殊动作 ...
     * 这取决于特定的selectClause 的功能 ...
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun selectExpression_Tests() {
        runBlocking {
         val a = produce<String> {
                repeat(5) {
                    kotlinx.coroutines.delay(20)
                    send("produce 2 $it")
                }
            }
        val b = produce<String> {
                repeat(5) {
                    kotlinx.coroutines.delay(30)
                    send("produce 1 $it")
                }
            }
            // 这里的代码很奇怪,它仅仅会从select中返回,并且还能为SelectClause 包装函数,并不会影响子句的结果 ..
            // 设计真的妙 ..
            repeat(10) {
                val result = select<String> {
                    kotlin.runCatching {
                        a.onReceive {
                            println("receive a")
                            it
                        }
                    }.exceptionOrNull()?.let {
                        // pass 有可能全是 b的管道将数据抓取完毕 ... 那么后续的 b的管道将关闭,正常情况下 会报错 ...
                        println("a channel end ${it.message}")
                    }

                    kotlin.runCatching {
                        b.onReceive {
                            println("receive b")
                            it
                        }
                    }.exceptionOrNull()?.let {
                        // a 同上
                        //  // pass 有可能全是 b的管道将数据抓取完毕 ... 那么后续的 b的管道将关闭,正常情况下 会报错 ...
                        println("b channel end ${it.message}")
                    }
                }

                println("select value $result")
            }
        }

        Unit
    }

    suspend  fun selectAorB(a: ReceiveChannel<String>, b: ReceiveChannel<String>): String =
        select<String> {
            a.onReceiveCatching { it ->
                val value = it.getOrNull()
                if (value != null) {
                    "a -> '$value'"
                } else {
                    "Channel 'a' is closed"
                }
            }
            b.onReceiveCatching { it ->
                val value = it.getOrNull()
                if (value != null) {
                    "b -> '$value'"
                } else {
                    "Channel 'b' is closed"
                }
            }
        }

    // ----------------------------------------- onReceiveCatching ----------------------------------------
    /**
     * onReceiveCatching ...
     * 虽然select 仅仅只会等待一次,如果我们在已经关闭的管道上进行select 那么它也会选择第一个clause 的onReceiveing 进行返回 ...
     * 因为关闭的管道总是能够立即返回 ...
     *
     * 所以我们需要注意以下几点： 也就是它的工作原理
     *  -  它非常倾向于第一个clause ...  当所有的clause 在同一时间都是可选择的,偏向于第一个 ..
     *  - 工作原理: 由于子句是suspend 函数 ..  那么就给了其他子句机会(当子句暂停时,其他子句就有了机会,例如管道 就可以发送值 ....)
     *  在这里,a 时不时的阻塞,那么b就有了机会进行 send,然后select 就可以选择管道中已经有了数据进行处理 ...
     */
    @Test
    fun runOnReceieveCatchingTests() {
        runBlocking {
            val a = produce<String> {
                repeat(4) { send("Hello $it") }
            }
            val b = produce<String> {
                repeat(4) { send("World $it") }
            }
            repeat(9) { // print first eight results
                println(selectAorB(a, b))
            }

            println("结束了 ...")

            // 猜想一下为什么这段代码和上面的一段代码 (runCatching) 不一样 ...
            a.cancel()
            b.cancel()
        }

        // 原因是 runCatching 已经导致 a管道报错了,那么 对应的协程也会退出 .... 于是自动结构化并发退出 ..
        // 但是这里,我们为 管道实现了协程安全的回调处理 ..
        // 那么 a / b的协程将不会死亡 .. 所以我们需要手动关闭 ...
    }

//    ------------------------------------- select to send ------------------------------

    @OptIn(ExperimentalCoroutinesApi::class)
    fun CoroutineScope.produceNumbers(side: SendChannel<Int>) = produce<Int> {
        for (num in 1..10) { // produce 10 numbers from 1 to 10
            delay(100) // every 100 ms
            select<Unit> {
                onSend(num) {} // Send to the primary channel
                side.onSend(num) {} // or to the side channel
            }
        }
    }
    /**
     * Selecting to send
     * 当消费者无法更上生产者的节奏时,我们可以将多余的数据缓存起来,然后后续处理,或者在合适的机会上处理 ...
     *
     * 这是一个错误示例,actor 协程不会退出 ....
     */
    @OptIn(ExperimentalCoroutinesApi::class, ObsoleteCoroutinesApi::class)
    @Test
    fun selectingOnSelect() {
        //
        runBlocking {
            val secondChannel = actor<Int> {
                /**
                 *  for(data in channel) {
                 *      sout("$it")
                 *  }
                 */

                // 记住使用API 需要查看它的Doc ...
                // 这一类的consumexxx都是安全方法 ...
                this.consumeEach {
                    delay(200)
                    println("second receive result $it")
                }
            }
        val producer =  produce<Int> {
                for(i in 1 .. 10) {
                    select<Unit> {
                        onSend(i) {
//                            println("发送 $i 到主管道 ...")
                        }
                        secondChannel.onSend(i) {
//                            println("发送到子管道 ....")
                        }
                    }
                }

            // 管道关闭,和对应的具有管道作为句柄的协程构建器 是两件事情 ..
            // 管道关闭,仅仅是做一个标记,当发送或接收会抛出异常,如果我们使用安全的xxxxCatching .. 可能会将异常转为 null,那么管道关闭所抛出的异常将不会影响协程的运行 ...
            // 所以可以手动判断,检查结果值进行协程退出条件 ..
            // 否则在这个例子中,程序不会退出 ....
                secondChannel.close()
            }

            launch {
//                delay(30 * it.toLong())
//                println("primary channel receive data ${producer.receiveCatching().getOrNull()}")
                producer.consumeEach {
                    delay(30 * it.toLong())
                    println("primary channel receive data $it")
                }
            }

       }

    }


//    -------------------------------------- selecting to deferred ---------------------------------
    /**
     *  有些时候我们需要选择 延迟的数据 ...(就是谁先来谁处理) .... 本质上跟其他没有什么区别 ...什么时候发送结果,什么时候消费你 ...
     *  于是,我们也学到了 select dsl 可以使用任意代码,为它注册 clause ....
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun selectingToDeferred() {
        fun CoroutineScope.asyncStringsList(): List<Deferred<Long>> {
            val random = Random(3)
            return List(12) {
                async {
                    val time = (random.nextDouble() * 100).toLong()
                    delay(time)
                    return@async time
                }
            }
        }
        runBlocking {
            val list: List<Deferred<Long>> = asyncStringsList()

            // 这种灵活的 select 关注(一次性) ...
            // 其实这个就等价于CompletableFuture.anyOf( ...)
            select<Unit> {
                list.forEach {
                    // 注册 clause ...
                    it.onAwait { data ->
                        println("接收到 $data")
                    }
                }
            }

            // 还位于请求中的 .....
            println(list.count { it.isActive })
        }
    }


        // ---------------------------------------- 管道上进行切换 -------------------------------------------------
        fun CoroutineScope.switchMapDeferreds(input: ReceiveChannel<Deferred<String>>) = produce<String> {
            var current = input.receive() // start with first received deferred value
            while (isActive) { // loop while not cancelled/closed
                val next = select<Deferred<String>?> { // return next deferred value from this select or null
                    input.onReceiveCatching { update ->
                        update.getOrNull()
                    }
                    current.onAwait { value ->
                        send(value) // send value that current deferred has produced
                        input.receiveCatching().getOrNull() // and use the next deferred from the input channel
                    }
                }
                if (next == null) {
                    println("Channel was closed")
                    break // out of loop
                } else {
                    current = next
                }
            }
        }
    /**
     * 另类的生产者消费者模型 ...(如果input管道足够慢)
     * 也是管道切换(否则 也可以记录那些管道的数据来的慢,但是这个记录不是特别严格 ...) ...
     * 总而言之,当input没有数据的时候,则可以等待它,并在它的数据到来时,给另一个管道发送数据 ...
     *
     * 查看下一个例子,官方用它进行 生产者消费者模型处理 ...
     */
    @Test
        fun switchOnChannel() {

            runBlocking {
                val actor = produce<Deferred<String>> {
                    repeat(10) {
                        async {
                            val value = (Math.random() * 100).toLong()
                            delay(value)
                            send(coroutineContext[Job] as Deferred<String>)
                            value.toString()
                        }

                    }
                }
                switchMapDeferreds(actor).consumeEach {
                    println("接收到生产者产生一个的数据: $it")
                }
            }
        }


    fun CoroutineScope.asyncString(str: String, time: Long) = async {
        delay(time)
        str
    }

    /**
     * 但是这种方式不是很建议,只是可以说 select 可以做这样的事情 ...
     *
     * 这个方式 通过两个协程来实现,非常简单 ....
     */
    @Test
    fun selectProduceOrConsumer() {

        runBlocking {
            val chan = Channel<Deferred<String>>() // the channel for test
            launch { // launch printing coroutine
                for (s in switchMapDeferreds(chan))
                    println(s) // print each received string
            }
            chan.send(asyncString("BEGIN", 100))
            delay(200) // enough time for "BEGIN" to be produced
            chan.send(asyncString("Slow", 500))
            delay(100) // not enough time to produce slow
            chan.send(asyncString("Replace", 100))
            delay(500) // give it time before the last one
            chan.send(asyncString("END", 500))
            delay(1000) // give it time to process
            chan.close() // close the channel ...
            delay(500) // and wait some time to let it finish
        }

    }

    @Test
    fun equipmentToThis() {
        runBlocking {
            val channel = Channel<Long>(Channel.RENDEZVOUS)

            launch {
                channel.consumeEach {
                    delay(20)
                    println(it)
                }
            }

            launch {
                repeat(10) {
                    val time = 10 * (Math.random() * 100).toLong();
                    delay(time)
                    channel.send(time)
                }
                channel.close()
            }

        }
    }
}