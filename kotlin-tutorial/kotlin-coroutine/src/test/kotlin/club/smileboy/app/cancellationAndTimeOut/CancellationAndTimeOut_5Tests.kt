package club.smileboy.app.cancellationAndTimeOut

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test

/**
 * @author FLJ
 * @date 2022/6/16
 * @time 17:04
 * @Description 协程的取消和超时 ...
 *
 * contents
 *  - 取消 -- 24行
 *  - 超时
 *
 * 协程的取消非常的简单,只需要调用协程 job.cancel()即可 ..
 *
 * 协程也可以合作性取消,在Kotlinx.coroutines中的暂停函数都是可取消的 ..
 * what ? 暂停函数可以取消 ???
 * 当调用的时候 它们会检测协程的取消,并且取消时抛出一个异常  CancellationException ...
 * 但是如果运行在一个密集计算型任务中,那么无法被取消 ...
 */
class CancellationAndTimeOut_5Tests {
    // ------------------------------------ 取消 ---------------------------------------

    /**
     * 在前面的章节中我们可以知道, 构建器的lambda 都是suspend 函数,kotlin 任务它的所有暂停函数都是可以取消的 ...
     * 但是密集型计算任务无法取消 ..
     */
    @Test
    fun computationByCpuConsume() = runBlocking {

        val startTime = System.currentTimeMillis()
        val job = launch(Dispatchers.Default) {
            var nextPrintTime = startTime
            var i = 0
            while (i < 5) { // computation loop, just wastes CPU
                // print a message twice a second
                if (System.currentTimeMillis() >= nextPrintTime) {
                    println("job: I'm sleeping ${i++} ...")
                    nextPrintTime += 500L
                }
            }
        }
        delay(1300L) // delay a bit
        println("main: I'm tired of waiting!")
        job.cancelAndJoin() // cancels the job and waits for its completion
        println("main: Now I can quit.")
    }

    /**
     * 尝试job 取消
     *
     * 这是一种错误示例 ... 因为无法捕捉到 CancellationException 异常(这个异常被认为是协程正常完成的情况) ...
     * 我们仅仅能够捕捉 暂停函数检测所爆发的错误 ... 如果你的协程中不使用暂停函数,那么就称为密集型任务运算 ...
     * 当前此示例,使用了暂停函数,它会检测协程的取消...
     */
    @Test
    fun jobCancel() {

        runBlocking {
            var job: Job? = null;
            try {
                val job1 = launch {
                    var i = 0
                    repeat(5) {
                        // print a message twice a second
                        println("job: I'm sleeping ${i++} ...")
                        delay(500)
                    }
                }
                job = job1;
            } catch (e: Exception) {
                println("root catch exception: ${e.message}")
            }

            delay(1300L) // delay a bit
            println("main: I'm tired of waiting!")
            job?.let {
                it.cancelAndJoin() // cancels the job and waits for its completion
            }
            println("main: Now I can quit.")
        }

    }

    /**
     * 让计算能够取消 ...
     * 捕捉异常是一种针对的模式,但是可能会出现一些疑问 ...
     * 例如 runCatching 函数运行计算时,它不会重新抛出 CancellationException
     *
     * 请注意,这些方法包括异常捕获,都应该紧贴 暂停函数 .. .
     * 否则它无法捕捉异常,并导致结构化并发自动协作取消 ...
     */
    @Test
    fun takeComputationCancel() {
        runBlocking {
            val job = launch {
                // 它会捕捉
                val result = kotlin.runCatching {
                    repeat(5) {
                        println("i'm sleeping $it!!")
                        delay(500)
                    }
                }

                println("捕捉结果,如果有异常,则不为空; 否则它有对应的状态(Success / Failure)  result: $result")
            }

//            delay(1300L) // delay a bit
            delay(3300L) // delay a bit
            println("main: I'm tired of waiting!")
            job?.let {
                it.cancelAndJoin() // cancels the job and waits for its completion
            }
            println("main: Now I can quit.")
        }

    }

    /**
     * 对于开发者自己,我们可以自己定义检测协程的状态 ... 来实现计算可取消...
     * 非常简单
     */
    @Test
    fun checkCoroutineStatus() {
        runBlocking {
            launch {
                // 它是一个协程 scope 的扩展属性...
                while (isActive) {
                    // 逻辑 ...
                }
            }

            // cancelAndjoin job ....
        }
    }


    /**
     * 同样 通过finally 关闭资源 ...
     * 但是关闭资源也需要小心
     * 例如:
     *
     * 在finally中使用暂停函数将会爆发异常   CancellationException,因为它要求协程运行这段代码是可取消的..
     *
     * 通常这不是一个问题,关闭文件 / 管道都是非阻塞的 ..且不会涉及到任何暂停函数 ...
     *
     * 但是有些时候你真的需要 ...这样的操作
     * 使用withContext 函数指定上下文进行运行:
     *  withContext(NonCancellable) {...}
     *
     *  请看下一个例子
     */
    @Test
    fun tryFinallyHandle() = runBlocking {
        val job = launch {
            try {
                repeat(1000) { i ->
                    println("job: I'm sleeping $i ...")
                    delay(500L)
                }
            } finally {
                println("job: I'm running finally")
            }
        }
        delay(1300L) // delay a bit
        println("main: I'm tired of waiting!")
        job.cancelAndJoin() // cancels the job and waits for its completion
        println("main: Now I can quit.")
    }

