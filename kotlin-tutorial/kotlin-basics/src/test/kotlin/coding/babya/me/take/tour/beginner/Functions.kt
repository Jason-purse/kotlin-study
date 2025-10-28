package coding.babya.me.take.tour.beginner

import org.junit.jupiter.api.Test
import kotlin.math.PI

/**
 *
 * 函数
 *
 * 下一章 [Classes]
 * @author jasonj
 * @date 2025/10/14
 * @time 10:01
 *
 * @description
 *
 **/
class Functions {
    /**
     * 通过 fun 关键字声明你的函数
     */
    @Test
    fun hello() {
        println("hello,world")
    }

    @Test
    fun main() {
        hello();
    }

    /**
     * 在Kotlin中, 函数参数写在 ()中,每一个参数必须有一个类型, 并且多个参数必须通过,分割 .
     *
     * 返回类型写在函数的()之后, 通过:分割
     *
     * 函数体写在{}之内
     *
     * return 关键字被用来退出函数以及从函数内返回某些事情
     *
     * 如果一个函数不需要返回有意义的事情, 返回类型和return 关键字可以被省略, 了解[无返回函数](https://kotlinlang.org/docs/kotlin-tour-functions.html#functions-without-return)
     *
     * 在下面的示例中:
     * - x,y 是函数参数
     * - x,y 类型Int
     * - 返回类型是Int
     * - 函数返回的是 x + y 的总和的结果(当调用的时候)
     */
    fun sum(x: Int, y: Int): Int {
        return x + y
    }

    /**
     * 推荐代码约定中(函数名以小写形式开始,然后使用驼峰而不是下划线)
     */
    @Test
    fun sumTest() {
        println(sum(1, 2))
    }

    /**
     * ### Named arguments
     *
     * 为了代码简明, 当你调用函数的时候,你不需要包含参数名称 ..
     *
     * 然而,包括参数名能够使得你的代码更容易阅读 .. 这叫做命名参数 ..
     *
     * 如果你包括参数名,那么你能够以任何顺序写参数 ..
     *
     * > 在下面的示例中,字符串模版($) 被用来访问参数值,转换它们到String 类型, 然后关联它们到字符串为了打印。
     */
    fun printMessageWithPrefix(message: String, prefix: String = "Info") {
        println("[$prefix] $message")
    }

    /**
     * 使用了交换参数顺序的命名参数
     */
    @Test
    fun namedArguments() {
        printMessageWithPrefix(prefix = "Log", message = "Hello")
    }

    /**
     * 你能够为你的函数参数定义默认值, 任何具有默认值的参数能够在调用你的函数的时候省略,为了声明一个默认值, 在类型之后使用赋值操作符 **=** ..
     *
     * > 你能够跳过具有默认值的特定参数, 而不是省略所有, 但是,第一个跳过的参数之后, 你必须命名所有的后续参数 ..
     *  println(intervalInSeconds(0, seconds = 10, minutes = 0))
     */
    @Test
    fun defaultParameterValue() {
        printMessageWithPrefix("Hello","Log");
        printMessageWithPrefix("Hello");
        printMessageWithPrefix(prefix = "Log", message = "Hello")
        printMessageWithPrefix(message = "Log");
    }
    fun multipleNamedFun(a: Int  = 1, b: Int = 2, c: Int = 3): Int {
        return a + b + c
    }

    @Test
    fun firstSkipedArgument() {
        // 命名指定你所需要覆盖的某些参数
        multipleNamedFun(b = 2);
    }

    /**
     * 无返回函数, 如果你的函数不返回任何有用的值,那么返回类型是**Unit**, 它是一个仅具有一个值的类型(值 = "Unit"),
     * 你不需要显式的声明Unit类型,省略也是表示此类型, 这意味着你不需要使用return 关键字或者声明返回类型 ..
     */
    @Test
    fun onReturnFunction() {
        fun printMessage(message: String) {
            println(message)
        }

        printMessage("Hello")
    }

