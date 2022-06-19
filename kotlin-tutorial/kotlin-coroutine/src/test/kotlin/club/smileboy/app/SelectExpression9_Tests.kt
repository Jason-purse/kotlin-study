package club.smileboy.app

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.select
import org.junit.jupiter.api.Test

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

    /**
     * onReceiveCatching ...
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
            repeat(8) { // print first eight results
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
}