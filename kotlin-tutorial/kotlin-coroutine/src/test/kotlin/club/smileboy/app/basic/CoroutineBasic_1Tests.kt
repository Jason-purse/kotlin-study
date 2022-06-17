package club.smileboy.app.basic

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

/**
 * @author FLJ
 * @date 2022/6/16
 * @time 10:35
 * @Description 协程基础
 *
 *
 * 协程是kotlin 对于异步编程的一个抽象,它通过编写命令式逻辑来实现异步操作,而没有future / promise  / callback 另类式编程式代码 ...
 * 并且与 promise 不同,它并没有引入 async / await 这样的关键字,而是有一套自己专属的底层api 抽象 ..
 * 并且通过可暂停函数提供更加安全且很少出错的抽象执行异步操作 ...
 * 其中 kotlinx.coroutines 是 jetbrains 对协程抽象的核心包 ...
 *
 * 使用协程时,导入核心包 kotlinx-coroutines-core module
 *
 *
 * 本章测试包含几个内容:
 *  - 结构化并发 - 36行
 *  - 暂停函数
 *  - Scope builder
 *  - Scope builder 和并发
 *  - 显式的Job
 *  - 协程是非常轻量级的 .
 *
 *
 *
 */
class CoroutineBasic_1Tests {

    // --------------------------------------- 结构化并发  和 暂停函数 -------------------------------

    /**
     * 首先学习第一个协程
     *
     * 通过协程构建器 创建一个结构化协程 ..
     * 每一个协程都包含了一个所属的协程范围
     * 所有的协程都必须存活在协程作用域中 ...
     *
     * 通过 launch 函数同样也是一个协程构建器(只不过是异步协程构建器,不会阻塞运行当前协程的线程 ,而runBlocking会 ...)  开启一个新的子协程 ..
     *
     * delay 是一个暂停函数 ...   暂停函数的特点就是 它可以暂停当前协程,不会影响运行当前协程的线程,当暂停当前线程之后 它允许当前线程的其他协程进行运行 ...
     * 于是 暂停函数还有一个特点,顺序性执行 ... 我们查看secondCoroutineTest
     *
     * 其次 launch 这种非同步协程构建器只能够在coroutineScope中进行使用 ...
     *
     * 真实代码中 runBlocking 运用的比较少,因为阻塞线程是不明智的 ....
     */
    @Test
    fun firstCoroutineTest() {
        // 这是一个协程构建器 ...
        runBlocking {
            // launch a new coroutine and continue
            launch {
                delay(3000L) // non-blocking delay for 1 second (default time unit is ms)
                println("World!") // print after delay
            }
            println("Hello") // main coroutine continues while a previous one is delayed
        }
    }

    /**
     * 在这个测试中 我们可以发现,同一个协程中的暂停函数会依次执行 ...
     * 其次,暂停函数只能在coroutineScope中执行 ...
     * 也就是能够在coroutineScope的 上下文中进行执行 ...
     * 同样 暂停函数中也能够使用暂停函数,本质上它和普通函数没有什么区别,但是能够执行一些暂停协程的操作 ....
     * 也就是它只能够结合子协程使用 ....
     *
     * 于是结合这个特点,我们可以抓取函数进行重构 ...
     */
    @Test
    fun secondCoroutineTest() = runBlocking {
        launch {
            suspend {
                println("你好")
            }.invoke()
            suspend {
                println("再说一次你好 !!!")
            }.invoke()
        }
        println("主协程会先执行 ...")
    }

    private suspend fun refactorOne(action: () -> Unit) {
        delay(1000L)
        action.invoke()
    }

    /**
     * 我们可以看到 第一个抽象的暂停函数 完美的运行了 ...
     * 这个暂停函数  暂停了协程 1秒中,然后执行回调 打印 world!!!
     */
    @Test
    fun firstSuspendFunctionTest(): Unit = runBlocking {
        launch {
            refactorOne {
                println("World!")
            }
        }
    }


//    ----------------------------------------------- scope builder ----------------------------------

