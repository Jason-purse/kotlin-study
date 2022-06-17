package club.smileboy.app.basic

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

/**
 * @author FLJ
 * @date 2022/6/16
 * @time 15:55
 * @Description 持续性的为用户渲染数据,提高用户体验 ..
 * 前面的CoroutineAndChannelTests 中 只提到了协程的并发使用,但是也不是最高效的 ..
 * 它是等待所有的数据加载完毕之后进行渲染,有些时候我们可以让用户快速的得到一部分数据 ..
 * 基于这个原理:
 *
 * 我们可以改进结构化并发 ..
 *
 * 我们先尝试使用一个没有并发形式的解决方案 ..
 * 我们将启动一个全新的协程,在这里里面我们依次的加载每个仓库的数据 ..
 * 可以明白,依次加载,就算每一个仓库又通过一个独立的协程进行加载,它们之间的中间状态 添加也具备依次性 ...
 * 也就是非线程安全的,只能够在同一个协程或者线程中操作 ...
 *
 *
 *
 * 官方的说法:
 * 请注意，协程和通道都不会完全消除并发带来的复杂性，但是当您需要推理并了解正在发生的事情时，它们肯定会让您的生活更轻松。
 *
 */
class ShowingProgress_3Tests {


    // 编写一个扩展函数
    /**
     * 聚合 ...
     *
     * 这里的聚合 比如需要归并贡献者的 提交次数 ... 不是重要的事情 ...
     */
    fun <T> List<T>.aggregate(): List<T> {
        return this;
    }


    @Test
    fun noConcurrencyImplementation() {
        suspend fun loadContributorsProgress(
            service: Any,
            req: Any,
            updateResults: suspend (List<Any>, completed: Boolean) -> Unit
        ) {
            // 这里假设通过一个suspend 执行( 代替通过协程处理并等待他完成的一个过程) ....
            // 拿到仓库
            val repos = suspend {
                mutableListOf<Int>(1,2,3,4,6)
            }.invoke()



            var allUsers = emptyList<Any>()
            var index = 0;
            repeat(repos.size) {
            val users = suspend {
                index = it
                    mutableListOf(1,2,3,4)
                }.invoke()

                // 这一步操作要保证"线程安全"
                // 如何保证,kotlin中通过管道实现 ...
                allUsers = (allUsers + users).aggregate()
                updateResults(allUsers, index == repos.lastIndex)
            }
        }
    }

