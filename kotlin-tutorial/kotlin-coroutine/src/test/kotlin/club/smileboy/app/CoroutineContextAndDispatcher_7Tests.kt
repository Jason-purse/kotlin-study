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
 *   - 派发器和线程
 *   - Unconfined vs confined dispatcher
 *   - 线程之间切换(协程上下文)
 *   - 上下文中的Job
 **/
class CoroutineContextAndDispatcher_7Tests {
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

}