    /**
     * 单表达式函数
     *
     * 为了使得代码更加简洁, 能够使用单表达式函数, 例如,**sum**函数能够更加简洁
     *
     */
    @Test
    fun singleExpressionFunctions() {
        fun sum(a: Int, b: Int): Int {
            return a + b
        }

        // 可以转变为
        /**
         * 直接去掉花括号并且通过 = 声明函数体
         *
         * 当你使用 =,Kotlin 会使用类型推断,因此你能够省略返回类型, sum函数将仅仅一行 .
         */
        fun sum1(a: Int, b: Int) = a + b;

        // 但是如果你想要你的代码能够快速的被其他开发者理解,好的方式是显式的定义返回类型,即使你使用 单行函数表达式形式(=) ..
        // 如果使用{}声明函数体, 你必须声明返回类型除非它本身就是Unit类型 ..
    }

    /**
     * 为了停止正在执行的函数中的代码超过某个点, 使用return 关键字, 这个示例使用**if** 去从函数中更早的返回,如果某个条件表达式发现为true ..
     */
    @Test
    fun earlyReturnInFunctions() {
        // A list of registered usernames
        val registeredUsernames = mutableListOf("john_doe", "jane_smith")

        // A list of registered emails
        val registeredEmails = mutableListOf("john@example.com", "jane@example.com")

        fun registerUser(username: String, email: String): String {
            // Early return if the username is already taken
            if (username in registeredUsernames) {
                return "Username already taken. Please choose a different username."
            }

            // Early return if the email is already registered
            if (email in registeredEmails) {
                return "Email already registered. Please use a different email."
            }

            // Proceed with the registration if the username and email are not taken
            registeredUsernames.add(username)
            registeredEmails.add(email)

            return "User registered successfully: $username"
        }

        println(registerUser("john_doe", "newjohn@example.com"))
        // Username already taken. Please choose a different username.
        println(registerUser("new_user", "newuser@example.com"))
        // User registered successfully: new_user
    }

    /**
     * 写一个函数叫做 circleArea 它需要整数形式的圆的半径作为参数并且输出圆的面积 ..
     * > 这个示例中, 你需要导入一个包 能够让你访问 pi的值(通过PI),有关导入包的更多信息,查看 [包和导入](https://kotlinlang.org/docs/packages.html)
     */
    @Test
    fun exercise1() {
        fun circleArea(radius: Int): Double {
            return kotlin.math.PI * radius * radius;
        }

        println(circleArea(2))
    }

    /**
     * 重写circleArea 函数 从之前的练习中 转换为单表达式函数
     */
    @Test
    fun exercise2() {
        fun circleArea(radius: Int) = kotlin.math.PI * radius * radius;
    }

    /**
     * 你有一个函数能够翻译时间周期(给定小时、分钟、秒) 到毫秒,在大多数情况下,你需要传递仅一个或者2个函数参数 然后剩余的等于0,
     * 改进这个函数并且使得通过默认参数值调用函数以及命名需要的参数让代码更容易阅读 ..
     */
    @Test
    fun exercise3() {
        fun intervalInSeconds(hours: Int = 0, minutes: Int = 0, seconds: Int = 0) =
            ((hours * 60) + minutes) * 60 + seconds


        println(intervalInSeconds(1, 20, 15))
        println(intervalInSeconds(0, 1, 25))
        println(intervalInSeconds(hours = 2, minutes = 0, seconds = 0))
        // 这里就是跳过了第一个之后 所有的都需要命名(当你都给定入参时)
        println(intervalInSeconds(0, seconds = 10, minutes = 0))
        println(intervalInSeconds(1, 0, 1))
        println(intervalInSeconds(seconds = 0, minutes = 1))
    }

