package club.smileboy.app

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test

/**
 * @author JASONJ
 * @date 2022/6/16
 * @time 23:04
 * @description 协程上下文和派发器...
 *
 *  协程总是执行在由CoroutineContext类型所表示的一些上下文中 ..
 *  协程上下文主要包含协程的Job 以及 派发器 ...
 *
 *
 *  contents
 *   - 派发器和线程 -- 25行
 *   - Unconfined vs confined dispatcher -- 56行
 *   - 线程之间切换(协程上下文) -- 92行
 *   - 上下文中的Job -- 118行
 *   - 协程的孩子 ... 子协程 -- 133行
 *   - 父亲的责任 .. -- 197行
 *   - 协程命名 -- 222行
 *   - 协程scope -- 241行
 *   - thread local data -- 293行 ..
 *
 **/
class CoroutineContextAndDispatcher_7Tests {

    // --------------------------------------- 派发器 / 线程  -------------------------------------------
    /**
     * 协程上下文包含了一个派发器 用来指定协程应该执行在那个线程或者那些线程上...
     * 可以配置 也可以 unconfiged ...
     * 不限制协程应该执行在那些线程或者那个线程上调度 ..
     *
     * 通过launch 来显式的指定派发器 ...
     *
     * 默认如果不设置,那么继承父协程作用域的上下文 ....
     */
    @Test
    fun dispatcherAndThread() {
        runBlocking {
            launch { // context of the parent, main runBlocking coroutine
                println("main runBlocking      : I'm working in thread ${Thread.currentThread().name}")
            }
            // 比较特殊 ...
            launch(Dispatchers.Unconfined) { // not confined -- will work with main thread
                println("Unconfined            : I'm working in thread ${Thread.currentThread().name}")
            }
            // 共享一个资源池 ...
            launch(Dispatchers.Default) { // will get dispatched to DefaultDispatcher
                println("Default               : I'm working in thread ${Thread.currentThread().name}")
            }
            // 线程资源昂贵,如果不使用应该被释放(close函数关闭,或者应该作为全局变量(kotlin 认为顶级变量也可以 ...),通过应用重用) ...
            launch(newSingleThreadContext("MyOwnThread")) { // will get its own new thread
                println("newSingleThreadContext: I'm working in thread ${Thread.currentThread().name}")
            }
        }
    }

    // ----------------------------- unconfiged vs config dispatcher
    /**
     * unconfiged 首先运行在调用者 线程中启动协程 .. 但是 当第一次暂停之后,就随机恢复 ..
     * 它主要适用于不对运行环境有条件的情况(且不会产生副作用的情况下,使用特别好) ..
     * 例如非cpu 消耗 / UI线程的情况下 ... 指定一个上下文 ....
     *
     * 默认来说,由于没有显式配置dispatcher 那么默认继承外层协程作用域的上下文的派发器,那么它们的行为因父协程scope 的上下文而异 ..
     * 例如 runBlocking 中开启子协程,那么 所有的子协程 .. 应该都是使用这个上下文(因为runBlocking 默认使用指定上下文) ..
     *
     * kotlin 提示: unconfiged 的使用场景非常少 ...前面已经提到 .. 对运行环境没有要求且不会产生 副作用 ..
     * The unconfined dispatcher is an advanced mechanism that can be helpful in certain corner cases
     * where dispatching of a coroutine for its execution later is not needed or produces undesirable side-effects,
     * because some operation in a coroutine must be performed right away. The unconfined dispatcher should not be used in general code.
     */
    @Test
    fun unConfigedVsDispatcher() {
        runBlocking {
            launch(Dispatchers.Unconfined) { // not confined -- will work with main thread
                println("Unconfined      : I'm working in thread ${Thread.currentThread().name}")
                delay(500)
                println("Unconfined      : After delay in thread ${Thread.currentThread().name}")
            }
            repeat(3) {
                launch { // context of the parent, main runBlocking coroutine
                    println("main runBlocking: I'm working in thread ${Thread.currentThread().name},当前协程 $it")
                    delay(1000)
                    println("main runBlocking: I'm working in thread ${Thread.currentThread().name},当前协程 $it")
                    delay(1000)
                    println("main runBlocking: I'm working in thread ${Thread.currentThread().name},当前协程 $it")
                    delay(1000)
                    println("main runBlocking: After delay in thread ${Thread.currentThread().name},当前协程 $it")
                }
            }
        }
    }

    // ------------------------------------ 协程上下文切换
    @OptIn(DelicateCoroutinesApi::class)
    @Test
    fun coroutineContextCheck()  = runBlocking {
        // 创建一个上下文并使用 ...
        // 虽然能够通过 use 来使用 同时在不需要的时候进行关闭 ...但是我们同样可以用 作用域函数 ..
        newSingleThreadContext("thread1").use { one ->
            newSingleThreadContext("thread2").use {  two ->
                runBlocking {
                    println("原始上下文 ${two}")
                    withContext(one) {
                       println("当前上下文 ${one}")
                   }
                    println("back to ${two}")
                }
                two.close()
            }
            one.close()
        }

        // 同样可以关闭..
        newSingleThreadContext("thead3").let {
            it.close()
        }
    }

