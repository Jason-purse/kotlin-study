package coding.babya.me.take.tour.beginner

import org.junit.jupiter.api.Test
import kotlin.random.Random

/**
 *
 * 像其他编程语言那样,Kotlin 能够基于一块代码评估为true来做出决定, 这样的代码叫做**条件表达式**;
 *
 * Kotlin 也能够创建/迭代循环 ..
 *
 *
 * 此类为when的表达式代码教程 ..
 *
 * [RangeCF]
 *
 * @author jasonj
 * @date 2025/10/13
 * @time 16:03
 *
 * @description
 *
 **/
class ControlFlow {
    /**
     * 条件性表达式
     *
     * - if
     * - when
     *
     * 如果需要在 if / when 之间做选择, 推荐使用when, 因为:
     *
     * 1. 使得你的代码更容易阅读
     * 2. 使得它更容易去增加其他分支
     * 3. 能够减少错误
     */
    @Test
    fun conditionalExpressions() {

    }

    /**
     * if,为了使用**if**, 在括号中增加条件表达式以及需要采取的动作,如果评估结果为true(在{})中写动作指令
     *
     * 在kotlin中没有三元表达式**condition ? then: else**, 相反还是使用if 写表达式, 相当于每个动作仅一行代码,所以花括号是可选的 ..
     */
    @Test
    fun ifExpression() {
        val d: Int
        val check = true

        if (check) {
            d = 1
        } else {
            d = 2
        }

        println(d)
        // 1
    }


    @Test
    fun ternaryExpression() {
        val a = 1
        val b = 2

        println(if (a > b) a else b) // Returns a value: 2
    }

    /**
     * 当具有多个表达式分支的情况下可以使用when
     *
     * 如果使用when:
     * 1. 放置表达式在()中
     * 2. 放置分支在花括号中
     * 3. 使用**->** 在每一个分支上去分离每一个检查到将要执行的动作(如果检查成功)
     *
     * when 能够作为语句或者表达式使用,语句并不会返回任何事但是相反可以执行动作 ..
     *
     * 这里有使用when作为语句的示例 ..
     */
    @Test
    fun whens() {

    }

    /**
     * 注意到所有分支条件将会依序检查直到第一个满足的条件, 因此第一个适合的分支将会执行 ..
     */
    @Test
    fun whenStatement() {
        val obj = "Hello"

        when (obj) {
            // Checks whether obj equals to "1"
            "1" -> println("One")
            // Checks whether obj equals to "Hello"
            "Hello" -> println("Greeting")
            // Default statement
            else -> println("Unknown")
        }
        // Greeting
    }

    /**
     * 表达式能够返回一个值 - 能够在后续使用
     *
     * 下面是一个示例,when 作为表达式, when 表达式会立即赋值结果到变量(后面在println函数中使用的)
     */
    @Test
    fun whenExpression() {
        val obj = "Hello"

        val result = when (obj) {
            // If obj equals "1", sets result to "one"
            "1" -> "One"
            // If obj equals "Hello", sets result to "Greeting"
            "Hello" -> "Greeting"
            // Sets result to "Unknown" if no previous condition is satisfied
            else -> "Unknown"
        }
        println(result)
        // Greeting
    }

    /**
     * 目前为止when示例都有一个主体: obj, 但是when 也能够在没有主体的情况下使用
     *
     * 这个示例使用when表达式,但是没有 主体去检查一组boolean 表达式链:
     */
    @Test
    fun noSubjectWhen() {
        val trafficLightState = "Red" // This can be "Green", "Yellow", or "Red"

        val trafficAction = when {
            trafficLightState == "Green" -> "Go"
            trafficLightState == "Yellow" -> "Slow down"
            trafficLightState == "Red" -> "Stop"
            else -> "Malfunction"
        }

        println(trafficAction)
        // Stop
    }

    /**
     * 从上一个示例中,其实 trafficLightState 就是主体
     *
     * 使用when, 代码更容易阅读和维护, 当使用具有主体的when表达式,能够帮助Kotlin 检查所有可能的条件是否覆盖了 ..
     * 否则,如果你没有使用主体的表达式,你通常需要提供一个else 分支 ..
     */
    @Test
    fun  simpleWhenFromLastExample() {
        val trafficLightState = "Red" // This can be "Green", "Yellow", or "Red"

        val trafficAction = when (trafficLightState) {
            "Green" -> "Go"
            "Yellow" -> "Slow down"
            "Red" -> "Stop"
            else -> "Malfunction"
        }

        println(trafficAction)
        // Stop
    }

    /**
     * Exercise 1
     *
     * 创建一个简单的游戏  当假设抛出两个骰子最终相同数字的结果时你赢, 使用 **if** 去打出 "You win :)",如果不相同,"you lose :("
     *
     * 在这个示例中, 你需要导入一个包 因此你能够使用**Random.nextInt()** 函数去获取一个随机的Int.
     *
     * 有关导入包的更多信息,查看[包和导入](https://kotlinlang.org/docs/packages.html) ..
     *
     */
    @Test
    fun conditionalExpressionsPractice() {
        val firstResult = Random.nextInt(6)
        val secondResult = Random.nextInt(6)

        if(firstResult == secondResult) {
            println("You win :)")
        }
        else {
            println("You lose :(")
        }

        // Write your code here
//        when(firstResult) {
//            secondResult -> println("You win :)")
//            else -> println("You lose :(")
//        }
    }

    /**
     * 使用 when expression, 更新下面的程序让它打印相关的动作 - 当你输入游戏控制按钮的名称 ..
     *
     * ```txt
     *
     * Button                   Action
     *
     * A                        Yes
     * B                        No
     * X                        Menu
     * Y                        Nothing
     * Other                    There is no such button
     * ```
     */
    @Test
    fun whenExpressionExercise() {
        val button = "B"

        println(
                when(button) {
                    "A" -> "Yes"
                    "B" -> "No"
                    "X" -> "Menu"
                    "Y" -> "Noting"
                    "Other" -> "There is no such button"
                    else -> {}
                }
        )
    }
}