    /**
     * Lambda 表达式
     *
     * Kotlin 允许你写甚至更加简洁的代码来替代函数,也就是lambda,例如下面的uppercaseString()函数
     */
    @Test
    fun lambdaExpression() {
        fun uppercaseString(text: String): String {
            return text.uppercase();
        }

        println(uppercaseString("hello"))


        // 也可以写成lambda 表达式

        val upperCaseLambda = {text: String -> text.uppercase()};

        val upperCaseMethodRef: (text: String) -> String = String::uppercase;

        /**
         * lambda 表达式第一眼可能很难理解 因此把它分解掉,Lambda 表达式能够写在花括号中 ..
         *
         * 在lambda表达式中, 你能够写:
         *
         * 1. 参数后跟 ->
         *
         * 2. 函数体在 -> 之后
         *
         * 当前示例中,text 是函数参数,text 具有String类型 ..
         *
         * 这个函数返回的是在text上调用**.uppercase()**函数的结果 ..
         *
         * 3. 完整的表达式赋值到upperCaseString变量上 - 通过赋值符号 ..
         * 4. lambda 表达式能够通过使用upperCaseString变量像函数那样调用并且传递字符串"hello"作为 参数 ..
         * 5. **println()** 函数输出结果 ..
         *
         * > 如果你声明了一个没有参数的lambda, 那么不需要使用 ->, 例如
         */
        // 还可以这样写,真的离谱(类似于js)
        ({ println("Log message")})();

        /**
         * lambda 表示能够有以下几种使用方式,你能够:
         * 1. 传递lambda 表达式作为参数到其他函数
         * 2. 从函数中返回lambda表达式
         * 3. 执行lambda表达式
         */
    }

    /**
     * 一个非常好的实例是传递lambda表达式到函数, 在集合上使用`.filter()` 函数
     *
     * .filter函数接受一个lambda 表达式作为条件并且应用它到列表中每一个元素中,这个函数仅仅会保留断言为true的元素 ..
     *
     * 1. {x -> x > 0} 表示元素是正整数
     * 2. {x -> x < 0} 表示元素是负整数
     *
     * 这个示例说明了两种传递lambda表达式到函数的方式
     *
     * 1. 一种是直接传递到filter函数的lambda
     * 2. 通过isNegative 变量传递为filter的函数参数, 在这种情况下需要制定x函数参数的类型 ..
     *
     * 如果lambda 表达式是唯一的函数的参数,可以直接移除函数括号() .. (也就是filter函数仅有一个函数参数),可以去掉filter的括号
     * > ```kotlin
     * > val positives = numbers.filter { x -> x > 0}
     * > ```
     *
     * 这是[尾缀lambda](https://kotlinlang.org/docs/kotlin-tour-functions.html#trailing-lambdas)的示例, 将会在章节的末尾讨论更多 ..
     */
    @Test
    fun passToAnotherFunction() {
        val numbers = listOf(1, -2, 3, -4, 5, -6)

        // val positives = numbers.filter ({ x -> x > 0 })
        // 也可以是it
        val positives = numbers.filter { it > 0 }

        val isNegative = { x: Int -> x < 0 }
        val negatives = numbers.filter(isNegative)

        println(positives)
        // [1, 3, 5]
        println(negatives)
        // [-2, -4, -6]
    }

    /**
     * 这个示例 中,通过映射函数map 来实现对集合中的项进行转换 从而得到新集合
     *
     * 1. 它接受一个lambda 表达式作为转换函数
     * - {x -> x * 2} 表示每一个元素都乘以2
     * - {x -> x * 3} 表示每一个元素都乘以3
     */
    @Test
    fun mapLambda() {
        val numbers = listOf(1, -2, 3, -4, 5, -6)
        val doubled = numbers.map { x -> x * 2 }

        val isTripled = { x: Int -> x * 3 }
        val tripled = numbers.map(isTripled)

        println(doubled)
        // [2, -4, 6, -8, 10, -12]
        println(tripled)
        // [3, -6, 9, -12, 15, -18]
    }