    /**
     *  这允许在finally 中执行不可取消的代码 ....
     */
    @Test
    fun useWithContextNoCancellable() = runBlocking {
        val job = launch {
            try {
                repeat(1000) { i ->
                    println("job: I'm sleeping $i ...")
                    delay(500L)
                }
            } finally {
                withContext(NonCancellable) {
                    println("job: I'm running finally")
                    delay(1000L)
                    println("job: And I've just delayed for 1 sec because I'm non-cancellable")
                }
            }
        }
        delay(1300L) // delay a bit
        println("main: I'm tired of waiting!")
        job.cancelAndJoin() // cancels the job and waits for its completion
        println("main: Now I can quit.")
    }


    // --------------------------------------------------------- timeout -----------------
    /**
     * 通过简单的withTimeout ... 就可以简单的实现超时取消协程 ...
     * 这个函数也要特别注意:
     *   小心它带来的内存泄露 ...问题
     *
     *   注意在没有使用这个函数之前,我们没有看到这个错误,因为这个异常  CancellationException 在一个可取消的协程中算作 协程完成的正常原因 ..
     *   但是我们在这里使用了这个函数将抛出异常 ...
     *
     *   你可以在它之上使用try-catch 来捕获异常处理首尾动作,
     *   但是你也可以使用withTimeoutOrNull 来获取结果 ...(返回null 代表出现了异常,替代异常) ...
     */
    @Test
    fun timeoutHandler() = runBlocking {
        // 它会抛出一个异常  TimeoutCancellationException 是它的子类 CancellationException
        try {
            withTimeout(1300L) {
                repeat(1000) { i ->
                    println("I'm sleeping $i ...")
                    delay(500L)
                }
            }
        }catch (e: Exception) {
            //
        }

        // 注意如果你在这个具有超时动作的块中执行获取资源的动作,可能会导致资源泄露 ...
        // 但是有可能超时没有返回成功,那么资源可能没有成功关闭 ...
        // 查看 下面的示例
        withTimeoutOrNull(1300L) {
            // 例如这里打开了资源 ...
            delay(2000)
            // 并在这里返回 ....
            12312
        }
    }
// ------------------------------------------- Asynchronous timeout and resources --------------------------
    /**
     * 异步超时和资源
     * The timeout event in withTimeout is asynchronous with respect to the code running in its block and may happen at any time,
     * even right before the return from inside of the timeout block.
     * Keep this in mind if you open or acquire some resource inside the block that needs closing or release outside of the block.
     *
     * 下面的例子中我们尝试模拟资源创建,并计数 ...
     * 打印出获取的资源,一旦有一个出现了问题,那么整体的结构化并发将会结束 ...
     * 并且以下这段代码是不可信任的,withTimeout是异步计算代码的执行时间的 ,有可能在任意时间返回,所以不能够信任 withTimeout ....
     *
     * 假设创建成功,但是没有成功返回,但是此时资源无法关闭,因为抛出了异常 ....
     *
     * 所以我们需要真实的拿到资源并正确关闭 ...
     *
     * 查看下面 ...例子 ...
     */
    @Test
    fun asyncTimeoutAndResource() {
        var acquired = 0

        class Resource {
            init { acquired++ } // Acquire the resource
            fun close() { acquired-- } // Release the resource
        }

        runBlocking {
            repeat(100_000) { // Launch 100K coroutines
                launch {
                    val resource = withTimeout(600) { // Timeout of 60 ms
                        delay(599) // Delay for 50 ms
                        Resource() // Acquire a resource and return it from withTimeout block
                    }
                    resource.close() // Release the resource
                }
            }
        }
        // Outside of runBlocking all coroutines have completed
        println(acquired) // Print the number of resources still acquired

    }

    /**
     * 正确关闭异步超时和资源的问题 ..
     *
     * 下面的示例是使用withTimeoutOrNull,但是你也能够使用withTimeout 结合try -catch 即可 ... 或者 try-finally ...
     * 这样资源将不会被泄露 ....
     */
    @Test
    fun asyncTimeoutAndResourceByCorrect() {
        var acquired = 0

        class Resource {
            init { acquired++ } // Acquire the resource
            fun close() { acquired-- } // Release the resource
        }

        runBlocking {
            repeat(100_000) { // Launch 100K coroutines
                launch {
                    var resource: Resource? = null;
                    withTimeoutOrNull(60) { // Timeout of 60 ms
                        delay(50) // Delay for 50 ms
                        // 当它创建完毕,立即赋值给外边的引用 ...
                        resource = Resource() // Acquire a resource and return it from withTimeout block
                    }
                    resource?.close() // Release the resource
                }
            }
        }
        // Outside of runBlocking all coroutines have completed
        println(acquired) // Print the number of resources still acquired

    }
}