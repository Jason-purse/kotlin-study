package coding.babya.me.take.tour.beginner

import org.junit.jupiter.api.Test

/**
 * Ranges
 *
 * 在开始说循环之前, 需要知道如何构造循环范围去迭代
 *
 * 大多数常见方式创建范围是使用**..** 操作符,例如 1 .. 4 等价于 1,2,3,4
 *
 * 为了声明一个右开区间的范围, 使用 ..< 操作符, 例如 1..< 4 等价于 1,2,3
 *
 * 为了声明一个相反顺序的范围, 使用downTo,例如 4 downTo 1 等价于  4,3,2,1
 *
 * 为了声明一个具有给定步长的范围, 使用step 关键字以及 想要设定的步长值即可, 1..5 step 2,等价于 1,3,5
 *
 * 在Char 范围中能够做相同的事情:
 * 1. 'a' .. 'd',等价于 'a','b','c','d'
 * 2. 'z' downTo 's' step 2 等价于 'z','x','v','t'
 *
 * [Functions] 函数
 *
 * @author jasonj
 * @date 2025/10/13
 * @time 16:43
 *
 * @description
 *
 **/
class RangeCF {
    /**
     * 循环在编程语言中大多数两种结构,**for**,**while**, 使用**for** 在一个范围上进行迭代并执行一个动作, 使用**while**是
     * 直到某个特定条件满足之后继续某个动作.
     */
    @Test
    fun loops() {

    }

    /**
     * 使用新的范围知识,能够创建一个在1到5的范围上迭代的for 循环并且每次打印当前数字 ..
     *
     * 使用in将迭代器以及范围放置在()中, 在花括号中增加你想要执的动作:
     *
     */
    @Test
    fun forLoop() {
        for (number in 1..5) {
            // number is the iterator and 1..5 is the range
            print(number)
        }
        // 12345
    }

    /**
     * 集合也可以被循环迭代
     */
    @Test
    fun loopOverCollection() {
        val cakes = listOf("carrot", "cheese", "chocolate")

        for (cake in cakes) {
            println("Yummy, it's a $cake cake!")
        }
        // Yummy, it's a carrot cake!
        // Yummy, it's a cheese cake!
        // Yummy, it's a chocolate cake!
    }

    /**
     * while 有两种使用方式
     * 1. 当条件表达式为true的时候执行一个代码块
     * 2. 先执行一次代码块,然后检查条件表达式是否为true(do-while)
     *
     * 在第一种使用情况中
     * 1. 在括号中声明继续循环的条件表达式
     * 2. 增加继续循环的动作
     *
     * > 下面的示例使用增加操作符**++** 去增加**cakesEaten**变量的值 .
     */
    @Test
    fun whileLoop() {
        var cakesEaten = 0
        while (cakesEaten < 3) {
            println("Eat a cake")
            cakesEaten++
        }
        // Eat a cake
        // Eat a cake
        // Eat a cake
    }

    /**
     * 在do-while中, 在括号中声明需要继续循环的条件表达式
     *
     * 在do后面的花括号中定义需要继续循环的条件表达式
     *
     * 有关条件表达式和循环的更多示例,查看 [条件和循环](https://kotlinlang.org/docs/control-flow.html)
     */
    @Test
    fun doWhileLoop() {
        var cakesEaten = 0
        var cakesBaked = 0
        while (cakesEaten < 3) {
            println("Eat a cake")
            cakesEaten++
        }
        do {
            println("Bake a cake")
            cakesBaked++
        } while (cakesBaked < cakesEaten)
        // Eat a cake
        // Eat a cake
        // Eat a cake
        // Bake a cake
        // Bake a cake
        // Bake a cake
    }

    /**
     * 你有一个程序统计pizza slices(块) 直到整个pizza 具有8块,
     */
    @Test
    fun exercise1() {
        var pizzaSlices = 0
        // Start refactoring here
//        pizzaSlices++
//        println("There's only $pizzaSlices slice/s of pizza :(")
//        pizzaSlices++
//        println("There's only $pizzaSlices slice/s of pizza :(")
//        pizzaSlices++
//        println("There's only $pizzaSlices slice/s of pizza :(")
//        pizzaSlices++
//        println("There's only $pizzaSlices slice/s of pizza :(")
//        pizzaSlices++
//        println("There's only $pizzaSlices slice/s of pizza :(")
//        pizzaSlices++
//        println("There's only $pizzaSlices slice/s of pizza :(")
//        pizzaSlices++
//        println("There's only $pizzaSlices slice/s of pizza :(")
//        pizzaSlices++

        // kotlin 1.6.10-RC 不支持这个语法
//        while((pizzaSlices ++) < 7) {
//            println("There's only $pizzaSlices slice/s of pizza :(")
//        }

        do {
            pizzaSlices++;
            println("There's only $pizzaSlices slice/s of pizza :(")
        }while (pizzaSlices < 8);
        // End refactoring here
        println("There are $pizzaSlices slices of pizza. Hooray! We have a whole pizza! :D")

    }

    /**
     * 编写一个程序模拟 Fizz buzz 游戏,(发出蜂鸣声),你的任务是打印从1到100的数字,能够被3整除的任何数字通过'fizz'替代, 然后任何能够被
     * 5整除的数字使用'buzz'替换 ..
     *
     * 同时被3和5整除的额 通过'fizz buzz'替换
     */
    @Test
    fun exercise2() {
        for(i in 1.. 100) {
            // when 相比于if更加直接
//            if(i % 15 == 0) {
//                println("fizz buzz")
//            }
//            else if (i % 3 == 0) {
//                println("fizz")
//            }
//            else if(i % 5 == 0) {
//                println("buzz")
//            }
//            else {
//                println(i)
//            }

            for (number in 1..100) {
                println(
                    when {
                        number % 15 == 0 -> "fizzbuzz"
                        number % 3 == 0 -> "fizz"
                        number % 5 == 0 -> "buzz"
                        else -> "$number"
                    }
                )
            }
        }
    }
}