package club.smileboy.app.basic

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

/**
 * @author FLJ
 * @date 2022/6/16
 * @time 15:06
 * @Description 结构化并发
 *
 * # contents
 * - coroutine scope 的含义
 * - 如何正确结构化并发 ...
 * - Using the outer scope's context
 *
 *
 *
 * 首先,协程 scope 负责不同协程之间的结构以及父子关系 ... 我们总是在一个scope中开启一个新的协程 ...
 * 协程上下文存储了额外的技术信息(被用来运行给定的协程,例如 修改协程的名称 或者指定派发器 为线程 -来支持此协程的调度) ...
 *
 * 例如我们使用协程构建器开启协程的时候, launch  / async / runBlocking ,它们会自动的创建相关的scope, 这些函数(指的是协程构建器)都接收一个参数,称为接收器,隐式的接收器类型是 CoroutineScope ...
 * 新的协程仅仅只能够从scope中开始, launch / async 都是声明为 CoroutineScope的扩展 ... 因此当调用它们的时候都会传递一个接收者(这个接收者是对应的coroutine scope) ...
 * 通过runBlocking 开启的协程是一个意外,runBlocking 是一个顶级函数,它会阻塞当前线程并且主要使用在main函数中 以及作为测试的桥接函数 ....
 * 当在runBlocking / launch / async内部开启新的协程时, 它们(这些子协程)在scope内将自动启动(这个scope 是父协程的scope) ...
 *
 */
class StructuredConcurrencyTests {
    /**
     * 能够看见 此构建器包含一个lambda 且接收器类型为 CoroutineScope ...
     */
    @Test
    fun createCoroutine() {
        runBlocking {

        }
    }

    /**
     * 在runBlocking中的launch 都可以称为 runBLocking协程的子协程,它们的关系通过父 协程的 相关的scope 进行记录 ..
     * 它们从父协程的coroutine scope 开始 ...
     *
     * 同样我们前面学到,我们可以自己创建一个新的协程scope ... coroutineScope 暂停函数即可做到这个 ...
     * 当我们需要在暂停函数中以结构化的方式开始新的协程 而不访问外部scope时,我们能够创建一个新的coroutine scope ,然后它将自动作为调用此暂停函数的外部scope的孩子 ...
     *
     * 于是提供这种协程的结构的机制叫做结构化并发 ... 让我们看一看结构化并发有哪些好处,相比于全局范围 ...
     *  结构化协程 scope 对所有的子协程负责,它们的生命周期依附于scope的生命周期 ...
     *  这个scope 能够自动取消子协程(如果某些事情出现了错误,或者用户简单的改变了它们的想法并执行了一个重新执行的操作) ...
     *  这个scope 会自动的等待所有的子协程完成,因此如果这个 scope 关联到一个协程,那么父协程不会完成直到从这个父协程相关的scope 启动的所有协程完成之后才会完成 ....
     *
     *  对比 GlobalScope.async 它们没有结构化并发的特性, 没有结构能够将各个协程绑定到更小的scope中 ..
     *  从global scope中开始的所有协程都是相互独立的, 它们的生命周期仅仅被整个应用限制 ...
     *
     *  我们可以尝试存储一个来自global scope的协程引用 并且等待它完成或者显式的取消 ... ，但它不会像结构化的那样自动发生。
     */
    @Test
    fun parentAndChild() {
        runBlocking { /* this: CoroutineScope */
            launch { /* ... */ }
            // the same as:
            this.launch { /* ... */ }
        }
    }


    // --------------------------------------------------------------- 使用外部scope的上下文 ---------------------------------------
    /**
     * 当我们在给定的scope 开启新的协程时,能否容易确定它们运行在相同的上下文中 ... 由于上下文 信息继承 ...
     * 并且也非常容易的替换上下文,如果需要 ...
     * 现在我们思考这个问题 : 如何使用来自外部scope的dispatcher 进行工作  或者更具体的说:  如何使用来自外部scope的上下文的派发器进行工作 ...
     *
     * 对于coroutineScope 或者 coroutine builder 创建的新的scope,总是继承来自外部scope的上下文 ...
     * 这种情况下,外部scope 很好确定  ...
     * 例如 launch(Dispatchers.Default) {  // outer scope
     * val users = loadContributorsConcurrent(service, req)
     *     // ...
     *  }
     *
     *  所有的内嵌的协程将会使用继承的上下文开始, 并且派发器是这个上下文的一部分  ...
     *  那就是为什么所有通过async 开始的所有协程 都是用默认派发器的上下文 ..(这里说的上下文,是根据上面的例子所说) ...
     *
     *  注意: 新开的协程都有自己的coroutine scope ....
     *
     *  于是在使用结构化并发的时候,我们能够指定主要的上下文元素 ..
     *  例如派发器(主要的) ...
     *  于是所有的内嵌协程继承这个上下文(能够统一化管理) ...
     *
     *  注意当我们使用协程为UI应用编写代码的时候,对于Android,通常使用的是
     *  CoroutineDispatchers.Main ...(作为顶级协程的派发器) ..
     *  当需要在不同线程运行代码时进行显式的不同派发器修改 ....
     */
    @Test
    fun usingOuterScopeContext() {

    }
}