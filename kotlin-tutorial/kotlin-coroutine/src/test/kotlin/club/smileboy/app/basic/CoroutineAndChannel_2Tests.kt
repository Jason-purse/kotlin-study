package club.smileboy.app.basic

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

/**
 * @author FLJ
 * @date 2022/6/16
 * @time 12:59
 * @Description 协程和管道学习  hands-on
 *
 *  - suspend 函数真正的 能力 -- 39行
 *  - 并发/ 优化  --
 *
 *
 * 在官方的例子中, 给出了一个Retrofit 框架的示例 ...
 * 它想通过suspend 函数表达的意思是, suspend 函数 其实和普通函数没有什么区别,但是能够暂停协程,也就是通过暂停协程的方式 来代替暂停线程  ...
 *  ```pre
 * suspend fun loadContributorsSuspend(service: GitHubService, req: RequestData): List<User> {
 *    val repos = service
 *     .getOrgRepos(req.org)
 *     .also { logRepos(req, it) }
 *     .bodyList()
 *
 *  return repos.flatMap { repo ->
 *     service.getRepoContributors(req.org, repo.name)
 *    .also { logUsers(repo, it) }
 *       .bodyList()
 *    }.aggregate()
 * }
 *  ```
 *
 *  最终结果 =>
 *  block -> suspend
 *  thread -> coroutine
 */
class CoroutineAndChannel_2Tests {
//    ========================================================== suspend 函数 能力 ======================================
    /**
     * 例如官方给出这样的一段代码,如何调用上面示例的方法,如何调用一个暂停函数
     * 它的答案是使用协程
     * 代码逻辑无关紧要,我们通过一个doHandle suspend 函数来模拟它 ...
     *
     * 下面这个函数的流程是:
     * runBlocking 开启了一个阻塞当前线程的协程, 对此我们的doHandle 函数,发起一个请求(通过延时暂停函数代替) .. 最终返回请求结果 ...
     *
     * 底层动作是:  开启阻塞当前线程的协程,当执行我们的doHandle 暂停函数,就意味着 当前协程开启了一个可暂停的计算,当doHandle 中的 delay 函数执行,相当于请求,
     * 那么暂停了当前协程,于是如果这是一个异步协程(也就是使用了其他的协程构建器,不阻塞当前线程的协程构建器) 那么底层的线程将会被释放, 当请求完成回来时,线程又可以继续开启
     * 此可暂停计算,返回结果 ....
     *
     * 所以runBlocking 只是这个现象的一种呈现,你当然可以换成 launch,那这样的话, 主线程中的打印将不会等待你  ..
     * 并且launch 不能够直接使用在简单函数中 ....
     * 你需要自己创建一个 coroutineScope ...
     *
     *
     * 当协程暂停的时候,线程将释放,协程的数据(上下文等信息) 存储在内存中 ...线程可以参与其他活动 ...
     * 点此链接查看 线程活动 : <a href="https://play.kotlinlang.org/resources/hands-on/Introduction%20to%20Coroutines%20and%20Channels/assets/4-suspend/SuspensionProcess.gif">点此</a>
     *
     * 这个函数,可以知道还没有利用好协程的并发能力,仅仅只是将请求操作和  UI 显示分离了 ...
     * 这里的主线程假设就是 UI 线程,并且当请求完毕之后还需要进行渲染 ,并且UI 线程还需要支持其他页面事件操作支持 ...
     *
     * 于是所有请求的过程还可以进行优化 ... 将每一个请求拆分为单独的协程  ...
     *
     * 并且通过 -Dkotlinx.coroutines.debug jvm 选项标记 能够查看运行的代码位于哪一个协程 ..
     *
     */
    @Test
    fun suspendFunctionHandle() {
        suspend fun doHandle(): String {
            delay(500)
            // 所有的请求依次处理 ...
            return "done"
        }

        var result = runBlocking {
            doHandle()
        }

        // 我们等待了这个协程结束并获取最终结果  ...
        println(result)
    }
    // ----------------------------- improve / concurrency --------------------------
    /**
     * 首先协程非常廉价 ...
     * 并且 不同的库可能会定义不同的 协程构建器 .. launch / async / ruBlocking ..
     *
     * 那么 async 启动了一个异步并返回一个Deferred Job 任务 ..
     * Deferred 的概念类似于 Future / Promise ...
     * 它存储一个计算,并在将来的某一个时刻返回结果 .. - 它承诺在未来某一个时刻必然有一个结果 ...
     *
     * 那么 launch 和 async 的不同是 launch 用来开启一个协程(计算)但是不关心具体的结果 ..(也就是不需要返回值) ...
     *
     * 于是 launch 返回Job,它代表协程,但是它不能够获取结果,因为它本身也不期待结果 ...
     * 但是async 期待结果,所以它扩展了Job,返回Deferred,且包含一个泛型返回结果类型 ,并在将来某一时刻能够获取此最终结果 ...
     * 两者同时都支持阻塞调用,含义就是必须等待这个协程(计算完成) ...
     * launch 通过 job.join 完成等待调用 ..
     * async 和launch 有所不同,它需要通过await 等待协程计算完成且返回最终结果 ..
     *
     * 当你调用了await,那么当前协程就会阻塞在这行代码,等待子协程(Job完成返回) ...
     * 也就是说暂停一个协程的方式 也可以通过等待一个协程完成实现 ...
     *
     *
     */
    @Test
    fun improveSuspendFunctionByConcurrency() {
        suspend fun loadData(): Int {
            println("loading...")
            delay(1000L)
            println("loaded!")
            return 42
        }

        runBlocking {
            val deferred: Deferred<Int> = async {
                loadData()
            }
            println("waiting...")
            println(deferred.await())
        }
    }

