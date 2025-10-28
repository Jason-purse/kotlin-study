package coding.babya.me.basic

//import com.google.gson.Gson
//import com.google.gson.JsonElement
//import com.google.gson.JsonPrimitive
import java.io.File
import java.math.BigDecimal
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import kotlin.collections.iterator

/**
 *
 * [coding.babya.me.learningmaterials.AdventOfCodePuzzles]  代码难题
 *
 *
 *
 *
 *
 * pojos / pocos
 *
 * 数据类
 *
 * 这个类有以下的方法
 * getter / setter(对所有的var 变量包含这些getter/setter方法)
 * 包含 equals() 方法 / hashCode方法 / toString() 方法 / copy方法
 * component1() , component2()方法, .. 对于所有属性(查看[数据类](查看https://kotlinlang.org/docs/data-classes.html))
 */
data class Customer(val name: String, val email: String) {

}

/**
 * data class 支持解构
 */
fun destructDataClassObj() {
    val (a,b) = Customer("1","b")
}


/**
 * 对于函数的参数可以设置默认值
 */
fun foo(a: Int = 0,b: String = "") {
    println("$a $b")
}

/**
 * 过滤一个列表
 */
fun filter(list: Iterable<String>) {
    // 针对特定的类型,比较可以直接进行,比较 是很智能的
    println(list.filter { x -> x >= "d" })
}

/**
 * 或者可以更短
 */
fun filter(list: IntRange) {
    println(list.filter { it >= 5 })
}


/**
 * [学习java 和 Kotlin的过滤之间的不同](https://kotlinlang.org/docs/java-to-kotlin-idioms-strings.html#create-a-string-from-collection-items)
 *
 * 检查在集合中的元素是否出现 in操作符 或者 !in
 */
fun inTest() {
    var emailsList = listOf("apple","js","java","john@example.com")
    var array = Array<String>(10) { it -> "1".repeat(it)};
    if ("john@example.com" in emailsList) {
        println("a") }

    if ("jane@example.com" !in emailsList) {
        println("b") }

    println(array.forEach { it -> println(it) })
}

/**
 * 字符串插值
 *
 * 了解[java 和 kotlin之间的字符串连接的不同](https://kotlinlang.org/docs/java-to-kotlin-idioms-strings.html#concatenate-strings)
 */
fun strConcat() {
    val a = "12"
    println("expression is $a")
}

/**
 * 有关更多信息,查看[读取标准输入](https://kotlinlang.org/docs/read-standard-input.html)
 */
fun readStandardInputSafety() {
    // 读取一个字符串 并且返回null(如果输入不能转换到一个Int值)
    val wrongInt = readln().toIntOrNull()
    println(wrongInt)

    // 读取一个字符串能够转换到一个 整型否则返回null
    val correctInt = readln().toIntOrNull()
    println(correctInt)


}

/**
 * 实例检查
 */
class Foo
class Bar
fun instanceCheck() {
    val x: Any = 10
    when (x) {
        is Foo -> println("is foo")
        is Bar -> println("is bar")
        is Int -> println("is int")
        else   -> println(" is default")
    }
}

/**
 * 只读列表
 */
fun readOnlyList() {
    val list = listOf("a","b","c")
}

/**
 * 只读map
 */
fun readOnlyMap() {
    val map = mapOf("a" to 1, "b" to 2, "c" to 3)
}


/**
 * 访问一个map值
 */
fun mapAccess() {
    // 访问可变的map
    var map = mutableMapOf("a" to 1, "b" to 2, "c" to 3)
    println(map["a"])
    println("update")
    map["a"] = 3
    println(map["a"])


    // 遍历一个map
    for ((key, value) in map) {
        println("key: $key,value: $value")
    }
}


/**
 * 范围迭代
 */
fun iterateToRange() {
    val a = 0 .. 9
    val b = 10 until 99
    // 新语法,等价于until
    val c = 10 ..< 99;
    println(a.last) // 查看最后一个
    println(b.last) // 查看最后一个  意味着 util 是一个右开区间

    /*for (x in 2..10 step 2) { ... }
    for (x in 10 downTo 1) { ... }
    if (x in 1..10) { ... }*/

}

/**
 * 懒惰属性
 */
fun lazyFunction() {
    val p: String by lazy {
        return@lazy "1"
    }

    println(p)
}

//函数扩展

fun String.spaceToCamelCase(): String {
    return toString().replace(Regex("_(.)?")) { println(it.groupValues[1]); it.groupValues[1].uppercase()}
}

/**
 * 创建一个单例
 *
 * 对象声明
 */
object Resource {
    var name = "Name"
}

