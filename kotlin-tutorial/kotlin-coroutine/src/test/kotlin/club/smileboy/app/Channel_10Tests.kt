package club.smileboy.app

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.map
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.Condition

/**
 * @author JASONJ
 * @date 2022/6/19
 * @time 17:29
 * @description Channel  学习..
 *  Contents
 *
 *  在协程中,通过Deferred 来传递单个值 .
 *  在管道中,传递给多个值  - 流式传输多个值 ...
 *
 *  kotlin 认为 管道非常类似于 BlockingQueue ...
 *  但是它所有的函数对应blockQueue来说都是可暂停的,意味着Channel 是协同协程工作 ..
 *  例如 put -> suspend send
 *      take -> suspend receive ..
 *
 *      - blockingQueue and channel compare
 *      - 迭代并关闭管道
 *      - 构建生产者
 *      - Pipeline ...
 *      - 包含素数的pipeline ...
 *      - fan out
 *      - fan in
 *      - buffered channel
 *       - channel is fair ..
 *      - tick channel
 *
 *
 **/
class Channel_10Tests {
    /**
     * blockingQueue 和 channel 比较
     */
    @Test
    fun blockingQueueAndChannelCompareTests() {
        runBlocking {
            val channel = Channel<Int>()
            launch {
                // this might be heavy CPU-consuming computation or async logic, we'll just send five squares
                for (x in 1..5) channel.send(x * x)
            }

            // here we print five received integers:
            repeat(5) { println(channel.receive()) }
        }

    }

    // ---------------------------------- closing and iteration channels -----------------------------------
    /**
     * 和队列相比,管道可以通过close来表达没有更多的数据流入
     *
     * 在接收者这方,那么很容易使用for-each 进行数据接收,并关闭管道 ...(例如发送方关闭了此管道,那么在消费方这边在迭代完成数据之后,也可以表示接收方关闭) ...
     *
     * 那么close 做了什么呢,就是添加了一个特殊的类型数据到管道中,在迭代中获取到这个值就停止迭代操作 ....
     * 于是前面的元素都能够被消费到 ....
     *
     * 将上面的例子改写就该是 ..
     *
     * 但是这样的话,这个例子不能够正确的停止,因为管道没有关闭,for-each 永远阻塞 ...
     */
    @Test
    fun closingAndIterationTests() {
        runBlocking {
            val channel = Channel<Int>()
            launch {
                // this might be heavy CPU-consuming computation or async logic, we'll just send five squares
                for (x in 1..5) channel.send(x * x)
//                channel.close()
                // 但是cancel 不能够被调用 ...
//                channel.cancel()
            }

            // here we print five received integers:
            // here we print received values using `for` loop (until the channel is closed)
            for (y in channel) println(y)
            println("Done!")
        }
    }

    // ---------------------------------------------- 构建生产者 --------------------------------------
    /**
     * 协程产生一系列元素的模式很常见。这是在并发代码中经常出现的生产者-消费者模式的一部分。你可以将这样的生产者抽象为一个以通道为参数的函数，但这违背了函数必须返回结果的常识。
     * 当然我觉得这里有点扯淡,函数也可以没有返回值 ...(但是生产者 必然应该有函数结果,基于这个特性,没有返回值也不是很好) ..
     * 有一个方便的协程构建器，名为produce，可以很容易地在生产者端完成，还有一个扩展函数consumeEach，它替换了消费者端的for循环：
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun producer() = runBlocking {
        val produce = produce<String> {
            // 这里面是协程工作的action
            (1..5).forEach {
                send((it * it).toString())
            }
        }

        produce.consumeEach { println("receive data $it") }
    }

    // ------------------------------ Pipeline --------------------------------
    /**
     * pipeline 的工作形式 就是将流式的数据,一个阶段一个阶段的处理,整体是一个管道流 ...
     * 例如生产者在不断生产数据,然后后续不断加工,当下游阶段处理不过来的时候,反压上流,减缓生产 ...
     */
    @Test
    fun pipelineTests() {
        runBlocking {
            produce<Int> {
                produce<Int> {
                    repeat(10) {
                        send(it)
                    }
                    close()
                }.consumeEach {
                    send(it * it)
                }.let {
                    close()
                }
            }.consumeEach {
                println("pipeline result $it")
            }
        }
    }

