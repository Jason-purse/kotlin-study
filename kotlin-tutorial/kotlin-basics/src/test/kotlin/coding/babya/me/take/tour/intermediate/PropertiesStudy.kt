package coding.babya.me.take.tour.intermediate

import org.junit.jupiter.api.Test
import kotlin.properties.Delegates
import kotlin.properties.Delegates.observable

/**
 * 属性学习
 *
 * 在初学者教程中 已经学习了如何使用属性去声明类实例的特征以及如何访问它们, 这个章节进一步了解Kotlin中属性如何工作已经探索在代码中使用它们的其他方式 ..
 *
 * [NullSafety]
 * @author jasonj
 * @date 2025/10/16
 * @time 16:03
 *
 * @description
 *
 **/
class PropertiesStudy {
    /**
     * 在Kotlin中, 属性默认有get / set函数, 称为属性访问器, 它们能够处理值的获取和修改, 然而这些默认的函数默认未在代码中显式显示, 编译器自动的生成它们
     * 去在访问属性的场景下管理属性访问 ..
     *
     * 这些访问器使用使用一个支持字段(backing field) 去存储实际的属性值 ..
     *
     * 支持字段存在的条件是,要么下面之一成立:
     * 1. 使用默认的get / set 函数
     * 2. 尝试通过field关键字访问属性值
     *
     * get / set 函数称为getter / setter
     *
     * 例如, category属性并没有自定义 get() / set() 函数 那么它们使用默认的实现 ..
     *
     */
    @Test
    fun backingFields() {
        class Contact(val id: Int, var email: String) {
            var category: String = ""
        }

        /**
         * 在幕后, 等价于下面的伪代码:
         *
         * 在这个示例中, get()抓取来自字段""的属性值
         * set() 函数访问value参数并将它复制给field(字段),value 在这里是""
         *
         */
        class PseudoCode_Contact(val id: Int, var email: String) {
            var category: String = ""
                get() = field
                set(value) {
                    field = value
                }
        }
    }

    /**
     * 访问支持字段是有用的,当你想要在get / set中增加一些额外的逻辑时(而不会导致无穷循环),例如你有一个具有name属性的Person类
     */
    @Test
    fun PersonBackingFields() {
        class Person {
            // var name: String = ""
            var name: String = ""
                set(value) {
                    // This causes a runtime error
                    name = value.replaceFirstChar { firstChar -> firstChar.uppercase() }
                }
        }

        /**
         * 如果你想要确保name属性的第一个字符是大写,你需要创建自定义set函数 然后使用.replaceFirstChar() 以及 uppercase() 扩展函数 ..
         *
         * 然而,如果你直接在set函数中引用属性,将会引发无穷递归,最终StackOverflowError 错误(运行时)
         */

        val person = Person()
        person.name = "kodee"
        println(person.name)
        // Exception in thread "main" java.lang.StackOverflowError
    }
    /**
     * 为了修复, 你能够使用支持字段(在set函数中)代替使用field关键字去引用它 ..
     *
     * 支持字段是非常有用的(当你想要增加日志,当一个属性值改变时发送通知,或者 使用额外的逻辑比较新老属性值)
     *
     * 有关更多信息,查看 [Backing Fields](https://kotlinlang.org/docs/properties.html#backing-fields)
     */
    @Test
    fun backingFieldUse() {
        class Person {
            var name: String = ""
                set(value) {
                    field = value.replaceFirstChar { firstChar -> firstChar.uppercase() }
                }
        }

        val person = Person()
        person.name = "kodee"
        println(person.name)
        // Kodee
    }

    /**
     * 就像扩展函数那样, 也支持扩展属性, 扩展属性允许你增加新的属性到现存的类而不会修改源代码 ..
     *
     * 然而, 扩展属性没有支持字段, 这意味着你需要自己写get / set函数, 除此之外缺失支持字段意味着它不能持有任何状态 ..
     *
     * 为了声明一个扩展属性, 写类名(你想要继承的)后跟.以及属性名, 就像普通的类属性,你需要声明属性的类型,例如:
     * ```kotlin
     *  val String.lastChar: Char
     * ```
     *
     * 扩展属性是最有用的(当你想要一个属性包含一个计算值 而不需要使用继承), 你能够认为扩展属性工作像仅单个参数的函数(参数是接受者)
     *
     * 例如, 如果你有一个数据类叫做Person 且具有两个属性 firstName / lastName
     */
    data class Person(val firstName: String, val lastName: String)

    // 你想要能够访问Person的全名 而不需要修改Person 数据类或者继承它,你能够通过创建一个具有自定义get()函数的扩展属性来实现这个功能

    // Extension property to get the full name
    // 不能声明在函数内部
    val Person.fullName: String
        get() = "$firstName $lastName"