/**
 * 对于类型安全的值 使用内联值类
 *
 * 仅仅对于jvm 后端才需要@JvmInline
 */
@JvmInline
value class EmployeeId(private val id: String);

@JvmInline
value class CustomerId(private val id: String);


/**
 * 实例化一个抽象类
 */
abstract class MyAbstractClass {
    abstract fun doSomething();
    abstract fun sleep();
}

fun doUseAbstractClass() {
    val myObject = object : MyAbstractClass() {
        override fun doSomething() {
            // TODO("Not yet implemented")
        }

        override fun sleep() {
            TODO("Not yet implemented")
        }
    }

    myObject.doSomething();
}

fun  ifNotNullShorthand() {
    // 非空简写形式
    val files = File("Test").listFiles()
    println(files?.size)
}

fun ifNotNullElseShortHand() {
    // ?:


    fun getSomeSize() = 123;

    val files = File("Test").listFiles()

// For simple fallback values:
    println(files?.size ?: "empty") // if files is null, this prints "empty"

// To calculate a more complicated fallback value in a code block, use `run`
    val filesSize = files?.size ?: run {
        val someSize = getSomeSize()
        someSize * 2
    }
    println(filesSize)
}

fun ifExpressionIsNullExecute() {
    val values = mapOf("email" to 2, "address" to 3)
    val email = values["email"] ?: throw IllegalStateException("Email is missing")
}

// 集合可为无元素集合
/**
 * [java and kotlin first item getting](https://kotlinlang.org/docs/java-to-kotlin-collections-guide.html#get-the-first-and-the-last-items-of-a-possibly-empty-collection)
 */
fun getCollectionFirstItem() {
    val emails = listOf(1,2,3,4,5);
    val mainEmail = emails.firstOrNull() ?: "";
}

/**
 * 如果不为空则执行
 */
fun ifNotNullExecute() {
    val value = 123;
    value?.let {
        // execute this block if not null
    }
}

fun ifNotNullMapToNullableValue() {
    val value: Int? = 1231;
    fun transformValue(data: Int): Int? {
        return if(data % 2 == 0) data * 2 else null;
    }
    val mapped = value?.let{ transformValue(it) } ?: "defaultValue";
}


/**
 * when 语句返回值
 */
fun transform(color: String): Int {
    return when (color) {
        "Red" -> 0
        "Green" -> 1
        "Blue" -> 2
        else -> throw IllegalArgumentException("Invalid color param value")
    }
}

/**
 * try-catch 表达式(也可以返回一个东西)
 */
fun capture() {
    val result = try {
        // throw NullPointerException("百度一下")
        // 最后一行作为返回值
        123;
    }catch (ex: Exception) {
        // pass
        println(ex.message)
    }

    println(result)
}

/**
 * if 条件表达式的特殊使用,这对比java 确实上了一个的档次
 */
fun foo(param: Int): Boolean{
    return if(param == 1) true else if(param == 2) false else true
}

fun ifExpression() {
    val x = 123;
    val y = if(x== 1) 1 else if(x == 2) 2 else 3
}

/**
 * 返回单元的方法的构建器式使用
 */
fun arrayOfMinusOnes(size: Int): IntArray {
    // apply一个单元的使用方式
    return IntArray(size).apply {
        // 此方法只执行一次
        fill(1)
    }
}



// 单个表达式函数
fun theAnswer() = 42 // 直接返回数据

// 上面这个函数等价于
fun theAnswer1(): Int {
    return 42
}


/**
 * 等价于 java switch 但是功能更加强大
 */
fun theAnswer2(a: Any) = when(a) {
    is Int -> println("a is $a , type is Int")
    !is Int -> println("b is $a , type is Int")
    3 -> println("woo!,is three")
    in listOf(3,4,5) -> println("is contains item")
    !in setOf(3,4,5) -> println("is not contains item")
    else -> 123
}

/**
 * with
 * 在一个对象上调用多个方法
 */

class Turtle {
    var a =1;
    fun penDown() {
        println("下降")}
    fun penUp() {
        println("上升")}
    fun turn(degrees: Double) {
        println("升级")}
    fun forward(pixels: Double) {
        println("转发")}
}

fun withCall() {
    Turtle().apply {
        penDown();
        for (i in 1..4) {
            forward(100.0)
            turn(90.0)
        }
        penUp()
    }

    // 等价于with
    with(Turtle()) {
        penDown();
        for (i in 1..4) {
            forward(100.0)
            turn(90.0)
        }
        penUp()
    }
}


/**
 * 配置一个对象的属性
 * val myRectangle = Rectangle().apply {
 *   length = 4
 *   breadth = 5
 *   color = 0xFAFAFA
 *   }
 */

