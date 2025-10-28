package coding.babya.me.take.tour.intermediate

import org.junit.jupiter.api.Test

/**
 *
 * 学习使用接受者和其他类型的函数,lambda 表达式,它们如何帮助你创建特定领域的语言 ..
 *
 * [ClassAndInterfaces]
 * @author jasonj
 * @date 2025/10/15
 * @time 14:42
 *
 * @description
 *
 **/
class LambdaExpressionWithReceiver {
    /**
     * 在初学者教程中已经学习了如何使用lambda 表达式,但是Lambda表达式仍然可以有接受者, 在这种情况下,lambda 表达式能够访问任何成员函数或者接受者的属性 而不需要显式
     * 的每次指定接受者, 没有这些额外的引用, 你的代码更容易阅读和维护 ..
     *
     * Lambda 表达式(使用接受者的) 也称为具有接受者的函数文本
     *
     * 具有接受者的lambda 函数语法有所不同,当你定义这样的函数类型时, 首先你需要确定你想要扩展的接受者是, 其次,.然后完成函数类型的剩余部分定义,例如:
     * ```kotlin
     *  MutableList<Int>.() -> Int
     * ```
     */
    @Test
    fun lambdaExpressionWithReceiver() {
        /**
         * MutableList<Int> 是一个接受者,()内无入参
         * 无返回类型
         */
        val listPrint: MutableList<Int>.() -> Unit = { println(this) }

        // 例如 在画布上画形状的示例
        class Canvas {
            fun drawCircle() = println("🟠 Drawing a circle")
            fun drawSquare() = println("🟥 Drawing a square")
        }

        // Lambda expression with receiver definition
        fun render(block: Canvas.() -> Unit): Canvas {
            val canvas = Canvas()
            // Use the lambda expression with receiver
            canvas.block()
            return canvas
        }
        /**
         * 在这个示例中,Canvas 具有两个函数,模仿画圆形或者方形
         *
         * render函数需要一个block参数并且返回Canvas的实例
         *
         * block 参数是一个具有接受者的lambda 表达式, Canvas类是接受者
         *
         * render函数创建Canvas 类并且调用block() lambda表达式(在canvas实例上),使用它作为接受者
         *
         * render函数使用lambda表达式进行调用, 这将传递给block 参数
         *
         * 在lambda 传递给render函数的body中,程序调用drawCircle() 以及 drawSquare() 函数(分别在Canvas类的实例上调用)
         *
         * 因为这两个函数都是在具有接受者的lambda表达式中调用,它们能够直接调用就像在Canvas类中一样 ..
         *
         * 具有接受者的lambda 表达式 能够很有用,假设你想要创建特定于领域的语言(DSL)
         *
         * 因为你能够访问接受者的成员函数和属性 而不需要显式引用接受者,你的代码变得更加简洁(leaner) / 更精炼
         *
         * 为了说明这个DSL, 考虑一个示例(在菜单中配置项), 让我们以MenuItem 和Menu类(它包含了一个函数去增加项到菜单 -叫做item())开始,
         * 同样所有的菜单项目的列表函数 **items** ..
         */
        render {
            drawCircle()
            // 🟠 Drawing a circle
            drawSquare()
            // 🟥 Drawing a square
        }
    }

    /**
     * 现在使用一个具有接受者的lambda表达式 传递为函数参数(init) 到menu函数(构建菜单作为起点)
     */
    @Test
    fun menuExample() {
        class MenuItem(val name: String)

        class Menu(val name: String) {
            val items = mutableListOf<MenuItem>()

            fun item(name: String) {
                items.add(MenuItem(name))
            }
        }

        fun menu(name: String,init: Menu.() -> Unit): Menu {
            // Creates an instance of the Menu class
            return Menu(name).apply {
                // Calls the lambda expression with receiver init() on the class instance
                init()
            }
        }

        // 现在能够使用DSL 去配置一个菜单 并且创建printMenu 函数去打印菜单结构到控制台
        fun printMenu(menu: Menu) {
            println("Menu: ${menu.name}")
            menu.items.forEach { println("  Item: ${it.name}") }
        }

        menu("Main Menu") {
            // Add items to the menu
            item("Home")
            item("Settings")
            item("Exit")
        }.also { println(it) }

        // 正如你所见, 使用一个具有接受者的lambda 表达式 能够极大的简化需要创建菜单的代码 ..
        // LambdaExpressionWithReceiver 表达式不仅对于初始化和构建有用,而且配置也是有用的
        // 它们常用于构建给定API的DSL, UI框架,以及配置构建器去产生流水线代码, 允许你集中区更容易理解代码结构和逻辑 ..

        // Kotlin的生态系统有这种设计模式的大量示例, 例如 来自标准库的 [buildList()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/build-list.html) 以及 [buildString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/build-string.html) 函数
        // Lambda 表达式(具有接受者的) 能够在kotlin中与类型安全构建者合并 去使得DSL 能够在编译时而不是运行时检查与类型相关的任何问题, 为了了解更多
        // 查看 [类型安全构建器](https://kotlinlang.org/docs/type-safe-builders.html)
    }

    /**
     * 你有一个fetchData 函数需要一个使用接受者的lambda表达式, 更新lambda表达式去使用append() 函数 这样代码的输出是:
     * Data received - Processed
     */
    @Test
    fun exercise1() {
        fun fetchData(callback: StringBuilder.() -> Unit) {
            val builder = StringBuilder("Data received")
            builder.callback()
        }

        fetchData {
            fetchData {
                append(" - ")
                append("Processed")
                println(this.toString())
            }
        }
    }

    /**
     * 你有一个Button 按钮 以及 ButtonEvent,Position 数据类, 编写某些代码触发Button类的**onEvent**的成员函数去触发双击事件 ..
     * 你的代码应该打印"Double click!"
     */
    @Test
    fun exercise2() {
        data class Position(
            val x: Int,
            val y: Int
        )
        data class ButtonEvent(
            val isRightClick: Boolean,
            val amount: Int,
            val position: Position
        )


        class Button {
            fun onEvent(action: ButtonEvent.() -> Unit) {
                // Simulate a double-click event (not a right-click)
                val event = ButtonEvent(isRightClick = false, amount = 2, position = Position(100, 200))
                event.action() // Trigger the event callback
            }
        }



        val button = Button()

        button.onEvent {
            // Write your code here
            // Double click!
            when {
                !isRightClick && amount == 2 -> println("Double click")
                else -> println("Unknown click")
            }
        }
    }

    /**
     * 编写一个函数(创建一组整数列表 - 每个元素依序增加1), 使用这提供的函数骨架(继承于List<Int>且具有一个 incremented 函数)
     */
    @Test
    fun exercise3() {
        fun List<Int>.incremented(): List<Int> {
            val originalList = this
            return buildList {
//                originalList.forEach {
//                    add(it + 1)
//                }
                for (i in originalList) {
                    add(i + 1)
                }
            }
        }

        val originalList = listOf(1, 2, 3)
        val newList = originalList.incremented()
        println(newList)
        // [2, 3, 4]
    }



}