    // ------------------------------- 上下文中的Job -----------------------
    /**
     * 上下文中的内容,需要通过CoroutineScope.Key 进行抓取 ..
     * 所以我们现在可以知道 coroutineScope的isActive 属性 是便利的快捷方式 等价于
     *  coroutineContext[Job]?.isActive == true.
     */
    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun JobfromContext() = runBlocking {
        println("job of coroutine context is ${coroutineContext[Job]}")
        println("dispatcher of coroutine context is ${coroutineContext[CoroutineDispatcher]}")
    }


    // ------------------------------- 协程的孩子 / 子协程 ----------------------\
    /**
     * 当一个协程从另一个协程的协程scope中唤起的时候,它会继承父协程的协程上下文(coroutineContext) ...
     * 然后子协程的job 是 父协程的孩子 ... 当父协程取消时,子协程同样会被(递归的)取消 ...
     *
     *
     * 那么如何改变一个子协程和父协程的关系呢 ? (例如不继承父协程的协程上下文) ..
     *  - GlobalScope.async ....等方法 ..
     *  - 显示指定协程上下文 .... 然后它会覆盖协程上下文 (也就是改变默认行为) ....
     */
    @Test
    fun coroutineAndChildCoroutine() {

        runBlocking {
            GlobalScope.launch {
                println("它没有继承runBlocking的协程上下文 ....")
            }
            // 显式的指定协程上下文 ...
            newSingleThreadContext("thread1").use { thread ->
                launch() {

                }.invokeOnCompletion {
                    thread.close()
                }
            }

            // 例如指定它继承一个Job 的 协程上下文
            val job = Job()
            launch(job) {
                println("当前子协程继承 job的协程上下文 ....")
            }

        }
    }

    /**
     * 一个结构化协程取消的用例,你可以猜想一下那些协程会被取消 ..
     */
    @Test
    fun structCoroutineCancelOrOtherCoroutineNoCancellable() {
        runBlocking {
            // launch a coroutine to process some kind of incoming request
            val request = launch {
                // it spawns two other jobs
                launch(Job()) {
                    println("job1: I run in my own Job and execute independently!")
                    delay(1000)
                    println("job1: I am not affected by cancellation of the request")
                }
                // and the other inherits the parent context
                launch {
                    delay(100)
                    println("job2: I am a child of the request coroutine")
                    delay(1000)
                    println("job2: I will not execute this line if my parent request is cancelled")
                }
            }
            delay(500)
            request.cancel() // cancel processing of the request
            println("main: Who has survived request cancellation?")
            delay(1000) // delay the main thread for a second to see what happens
        }
    }

    // ----------------------------------------- 父亲的责任 ..------------------------------------------
    /**
     * 父协程会等待所有的子协程 ...
     * 一般来说也不会显式的通过job.join 去等待子协程结束 ...
     */
    @Test
    fun parentCoroutineResponsibilities() {
// launch a coroutine to process some kind of incoming request
        runBlocking {
            val request = launch {
                repeat(3) { i -> // launch a few children jobs
                    launch  {
                        delay((i + 1) * 200L) // variable delay 200ms, 400ms, 600ms
                        println("Coroutine $i is done")
                    }
                }
                println("request: I'm done and I don't explicitly join my children that are still active")
            }
            request.join() // wait for completion of the request, including all its children
            println("Now processing of the request is complete")
        }
    }

    // ------------------------------------------- 协程命名 ------------------------------------------
    /**
     * 协程命名的作用是用来调试..
     *
     * 但是，当协程与特定请求的处理或执行某些特定的后台任务相关联时， 设置一个好的协程用来 调试是一件好事情 ..
     * 我们可以通过 设置协程上下文 来达到这个效果
     *
     * 结合kotlinx.coroutines.debug jvm flag结合框架日志实现调试 ...
     */
    @Test
    fun coroutineNamed() = runBlocking {
        launch(CoroutineName("daemon-coroutine")) {
            println("我的协程名字是 ${coroutineContext[CoroutineName]}")
            println("后台协程,测试 ....")
        }
        Unit
    }