    /**
     * 扩展属性不能覆盖一个类的已有属性
     *
     * 就像扩展函数那样,Kotlin标准库使用扩展属性(广泛的),查看 CharSequence的 [lastIndex属性](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/last-index.html)
     */
    @Test
    fun extensionProperties() {
        val person = Person(firstName = "John", lastName = "Doe")

        // Use the extension property
        println(person.fullName)
        // John Doe
    }

    /**
     * 委派属性
     *
     * 你已经在类和接口章节中学习了委派, 你能够使用委派属性去委派它们的属性访问器到另一个对象, 这事非常有用的 当你无法使用简单支持字段处理存储属性的更加
     * 复杂的需求时, 例如在数据库表中存储值, 浏览器会话或者 map ..
     *
     * 使用委派的属性能够减少大量的模版代码 因为获取和设置你属性的逻辑仅仅包含在你委派的目标对象中 ..
     *
     * 这个语法类似于使用委派类 但是运用在不同的级别上 。。
     *
     * 声明你的属性, 后跟by 关键字以及你想要委派的对象之上即可 。。
     *
     * ```kotlin
     *   val displayName: String by Delegate
     * ```
     *
     * 这里 委派的属性displayName 引用 Delegate对象(为了它的属性访问)
     *
     * 对于委派的目标对象必须有一个getValue 操作符函数, 其中Kotlin被用来抓取被代理的属性的值, 如果属性是可修改的, 它必须有一个setValue操作符函数(让
     * Kotlin 能够设置它的值)
     *
     * 默认情况下, getValue / setValue 函数有以下结构
     *
     * ```kotlin
     * operator fun getValue(thisRef: Any?, property: KProperty<*>): String {}
     *
     * operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {}
     * ```
     *
     * 在这些函数中,operator 关键字标识这些函数是操作符函数, 让它们去重载 get() / set 函数 ..
     *
     * thisRef 参数指的是 包含了被代理属性的对象,默认情况这个类是Any?, 但是你也许需要声明更加精确的类型 ..
     *
     * property 参数指的是哪个属性的值访问或者改变了, 你能够使用这个参数去访问例如属性名或者类型的信息, 默认情况下, 类型设置为KProperty<*>,
     * 但是也可以是Any?, 你不需要担心在代码中改变它 ..
     *
     * getValue函数 默认返回String 类型,但是你可以调整它 .
     *
     * setValue函数有一个额外的参数value, 它能够被用来获取分配给这个属性的新值 ..
     *
     * 因此, 事实上它如何工作, 假设你想要一个计算属性, 例如用户的显示名, 它仅仅会计算一次,因为操作是昂贵的 并且你的应用是性能敏感的, 你能够使用一个委派属性(代理属性)
     * 去缓存显示名,那样它仅仅会计算一次,但是能够多次访问没有性能影响 ..
     *
     * 首先,你需要创建去代理的对象, 在当前示例下, 对象可以是CachedStringDelegate 类的实例
     *
     * 然后cachedValue 属性包含被缓存的值, 在CachedStringDelegate类中, 增加你想要从被代理的属性的get方法到 getValue()操作符函数体的行为
     *
     * getValue 函数检查cachedValue属性是否为null, 如果是 函数分配一个"Default v alue"并且打印一个字符串(为了日志目的),如果cachedValue属性已经被计算了,
     * 那么属性不是null, 在这种情况下,打印另一个字符串(对于日志目的), 最终,函数使用Elvis 操作符去返回缓存的值 或者"Unknown" - 如果值是 null..
     *
     * 现在你能够代理你想要缓存的(val displayName)属性到CachedStringDelegate类的实例
     */
    @Test
    fun DelegatedProperties() {
        class CachedStringDelegate {
            var cachedValue: String? = null

            operator fun getValue(thisRef: Any?, property: Any?): String {
                if (cachedValue == null) {
                    cachedValue = "Default Value"
                    println("Computed and cached: $cachedValue")
                } else {
                    println("Accessed from cache: $cachedValue")
                }
                return cachedValue ?: "Unknown"
            }
        }
    }

    /**
     * 这个示例中, 在类头中包含了两个属性(User 类), firstName,lastName 以及在类体中有一个属性 displayName ..
     *
     * 委派displayName 属性到 CachedStringDelegate 类的实例 ..
     *
     * 创建一个叫做 user的User类的实例 ..
     *
     * 打印访问user实例上displayName属性的结果
     *
     * 注意到 在getValue()函数中, 对于thisRef参数的类型是从Any?类型限制到User对象类型. 这样编译器能够访问User类中的firstName,lastName属性 ..
     */
    class User(val firstName: String, val lastName: String) {
        val displayName: String by CachedStringDelegate()
    }
    class CachedStringDelegate {
        var cachedValue: String? = null

