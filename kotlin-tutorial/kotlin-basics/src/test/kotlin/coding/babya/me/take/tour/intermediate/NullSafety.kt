package coding.babya.me.take.tour.intermediate

import org.junit.jupiter.api.Test

/**
 *
 * 在初学者教程中已经学习如何处理 null, 这个章节涵盖了常见使用情况(有关空安全的特性) 以及如何利用它们 ..
 *
 * [LibrariesAndAPIs]
 *
 * @author jasonj
 * @date 2025/10/17
 * @time 15:47
 *
 * @description
 *
 **/
class NullSafety {
    /**
     * Kotlin 有时能够不需要显式声明并推断类型, 当你告诉Kotlin 处理一个变量或者对象(就像属于某个特定类型的),那么这种过程叫做强转,
     * 当一个类型自动强转时, 就像它自动推断, 叫做智能强转 ..
     *
     * 1. is and !is 操作
     *
     * 在探索强转如何工作之前, 让我们看一下你如何检查一个对象是否为某个类型,例如,我们可以使用 is / !is 操作符(在when中或者if条件表达式中)
     *
     * 1. is 检查是否对象具有给定类型并且返回 boolean 值
     * 2. !is 检查是否某个对象不是特定类型并返回boolean值
     *
     * 你已经看到了如何在when条件表达式中使用 is / !is 操作符 .. [open and other special class](https://kotlinlang.org/docs/kotlin-tour-intermediate-open-special-classes.html#sealed-classes)
     */
    @Test
    fun smartCastsAndSafeCasts() {
        fun printObjectType(obj: Any) {
            when (obj) {
                is Int -> println("It's an Integer with value $obj")
                !is Double -> println("It's NOT a Double")
                else -> println("Unknown type")
            }
        }

        val myInt = 42
        val myDouble = 3.14
        val myList = listOf(1, 2, 3)

        // The type is Int
        printObjectType(myInt)
        // It's an Integer with value 42

        // The type is List, so it's NOT a Double.
        printObjectType(myList)
        // It's NOT a Double

        // The type is Double, so the else branch is triggered.
        printObjectType(myDouble)
        // Unknown type
    }

    /**
     * as 以及 as? 操作符
     *
     * 为了显式的强转一个对象到任何其他类型, 使用 as 操作符, 这包含从一个可空的类型强转到 它的非可空对应物,如果强转不可能,那么程序将会在运行时崩溃,
     * 这就是为什么叫做不安全的强转操作符 ..
     *
     * 为了显式的强转一个对象到非可控类型,但是返回null(而不是抛出一个错误 - 在失败的时候), 使用 as? 操作符, 因为它不会在失败的时候触发错误,
     * 它叫做safe操作符 ..
     */
    @Test
    fun asAndAsOptional() {
        val a: String? = null
        val b = a as String
        // Triggers an error at runtime
        println(b)
    }

    @Test
    fun asSafeOperator() {
        val a: String? = null
        val b = a as? String

        // Returns null value
        print(b)
        // null
    }

    /**
     * 你能够合并 as 操作符和 elvis 操作符 去减少多行代码到一行, 举个例子
     * calculateTotalStringLength() 函数 计算在混合列表中提供的所有字符串的所有长度 ..
     */
    @Test
    fun safeAsCombineElvisOperator() {
        fun calculateTotalStringLength(items: List<Any>): Int {

            //for (item in items) {
//                totalLength += if (item is String) {
//                    item.length
//                } else {
//                    0  // Add 0 for non-String items
//                }
            // totalLength += (item as? String)?.length ?: 0
            // }

            // return totalLength
            // 使用sumOf 扩展函数并提供了一个lambda 表达式, 对于列表中的每一项,使用安全强转操作符 as? 强转到String ..

            // 使用安全调用?. 访问length属性(如果调用不返回null ,代指前面的as?)
            // 使用elvis 操作符 ?: 去返回0,如果安全调用返回 null 值 ..
            return items.sumOf { (it as? String)?.length ?: 0 }
        }
    }

