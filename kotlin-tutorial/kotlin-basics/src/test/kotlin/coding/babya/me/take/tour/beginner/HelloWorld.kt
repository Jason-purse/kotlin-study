package coding.babya.me.take.tour.beginner

import org.junit.jupiter.api.Test

/**
 *
 * Hello world
 *
 * 下一个部分[BasicTypes]
 * @author jasonj
 * @date 2025/10/13
 * @time 10:01
 *
 * @description
 *
 **/
class HelloWorld {
    /**
     * 打印 hello world
     * - 在kotlin中 `fun` 关键字用来声明一个函数
     * - `main()` 函数表示程序开始的地方
     * - 函数体编写在`{}` 中。
     * - **println** 以及 **print** 函数打印它们的参数到标准输出。
     *
     * ---
     * 函数是执行特定任务的一组指令, 一旦你创建了一个函数,能能够使用它(当你想要执行某个任务的时候) ..
     * 而不需要再次编写所有指令, 函数将会在一组章节中讨论更多细节, 到现在为止,所有的示例使用`main` 函数。
     */
    fun helloWorld() {
        println("Hello, world!")
    }

    /**
     * ### 变量
     * 所有的程序都需要存储数据, 变量能够帮助你做到那样, 在Kotlin中,你能够声明
     * - 只读变量 `val`
     * - 可修改变量 `var`
     *
     * 为了分配变量一个值,则使用赋值操作符`=` .
     *
     * > 可以在在**main**函数之外声明变量,这种方式的变量将会声明为顶级变量。
     *
     * 因为**customers** 是一个可变变量, 它的值能够在声明之后重新分配.
     *
     * > 我们推荐声明所有的变量默认为**val**, 仅当你确实需要使用可变变量才使用**var** ..
     * > 也就是说,你这样会很少意外的改变本不希望改变的事情.
     *
     */
    fun  variables() {
        val popcorn = 5; // 表示 popcorn的5个箱子
        val hotdog = 7; // 表示7个 hotdogs.
        var customers = 10; // 表示队列中有10个顾客 ..
        customers = 8; // 有些顾客离开了这个队列 ..
        println(customers);
    }

    /**
     * 字符串模版
     *
     * 这是有助于知道如何打印变量的内容到标准输出的.  你能够通过字符串模版做到这样 ..
     *
     * 你能够使用模版表达式去访问存储在变量和其他对象中的数据, 并且将它们转换到字符串中, 一个**string** 值说的是位于双引号**"**中的字符序列,
     * 模版表达式从**$** 开始。
     *
     * 为了评估模版表达式中的代码块, 在$符号之后添加{}, 将代码块放置在{}中。
     *
     * 有关更多信息查看[string templates](https://kotlinlang.org/docs/strings.html#string-templates)
     *
     * 你能够注意到当前没有为变量声明任何类型, Kotlin 已经推断类型为`Int`, 这个教程中在[下个章节](https://kotlinlang.org/docs/kotlin-tour-basic-types.html)中解释了不同的Kotlin 基本类型以及如何声明它.
     */
    @Test
    fun stringTemplates() {
        val customers = 10;
        println("There are $customers customers")

        println("There are ${customers + 1} customers")
    }

    /**
     * 完善代码使得程序打印`Mary is 20 years old` 到标准输出.
     */
    @Test
    fun practice() {
        val name = "Mary"
        val age = 20

        println("${name} is $age years old")
    }
}