        operator fun getValue(thisRef: User, property: Any?): String {
            if (cachedValue == null) {
                cachedValue = "${thisRef.firstName} ${thisRef.lastName}"
                println("Computed and cached: $cachedValue")
            } else {
                println("Accessed from cache: $cachedValue")
            }
            return cachedValue ?: "Unknown"
        }
    }

    @Test
    fun delegatedPractice() {
        val user = User("John", "Doe")

        // First access computes and caches the value
        println(user.displayName)
        // Computed and cached: John Doe
        // John Doe

        // Subsequent accesses retrieve the value from cache
        println(user.displayName)
        // Accessed from cache: John Doe
        // John Doe


    }

    /**
     * 标准代理, Kotlin 标准库提供了一些有用的代理 这样你不需要总是从头开始创建,  如果你使用这些代理之一, 你不需要定义getValue / setValue()函数,因为标准库中自动提供了它们 ..
     *
     * 1. Lazy 属性
     *
     * 为了在第一次访问时才初始化一个属性, 使用懒属性,标准库提供了Lazy接口来进行代理 ..
     *
     * 为了创建一个Lazy接口的实例, 使用lazy()函数 并给它提供一个lambda表达式去执行(当第一次调用属性的get函数时), 后续get函数的调用总是返回相同结果(和第一次调用时)
     *
     * 懒属性使用尾随lambda 语法去传递lambda表达式 ..
     *
     *
     * 在这个示例中, Database 类具有connect / query成员函数
     *
     * connect函数打印字符串到控制台,query 函数接受一个sql 查询并返回一个列表
     *
     * databaseConnection 属性是一个懒属性
     *
     * lambda表达式提供给lazy函数, 1. 创建Database 类的实例, 在实例上调用connect成员函数,返回这个示例
     *
     * 仅当调用fetchData函数的时候,第一次访问databaseConnection,懒加载属性初始化,第二次直接返回已经初始化的属性值 ..
     *
     * 懒属性不仅在当初始化资源密集型的时候 而且当属性不在代码中使用时都有用, 除此之外,懒属性是线程安全的(默认是),
     * 当你工作在并发环境的情况下 这也是非常有用的 ...
     */
    @Test
    fun standardDelegates() {
        class Database {
            fun connect() {
                println("Connecting to the database...")
            }

            fun query(sql: String): List<String> {
                return listOf("Data1", "Data2", "Data3")
            }
        }

        val databaseConnection: Database by lazy {
            val db = Database()
            db.connect()
            db
        }

        fun fetchData() {
            val data = databaseConnection.query("SELECT * FROM data")
            println("Data: $data")
        }

        fun main() {
            // First time accessing databaseConnection
            fetchData()
            // Connecting to the database...
            // Data: [Data1, Data2, Data3]

            // Subsequent access uses the existing connection
            fetchData()
            // Data: [Data1, Data2, Data3]
        }
    }

    /**
     * 可观察的属性
     *
     * 为了监控一个属性的值改变, 使用一个观察性属性, 一个可观察属性在你想要探查属性值的改变以及使用这个信息触发一个反应/交互的情况下是非常有用的,
     * 标准库提供了Delegates 对象来进行代理 ..
     *
     * 为了创建一个可观察属性, 你必须首先导入kotlin.properties.Delegates.observable, 然后使用observable() 函数并且给它提供一个lambda表达式去执行
     * (只要属性改变的时候), 就像懒属性,可观察属性使用尾随lambda语法去传递lambda表达式 ..
     *
     * Thermostat类包含了一个可观察的属性 temperature ..
     *
     * observable函数接受整数20作为参数并使用它去初始化初始化属性.
     *
     * lambda 表达式提供给 observable() 函数:
     *  1. 有三个参数
     *  - _, 表示属性自己
     *  - old 属性的旧值
     *  - new 属性的新值
     *
     *
     *  检查新值是否大于25, 依赖于结果,最终打印字符串到控制台
     *
     *   创建一个叫做 thermostat的 Thermostat类的实例
     *
     *   更新示例的temperature属性的值到22.5,这会触发温度更新的打印语句
     *
     *   更新到27.0, 警告语句打印 ..
     *
     *   可观察属性是有用的(例如日志、调试), 你能够使用它们(例如更新UI 或者执行可选检查的情况下), 例如验证数据的有效性 ..
     *
     *   有关更多信息,查看[可观察属性](https://kotlinlang.org/docs/delegated-properties.html#observable-properties)
     */
    @Test
    fun observableProperties() {
        class Thermostat {
            var temperature: Double by observable(20.0) { _, old, new ->
                // 此lambda 表示属性发生改变之后 需要交互的动作 ..
                if (new > 25) {
                    println("Warning: Temperature is too high! ($old°C -> $new°C)")
                } else {
                    println("Temperature updated: $old°C -> $new°C")
                }
            }
        }

        val thermostat = Thermostat()
        thermostat.temperature = 22.5
        // Temperature updated: 20.0°C -> 22.5°C

        thermostat.temperature = 27.0
        // Warning: Temperature is too high! (22.5°C -> 27.0°C)
    }

