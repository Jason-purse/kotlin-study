package coding.babya.me.take.tour.intermediate

import org.junit.jupiter.api.Test

/**
 * Open 以及 特定类
 *
 * 这个章节我们学习open class, 它们如何和接口一起工作 以及 在Kotlin中类的其他特殊类型 ..
 *
 * [PropertiesStudy]
 * @author jasonj
 * @date 2025/10/16
 * @time 14:31
 *
 * @description
 *
 **/
class OpenAndSpecialClasses {
    /**
     * 如果你不能够使用接口或者抽象类,能能够显式的标记一个类是可继承的,通过open关键字声明,为了这样做,例如
     */
    @Test
    fun openClasses() {
        open class Vehicle(val make: String, val model: String)
    }

    /**
     * 为了创建一个继承于其他类的类, 增加**:**在你的类头之后后跟父类(你想要继承的)构造器调用,在这个示例中,Car 类继承于 Vehicle 类
     *
     * 就像创建其他普通类实例那样, 如果你的类继承于父类, 那么必须初始化声明在父类类头中的所有参数, 因此在这个示例中, Car类的car实例必须初始化父类
     * 参数 make,model ..
     */
    @Test
    fun openClassessTest() {
        open class Vehicle(val make: String, val model: String)
        class Car(make: String, model: String, val numberOfDoors: Int) : Vehicle(make, model)

        // Creates an instance of the Car class
        val car = Car("Toyota", "Corolla", 4)

        // Prints the details of the car
        println("Car Info: Make - ${car.make}, Model - ${car.model}, Number of doors - ${car.numberOfDoors}")
        // Car Info: Make - Toyota, Model - Corolla, Number of doors - 4
    }

    /**
     * 覆盖继承的行为,如果你想要继承于某一个类,但是需要改变一部分行为,你能够覆盖继承行为 ..
     *
     * 默认情况下,不可能覆盖一个父类的成员函数或者属性, 就像抽象类那样,你需要增加 special(或者特定) 关键字 ..
     */
    @Test
    fun overrideInheritedBehavior() {
        /**
         * 成员函数覆盖
         * 为了允许覆盖父类中的某个函数,那么必须使用open 修饰父类中的方法声明
         */
        open class A {
            open fun displayInfo() {}
        }

        /**
         * 为了覆盖继承的成员函数,使用Override关键字
         */
        class B : A() {
            override fun displayInfo() {}
        }

        open class Vehicle(val make: String, val model: String) {
            open fun displayInfo() {
                println("Vehicle Info: Make - $make, Model - $model")
            }
        }

        class Car(make: String, model: String, val numberOfDoors: Int) : Vehicle(make, model) {
            override fun displayInfo() {
                println("Car Info: Make - $make, Model - $model, Number of Doors - $numberOfDoors")
            }
        }

        val car1 = Car("Toyota", "Corolla", 4)
        val car2 = Car("Honda", "Civic", 2)

        // Uses the overridden displayInfo() function
        car1.displayInfo()
        // Car Info: Make - Toyota, Model - Corolla, Number of Doors - 4
        car2.displayInfo()
        // Car Info: Make - Honda, Model - Civic, Number of Doors - 2
    }

    /**
     * 属性覆盖
     * 在Kotlin中, 它不是一个常见情况(让属性可以可以通过open关键字变得可继承以及覆盖它）
     *
     * 大多数情况,你使用 抽象类或者接口,默认属性都是可继承的 ..
     *
     * 在open类中的属性能够被它们的子类访问, 通常情况下,更好的是直接访问它们而不是使用新属性覆盖它们 ..
     *
     * 例如,假设你有一个**transmissionType** 属性 你想要在之后覆盖, 这个覆盖属性的语法完全和覆盖方法是一样的, 你能够根据以下方式实现
     */
    @Test
    fun propertiesOverride() {
        open class Vehicle(val make: String, val model: String) {
            open val transmissionType: String = "Manual"
        }

        class Car(make: String, model: String, val numberOfDoors: Int) : Vehicle(make, model) {
            override val transmissionType: String = "Automatic"
        }
    }