    /**
     * 一般情况下,我们都是通过不同的构建器提供的协程 scope 进行工作,但是我们能够通过 coroutineScope 构建器创建自己的 协程 scope ..
     * 我们需要明确 coroutineScope 的特点就是 结构化并发 .. 会等待所有的子协程结束 ..
     *
     * runBlocking 和 coroutineScope 相似,相似点是都会 一个等待body 而另一个等待它的所有子协程完成 ...
     * 主要不同是 runBlocking 会阻塞当前线程,而coroutineScope 仅仅暂停协程,它将释放底层的线程 - 而不会阻塞线程 ...
     * 由于这个不同 runBlocking 是一个普通函数,而 coroutineScope 是一个暂停函数 ...
     * 结合这个特点,我们也能够对应前面结合suspend 函数所描述的特点 ...
     * 同样也明白了如何区分 普通函数和协程函数 ..
     * 暂停协程和阻塞线程的概念区分 ....
     *
     * 既然如此,coroutineScope 构建器依旧只能运行在协程中 . 准确的说是运行在 coroutineScope上下文中 ...
     *
     * 那么 根据暂停函数的定义,那么暂停函数能够被其他暂停函数调用,然后我们查看 runBlocking 以及 coroutineScope 的 action 闭包,发现都是 suspend 函数 ...
     * 特别的是 它们都声明了函数的代理对象 ... (也就是接收者对象) ....
     *
     *
     * 当我第一次打印这个结果的时候,我有点懵逼,因为我以为"协程 scope 构建结束"会快速打印
     * 如果你也这样想的话,说明你还没有熟悉 暂停函数的定义 ...
     * 你还没有明确coroutineScope 到底是什么函数 ...
     *
     * 很显然它是一个暂停函数,那么 在内层的coroutineScope 没有执行完毕之前,下面的逻辑就不会执行 ...
     *
     * 那么结合这个 特点我们也能在协程中完美控制执行流程 ...
     */
    @Test
    fun firstCoroutineScopeBuilderTest()  = runBlocking {
        println("开始构建一个协程 scope")
        coroutineScope {
            suspend {
                println("coroutine scope 中的这个协程 会等待 2000 打印 ...")
                delay(2000)
            }()

            // 如果这个子协程放在suspend 代码块之前,你还认为它在两秒钟之内会打印吗???? 给出你的答案 ...
            // 下一个例子给出这个答案 ..
            launch {
                println("命令式编程,导致我在suspend函数之后执行,这就是特点 !!!!")
            }
        }
        println("协程scope 构建结束 ...")
        println("假设当前主线程释放 ....")
        Unit
    }

    /**
     * 如果结构化并发在暂停函数之前启动了,那么 就是一个新的子协程,新的子协程不会被父协程暂停受影响 ..
     * 但是我们知道  子协程会影响父协程 ...
     * 或者更明确的说 子协程 会影响它所在的coroutineScope 以及它的父 coroutineScope ...
     */
    @Test
    fun secondCoroutineScopeBuilderTest() = runBlocking {
        println("开始构建一个协程 scope")
        coroutineScope {
            launch {
                // 尝试演示 500 毫秒,来保证suspend函数肯定执行了 ...
                delay(1000)
                println("命令式编程,导致我在suspend函数之后执行,这就是特点 !!!!")
            }
            suspend {
                delay(2000)
                println("coroutine scope 中的这个协程 会等待 2000 打印 ...")
            }()
        }
        println("协程scope 构建结束 ...")
        println("假设当前主线程释放 ....")
        Unit
    }

    /**
     * 在来看一个例子,结合 coroutineScope 实现并发 ...
     *
     * 同样我们可以加入Delay 暂停函数来测试它到底是不是无序性并发 ..
     *
     * 我们尝试并发打印两句话,然后当这两个协程结束之后,我们再结束程序 ...
     */
    @Test
    fun coroutineScopeBuilderAndConcurrency()  = runBlocking {
        coroutineScope {
            launch {
                println("1")
            }

            launch {
                println("2")
            }
        }

        println("Done")
    }

    // ------------------------------------------------------- job -------------------------------------------------
    /**
     * 根据上面coroutineScope 构建器会等待它的所有子协程 完成,那么 我们来看看是否拥有等价物 ...
     * 答案就是一个显式的job 等待调用
     *
     * 首先 launch 协程构建器 会返回一个Job 用来处理协程 .. 它能够显式的调用来让当前线程或者协程阻塞等待它完成 ..
     *
     * 理论上来说,我们应该可以将 Job 暴露到线程上下文的代码逻辑中 ... 但是 实际上可能是不行的,基于实现 如果真这样做,可能会报错,
     * 所以Job 应该是只能阻塞协程 ..
     */
    @Test
    fun firstJobWait() = runBlocking {
        val job = launch { // launch a new coroutine and keep a reference to its Job
            delay(1000L)
            println("World!")
        }
        println("Hello")
        job.join() // wait until child coroutine completes
        println("Done")
    }

    // ----------------------------------- 协程是轻量级的 -----------------------------------
    /**
     * 例如开 10万个协程 也消费1点点内存,but 如果你开2千个线程可能就会内存溢出 ..
     */
    @Test
    fun tenThousandCoroutine() = runBlocking {
        repeat(100000) {
            launch {
                println(it)
            }
        }
    }


    // ---------------------------------- 暂停函数内部是命令式还是 协程并发的  ... ------------------------
    /**
     * 查看这段代码,可以发现 同一协程的suspend 函数虽然是顺序性执行,但是它们本身也是普通函数,只是具有了暂停协程的性质, 于是它们也会受异步协程的影响 .
     * // 不要指望 它们它会等待它们内部的协程, 仅仅只有 coroutineScope 构建器才会等待 ...
     */
    @Test
    fun suspendFunctionWithCoroutineTest() = runBlocking {
        var value = suspend {
            launch {
                delay(3000)
                println("你好")
            }

            123
        }()
        println("suspend return value is $value ...");
    }

}