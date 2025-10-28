package coding.babya.me.basic

import org.junit.jupiter.api.Test

/**
 * 这是一组带有示例的基础语法元素的集合, 在每个部分的结尾处, 你能够发现一个链接到相关主题的详细描述的链接 ..
 *
 * 你能够通过JetBrains Academy的免费[Kotlin Core 课程](https://hyperskill.org/tracks?category=4&utm_source=jbkotlin_hs&utm_medium=referral&utm_campaign=kotlinlang-docs&utm_content=button_1&utm_term=22.03.23&_gl=1*1lfgy9i*_gcl_au*MTE5NjgzNDg1MC4xNzYwMDc5Njg3*_ga*MTU1OTIwMzMxLjE3NDE0MzgxOTc.*_ga_9J976DJZ68*czE3NjEyMDc3NjQkbzI2JGcxJHQxNzYxMjA4NDg0JGoyOCRsMCRoMA..)学习所有的Kotlin 基础知识 ..
 *
 * @author jasonj
 * @date 2025/10/23
 * @time 16:35
 *
 * @description
 *
 **/
class BasicSyntax {
    /**
     * 包定义和导入
     *
     * ```kotlin
     *    package my.demo
     *
     *   import kotlin.text.*
     *
     * ```
     *
     * 它不需要匹配目录和包, 源文件能够放在文件系统的任意位置 ..
     *
     * [Packages](https://kotlinlang.org/docs/packages.html)
     */
    @Test
    fun packageDefinitionAndImports() {

    }

    /**
     * Kotlin 应用的入口是 main 函数
     */
    @Test
    fun programEntryPoint() {
        fun main() {
            println("Hello world!")
        }

        /**
         * 另一种形式的main函数接受一个可变数量的String数组参数
         */
        fun main(args: Array<String>) {
            println(args.contentToString())
        }
    }

    /**
     * 打印到标准输出
     */
    @Test
    fun printToStandardOutput() {
        println("Hello ")
        println("World")

        // println 会自动的增加一个换行符, 因此下面的打印语句将会出现在不同行 .
        println("Hello world!")
        println(42)
    }

    /**
     * 从标准输入读取
     *
     * readln() 函数能够从标准输入中读取, 下面的函数读取整行(用户输入)作为祖父穿
     *
     * 你能够使用println / readln / print函数一起去打印请求的消息并展示用户输入:
     *
     * 有关更多信息,查看 [Read standard input](https://kotlinlang.org/docs/read-standard-input.html)
     *
     *
     */
    @Test
    fun readFromStandardInput() {
        // Prints a message to request input
        println("Enter any word: ")

// Reads and stores the user input. For example: Happiness
        val yourWord = readln()

// Prints a message with the input
        print("You entered the word: ")
        print(yourWord)
// You entered the word: Happiness
    }

    /**
     * 一个函数具有两个Int 参数 并且Int 返回类型
     */
    @Test
    fun functions() {
        fun sum(a: Int, b: Int): Int {
            return a + b
        }

        val sum: (a: Int, b: Int) -> Int = { a, b -> sum(a, b) }

        fun sum1(a: Int, b: Int) = a + b

        // 返回无意义的值的函数
        fun printSum(a: Int, b: Int) {

        }

        // Unit 返回类型可以被省略
        fun printSum1(a: Int, b: Int) {}
    }

    /**
     * 变量
     *
     * kotlin 可以通过val / var 声明变量, 后跟变量名
     *
     * 使用 val 关键字去声明的变量仅仅只能赋值一次, 它们是不可变的,只读变量无法重新分配不同的值(当初始化之后)
     */
    @Test
    fun variables() {
        // Declares the variable x and initializes it with the value of 5

        // var 关键字声明的变量可以修改, 它们叫做可变变量, 在初始化之后能够改变它们的值

        var y: Int = 5

        y += 1

        // 6

        // Kotlin 支持类型推断以及自动的识别声明变量的数据类型, 当你声明一个变量的时候, 你能够在变量名后省略类型 ..

        // 5 => Int

        // 仅能够在初始化之后使用变量, 你能够要么在声明的时候同时初始化变量  或者首先声明变量 然后初始化它, 第二种情况下,你必须指定数据类型:

        val c: Int

        c = 3
        // 5
        // 3

        // 可以在顶级声明变量,例如类文件中 ..
    }

    val PI = 3.14

    var x = 0

    fun incrementX() {
        x += 1
    }

    // x = 0; PI = 3.14

    // incrementX()

    // x = 1; PI = 3.14

    // 更多声明属性的信息,查看 [Properties](https://kotlinlang.org/docs/properties.html)
    /**
     * 创建 classes 以及 实例
     *
     * 为了定义一个类,必须使用class 关键字
     */
    @Test
    fun creatingClassesAndInstance() {
        class Shape

        // 类的属性能够列在它的声明中或者内容中
        class Rectangle(val height: Double, val width: Double) {
            val perimeter = (height + width) * 2
        }

        // 默认的构造器(在类声明中列出的参数)将自动可用

        val rectangle = Rectangle(5.0, 2.0)
        println("The perimeter is ${rectangle.perimeter}")

        // 继承 则通过: 声明, 默认类是final的, 如果为了让类能够继承, 标记它为open ..

        open class Shape1

        class Rectangle1(val height: Double, val width: Double) : Shape1() {
            val perimeter = (height + width) * 2
        }

        // 更多构造器和继承的更多信息,查看[类](https://kotlinlang.org/docs/classes.html)和[对象以及实例](https://kotlinlang.org/docs/object-declarations.html)
    }