    /**
     * 你在书店中管理一个清单系统, 这个清单存储在列表中(每一项标识特定书库存量)
     *
     * 例如, listOf(3,0,7,12) 意味着 商店有第一本书 有3份, 第二本书0本,第三本书7本, 第四本书 12本 ..
     *
     * 写一个函数叫做findOutOfStockBooks()  - 该函数返回所有缺货书籍的索引列表
     *
     * 下面的示例使用标准库的 [indices](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/indices.html) 扩展属性即可
     */
    @Test
    fun exercise1() {
        fun findOutOfStockBooks(inventory: List<Int>): List<Int> {
            // Write your code here
            return buildList {
                inventory.forEachIndexed {
                    index, item ->
                    if (item <= 0) {
                        add(index);
                    }
                }
            }
        }

        fun main() {
            val inventory = listOf(3, 0, 7, 0, 5)
            println(findOutOfStockBooks(inventory))
            // [1, 3]
        }
    }

    /**
     * 你有一个旅行app(需要显示距离 - 公里和英里.
     *
     * 创建一个叫做asMiles的Double 类型的扩展属性 去转换公里距离到英里 ..
     *
     * 转换千米到米的的公式是: miles = kilometers * 0.621371
     */
    val Double.asMiles: Double
        get() = this * 0.621371
    @Test
    fun exercise2() {
        // val // Write your code here
        // 匿名函数
        (fun () {
            val distanceKm = 5.0
            println("$distanceKm km is ${distanceKm.asMiles} miles")
            // 5.0 km is 3.106855 miles

            val marathonDistance = 42.195
            println("$marathonDistance km is ${marathonDistance.asMiles} miles")
            // 42.195 km is 26.218757 miles
        })()
    }

    /**
     * 你有一个系统健康指示器 能够确定/测量云系统的状态,
     *
     * 然而,这两个函数能够让你运行去执行一个健康检查(但是是性能密集型的),使用懒属性去初始化这个检查,这样昂贵的功能仅仅在需要的时候运行 .
     */
    @Test
    fun exercise3() {
        fun checkAppServer(): Boolean {
            println("Performing application server health check...")
            return true
        }

        fun checkDatabase(): Boolean {
            println("Performing database health check...")
            return false
        }

        // 委派到懒属性
        val isAppServerHealthy by lazy { checkAppServer() };
        val isDatabaseHealthy by lazy { checkDatabase() };
        when {
            isAppServerHealthy -> println("Application server is online and healthy")
            isDatabaseHealthy -> println("Database is healthy")
            else -> println("System is offline")
        }
        // Performing application server health check...
        // Application server is online and healthy
    }

    /**
     * 你能够构建一个简单的预算跟踪器应用程序 .. 这个 app 需要观察用户剩余预算的改变并且通知他们(无论何时预算低于某个阈值).
     *
     * 你有一个 Budget类 (需要使用一个totalBudget属性(包含初始的预算额)初始化),在这个类中, 创建一个可观察的属性叫做remainingBudget(进行打印?)
     *
     * 当值小于低于初始预算的20%的时候 发出警告
     *
     * 当预算相比于之前值发生了增长, 发出好消息(鼓励的, 赞助的,促进的) - encouraging ..
     */
    @Test
    fun exercise4() {

        class Budget(val totalBudget: Int) {
            // var remainingBudget: Int // Write your code here
//            var remainingBudget: Int = 0
//                set(value) {
//                    if(value < totalBudget * .2) {
//                        println("Warning: Your remaining budget ($totalBudget) is below 20% of your total budget.")
//                    }
//                    // 增长
//                    else if(field < value) {
//                        println("Good news: Your remaining budget increased to $field.")
//                    }
//                }

            var remainingBudget: Int by observable(totalBudget) { _, oldValue, newValue -> {
                if (newValue < totalBudget * 0.2) {
                    println("Warning: Your remaining budget ($newValue) is below 20% of your total budget.")
                } else if (newValue > oldValue) {
                    println("Good news: Your remaining budget increased to $newValue.")
                }
            }}
        }

        fun main() {
            val myBudget = Budget(totalBudget = 1000)
            myBudget.remainingBudget = 800
            myBudget.remainingBudget = 150
            // Warning: Your remaining budget (150) is below 20% of your total budget.
            myBudget.remainingBudget = 50
            // Warning: Your remaining budget (50) is below 20% of your total budget.
            myBudget.remainingBudget = 300
            // Good news: Your remaining budget increased to 300.
        }
    }
}