package coding.babya.me.take.tour.beginner

import org.junit.jupiter.api.Test

/**
 * 在kotlin中 变量和数据结构都是类型, 类型是非常重要的,因为他们将会告诉编译器能够允许变量或者数据结构做什么, 换句话说,
 * 它有什么函数和属性。
 *
 * 在上一个章节中, Kotlin 已经能够告诉我们前面的示例**customers** 有**Int**.
 *
 * Kotlin**infer**的能力叫做类型推断,**customers** 将分配为integer 值, 从这里来说,Kotlin 推断**customers** 具有数字类型**Int**
 * 因此,编译器知道你能够使用**customers** 执行算术操作..
 * @author jasonj
 * @date 2025/10/13
 * @time 11:00
 *
 * @description
 *
 **/
class BasicTypes {
    /**
     * 顾客操作
     *
     * **(+,-,*,/,%)=** 都是增强型赋值操作符,有关更多信息 ,查看[增强型操作符](https://kotlinlang.org/docs/operator-overloading.html#augmented-assignments)
     *
     * 总体来说,Kotlin 具有以下基础类型:
     *
     * ```txt
     *   Category(分类)           Basic types(基础类型)       Example code(示例代码)
     *
     *   Integers                 Byte,Short,Int,Long       val year: Int = 2020
     *
     *   Unsigned integers        UByte,UShort,UInt,ULong   val  score: UInt = 100u
     *
     *   Floating-point numbers   Float,Double              val currentTemp: Float = 24.5f,val price: Double = 19.99
     *
     *   Booleans                 Boolean                   val isEnabled: Boolean = true
     *
     *   Characters               Char                      val separator: Char = ','
     *
     *   Strings                  String                    val message: String = "Hello,world!"
     * ```
     * 有关更多基本信息以及它们的属性,查看[基础类型](https://kotlinlang.org/docs/basic-types.html)
     *
     * 通过此知识,你能够声明变量并在后续初始化它, Kotlin 能够管理此变量(只要变量在第一次读取之前初始化它)
     */
    @Test
    fun customerOperations() {
        var customers = 10

        // Some customers leave the queue
        customers = 8

        customers = customers + 3 // Example of addition: 11
        customers += 7            // Example of addition: 18
        customers -= 3            // Example of subtraction: 15
        customers *= 2            // Example of multiplication: 30
        customers /= 3            // Example of division: 10

        println(customers) // 10
    }

    /**
     * 声明变量但不初始化它, 通过**:** 指定变量类型,例如
     *
     */
    @Test
    fun  declareVariableWithoutInitialized() {
        // 声明 但是不初始化
       val d: Int;
        // 初始化
       d = 3;
        // 显式声明类型和初始化
        val e: String = "hello";

        // 变量能够读取 因为它们已经初始化了 ..
        println(d);
        println(e);
    }

    /**
     * 在读取之前没有初始化变量,则将会看到静态编译错误
     */
    @Test
    fun  dontInitializeVariableBeforeRead() {
        val d: Int;
        // Variable 'd' must be initialized
        // println(d)
    }

    /**
     * 联系, 显式声明每一个变量的正确类型
     *
     * 下一个部分是[集合](https://kotlinlang.org/docs/kotlin-tour-collections.html)
     */
    @Test
    fun exercise() {
        val a: Int = 1000
        val b: String = "log message"
        val c: Double = 3.14
        val d: Long = 100_000_000_000_000
        val e: Boolean = false
        val f: Char = '\n'
    }
}