    /**
     * kotlin 认为通过一个可变的共享状态分享信息 相比交流信息之下,更为危险且容易出错
     * 在协程中,通过管道进行通信 - 信息交换 ...
     *
     * 在kotlin 中管道是通信原语 ... 允许在不同的协程中传递数据 ...
     * 一个协程能够发送信息到一个管道中,然后另一个协程能够接收数据 ...
     *
     * 发送信息的协程称为生产者, 消费信息的协程称为消费者 ...
     * 当需要时,多个协程也能够发送消息到同一个管道中 ... 并且多个协程能够接收这个管道的信息 ...
     *
     * 值得注意的是,当从相同管道中获取消息时,仅仅只有一个协程能够消费数据,处理它将自动的从管道中移除 ..
     *
     * 我们能够想象一个管道是一个元素集合(直接对比也可以是队列: 元素被增加,从另一个头进行消费) ..
     * 然而它们有本质的不同,对比集合,在管道中,即使在它们的同步版本中，通道也可以暂停发送和接收操作(send / receive) ...
     * 当管道为空或者满的时候 ...能够暂停(管道的尺寸能够被约束,它们也能够被填满) ...
     *
     * 管道根据生产者消费者模型分为两种接口: SendChannel / ReceiveChanel ...
     * 然后Channel 同时继承这两者接口 ...  注意到这个两个接口上的send / receive 方法都是suspend 函数 ....
     *
     * 生产者能够关闭一个管道(表示没有更多的数据能够进入) ...
     *
     * kotlin 提供了各种管道 ... 不同之处就是它们能够存储的元素个数 ... 以及 send 调用是否能够暂停或者否 ...
     * 对于所有的管道类型,receive 调用都是相同的方式.. 管道不为空获取一个元素,否则暂停 ...
     *
     * 不限制管道:
     *   类似于队列,生产者可以无限发送元素到队列中 ... send 调用绝不会暂停 .. 如果没有足够的内存,它将抛出一个OutOfMemoryException ...
     *   不同于队列(当消费者尝试从空管道中获取 它将暂停 等待管道输入元素) ...
     *
     * 限制容量的管道
     *    具有有限容量, 生产者send 可能会暂停,直到拥有足够的空间 ...
     *    消费者 receive 形式同上,为空 暂停等待 ...
     *
     * Rendezvous channel
     *    面对面管道:
     *    没有buffer,管道容量为0,它们的含义是 生产者的数据一定要亲手给到消费者 .. 也成为会面的意思 ...
     *    消费者和生产者总是会暂停,等待其中另一个操作被调用(无论是 send / receive) ...
     *
     * 合并管道
     *   一个新的元素 将会重写之前发送到管道的元素 ... 因此接收者总是会获取到最新的元素,send 将不会暂停 ..
     *   这就是一种覆盖管道 ...
     *
     *
     * 如何创建管道 ...
     * 通过指定管道的类型和容量 ....
     * val rendezvousChannel = Channel<String>()
     *   val bufferedChannel = Channel<String>(10)
     *  val conflatedChannel = Channel<String>(CONFLATED)
     *   val unlimitedChannel = Channel<String>(UNLIMITED)
     */
    @Test
    fun coroutineCommunicationTests() {
        runBlocking {
            // 不限制管道
            suspend {
                var channel = Channel<String>()
                launch {
                    repeat(10) {
                        channel.send(it.toString())
                    }

                    val sendChannel  = channel as SendChannel<String>
                    // 不再发送(只要关闭两边都无法获取或者发送数据) ...
                    // 尝试处理,则 抛出 ...
                    // ClosedSendChannelException
                    sendChannel.close()
                }

                launch {
                    // 由于关闭管道之后, isClosedForReceive 会等待管道中的所有数据读取完毕之后才会变为true ...
                    // 详见 Channel#close 方法
                    while (!channel.isClosedForReceive) {
                        var receive = channel.receive()
                        println("获取的数据为: $receive")
                    }
                    println("生产者消费者模型结束 ....,isCloseForReceive: ${channel.isClosedForReceive}")
                    channel.receive()
                }

            }()
        }
    }

    /**
     * 使用管道进行协程通信 然后更新中间状态 ....
     */
    @Test
    fun structConcurrencyAndChannel() {
        // 通过管道,我们能够改写需要通过的那一段代码 ..
        // 仅仅需要接收可暂停的通道的数据,来实现任意完成仓库贡献者加载的数据进行中间状态更新即可,使其线程安全 ...

        suspend fun loadContributorsProgress(
            service: Any,
            req: Any,
            updateResults: suspend (List<Any>, completed: Boolean) -> Unit
        ) {
            // 这里假设通过一个suspend 执行( 代替通过协程处理并等待他完成的一个过程) ....
            // 拿到仓库
            val repos = suspend {
                mutableListOf<Int>(1,2,3,4,6)
            }.invoke()



            var allUsers = emptyList<Any>()
            var index = 0;
            val channel  = Channel<List<Any>>()
            // 尝试使用暂停函数增加一个新的coroutine scope 进行结构化并发 ...
            coroutineScope {
                repeat(repos.size) {
                    launch {
                        delay(202 * it.toLong() / ((Math.random()) * 100).toLong())
                        channel.send(mutableListOf(1,2,3,4,5))
                    }
                }

                repeat(repos.size) {
                    // 这里无法直接调用 创建新协程的方法 ....
                    // 这一步操作要保证"线程安全"
                    // 如何保证,kotlin中通过管道实现 ...

                    allUsers = (allUsers + channel.receive()).aggregate()
                    updateResults(allUsers, index == repos.lastIndex)
                }
            }

        }
    }
}