    // 然而这不是一个好的示例, 相反你能够增加属性到可继承的类的构造器中 然后当你创建Car子类的时候声明它的值 ..
    /**
     * 直接访问相比于覆盖它们 能够使得代码更简单以及更好阅读 ..
     *
     * 通过声明属性在父类一次 然后通过构造器传递他们的值，你能够减少在子类中的不必要的覆盖 。。
     *
     * 有关更多类继承和覆盖类行为的信息,查看[继承](https://kotlinlang.org/docs/inheritance.html)
     */
    @Test
    fun classHeaderPropertiesAccess() {
        open class Vehicle(val make: String, val model: String, val transmissionType: String = "Manual")

        class Car(make: String, model: String, val numberOfDoors: Int) : Vehicle(make, model, "Automatic")
    }

    /**
     * 你能够创建一个继承于其他类以及实现多个接口的类, 在这种情况下, 你必须在:之后先声明父类,然后是列出接口列表(,分割)
     */
    // Define interfaces
    interface EcoFriendly {
        val emissionLevel: String
    }

    interface ElectricVehicle {
        val batteryCapacity: Double
    }

    @Test
    fun openClassesAndInterfaces() {

        // Parent class
        open class Vehicle(val make: String, val model: String)

        // Child class
        open class Car(make: String, model: String, val numberOfDoors: Int) : Vehicle(make, model)

        // New class that inherits from Car and implements two interfaces
        class ElectricCar(
            make: String, model: String, numberOfDoors: Int, val capacity: Double, val emission: String
        ) : Car(make, model, numberOfDoors), EcoFriendly, ElectricVehicle {
            override val batteryCapacity: Double = capacity
            override val emissionLevel: String = emission
        }
    }

    /**
     * 特定类, 除了抽象、open\数据类,Kotlin还有一些特定的类类型 设计于各种目的,例如限制特定行为 或者减少创建小对象时的性能影响 ..
     */
    sealed class Mammal(val name: String)
    class Cat(val catName: String) : Mammal(catName)
    class Human(val humanName: String, val job: String) : Mammal(humanName)

    fun greetMammal(mammal: Mammal): String {
        when (mammal) {
            /**
             * is 操作符将会[空安全章节](https://kotlinlang.org/docs/kotlin-tour-intermediate-null-safety.html)了解更多 ..
             */
            is Human -> return "Hello ${mammal.name}; You're working as a ${mammal.job}"
            is Cat -> return "Hello ${mammal.name}"
        }
    }

    @Test
    fun specialClasses() {

        /**
         * 密闭类, sealed class
         *
         * 有很多时候你想要限制继承性, 你能够通过密闭类实现这个事情, 密闭类是一种特定类型的抽象类, 一旦你声明一个类是密闭的,
         * 你能够仅在相同包中根据它创建子类,  无法超过这个范围去继承密闭类 ..
         *
         * 为了创建密闭类, 使用sealed 关键字
         *
         * ```kotlin
         *  sealed class Mammal
         * ```
         *
         * 密闭类是非常有用的  当和when表达式合并时非常有用, 通过使用when表达式, 你能够定义限制所有可能子类的行为, 例如
         *
         * 这里有一个密闭类叫做Mammal, 有一个name参数在构造器中 ..
         *
         * Cat 类继承于Mammal 密闭类 并使用catName参数在自己的构造器中作为来自Mammal类的name参数 ..
         *
         * Human类继承于 Mammal 密闭类 使用humanName参数在自己的构造器中 作为 Mammal 类的name参数,它也包含一个job参数在自己的构造器中 ..
         *
         * greetMammal函数接受一个Mammal 类型的参数并返回字符串 ..
         *
         * 在greetMammal 函数体中, when表达式使用 is操作符检查mammal的类型 并决定执行哪一个动作 ..
         *
         * greetMammal函数使用Cat类的实例以及叫做Snowy的name参数调用
         */

        println(greetMammal(Cat("Snowy")))
        // Hello Snowy
    }