fun config() {
    val turtle = Turtle()
    println(turtle.a)
    // apply 单元方法 可以用来执行属性赋值
    // 例如在创建对象时构造器中并没有设置属性的时候,可以通过方法设置属性!
    turtle.apply {
        a = 3
    }
    println(turtle.a)
}

/**
 * Java 7's try-with-resources
 *
 * use 表达式
 */
fun tryWith() {
    val stream = Files.newInputStream(Paths.get("/some/file.txt"))
    // 我认为 lambda表达式格式为 {...xx -> xxxx}
    // use 进行了try_with_resources
    stream.buffered().reader().use { reader ->
        println(reader.readText())
    }
}


/**
 * 需要泛型信息的泛型函数  函数扩展
 * inline fun <reified T: Any> Gson.fromJson(json: JsonElement): T = this.fromJson(json, T::class.java)
 */
//inline fun <reified T: Any> Gson.fromJson(json: JsonElement): T = this.fromJson(json, T::class.java)


/**
 * 可能为空的Boolean
 */
fun booleanFuncNull() {
    val b: Boolean? = null
    if (b == true) {
        println("真好,boolean: $b 不为空!")
    } else {
        // `b` is false or null
        println("不好,boolean: $b 为空")
    }
}


/**
 * 交换两个值
 */
fun swap() {
    var a = 1
    var b = 2
    // also
    a = b.also {
        b = a
    }

    println(" a: $a, b: $b")
}

/**
 * 标记代码为未完成的
 * TODO
 * kotlin 有一个标准的库拥有一个 TODO()函数,总会抛出一个NotImplementError.
 * 它的返回值是Nothing 因此它能够被使用,不管类型是什么,也能够重载去访问一个原因参数！
 *
 * IDEA kotlin插件会理解TODO的语义,并且在TODO 工具窗口增加代码指向!
 */

fun calcTaxes(): BigDecimal = TODO("Waiting for feedback from accounting")




fun main() {
    var customer = Customer("xiao fan","smileboy@foxmail.com")
    println(customer.email)

    filter(listOf("a","b","c","d"))

    filter(0..20)

    inTest()

    strConcat()

    instanceCheck()

    /**
     * read only list
     */
    val a = listOf("a","b","c");

    val b = mapOf("a" to "c","b" to "d","c" to "e")
    println(b.values)
    println(b.entries)

    mapAccess()

    iterateToRange()

    lazyFunction()

    println("abc_defg".spaceToCamelCase())

    // 单例使用
    println(Resource.name)
    Resource.name = "123"
    println(Resource.name)

    // 实例化一个抽象类
    // 比较特殊 让它等于一个单例 类型为MyAbstractClass
    val instance = object : MyAbstractClass() {
        override fun doSomething() {
            println("do Something!")
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun sleep() {
            println("sleep")
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }


    }

    println(instance.doSomething())

    // groovy 借鉴的非空表达式

    val ee = "123";

    println(ee ?: "456")

    /**
     * val files = File("Test").listFiles()
     *
     *   println(files?.size ?: "empty") // if files is null, this prints "empty"
     */
    //     如果为空,执行一个语句
    val values = mutableMapOf("a" to 1,"b" to 2,"email" to 3)
    val email = values["email"] ?: throw IllegalStateException("Email is missing!")
    // 如果出错,那么直接报错!
    println(email)

    // 使用判空表达式进行条件执行
    val emptyList: List<Any> = emptyList<Int>();
    println(emptyList.firstOrNull() ?: "判空表达式生效了!")

    // 如果不为空执行
    emptyList?.let {
            it ->
        println("当前参数不为空,可以执行某些东西!")
    }

    val mutableMapOf = mutableMapOf("a" to 1, "b" to 2, "b" to 3)
    println(mutableMapOf?.let { it ->
        it["a"] ?: "default value"
    })
    println(mutableMapOf?.let { it ->
        it["c"] ?: "default value"
    })

    // 捕获异常
    capture()

    println(foo(2))

    // 内容转换为字符串!
    println(arrayOfMinusOnes(3).contentToString())

    println(theAnswer2(2))

    val myTurtle = Turtle()
    with(myTurtle) { //draw a 100 pix square
        penDown() // 调用对象方法,这里面属于this
        for (i in 1..4) {
            forward(100.0)
            turn(90.0)
        }
        penUp()
    }

    config()

//    tryWith()

//    val gson = Gson()
//    println(gson.fromJson<String>(JsonPrimitive("1232342sldfkjsaldf")))

    booleanFuncNull()

    swap()

    calcTaxes()
}