    /**
     * 空值和集合, 在Kotlin中, 处理集合经常涉及到处理空之 以及过滤不必要的元素 ..
     * Kotlin 有一些非常好用的函数能够让你写出清晰、高效并且空安全的代码, 当你和列表、Set,map 以及其他类型的集合工作时 ..
     *
     * 为了从列表中过滤出null 元素, [filterNotNull function](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/filter-not-null.html)
     */
    @Test
    fun nullValuesAndCollections() {
        val emails: List<String?> = listOf("alice@example.com", null, "bob@example.com", null, "carol@example.com")

        val validEmails = emails.filterNotNull()

        println(validEmails)
        // [alice@example.com, bob@example.com, carol@example.com]


        // 如果想要在创建列表的时候过滤null元素,使用listOfNotNull函数 ..
        val serverConfig = mapOf(
            "appConfig.json" to "App Configuration", "dbConfig.json" to "Database Configuration"
        )

        val requestedFile = "appConfig.json"
        val configFiles = listOfNotNull(serverConfig[requestedFile])

        println(configFiles)
// [App Configuration]

        // 如果所有元素都为空,那么返回空集合 ..
    }

    /**
     * 最小/最大值
     *
     * 这个示例使用?: 操作符来兜底 ..
     *
     * maxOrNull 以及 minOrNull 被设计使用在不包含null的集合中,否则你不能说此函数没有找到想要的值 或者 还是它发现了一个 null值.
     *
     */
    @Test
    fun minOrMaxInCollection() {
        // Kotlin 也提供了一些去发现集合中元素的函数, 如果值没有发现, 返回 null 而不是触发错误..

        // 1. maxOrNull 发现最大值, 如果不存在,则null
        // 2. minOrNull 发现最小值, 如果不存在,则null

        // Temperatures recorded over a week
        val temperatures = listOf(15, 18, 21, 21, 19, 17, 16)

// Find the highest temperature of the week
        val maxTemperature = temperatures.maxOrNull()
        println("Highest temperature recorded: ${maxTemperature ?: "No data"}")
// Highest temperature recorded: 21

// Find the lowest temperature of the week
        val minTemperature = temperatures.minOrNull()
        println("Lowest temperature recorded: ${minTemperature ?: "No data"}")
// Lowest temperature recorded: 15
    }

    /**
     * 你能够用singleOrNull 函数(它需要一个lambda表达式) 去发现匹配条件的单个元素, 如果不存在或者多个匹配,那么返回 null
     *
     * 此函数设计使用在不包含null值的集合中 ..
     */
    @Test
    fun singleOrNull() {
        // Temperatures recorded over a week
        val temperatures = listOf(15, 18, 21, 21, 19, 17, 16)

        // Check if there was exactly one day with 30 degrees
        val singleHotDay = temperatures.singleOrNull { it == 30 }
        println("Single hot day with 30 degrees: ${singleHotDay ?: "None"}")
        // Single hot day with 30 degrees: None
    }

    /**
     * 某些函数使用lambda表达式去转换一个集合并返回null(如果它没有满足目的时)
     *
     * 为了使用一个lambda表达式转换集合并返回第一个值(非空值),使用 firstNotNullOfOrNull 函数,
     * 如果没有这样的值存在,那么函数返回 null 值
     */
    @Test
    fun firstNotNullOfOrNull() {
        data class User(val name: String?, val age: Int?)

        val users = listOf(
            User(null, 25),
            User("Alice", null),
            User("Bob", 30)
        )

        val firstNonNullName = users.firstNotNullOfOrNull { it.name }
        println(firstNonNullName)
        // Alice
    }