    // ------------------------------ prime numbers with pipeline
    /**
     * 这是一种不寻常的方式去发现素数 ...
     *
     * 还可以通过iterator 协程构建器 来实现相同的逻辑..   (且可以拜托协程的范围,不在需要 runBlocking) ...
     *
     * 并且还可以利用多核心cpu的计算能力 ...
     *
     * 但是你不要使用iterator (普通的迭代器 / sequence) 因为它们不支持协程处理 ... 这一些内容在flow中学习 ...
     */
    @Test
    fun primeNumbersWithPipeline() {

        /*

         fun CoroutineScope.filter(numbers: ReceiveChannel<Int>, prime: Int) = produce<Int> {
            for (x in numbers) if (x % prime != 0) send(x)
        }
        runBlocking {

            // 创建一个Int.max 长度的channel ...
            var value = produce<Int> {
                for (i in 2 until 1000) {
                    send(i)
                }
            }

            repeat(10) {
                val receive = value.receive()
                // 素数 ...
                println(receive)
                value = filter(value,receive)
            }

            coroutineContext.cancelChildren()
        }*/
        var values = iterator<Int> {
            (2 until 1000).forEach { i ->
                yield(i)
            }
        }

        repeat(10) {
            if (values.hasNext()) {
                val value = values.next()
                println("prime value $value")
                val preIterator = values;
                values = iterator<Int> {
                    preIterator.forEach { next ->
                        if (next % value != 0) yield(next)
                    }
                }
            }
        }
    }


    // --------------------------------------- fan-out 多输出端 -----------------------------
    /**
     * 现在先说一下  close / cancel的区别 ..
     * 针对不同的Channel 它们的方法不一样  ... 对于生产者管道来说,它的输出端是 接收者 ..那么应该使用cancel ....
     * 那么会停止消费动作 ... 包括未消费的 ....
     *
     * 但是close 不会 ... 只是关闭发送管道 ...
     * 但是消费端还能够正常的 消费已有的数据 ....
     */
    fun CoroutineScope.launchProcessor(id: Int, channel: ReceiveChannel<Int>) = launch {
        for (msg in channel) {
            delay(1000)
            println("Processor #$id received $msg")
        }
    }

    /**
     * 这个 例子能够看出 cancel / close 的区别 ...
     *
     * 正因如此, 我们也了解了consumeEach 和普通for loop的区别 ..(仅说明管道) ...
     * 正常for-loop会安全的在多个协程中执行 ... 它不会取消掉底层的管道 ..
     * 因为for-loop失败之后,其他的consumer processor还能够继续处理(仅针对这个例子中的消费者processor说明) ,
     * 但是如果consumeEach 使用(如果迭代过程中失败,那么会取消底层的管道) ...
     * 我们通过下面的一个例子进行说明 ...
     */
    @Test
    fun fanOut() {

        runBlocking {
//            // 将会停止消费所有的数据 ...
//            produce<Int> {
//
//            }.cancel()

            // 例如我们开启一个生产者,5个消费者 同时消费一个生产者 ...
            val producer = produce<Int>(capacity = Channel.BUFFERED) {
                val that = this;
                var count = AtomicInteger(0);
                launch(Dispatchers.Default) {
                    delay(2000)
                    // 有可能我发出了很多,但是消费者会立即停止消费 ...
                    println("已经发送了 .... ${count.get()}")
//                    that.cancel()
                    that.close()
                }


                repeat(1000) {
                    // 使用close 那么这里将会报错,从而导致 producer 死亡 ... 但是我们为了消费继续,使用尝试Send即可.. ...
                    trySend(it).getOrNull() ?: return@repeat
                    count.incrementAndGet()
                }
//                close()
            }

            launchProcessor(1, producer)
            launchProcessor(2, producer)
            launchProcessor(3, producer)
            launchProcessor(4, producer)
            launchProcessor(5, producer)
        }

    }