    /**
     * 就像大多数现代语言那样, Kotlin 支持单行以及多行(块)注释 ..
     */
    @Test
    fun comments() {
        // This is an end-of-line comment

        /* This is a block comment on multiple lines. */

        /** Block 注释在Kotlin中能够内嵌
        the comment starts here
        /*  contains a nested comment */
        and ends here.
         */

        // 查看 [Kotlin代码中的记录(文档)](https://kotlinlang.org/docs/kotlin-doc.html) 了解文档注释语法的信息 ..
    }

    @Test
    fun stringTemplates() {
        // var a = 1;
        var a = 1
        val s1 = "a is $a"

        a = 2

        "${s1.replace("is", "was")}, but now is $a"

        // 查看 [字符串模版](https://kotlinlang.org/docs/strings.html#string-templates) 了解更多 ..
    }

    /**
     * [条件表达式](https://kotlinlang.org/docs/control-flow.html#if-expression)
     */
    @Test
    fun conditionalExpressions() {
        // 表达式写法
        fun maxOf(a: Int, b: Int): Int {
            return if (a > b) a else b
        }

        fun maxOf1(a: Int, b: Int): Int {
            if (a > b) {
                return a
            } else {
                return b
            }
        }
    }

    /**
     *  [for 循环](https://kotlinlang.org/docs/control-flow.html#for-loops)
     */
    @Test
    fun forloop() {
        val items = listOf("apple", "banana", "kiwifruit")
        for (item in items) {
            println(item)
        }

        // 或者
        for (index in items.indices) {
            println("item at $index is ${items[index]}")
        }
    }

    /**
     * [whileloop](https://kotlinlang.org/docs/control-flow.html#when-expressions-and-statements)
     */
    @Test
    fun whileloop() {
        fun describe(obj: Any): String = when (obj) {
            1 -> "one"
            "Hello" -> "Greeting"
            is Long -> "Long"
            !is String -> "Not a string"
            else -> "Unknown"
        }
    }

    /**
     * 判断某个数字是否在给定范围中, in 操作符
     */
    @Test
    fun ranges() {

        val x = 10

        val y = 9

        if (x in 1..y + 1) {
            println("fits in range")
        }
    }

    /**
     * [Ranges and progressions](https://kotlinlang.org/docs/ranges.html)
     */
    @Test
    fun notin() {
        val list = listOf("a", "b", "c")

        if (-1 !in 0..list.lastIndex) {
            println("-1 is out of range")
        }

        if (list.size !in list.indices) {
            println("list size is out of valid list indices range,too")
        }

        // iterate over  a range

        for (i in 1..5) {
            println(x)
        }

        // 或者在一个前进上跳
        for (i in 1..10 step 2) {
            println(i)
        }

        println()

        for (i in 9 downTo 0 step 3) {
            println(i)
        }

        println("9 downTo 10???")
        // invalid statement
        for (i in 9 downTo 100) {
            println(i)
        }
    }

    /**
     * [集合](https://kotlinlang.org/docs/collections-overview.html)
     *
     * 在集合上迭代  for in
     */
    @Test
    fun collections() {
        for (i in listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)) {
            println(i)
        }

        // 检查是否包含某个对象

        if (1 in listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)) {
            println("exists")
        }
        val list = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)

        when {
            1 in list -> println("one is in the list")
            2 in list -> println("two is in the list")
        }

        val listOf = listOf("banana", "avocado", "apple", "k9wifruit")
        listOf.filter { it.startsWith("a") }.sortedBy { it }.map { it.uppercase() }.forEach { println(it) }
    }

    /**
     * 一个引用必须显式标记为nullable(当null值 存在可能时), Nullable类型名称以?结尾
     *
     * 返回null,如果str 不包含一个整数 ..
     *
     * [Null-safety](https://kotlinlang.org/docs/null-safety.html)
     */
    @Test
    fun nullableValuesAndNullChecks() {
        fun parseInt(string: String): Int? {
            return string.toIntOrNull()
        }

        // 使用此函数去返回一个可空值
        fun printProduct(arg1: String, arg2: String) {
            val x = parseInt(arg1);
            val y = parseInt(arg2);

            if(x != null && y != null) {
                println(x * y)
            }
            else {
                println("$arg1 or $arg2 are null")
            }
        }

        // 或者另外一种形式
        fun printProduct1(arg1: String, arg2: String) {
            val x = parseInt(arg1);
            val y = parseInt(arg2);

            x?.let { y?.let { x * y } ?: "$arg1 or $arg2 are null" }.also { println(it) }
        }

        printProduct("1","2")
        printProduct1("1","2")
    }

    /**
     * 类型检查 以及 自动强转
     *
     * is 操作符检查(是否一个表达式是否为某个类型的实例), 如果一个不可变的本地变量或者属性是特定类型,这样的话不需要显式的强转它 ..
     *
     * [Classes](https://kotlinlang.org/docs/classes.html) 以及 [Type casts](https://kotlinlang.org/docs/typecasts.html)
     */
    @Test
    fun typeChecksAndAutomaticCasts() {
        fun getStringLength(obj: Any): Int? {
            if(obj is String) {
                // `obj` is automatically cast to `String` in this branch
                return obj.length
            }

            // `obj` is still of type `Any` outside of the type-checked branch
            return null;
        }

        // 或者
        fun getStringLength1(obj: Any): Int? {
            if(obj !is String) return null
            // `obj` is automatically cast to `String` in this branch
            return obj.length
        }

        // 或者

        fun getStringLength2(obj: Any): Int? {
            // `obj` is automatically cast to `String` on the right-hand side of `&&`
            if (obj is String && obj.length > 0) {
                return obj.length
            }
            return null
        }
    }

}