    //---------------------------------------------- coroutine and concurrency and improve--------------------
    /**
     * 之前我们知道所有的请求都在一个协程中,效率还是不够高
     *
     * 现在我们应该将每一个请求封装到 async 子协程中,并且在主协程中等待它们完成,最终交给 UI 协程 ...
     *
     * 你可能能够看到 我们通过withContext 指定了一个协程在应该在哪一个线程或者那些线程上执行 ..  但是 调用这个函数的当前协程暂停 且等待它完成 ....
     * CoroutineDispatcher 能够用来控制处理如何派发协程到线程上进行运行  它提供了一些可以使用的资源 ... 例如Default 资源池 / Main 主线程(UI) / IO 线程 ..
     *
     * 但是协程构造器和 withContext的行为不一样,它通过指定使用继承的外部协程scope 的上下文的 CoroutineDispatcher 来运行协程,  但是它不会阻塞当前协程 ,这是它们两个的区别 ..
     *
     * 例如 : launch(CoroutineDispatcher.Default) { .... } 和 withContext(CoroutineDispatcher.Main) { .... }
     *
     * 好好理解这两者的区别 ....
     *
     * 同样,既然withContext 会一直伴随 对应的协程,那么 它等价于 一开始指定这个协程的上下文并运行并等待它运行launch(...context ...).join()
     */
    @Test
    fun improveCoroutine()  {
        suspend fun CoroutineScope.doRequest() : Deferred<Long> {
         return async {
                val toLong = (Math.random() * 100).toLong()
                delay( toLong * 10)
                toLong
            }
        }

        suspend fun doUI() {
            println("渲染页面 ....")
        }

        runBlocking {

            launch {
                val array : MutableList<Deferred<Long>> = mutableListOf()
                repeat(5) {
                    array.add(doRequest())
                }

                // 阻塞等待所有协程完成 ..
                var awaitAll = array.awaitAll()
                println(awaitAll)
                withContext(Dispatchers.IO) {
                    doUI()
                }
            }
        }
    }

    /**
     * 获取 withContext 的概念还是有点模糊,我们尝试做一个测试 ...
     *
     * 通过这个我们可以明确 withContext的概念,它使用指定的上下文线程 启动一个新的协程并等待这个协程计算完成,但是至于这个协程的调度(暂停之后的恢复)还是取决于能够调度它的线程有哪些 ...
     *  那么假设我们外围的 Dispatcher 是IO ,那么情况会怎么样呢 .. 发现还是会使用额外的可用线程 ....
     *
     *  // 所以我认为 指定上下文 只是让它 在指定的线程上启动,后续无关 指定的线程 ...
     *
     * 这个测试我们使用 -Dkotlinx.coroutines.debug 查看运行代码的协程名称 ...
     */
    @Test
    fun withContextTests() {
        var logger = LoggerFactory.getLogger(CoroutineAndChannel_2Tests::class.java)
        runBlocking(Dispatchers.Default) {
            repeat(3) {
                launch {
                    logger.info("开始启动一个具有withContext的协程$it 计算...")
                    delay(100)
                    logger.info("开始启动一个具有withContext的协程$it 计算...")
                    delay(100)
                    logger.info("开始启动一个具有withContext的协程$it 计算...")
                    delay(100)
                    logger.info("开始启动一个具有withContext的协程$it 计算...")
                    delay(100)
                    logger.info("开始启动一个具有withContext的协程$it 计算...")
                    delay(100)
                    logger.info("开始启动一个具有withContext的协程$it 计算...")
                    delay(100)
                    logger.info("开始启动一个具有withContext的协程$it 计算...")
                    delay(100)
                    logger.info("开始启动一个具有withContext的协程$it 计算...")
                    delay(100)

                }
            }
            withContext(Dispatchers.IO) {
                logger.info("开始启动一个具有withContext的协程 withContext 计算..., ${this}")
                delay(100)
                logger.info("开始启动一个具有withContext的协程withContext 计算...")
                delay(100)
                logger.info("开始启动一个具有withContext的协程withContext 计算...")
                delay(100)
                logger.info("开始启动一个具有withContext的协程withContext 计算...")
                delay(100)
                logger.info("开始启动一个具有withContext的协程withContext 计算...")
                delay(100)
                logger.info("开始启动一个具有withContext的协程withContext 计算...")
                delay(100)
                logger.info("开始启动一个具有withContext的协程withContext 计算...")
                delay(100)
                logger.info("开始启动一个具有withContext的协程withContext 计算...")
                delay(100)
            }

            logger.info("会阻塞当前协程,这个代码应该会等待上面的withContext执行完毕 ...")
            logger.info("done")
        }
    }


}