    // ----------------------------------------------- 协程的 scope -------------------------------
    /**
     * 协程scope 到底来干什么?  如何组织 协程context / job 等其他信息的内容 ..
     * kotlin 说,任何对象都有生命周期,唯有在生命周期内才能够让对象活动,那么 协程scope 是用来约束 协程的生命周期的 ...
     * 那么协程应该在指定的协程scope中执行 ...
     *
     * 例如在andorid中 在一个activities 中 需要在后台抓取/ 更新页面 / 执行动画,这些我们假设是协程,那么当活动摧毁的时候,我们应该取消掉这些协程 ..
     * 那么问题是,我们如何取消它们呢? 当然 job.cancel确实能够取消 .. 但是我们应该使用结构化并发的形式进行整体协程取消 ....
     *
     * 那么我们也不可能将所有的协程都加入队列中进行 最终处理 （因为这很蠢) ..
     * 但是你也不能将结构化并发的顶级协程引用起来 ... (因为这个协程也有死亡的时候) ..
     * 因为一个协程scope 可以包含不止一个协程 ..
     * 所有我们应该通过协程scope 进行生命周期约束 ..
     *  当活动取消时,我们仅仅摧毁协程scope 就可以关闭所有的协程,释放所有的资源 ..
     *  (其实父协程等待子协程,也建立在协程scope 生命周期内) ..
     *  例如前面我们了解到的协程scope CoroutineScope 创建的协程scope 构建器 会等待内部的所有协程完成 ...
     *  当它(coroutineScope)生命周期结束,一切都被摧毁了 ...这能解释的通 ...
     *
     *  所以对于协程的资源管理,在普通应用中,通过CoroutineScope 创建一个 协程范围 进行协程约束 ..
     *  在UI 应用中,通过MainScope 创建一个协程scope,进行协程约束 ...
     *
     *  并且在Android中 提供了对协程的一等支持 ...
     *
     *
     *  所以回到最后,launch / async 并不是只能使用在协程中,而是它是协程scope的方法 ..
     *  而且这里说的这两个方法是针对于CoroutineScope的方法 ...
     *
     *  于是开启一个结构化并发 太简单了 ...
     *  协程内 我们通过coroutineScope 构建器构建协程scope ..
     *  在普通函数中,或者顶级变量中我们可以通过 CoroutineScope并设置协程上下文开启一个协程scope进行协程世界进入 ....
     *
     *  并且需要注意 CoroutineScope() 对象你就可以看作一个普通对象,说多了,简单的说,编写协程代码时你需要注意那些会导致异步,那些会导致同步依次执行 ..
     *  就如下面的代码,我第一次以为他会阻塞(真是太傻了) ...
     *  new 一个对象不就是一个简单的代码嘛 ... 哈哈哈哈哈 .. 并且在普通世界,它不可能阻塞等待协程,可能kotlin 规范上也不允许这样做(我的意思并不是代码层面我们不可以这样做)
     *  而是 协程应该只被协程scope 控制,真正桥接到 协程世界应该使用runBlocking ...
     *
     *  所以我们需要多写多练,了解同步 / 异步特性 ...
     */
    @Test
    fun coroutineScopeUnderstand() {

        CoroutineScope(Dispatchers.Default).apply {
            launch {
                println("你好")
            }
        }

        println("不会被阻塞 .. ...")
    }


    // ----------------------------------------------------- thread local data -------------------------------------------------------
    /**
     * 线程中的共享数据 在协程中传递非常简单(共享)
     * 如果手动写 它就是一块模板(boilerplate)代码 ..
     *
     * 所以对于ThreadLocal,有一个扩展函数能够做这样的事情 ... asContextElement
     * 并且当协程 每次恢复在指定协程上下文中调度时,恢复track的值 ...
     *
     * 所以我们将利用kotlin 友好的operator 特性 实现协程上下文的内容扩展
     *
     * 同样,不管协程运行在那个线程之上,它们协程上文中指定的asContextElement是多少,永远是多少 ...
     *
     *
     * ThreadLocal对协程原语提供了一等支持 ...
     *
     * 其次我们非常有可能在使用threadLocal时忘记设置 上下文元素 ...
     * 我们可以结合withContext 与 ensurePresent函数进行 threadlocal 是否在这个上下文的检查 ..
     * 如果没有 快速失败,取消不正确的使用 ...
     * 请看下一个示例 ...
     *
     */
    @Test
    fun theradLocalData() {
      val threadLocal =   object : ThreadLocal<String>() {
            override fun initialValue(): String {
                return "UNKNOWN";
            }
        }
      threadLocal.set("main")
      runBlocking {
          println("Pre-main, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")
         coroutineScope {
             repeat(5) {
                 val job = launch(Dispatchers.Default + threadLocal.asContextElement(value = "launch")) {
                     println("Launch start $it, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")
                     yield()
                     println("After yield $it, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")
                     yield()
                     println("After yield $it, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")
                     yield()
                     println("After yield $it, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")
                     yield()
                     println("After yield $it, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")
                 }
             }
         }
          println("Post-main, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")
      }
    }
    suspend inline fun <T> ThreadLocal<T>.getSafely(): T {
        ensurePresent()
        return get()
    }

    /**
     * 使用ensurePresent 抛出异常 ...当不存在threadLocal时...
     */
    @Test
    fun withContextWithEnsurePresent() = runBlocking {

         val threadLocal = object: ThreadLocal<String>() {
             override fun initialValue(): String {
                 return "unknown"
             }
         }

        launch {
            // 使用父协程的scope ... 这样会立即失败 ...
            kotlin.runCatching {
                withContext(coroutineContext) {
                    val value = threadLocal.getSafely() // Fail-fast in case of improper context
                }
            }.exceptionOrNull()?.let {
                println("异常 .... ${it.message}")
            }
        }

        Unit
    }

}