    /**
     * ## 函数类型
     *
     * 在从函数中返回lambda表达式之前,你需要理解下函数类型 ..
     *
     * 你已经学习了基础类型但是函数自己也是一种类型 ..
     *
     * Kotlin的类型推断也能够根据参数类型推断函数的类型,但是有时你需要显式的指定函数类型 ..
     * 编辑器需要函数类型 让它知道是什么并且对应这个函数那些是不允许的 ..
     *
     * 函数类型的语法是:
     * 1. 每一个参数的类型写在()中并且通过,分割
     * 2. 返回类型写在 -> 之后
     *
     * 例如(String) -> String, 或者(Int,Int) -> Int
     *
     * 假设 upperCaseString()的函数类型定义 那么lambda表达式看起来像啥:
     */
    @Test
    fun  functiontypes() {
        val upperCaseString: (String) -> String = {text -> text.uppercase()}
        println(upperCaseString("hello"))

        /**
         * 如果lambda 表达式没有参数,那么()是保持空, 例如() -> Unit ..
         *
         * 否则编译器不能够知道你的lambda的表达式的类型是
         *
         * 例如:
         * > val upperCaseString = {str -> str.uppercase()}  无法工作
         */
    }

    /**
     * Lambda 表达式能够从函数中返回, 因此为了编译器理解lambda表达式返回的类型是,必须声明函数类型
     *
     * 在下面的示例中, **toSeconds** 函数具有函数类型(Int) -> Int,因为它总是返回一个表达式(需要Int类型并且返回Int类型的值)
     *
     * 下面的示例中使用when表达式确定返回了哪一个表达式,当toSeconds调用的时候
     */
    @Test
    fun returnFromFun() {
        fun toSeconds(time: String): (Int) -> Int = when (time) {
            "hour" -> { value -> value * 60 * 60 }
            "minute" -> { value -> value * 60 }
            "second" -> { value -> value }
            else -> { value -> value }
        }

        val timesInMinutes = listOf(2, 10, 15, 1)
        val min2sec = toSeconds("minute")
        val totalTimeInSeconds = timesInMinutes.map(min2sec).sum()
        println("Total time is $totalTimeInSeconds secs")
    }

    /**
     * 单独执行
     * Lambda 表达式能够自行执行  通过增加()在花括号之后并包括参数接口.
     */
    @Test
    fun invokeSeparately() {
        println({ text: String -> text.uppercase() }("hello"))
        // HELLO
    }

    /**
     * 尾lambda, 正如你所见,如果labmda表达式是唯一的函数参数, 那么你能够删除函数括号, 如果lambda表达式作为函数的最后一个参数传递,那么
     * 表达式能够写在函数括号之外, 两种情况下,这种语法叫做尾缀lambda ..
     *
     * 例如,**.fold()** 函数接受一个初始值以及一个操作
     *
     * 有关lambda 表达式的更多信息,[lambda 表达式和匿名函数](https://kotlinlang.org/docs/lambdas.html#lambda-expressions-and-anonymous-functions)
     */
    @Test
    fun trailingLambdas() {
    // The initial value is zero.
    // The operation sums the initial value with every item in the list cumulatively.
            println(listOf(1, 2, 3).fold(0, { x, item -> x + item })) // 6

    // Alternatively, in the form of a trailing lambda
            println(listOf(1, 2, 3).fold(0) { x, item -> x + item })  // 6
    }

    /**
     * 你有一个被web service支持的一组动作,  一个共同的前缀(针对所有请求), 以及 特定资源的ID.
     *
     * 为了请求一个动作(在ID5上的 title), 你需要创建以下的URL,**https://example.com/book-info/5/title**,
     * 使用Lambda 表达式去根据动作列表创建URL列表..
     */
    @Test
    fun exercise_lambda_expression() {
        val actions = listOf("title", "year", "author")
        val prefix = "https://example.com/book-info"
        val id = 5
        // val urls = // Write your code here
        // val urls = actions.map { "https://$prefix/$id/$it" }
        val urls = actions.map { action -> "https://$prefix/$id/$action" }
        println(urls)
    }

    /**
     * 写一个函数 - 需要Int 值和一个动作(它是具有() -> Unit的类型的函数)
     *
     * 也就是需要重复给定次数的动作, 然后使用这个函数去打印 "Hello" 5次 ..
     */
    @Test
    fun exercise_lambda_expression1() {
        fun repeatN(n: Int, action: () -> Unit) {
            for (i in 1..n) {
                action();
            }
        }

        repeatN(5) { println("Hello") }
    }
}