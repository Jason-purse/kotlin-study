package coding.babya.me.take.tour.intermediate

import org.junit.jupiter.api.Test

/**
 * 当前章节,我们通过探索对象声明 更深的理解类 ..
 *
 * 这个知识将会帮助我们高效的管理项目之间的行为 ..
 *
 * [OpenAndSpecialClasses]
 * @author jasonj
 * @date 2025/10/16
 * @time 11:24
 *
 * @description
 *
 **/
class ObjectsStudy {
    /**
     * 对象声明,在kotlin中,你能够使用对象声明去使用单例 声明一个类, 在某种意义上, 你能够同时声明一个类并创建单个实例, 对象声明是有用的(当你想要创建
     * 一个类去用作为单个参考点(在你的程序中), 或者协调系统之间的行为 ..
     *
     * 如果仅有一个实例的类(很容易访问的实例) 叫做单例 ..
     *
     * 对象在Kotlin中是懒的, 意味着仅当访问它们的时候才会创建, Kotlin 同样确保所有对象是以线程安全的方式创建,因此你不需要手动检查 ..
     *
     * 为了创建一个对象声明, 使用 object 关键字
     *
     * 后跟在Object 一个名字, 在对象体中(由花括号中)增加任何属性和成员函数 ..
     *
     * 对象声明没有构造器,因此它们没有想像类一样有类头 ..
     *
     * 此对象有一个成员函数叫做 takeParams 能够接受一个username 以及 password 变量作为参数并打印字符串到控制台 ..
     *
     * 此对象仅当第一次调用它的函数时才会创建 ..
     */
    object DoAuth {
        fun takeParams(username: String, password: String) {
            println("input Auth parameters = $username:$password")
        }
    }

    /**
     * 例如, 假设你创建了一个叫做DoAuth的对象 相对于认证来说是合理的 ..
     */
    @Test
    fun objectDeclarations() {
        DoAuth.takeParams("coding_ninja","NlnjaC0ding!")
    }
    interface  Auth {
        fun takeParams(username: String, password: String);
    }
    /**
     * 对象能够继承接口和类, 例如
     */
    object DoAuth1 : Auth {
        override fun takeParams(username: String, password: String) {
            println("input Auth parameters = $username:$password")
        }
    }

    /**
     * 数据对象
     *
     * 为了更容易打印出对象声明的内容, Kotlin 有数据对象,类似于数据类,  你能够在初学者教程中了解更多, 数据对象自动的鞋带了额外的成员函数,toString(),equals
     *
     * > 不像数据类,  数据对象并不会自动的携带copy成员函数,因为它们仅单个实例(无法被复制)
     *
     * 为了创建一个数据对象,使用和对象声明相同的语法,但是前缀添加 data 关键字 ..
     *
     * 有关更多信息查看 [DataObjects](https://kotlinlang.org/docs/object-declarations.html#data-objects)
     */
    data object AppConfig {
        var appName: String = "My Application"
        var version: String = "1.0.0"
    }
    @Test
    fun dataObject() {
        println(AppConfig)
        // AppConfig

        println(AppConfig.appName)
        // My Application
    }

    /**
     * 伴生对象
     *
     * 在Kotlin中,一个类能够有一个对象, 那就是伴生对象,  有且仅有一个伴生对象, 一个伴生对象仅当类第一次引用的时候才会创建
     *
     * 任何在伴生对象内部创建的属性和函数都会在所有类的实例中共享 ..
     *
     * 为了创建伴生对象(在类中), 使用和对象声明相同语法,但是使用companion 关键字前缀 修饰 ..
     *
     * ```kotlin
     *  companion object Bonger {}
     * ```
     *
     * > 一个伴生对象并没有名字,如果你不定义一个,默认是Companion ..
     *
     * 为了访问伴生对象的属性或者函数,引用类名,例如
     */
    class BigBen {
        // 可以定义名字,也可以不定义
        // companion object {
            companion object Bonger {
            fun getBongs(nTimes: Int) {
                repeat(nTimes) {
                    println("BONG ")
                }
            }
        }
    }

    /**
     * 这个示例创建BigBen类(包含了一个叫做Bonger的伴生对象).  伴生对象有一个成员函数叫做`getBongs()` 接收一个整数 并且打印"BONG" 到控制台(具体根据
     * 重复几次的整数控制)
     *
     * getBongs() 函数能够根据类名进行调用,伴生对象将会在第一次访问时创建, getBongs()函数使用12进行调用
     *
     * 有关伴生对象,[查看更多](https://kotlinlang.org/docs/object-declarations.html#companion-objects)
     */
    @Test
    fun  companionObjects() {
        // Companion object is created when the class is referenced for the
        // first time.
        BigBen.getBongs(12)
        // BONG BONG BONG BONG BONG BONG BONG BONG BONG BONG BONG BONG
    }
    interface Order {
        val orderId: String
        val customerName: String
        val orderTotal: Double
    }

    data object OrderOne: Order {
        override val orderId = "001"
        override val customerName = "Alice"
        override val orderTotal = 15.50
    }
    data object OrderTwo: Order {
        override val orderId = "002"
        override val customerName = "Bob"
        override val orderTotal = 12.75
    }
    /**
     * 你开了一个咖啡店 并且有一个系统去跟踪顾客的订单 .. 考虑下面的代码并补全第二个数据对象的声明(这样的话程序正确运行)
     */
    @Test
    fun practice1() {

        // data object // Write your code here


        // Print the name of each data object
        println("Order name: $OrderOne")
        // Order name: OrderOne
        println("Order name: $OrderTwo")
        // Order name: OrderTwo

        // Check if the orders are identical
        println("Are the two orders identical? ${OrderOne == OrderTwo}")
        // Are the two orders identical? false

        if (OrderOne == OrderTwo) {
            println("The orders are identical.")
        } else {
            println("The orders are unique.")
            // The orders are unique.
        }

        println("Do the orders have the same customer name? ${OrderOne.customerName == OrderTwo.customerName}")
        // Do the orders have the same customer name? false
    }
    interface Vehicle {
        val name: String;
        fun move(): String;
    }
    object FlyingSkateboard: Vehicle {
        override val name: String
            get() = "Flying Skateboard"

        override fun move(): String {
            return "Glides through the air with a hover engine"
        }

        fun fly()  = "Woooooooo"
    }
    /**
     * 创建一个从Vehicle 接口继承的对象声明 去创建一个唯一的轮胎类型 FlyingSkateboard, 实现name 属性并且move()函数在对象声明中,这样让代码 正确运行 ..
     */
    @Test
    fun practice2() {
        println("${FlyingSkateboard.name}: ${FlyingSkateboard.move()}")
        // Flying Skateboard: Glides through the air with a hover engine
        println("${FlyingSkateboard.name}: ${FlyingSkateboard.fly()}")
        // Flying Skateboard: Woooooooo
    }
}