    /**
     * 为了依序的处理集合元素(在lambda中) 并且创建一个累积值(或者返回null,如果集合为空), 使用reduceOrNull
     *
     * 这个示例使用?: 去返回一个可打印语句(如果函数返回null)
     *
     * 此函数设计来使用在不包含null值的集合中
     *
     * 探索Kotlin的[标准库](https://kotlinlang.org/api/core/kotlin-stdlib/) 去发现更多能够被用来是的代码更安全的函数 ..
     */
    @Test
    fun reduceOrNull() {
        // Prices of items in a shopping cart
        val itemPrices = listOf(20, 35, 15, 40, 10)

// Calculate the total price using the reduceOrNull() function
        val totalPrice = itemPrices.reduceOrNull { runningTotal, price -> runningTotal + price }
        println("Total price of items in the cart: ${totalPrice ?: "No items"}")
// Total price of items in the cart: 120

        val emptyCart = listOf<Int>()
        val emptyTotalPrice = emptyCart.reduceOrNull { runningTotal, price -> runningTotal + price }
        println("Total price of items in the empty cart: ${emptyTotalPrice ?: "No items"}")
// Total price of items in the empty cart: No items
    }

    /**
     * 更早的返回 以及 elvis 操作符
     *
     * 在初学者教程中, 我们学习了如何更早的返回从函数中停止它处理进入到某个点, 能够使用 elvis 操作符 ?: 以及更早的返回去检查某个预置条件是否成立(在某个函数中) ..
     *
     * 这种方式是一种很好的方式保持你的代码精简,因为不需要你使用内嵌的检查,  极少了你代码的复杂性并且使得它更容易维护,例如
     *
     * 尽管这个示例仅仅通过elvis操作符检查了一个条件, 但是你能够增加多个检查覆盖任何关键错误路径, elvis操作符和早期返回阻止了你的程序做不必要的工作并且
     * 让你的代码通过停止的方式更安全(只要检测到null 或者无效情况)
     *
     * 有关更多如何在代码中使用return的更多信息,查看[Returns and Jumps](https://kotlinlang.org/docs/returns.html)
     */
    @Test
    fun earlyReturnAndTheElvisOperator() {
        data class User(
            val id: Int,
            val name: String,
            // List of friend user IDs
            val friends: List<Int>
        )

        // Function to get the number of friends for a user
        // 这个写法还可以写的更简单
        fun getNumberOfFriends(users: Map<Int, User>, userId: Int): Int {
            // Retrieves the user or return -1 if not found
//            val user = users[userId] ?: return -1
            // Returns the number of friends
//            return user.friends.size

            return users[userId]?.friends?.size ?: -1;
        }

        // Creates some sample users
        val user1 = User(1, "Alice", listOf(2, 3))
        val user2 = User(2, "Bob", listOf(1))
        val user3 = User(3, "Charlie", listOf(1))

        // Creates a map of users
        val users = mapOf(1 to user1, 2 to user2, 3 to user3)

        println(getNumberOfFriends(users, 1))
        // 2
        println(getNumberOfFriends(users, 2))
        // 1
        println(getNumberOfFriends(users, 4))
        // -1
    }

