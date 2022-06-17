package club.smileboy.app.composeSuspend

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import kotlin.system.measureTimeMillis

/**
 * @author JASONJ
 * @date 2022/6/16
 * @time 21:23
 * @description 组合暂停函数 ...
 *
 * contents
 *  - 了解暂停函数的特征
 *  - 使用异步进行并发
 *  - 惰性使用 async
 *  - 异步风格的函数
 *  - 结构化并发 ...
 *
 * 暂停函数的特点是:
 *  - 默认依次执行 ...
 **/
class ComposeSuspend_6Tests {
    /**
     * 它们将依次执行 ..'
     * 在协程中,暂停函数等同于普通函数,将会依次执行 ...
     */
    @Test
    fun suspendSequentialByDefault() = runBlocking {
        suspend fun doSomethingUsefulOne(): Int {
            delay(1000L) // pretend we are doing something useful here
            return 13
        }

        suspend fun doSomethingUsefulTwo(): Int {
            delay(1000L) // pretend we are doing something useful here, too
            return 29
        }

        doSomethingUsefulOne()
        doSomethingUsefulTwo()
    }


    /**
     * 一般来说,我们可能想要并发执行,就像ComplemetationFuture结合异步调用一样(allOf),例如让回答者更快,我们可以结合暂停函数
     * 和 协程构建器 ..
     *
     * 同样有些暂停函数只能够执行其他普通暂停函数或者指定协程scope的函数 ...
     * 如果你发现你在一个暂停函数中无法执行 协程scope的方法,那么说明你的暂停函数是一个普通暂停函数,它的接收者不是coroutine scope ..
     * 仅仅改写它让它依靠 协程scope 即可轻松使用协程帮助 ...
     */
    @Test
    fun answerFaster() {
        runBlocking {
            // 我们可以通过测试协程的工具方法进行测试 ..
            // 例如测试运行时间..
            val total = measureTimeMillis {
                suspend {
                    val a = async {
                        delay(200)
                        12
                    }
                    val b = async {
                        delay(100)
                        13
                    }
                    println("aswer times: ${a.await() + b.await()}")
                }()
            }

            println("total times is $total")

        }
    }

    // ------------------------------- lazily use async ------------------------
    /**
     * 启用之后,通过job的start 开启协程 ..
     *
     * 请注意，如果我们只是在 println 中调用 await 而没有首先在各个协程上调用 start，这将导致顺序行为，
     * 因为 await 启动协程执行并等待其完成，这不是惰性的预期用例。
     * 在计算值涉及可暂停函数的情况下， async(start = CoroutineStart.LAZY) 的用例是标准惰性函数的替代品。
     */
    @Test
    fun LazilyUseAsync() {
        runBlocking {
            // 这指定了使用协程的 形式 ...
            //有4种行为 ..
            async(start = CoroutineStart.LAZY) {
                val time = measureTimeMillis {
                    val one = async(start = CoroutineStart.LAZY) {
                        delay(200)
                        12
                    }
                    val two = async(start = CoroutineStart.LAZY) {
                        delay(300)
                        14
                    }
                    // some computation
                    one.start() // start the first one
                    two.start() // start the second one
                    println("The answer is ${one.await() + two.await()}")
                }
                println("Completed in $time ms")
            }
        }
    }

    /**
     * 也就是我们可以使用 lazy 函数代替这样的惰性async 使用 ..
     *
     * 但是切记并不是说它就是lazy函数的替代,仅仅是说,lazy用于普通情况下的懒加载 .. / 懒初始化
     *  而在涉及到参与函数 执行 数据的懒初始化 / 懒加载,可以通过惰性协程的使用方式  这两者的工作模式非常相似 ...
     *
     * 本来说 lazy 函数 仅仅是懒加载模式的实现而已 ....
     * 这里的withContext 等价于
     *  club.smileboy.app.basic.CoroutineAndChannelTests#improveCoroutine
     * @see club.smileboy.app.basic.CoroutineAndChannel_2Tests.improveCoroutine
     */
    @Test
    fun lazyFunction() = runBlocking {
        var value = lazy {
            runBlocking {

                // 这里我们也能够灵活使用我们学到的知识,对于需要执行协程并等待它执行,使用withContext是一件好事情 ...
                withContext(Dispatchers.Default) {
                    delay(300)
                    1123
                }
            }
        }.value
        println(value)
    }