    /**
     * 枚举类
     *
     * enum 类是很有用的,当你想要呈现一组穷举的不同值到类中 .
     *
     * 一个枚举类包含了枚举常量, 它是枚举类的自己的实例 ..
     *
     * 为了创建枚举类, 使用enum 关键字
     *
     * 使用enum 关键字创建枚举类
     */

    /**
     * 如果你想要创建包含进程状态的不同值的枚举类
     *
     * 每一个枚举产量那个必须通过,分割
     */
    enum class State {
        IDLE, RUNNING, FINISHED
    }

    /**
     * State 枚举类有三个枚举常量, 为了访问枚举常量,使用类名后跟.枚举常量名
     */
    @Test
    fun enumClasses() {
        val state = State.RUNNING

        /**
         * 你能够与when表达式和枚举使用去定义动作 根据枚举常量的值去做出抉择
         */
        val message = when (state) {
            State.IDLE -> "It's idle"
            State.RUNNING -> "It's running"
            State.FINISHED -> "It's finished"
        }
        println(message)
        // It's running
    }

    /**
     * 枚举类能够有属性和成员函数,就像普通类一样,
     *
     * 例如,如果你与HTML工作 你想要创建包含某些颜色的枚举类, 你希望每一个颜色都有一个属性,也就是说叫做 **rgb**,他们包含了16进制的RGB值,
     * 当创建枚举常量时, 你必须初始化它(通过此属性)
     *
     * kotlin 将16进制存储为整数, 因此rgb 是Int类型,不是String
     *
     * 为了给枚举类增加成员函数, 将它从枚举常量分类开,通过;号;
     */
    enum class Color(val rgb: Int) {
        RED(0xFF0000), GREEN(0x00FF00), BLUE(0x0000FF), YELLOW(0xFFFF00);

        fun containsRed() = (this.rgb and 0xFF0000 != 0)
    }

    /**
     * 这个示例中, containsRed 成员函数访问枚举常量的rgb属性, 使用this关键字并检查是否16进制值包含FF(第一个直接),并返回boolean值
     *
     * 有关更多信息,查看[EnumClasses](https://kotlinlang.org/docs/enum-classes.html)
     */
    @Test
    fun createEnum() {
        val red = Color.RED

        // Calls containsRed() function on enum constant
        println(red.containsRed())
        // true

        // Calls containsRed() function on enum constants via class names
        println(Color.BLUE.containsRed())
        // false

        println(Color.YELLOW.containsRed())
        // true
    }

    /**
     * 某些时候在你代码中, 你想要创建来自类的小对象 并短暂的使用它们 ..
     *
     * 这种方式有一定的性能影响,   内联值对象是一种特定的类型,避免性能影响, 然而他们仅包含值 ..
     *
     * 为了使用内联值对象, 使用value 关键字 并使用@JvmInline 注解
     *
     * 此注解引导Kotlin 去优化代码(当编译的时候),有关更多信息,查看[注解](https://kotlinlang.org/docs/annotations.html)
     *
     * 内联值类必须有一个在类头中初始化的属性
     *
     * 例如: 假设你想要创建一个手机邮件地址的类
     *
     * 这个地址属性必须在类头中初始化
     */
    @JvmInline
    value class Email(val address: String)

    /**
     * 当你使用内联值类的时候, 你需要使得类内联并且能够直接在代码中使用而不需要创建一个对象, 这能够极大的减少内存占用并且优化你的代码运行时性能..
     *
     * 有关内联值类型的更多信息, 查看[内联值类](https://kotlinlang.org/docs/inline-classes.html)
     */
    @Test
    fun inlineValueClasses() {
        fun sendEmail(email: Email) {
            println("Sending email to ${email.address}")
        }

        val myEmail = Email("example@example.com")
        sendEmail(myEmail)
        // Sending email to example@example.com
    }
    // sealed class // Write your code here

//    sealed class DeliveryStatus(
//        open val sender: String = "",
//        open val estimatedDeliveryDate: String = "",
//        open val recipient: String = "",
//        open val deliveryDate: String = "",
//        open val reason: String = ""
//    ) {
//        class Pending(name: String): DeliveryStatus(sender = name);
//        class InTransit(date: String): DeliveryStatus(estimatedDeliveryDate = date);
//        class Delivered(date: String,name: String): DeliveryStatus(estimatedDeliveryDate = date, sender = name);
//        class Canceled(name: String): DeliveryStatus(sender = name);
//    }