    /**
     * 你正在为app开发通知系统 这样用户能够启用或者禁用不同类型的通知, 补全getNotificationPreferences函数,这样的话:
     *
     * 1. validUser 变量使用as? 操作符去检查(如果user是User类的实例), 如果不是返回空列表
     *
     * 2. userName 变量使用?: 操作符确保 用户的名称默认是"Guest",如果它为空
     *
     * 3. 最终返回语句使用 .takeIf()函数去包含邮件和SMS 通知首选项匹配条件(如果它们启用了)
     *
     * takeIf函数会返回原始值(如果给定条件成立,否则返回null)
     * ```kotlin
     * // The user is logged in
     *         val userIsLoggedIn = true
     *         // The user has an active session
     *         val hasSession = true
     *
     *         // Gives access to the dashboard if the user is logged in
     *         // and has an active session
     *         val canAccessDashboard = userIsLoggedIn.takeIf { hasSession }
     *
     *         println(canAccessDashboard ?: "Access denied")
     *         // true
     * ```
     */
    @Test
    fun exercise1() {
        data class User(val name: String?)

        fun getNotificationPreferences(user: Any, emailEnabled: Boolean, smsEnabled: Boolean): List<String> {
            val validUser =  user as? User;  // Write your code here
            val userName = validUser?.name?: "Guest"// Write your code here

                return listOfNotNull(
                /* Write your code here */
                    "Email Notifications enabled for $userName".takeIf { emailEnabled },
                    "SMS Notifications enabled for $userName".takeIf { smsEnabled }
                )
        }

        fun main() {
            val user1 = User("Alice")
            val user2 = User(null)
            val invalidUser = "NotAUser"

            println(getNotificationPreferences(user1, emailEnabled = true, smsEnabled = false))
            // [Email Notifications enabled for Alice]
            println(getNotificationPreferences(user2, emailEnabled = false, smsEnabled = true))
            // [SMS Notifications enabled for Guest]
            println(getNotificationPreferences(invalidUser, emailEnabled = true, smsEnabled = true))
            // []
        }
    }

    /**
     * 你正工作在基于发布订阅流的服务 - 用户可以有多个订阅,但是一次只能激活一个, 补全getActiveSubscription()函数, 于是我们可以使用
     * singleOrNull 函数(携带一个条件) 去返回null(如果超过一个激活的订阅)
     */
    @Test
    fun exercise2() {
        data class Subscription(val name: String, val isActive: Boolean)

        fun getActiveSubscription(subscriptions: List<Subscription>): Subscription? // Write your code here
        {
            return subscriptions.singleOrNull { it.isActive }
        }

        fun main() {
            val userWithPremiumPlan = listOf(
                Subscription("Basic Plan", false),
                Subscription("Premium Plan", true)
            )

            val userWithConflictingPlans = listOf(
                Subscription("Basic Plan", true),
                Subscription("Premium Plan", true)
            )

            println(getActiveSubscription(userWithPremiumPlan))
            // Subscription(name=Premium Plan, isActive=true)

            println(getActiveSubscription(userWithConflictingPlans))
            // null
        }
    }

    /**
     * 你正工作在社交媒体平台, 用户有用户名和账户状态 ..
     *
     * 你想要查看当前活跃的用户名列表, 不全 getActiveUsernames() 函数 这样的话 mapNotNull函数可以有一个条件 去返回活跃的用户名,否则返回null(如果条件不成立)
     *
     */
    @Test
    fun exercise3() {
        data class User(val username: String, val isActive: Boolean)

        fun getActiveUsernames(users: List<User>): List<String> {
            return users.mapNotNull { user ->
            /* Write your code here */
                // it.takeIf { it.isActive }?.username
                user.username.takeIf { user.isActive }
            }
        }

        fun main() {
            val allUsers = listOf(
                User("alice123", true),
                User("bob_the_builder", false),
                User("charlie99", true)
            )

            println(getActiveUsernames(allUsers))
            // [alice123, charlie99]
        }
    }

    /**
     * 你正在工作在电商平台的清单管理系统, 处理销售之前, 你需要检查请求产品的数量是有效的,基于可用的存储 ..
     *
     * 补全validateStock 函数 这样让它更早的返回并且使用elvis 操作符(合理的)去检查是否:
     * 1. requested 变量为空
     * 2. available 变量为空
     * 3. requested 变量是负值
     *
     * requested 变量的数目是否比available变量的数目多
     *
     * 上述所有情况,函数必须使用 -1 尽早返回 ..
     */
    @Test
    fun exercise4() {
        fun validateStock(requested: Int?, available: Int?): Int {
            // Write your code here
            requested ?: return -1;
            available ?: return -1;
            if (requested < 0) return -1
            if (requested > available) return -1

            return requested;
        }

        fun main() {
            println(validateStock(5,10))
            // 5
            println(validateStock(null,10))
            // -1
            println(validateStock(-2,10))
            // -1
        }
    }

}