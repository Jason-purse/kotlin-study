package coding.babya.me.take.tour.beginner

import org.junit.jupiter.api.Test

/**
 * 空安全
 * @author jasonj
 * @date 2025/10/15
 * @time 09:45
 *
 *
 * @description
 *
 **/
class NullSafety {

    /**
     * 在Kotlin, 它可能有null 值, Kotlin 使用 null(当某些事情缺失或者还没有设置的情况下).
     *
     * 你已经看到了某些Kotlin的示例返回 null值(在集合章节) - 当你尝试访问一个key-value对(通过一个不存在于map中的key).
     *
     * 尽管在这种方式下使用Null 是很有用的, 但是如果你的代码没有准备好 处理它们可能会遇到一些问题 ..
     *
     * 为了帮助去阻止在程序中null值的问题, Kotlin 设置了空安全性, 空安全在编译阶段检测和null值 相关的一些潜在问题, 而不是运行时 ..
     *
     * Null 安全是一组特性允许你:
     * 1. 显式声明何时null在程序中允许
     * 2. 对null值进行检查
     * 3. 使用安全调用也许包含null值的函数或者属性
     * 4. 如果null值检测到,声明被采取的动作
     */
    @Test
    fun nullSafetyFeatures() {
        // Kotlin 支持可空类型  这允许一种可能性使得声明的类型能够具有null值, 默认情况下, 类型是不允许接受 null值的, 可空类型必须显式的
        // 在类型声明之后添加?

        var neverNull: String = "This can't be null"
        // 将会抛出异常
        // neverNull = null;

        // 强制确定不为空(但其实可能为空,所以不要乱用)
        neverNull = mapOf(1 to "2").get(3)!!;
        println(neverNull)
    }

    @Test
    fun nullType() {
        // nullable has nullable String type
        var nullable: String? = "You can keep a null here"

        // This is OK
        nullable = null

        // By default, null values aren't accepted
        var inferredNonNull = "The compiler assumes non-nullable"

        // Throws a compiler error
        // inferredNonNull = null

        fun strLength(notNull: String): Int {
            return notNull.length
        }

        println(strLength("128"))
        // throw compiler error
        // println(strLength(null))
    }

    /**
     * 你能够检查是否存在Null值(通过条件表达式), 在下面的示例中, describeString() 函数有一个**if** 表达式 - 检查是否maybeString 是非空的并且
     * 如果length 是大于0的 ..
     */
    @Test
    fun checkForNull() {
        fun describeString(maybeString: String?): String {
            if(maybeString != null && maybeString.length > 0) {
                return "String of length ${maybeString.length}"
            } else {
                return "Empty or null string"
            }
        }

        val nullString: String? = null;
        println(describeString(nullString))

        // Empty or null string
    }

    /**
     * 为了安全调用一个对象的属性(例如对象可能是null), 使用安全调用?., 这是一个安全操作符,如果对象或者它访问的属性是null则返回null, 这是非常有用的
     * 当你想要避免null值的出现在代码中触发错误.
     */
    @Test
    fun safeCall() {
        fun lengthString(maybeString: String?): Int? = maybeString?.length
        val nullString: String? = null;
        println(lengthString(nullString))
    }

    /**
     * 链式安全调用一个包含null值对象的属性时, 它返回null而不会抛出任何错误,例如
     * ```kotlin
     *  person.company?.address?.country
     * ```
     *
     * 安全调用操作符能够被用来安全调用一个扩展或者成员函数. 在这种情况下在函数调用之前会执行一个空检查,如果检查发现了一个空值,那么调用将会跳过并且null返回 ..
     *
     * 在下面的示例中, nullString 是null,因此**.uppercase()** 的调用将会跳过并返回null ..
     */
    @Test
    fun safeCallChainProperty() {
        (null as String?)?.uppercase();
        (null as? String?)?.uppercase();
    }

    /**
     * 猫王操作符, 如果不存在则是否默认值 ..
     *
     * 假设一个变量的值是null 则返回默认值,通过?: 实现 ..
     *
     * 操作符左边应该被用来检查是否有null值, 右手边应该返回null时候应该返回什么 ..
     *
     * 有关空安全的更多信息,查看[空安全](https://kotlinlang.org/docs/null-safety.html)
     */
    @Test
    fun elvisOperator() {
        val nullString: String? = null;
        println(nullString?.length ?: 0)
    }

    /**
     * 如果你有一个employeeById函数能够让你访问一个公司的员工数据库, 不幸的是这个函数返回的是Employee?类型, 所以结果可能为null,
     * 你的目的是写一个函数去返回员工的薪资,当提供员工id的情况下时, 或者0(如果员工从数据库中缺失时)
     */
    @Test
    fun exercise() {
        data class Employee (val name: String, var salary: Int)

        fun employeeById(id: Int) = when(id) {
            1 -> Employee("Mary", 20)
            2 -> null
            3 -> Employee("John", 21)
            4 -> Employee("Ann", 23)
            else -> null
        }

        // fun salaryById(id: Int) = // Write your code here
        fun salaryById(id: Int) = employeeById(id)?.salary ?: 0;

        fun main() {
            println((1..5).sumOf { id -> salaryById(id) })
        }
    }
}