    /**
     * 这个例子中,当consumeEach 出现错误之后,cancel 掉管道之后,其他协程也就自然退出 ... 就算除了processor 3的processor 使用for-loop 也会正常退出 ...
     */
    @Test
    fun consumeEachTests() {

        runBlocking {
            val produce = produce {
                repeat(1000) {
                    send(it)
                }
            }

            launch {
                var count = 0;
                produce.consumeEach {
                    if (it % 2 == 0) {
                        count++
                        if (count == 5) {
                            // 抛出这个异常,是为了协程正常退出,但是查看其他协程的反应(正常consumeEach) ...
                            throw CancellationException("network error")
                        }
                    }
                    println("processor number: 3,consume data is $it")
                }
            }

            launch {
//                produce.consumeEach {
                for (it in produce) {
                    println("processor number: 2,consume data is $it")
                }
            }

            launch {
//                produce.consumeEach {
                for (it in produce) {
                    println("processor number: 1,consume data is $it")
                }
            }

        }

    }

    /**
     * 多个协程同时对一个管道进行写入 ...
     * 程序的行为 取决于你管道的类型 ...(例如排他性写入 / 缓冲性写入) ..
     */
    @Test
    fun fanIn() {
        suspend fun sendString(channel: SendChannel<String>, s: String, time: Long) {
            while (true) {
                delay(time)
                channel.send(s)
            }
        }

        runBlocking {
            val channel = Channel<String>()
            launch { sendString(channel, "foo", 200L) }
            launch { sendString(channel, "BAR!", 500L) }
            repeat(6) { // receive first six
                println(channel.receive())
            }
            coroutineContext.cancelChildren() // cancel all children to let main finish
        }
    }

    // ----------------------------- buffered  channels ---------------------------------------------------------------
    /**
     * 默认produce 是rendezvous channel
     * 但是管道都是可以调整行为的...
     *
     * 阻塞行为等同于BLockingQueue ....
     *
     * The first four elements are added to the buffer and the sender suspends when trying to send the fifth one.
     */
    @Test
    fun bufferedChannels() {
        // rendezvous channel ..
        runBlocking {
            val channel = Channel<Int>(4) // create buffered channel
            val sender = launch { // launch sender coroutine
                repeat(10) {
                    println("Sending $it") // print before sending each element
                    channel.send(it) // will suspend when buffer is full
                }
            }
// don't receive anything... just wait....
            delay(1000)
            sender.cancel() // cancel sender coroutine
        }

    }

    // 管道是公平的 -------------------------------------------------------------------------------
    /**
     * 它们的顺序总是先入先出 ...
     * 例如 ping / pong 发送 /接收调用 ...
     *
     * 请注意，由于正在使用的执行器的性质，有时通道可能会产生看起来不公平的执行。有关详细信息，请参阅此问题。
     * https://github.com/Kotlin/kotlinx.coroutines/issues/111
     *
     * 为什么会这样?
     * 因为协程是 可控制的,不像线程,它的范围是立即可控的,例如,我们使用ping / pong 同时接收一个管道的数据 ..
     * ping / pong 谁先接收到数据取决于它们的调用顺序 ..
     * 但是 后续的数据接收,取决于它们的消费数据的能力快慢 ...
     *
     * 于是在这个例子中 你能够看出来它们的消费顺序 就是先入先出 ...
     *
     */
    @Test
    fun channelIsFair() {
        data class Ball(var hits: Int)
        suspend fun player(name: String, table: Channel<Ball>) {
            for (ball in table) { // receive the ball in a loop
                ball.hits++
                println("$name $ball")
                delay(300) // wait a bit
                table.send(ball) // send the ball back
            }
        }

        runBlocking {
            val table = Channel<Ball>() // a shared table
            launch { player("ping", table) }
            launch { player("pong", table) }
            table.send(Ball(0)) // serve the ball
            delay(1000) // delay 1 second
            coroutineContext.cancelChildren() // game over, cancel them
        }
    }