    // 密闭类中的数据类
    sealed class DeliveryStatus {
        data class Pending(val sender: String) : DeliveryStatus()
        data class InTransit(val estimatedDeliveryDate: String) : DeliveryStatus()
        data class Delivered(val deliveryDate: String, val recipient: String) : DeliveryStatus()
        data class Canceled(val reason: String) : DeliveryStatus()
    }

    fun printDeliveryStatus(status: DeliveryStatus) {
        when (status) {
            is DeliveryStatus.Pending -> {
                println("The package is pending pickup from ${status.sender}.")
            }

            is DeliveryStatus.InTransit -> {
                println("The package is in transit and expected to arrive by ${status.estimatedDeliveryDate}.")
            }

            is DeliveryStatus.Delivered -> {
                println("The package was delivered to ${status.recipient} on ${status.deliveryDate}.")
            }

            is DeliveryStatus.Canceled -> {
                println("The delivery was canceled due to: ${status.reason}.")
            }
        }
    }

    /**
     * 你管理了一个传递设备并且需要一种方式跟踪包的状态, 创建一个密闭类叫做(DeliveryStatus), 包含数据类去表示下面的状态:
     * Pending,InTransit,Delivered, Canceled, 补全DeliveryStatus 类声明,这样让代码成功运行 ..
     */
    @Test
    fun exercise1() {
        val status1: DeliveryStatus = DeliveryStatus.Pending("Alice")
        val status2: DeliveryStatus = DeliveryStatus.InTransit("2024-11-20")
        val status3: DeliveryStatus = DeliveryStatus.Delivered("2024-11-18", "Bob")
        val status4: DeliveryStatus = DeliveryStatus.Canceled("Address not found")

        printDeliveryStatus(status1)
        // The package is pending pickup from Alice.
        printDeliveryStatus(status2)
        // The package is in transit and expected to arrive by 2024-11-20.
        printDeliveryStatus(status3)
        // The package was delivered to Bob on 2024-11-18.
        printDeliveryStatus(status4)
        // The delivery was canceled due to: Address not found.
    }



    sealed class Status {
        data object Loading : Status()
        data class Error(val problem: Problem) : Status() {
            // Write your code here
            enum class Problem {
                NETWORK,TIMEOUT,UNKNOWN
            }
        }

        data class OK(val data: List<String>) : Status()
    }

    /**
     * 在这个程序中, 你想要能够处理不同状态和类型的错误 .. 你有一个密闭类去捕捉不同的状态(它们声明在数据类或者对象中),
     * 补全代码 通过创建一个叫做Problem的枚举类(它需要表示不同的问题类型, NETWORK，TIMEOUT，UNKNOWN)
     */
    @Test
    fun exercise2() {
        fun handleStatus(status: Status) {
            when (status) {
                is Status.Loading -> println("Loading...")
                is Status.OK -> println("Data received: ${status.data}")
                is Status.Error -> when (status.problem) {
                    Status.Error.Problem.NETWORK -> println("Network issue")
                    Status.Error.Problem.TIMEOUT -> println("Request timed out")
                    Status.Error.Problem.UNKNOWN -> println("Unknown error occurred")
                }
            }
        }

        val status1: Status = Status.Error(Status.Error.Problem.NETWORK)
        val status2: Status = Status.OK(listOf("Data1", "Data2"))

        handleStatus(status1)
        // Network issue
        handleStatus(status2)
        // Data received: [Data1, Data2]
    }
}