    // ------------------------------------- 异步风格的函数 -----------------------------------------------
    /**
     * 并没有说GlobalScope对应的协程构建器就没有勇武之地了,
     * 例如添加一个伴随应用生命周期的协程任务 ... 使用它就非常的合适 ...
     *
     * 并且我们通过 使用可取消的暂停函数感知 应用的生命周期,在结束的时候自动退出 ... 结合withTimeout / runCatching ... 实现异常捕获 / 异常吞咽处理 ...
     * 但是使用它需要加上标记
     * @OptIn(DelicateCoroutinesApi::class).
     *
     * 它包含xxxAsync / xxx -> 它们不是暂停函数,能够作为普通函数在任何地方使用 ...
     * Async 结尾的方法都表示它们的动作和执行代码是异步的 ... 并发性质的 ...
     *
     * 请注意,Globalscope中的每一个协程和这个socpe关联的顶级协程不具有结构性并发 ..但是在协程内部依然可以存在结构性并发 ...
     * 并且以下代码会立即退出 .. 因为runBlocking 和 GlobalScope的协程并没有任何关系 ...
     *
     *
     * 对于kotlin 来说,这个方式不推荐使用,它认为如果一个这种异步函数启动,但是在获取它结果await函数使用之前的代码中出现了问题,那么 初始化这一块逻辑的操作虽然中断,但是这个协程还在背后执行,
     * 违背了结构化并发的意愿, 例如在这种情况下,应用程序继续运行,可能会导致未知行为 ..
     *
     * 当一个GlobalScope中的协程出现错误之后,会有一个全局的异常处理器捕捉错误,记录并报告错误给开发者,但是这 不会阻止应用停止 ..
     * 所以GlobalScope 非结构化并发能用,但是需要注意 ..
     *
     */
    @Test
    fun globalScopeFunction() {
        // The result type of somethingUsefulOneAsync is Deferred<Int>
        @OptIn(DelicateCoroutinesApi::class)
        fun somethingUsefulOneAsync() = GlobalScope.async {
            delay(200)
            throw Exception("test global coroutine error !!!")
            1231
        }



        // The result type of somethingUsefulTwoAsync is Deferred<Int>
        @OptIn(DelicateCoroutinesApi::class)
        fun somethingUsefulTwoAsync() = GlobalScope.async {
            println("等待我的子协程结束 ...")
            delay(200)
            launch {
                delay(3000)
                println("我是global coroutine 的子协程 ...")
                println("子协程结束,我将退出 ...")
            }
            1231213
        }

        GlobalScope.launch {
            somethingUsefulOneAsync().await()
        }
       runBlocking {


           // 将以下代码通过一个暂停函数 进行包括处理即可 ... ,当然这样依旧不可以 ... 我们需要使用一个特殊的暂停函数 ...
           // 如果直接在暂停函数中,同步调用也是可以的 ...
           // 但是我们可以更加优雅一点 ... 阻塞子协程 ...
//           suspend {
           coroutineScope {
               somethingUsefulTwoAsync().await()
           }
//           }()
       }

    }

//    -------------------------------------------- 结构化并发------------------------------------------------
    /**
     * 其实我们知道 协程的生命周期依靠协程scope ..
     * 对于协程异常的监控,跟编写命令式代码是一致的 ... 我们仅仅需要在对应的代码块上进行监控即可 ...
     *
     * 但是尤为注意  CancellationException 它是一种协程认为正常完成的异常 ... 所以如果你的try -catch 没有捕获到它  ..
     * 这尤其发生在结构化并发中 ...
     *
     * 下面代码中 我们得出 对暂停函数进行异常捕获是一种好的选择 ..
     * 其次,自定义错误不应该使用 CancellationException 逃避 协程异常检查 ....
     */
    @Test
    fun tryCatchCoroutineException() {
        try {
            runBlocking {
                launch {
                    throw Exception("你好")
                }
            }
        }catch (e: Exception) {
            println("catch exception ${e.message}")
        }


        runBlocking {
            try {
                launch {
                    try {
                        coroutineScope {
                            launch {
                                try {
                                    delay(200)
                                    // 这种异常被认为是正常完成协程的异常 ...
                                    throw CancellationException("你好号")
                                }catch (e: Exception) {
                                    println("最深处捕获到异常 ... ${e.message}")
                                    throw e;
                                }
                            }
                        }
                    }catch (e: Exception) {
                        println("尝试在coroutine scope 中捕获异常 ...")
                    }
                }
            }catch (e: Exception) {
                println("catch child coroutine error ${e.message}")
            }

        }

        runBlocking {
           launch {
               // 这个方法可能会导致它阻塞调用等待异常 ... 因为 这是一个普通函数 ... 如果我们只是开启一个协程,不阻塞等待它,那么esceptionOrNull 不可能得到错误 ..
               runCatching {
                   launch {
                    delay(1000)
                        throw CancellationException("测试错误 ...")
                    }.join()
               }.apply {
                   println("异常信息: ${this.exceptionOrNull()}")
               }
               println("runBlocking 执行 .....")

           }

        }

    }

}