    // ---------------------------------------- Ticker Channels -------------------------
    /**
     * ticker 是一个特殊的rendezvous channel ...
     * 它具有conflated 的性质 ...
     */
    @Test
    fun tickChannelsWithOnFixedPeriod() {

        val tickerChannel = ticker(100,0)

       runBlocking {
           var nextElement = withTimeoutOrNull(1) { tickerChannel.receive() }
           println("Initial element is available immediately: $nextElement") // no initial delay

           nextElement = withTimeoutOrNull(50) { tickerChannel.receive() } // all subsequent elements have 100ms delay
           println("Next element is not ready in 50 ms: $nextElement")

           nextElement = withTimeoutOrNull(60) { tickerChannel.receive() }
           println("Next element is ready in 100 ms: $nextElement")

           // Emulate large consumption delays
           println("Consumer pauses for 150ms")
           delay(150)
           // Next element is available immediately
           nextElement = withTimeoutOrNull(1) { tickerChannel.receive() }
           println("Next element is available immediately after large consumer delay: $nextElement")
           // Note that the pause between `receive` calls is taken into account and next element arrives faster
           nextElement = withTimeoutOrNull(60) { tickerChannel.receive() }
           println("Next element is ready in 50ms after consumer pause in 150ms: $nextElement")

           tickerChannel.cancel() // indicate that no more elements are needed
       }
    }

    /**
     *  TickerMode.FIXED_DELAY 维持一个固定延时的 ticker channel
     *  上面的例子 TickerMode.FIXED_PERIOD  维持一个固定的速率的 ticker channel ...
     */
    @OptIn(ObsoleteCoroutinesApi::class)
    @Test
    fun tickerChannelWithFixedDelay() {
        val tickerChannel = ticker(100,0, mode = TickerMode.FIXED_DELAY)

        runBlocking {
            var nextElement = withTimeoutOrNull(1) { tickerChannel.receive() }
            println("Initial element is available immediately: $nextElement") // no initial delay

            nextElement = withTimeoutOrNull(50) { tickerChannel.receive() } // all subsequent elements have 100ms delay
            println("Next element is not ready in 50 ms: $nextElement")

            nextElement = withTimeoutOrNull(60) { tickerChannel.receive() }
            println("Next element is ready in 100 ms: $nextElement")

            // Emulate large consumption delays
            println("Consumer pauses for 150ms")
            delay(150)
            // Next element is available immediately
            nextElement = withTimeoutOrNull(1) { tickerChannel.receive() }
            println("Next element is available immediately after large consumer delay: $nextElement")

            // 由于消费者延时 150秒 ... 它产生了一个,但是 前面一个已经消费了,这个时候还需要等待100秒才能够获取数据 ...
            nextElement = withTimeoutOrNull(60) { tickerChannel.receive() }
            println("Next element is no ready in 50ms after consumer pause in 150ms: $nextElement")

            nextElement = withTimeoutOrNull(50) {
                tickerChannel.receive()
            }
            println("Next element is ready in 100ms: $nextElement")

            // 于是我们继续处理 ..
            nextElement = withTimeoutOrNull(100) {
                tickerChannel.receive()
            }
            println("Next element is ready in 100ms: $nextElement")
            nextElement = withTimeoutOrNull(100) {
                tickerChannel.receive()
            }
            println("Next element is ready in 100ms: $nextElement")
            nextElement = withTimeoutOrNull(100) {
                tickerChannel.receive()
            }
            println("Next element is ready in 100ms: $nextElement")

            tickerChannel.cancel() // indicate that no more elements are needed
        }
    }


}