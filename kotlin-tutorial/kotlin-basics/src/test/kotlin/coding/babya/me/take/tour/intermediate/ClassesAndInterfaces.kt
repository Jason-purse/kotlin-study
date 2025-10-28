package coding.babya.me.take.tour.intermediate

import org.junit.jupiter.api.Test

/**
 * 类和接口 ..
 *
 * 在初级教程中,学习了如何使用了类以及数据类去存储数据以及维护能够在代码中共享的集合特征. 本质上, 你想要创建一个体系去高效的在项目中共享代码 ..
 * 这个章节解释了Kotlin 提供为了共享代码的选项 以及它们如何让你的代码更安全以及更容易维护 ..
 *
 * [ObjectsStudy]
 *
 * @author jasonj
 * @date 2025/10/15
 * @time 16:02
 *
 * @description
 *
 **/
class ClassesAndInterfaces {
    /**
     * 前面的章节中, 我们学习了如何使用扩展函数去扩展类 而不需要修改原始的源代码, 但是如果某些事情复杂 但是在类之间共享代码可用, 在这些情况下,
     * 你能够使用类继承 ..
     *
     * 默认情况下 Kotlin中的类是无法继承的, Kotlin 设计的这种方式去阻止不期望的继承 以及使得你的类更容易维护 ..
     *
     * Kotlin 类仅支持单继承, 意味着它一次只能从一个类上继承, 这种类叫做父类(parent) ..
     *
     * (parent)从一个类继承于另一个类(grandparent), 形成一个体系, 在Kotlin的类体系顶部都有一个公共的父类(Any), 所有的类最终都继承于Any 类 ..
     *
     * Any -> Vehicle -> Car(从高到低)
     *
     * Any类提供了toString()函数作为成员函数 .. 因此你能够在任何类中使用继承的函数, 例如
     *
     * 如果你想要使用继承去在类之间共享某些代码, 首先考虑抽象类 ..
     */
    @Test
    fun classInheritance() {
        class Car(val make: String, val model: String, val numberOfDoors: Int);

        val car1 = Car("Toyota", "Corolla", 4)

        // Uses the .toString() function via string templates to print class properties
        println("Car1: make=${car1.make}, model=${car1.model}, numberOfDoors=${car1.numberOfDoors}")
        // Car1: make=Toyota, model=Corolla, numberOfDoors=4
    }

    /**
     * 抽象类
     *
     * 抽象类默认支持继承, 抽象类的目的就是提供被其他类继承或者实现的成员, 因此,它们有一个构造器, 但是你不能从抽象类创建实例,在子类中,
     * 你能够定义父类的属性和函数的行为(函数使用override关键字), 在这种方式中, 你能够说子类覆盖了父类的成员 ..
     *
     * > 当你定义了一个任何继承的函数或者属性的行为,我们叫做实现
     *
     * 抽象类能够同时包含函数以及属性(实现的) 以及 没有被实现的函数和属性, 称为抽象函数和属性 ..
     *
     * 为了创建一个抽象类, 使用abstract 关键字 ..
     *
     */
    @Test
    fun abstractClass() {
        abstract class Animal {
            // 为了声明一个抽象函数或者属性, 你也能够使用abstract 关键字 ..
            abstract fun makeSound();
            abstract val sound: String;
        }

        // 例如假设你想要创建一个叫做Product的抽象类,这样你能够创建子类去定义 完全不同的产品分类 ..
        /**
         * 在这个抽象类中
         *
         * 1. 构造器有两个属性(是产品的名称和价格)
         * 2. 抽象属性包含了产品的分类(类型是String)
         * 3. 这个函数打印有关产品的信息
         */
        abstract class Product(val name: String, var price: Double) {
            // Abstract property for the product category
            abstract val category: String

            // A function that can be shared by all products
            fun productInfo(): String {
                return "Product: $name, Category: $category, Price: $price"
            }
        }

        // 首先创建一个有关于电器的子类, 在你在子类定义category属性的实现之前, 你必须使用override 关键字
        /**
         * 在Electronic 类中
         *
         * 1. 继承于Product 抽象类
         * 2. 在构造器中有额外的参数`warranty`, 这是特定于电器的 ..
         * 3. 覆盖category属性去包含字符串 "Electronic"
         */
        class Electronic(name: String, price: Double, val warranty: Int) : Product(name, price) {
            override val category = "Electronic"
        }

        // 现在你能够使用这些类(像下面这样)
        // Creates an instance of the Electronic class
        val laptop = Electronic(name = "Laptop", price = 1000.0, warranty = 2)

        println(laptop.productInfo())
        // Product: Laptop, Category: Electronic, Price: 1000.0

        // 尽管抽象类能够以这种方式非常好的共享代码, 他们是有限制的(因为Kotlin中的类仅支持单继承), 如果你需要从多个来源继承,考虑使用接口 ..
    }

    /**
     * 接口类似于类, 但是有一些不同
     *
     * 1. 你不能创建接口的实例, 它们没有构造器或者类头
     * 2. 它们的函数以及属性隐式的默认可继承, 在Kotlin中, 我们说它们叫做"open",开放
     * 3. 你不需要标记它们的函数为abstract 如果你不需要定义它们的实现(也就是隐式抽象) ..
     *
     * 类似于抽象类, 你使用接口去定义一组功能和属性(能够被类继承以及之后实现), 这种方式帮助你集中在由接口描述的抽象,而不是特定的实现细节 ..
     *
     * 使用接口会让你的代码:
     * 1. 更模块化, 因为它隔离了不同部分, 允许他们独立的演化 ..
     * 2. 更容易理解(通过分组相关的函数到一个紧密结合的集合)
     * 3. 更容易测试, 因为你能够快速的交换实现(使用mock) 为了测试
     *
     * 为了声明一个接口,使用interface 关键字
     *
     * ```kotlin
     *  interface PaymentMethod
     * ```
     */
    @Test
    fun interfaces() {

    }
    interface PaymentMethod{
        // Functions are inheritable by default
        fun initiatePayment(amount: Double): String
    }

    /**
     * 接口支持多继承 也就是说一个类可以实现多个接口(一次), 首先让我们考虑一个场景(这里一个类实现一个接口)
     *
     * 为了创建一个实现一个接口的类, 在类头之后增加colon(冒号), 后跟接口名(你想要实现的), 你不需要在接口名之后使用() 因为接口没有构造器 ..
     *
     */
    @Test
    fun interfaceImplementation() {
//        class CreditCardPayment : PaymentMethod {
//            override fun initiatePayment(amount: Double): String {
//                TODO("Not yet implemented")
//            }
//        }


        class CreditCardPayment(val cardNumber: String, val cardHolderName: String, val expiryDate: String) : PaymentMethod {
            override fun initiatePayment(amount: Double): String {
                // Simulate processing payment with credit card
                return "Payment of $$amount initiated using Credit Card ending in ${cardNumber.takeLast(4)}."
            }
        }

        val paymentMethod = CreditCardPayment("1234 5678 9012 3456", "John Doe", "12/25")
        println(paymentMethod.initiatePayment(100.0))
        // Payment of $100.0 initiated using Credit Card ending in 3456.

    }
    interface PaymentType {
        val paymentType: String
    }
    /**
     * 为了创建实现多个接口的类, 在类头后面增加: 后跟接口名称列表(你想要实现的接口,通过逗号分隔)
     *
     * 这个示例中,PaymentMethod 是一个具有抽象方法 initiatePayment定义的接口
     *
     * PaymentType 是一个接口(paymentType 属性没有初始化的)
     *
     * CreditCardPayment 是一个实现了PaymentMethod , PaymentType 接口的类
     *
     * CreditCardPayment 类重写了继承的initiatePayment()函数以及 paymentType 属性 ..
     *
     * paymentMethod 是CreditCardPayment 类的实例 ..
     *
     * 覆盖的 initiatePayment()函数 在paymentMethod实例上使用100.0的参数调用 ..
     *
     * 覆盖的paymentType 属性在paymentMethod实例上进行访问 ..
     *
     * 有关接口和接口继承相关的更多信息,查看[接口](https://kotlinlang.org/docs/interfaces.html)
     */
    @Test
    fun multipleInterfaceInheritance() {
        class CreditCardPayment(val cardNumber: String, val cardHolderName: String, val expiryDate: String) : PaymentMethod,
            PaymentType {
            override fun initiatePayment(amount: Double): String {
                // Simulate processing payment with credit card
                return "Payment of $$amount initiated using Credit Card ending in ${cardNumber.takeLast(4)}."
            }

            override val paymentType: String = "Credit Card"
        }

        val paymentMethod = CreditCardPayment("1234 5678 9012 3456", "John Doe", "12/25")
        println(paymentMethod.initiatePayment(100.0))
        // Payment of $100.0 initiated using Credit Card ending in 3456.

        println("Payment is by ${paymentMethod.paymentType}")
        // Payment is by Credit Card


    }
    interface DrawingTool {
        val color: String
        fun draw(shape: String)
        fun erase(area: String)
        fun getToolInfo(): String
    }
    /**
     * 委托
     *
     * 接口是非常有用的,但是如果你的接口包含了大量的函数,它的子类最终可能包含大量的模版代码,如果你仅仅想要覆写类行为的一小部分, 你将存在大量的重复工作 ..
     *
     * 模版代码是一块可以重用的代码块, 在软件项目的多个部分中稍稍或者没有修改的情况下即可重用
     *
     * 例如,假设你有一个叫做DrawingTool(包含大量函数和一个叫做color属性的接口)
     *
     * 你创建了一个叫做PenTool的类 它实现了DrawingTool 接口并且提供了所有成员的实现
     */
    @Test
    fun delegation() {
        class PenTool : DrawingTool {
            override val color: String = "black"

            override fun draw(shape: String) {
                println("Drawing $shape using a pen in $color")
            }

            override fun erase(area: String) {
                println("Erasing $area with pen tool")
            }

            override fun getToolInfo(): String {
                return "PenTool(color=$color)"
            }
        }
    }

    /**
     * 你想要创建一个类似于PenTool的类(具有相同行为,但仅仅是color 值不同) ..
     * 一种方式是创建新类 接收一个实现DrawingTool 接口作为参数的对象, 例如 PenTool 类实例, 然后在类的内部,你能够覆盖Color 属性 ..
     * 但是在这种情况下,你需要为DrawingTool接口的每一个成员增加实现 ..
     *
     */
    @Test
    fun delegationBY() {
        class CanvasSession(val tool: DrawingTool) : DrawingTool {
            override val color: String = "blue"

            override fun draw(shape: String) {
                tool.draw(shape)
            }

            override fun erase(area: String) {
                tool.erase(area)
            }

            override fun getToolInfo(): String {
                return tool.getToolInfo()
            }
        }
    }

    /**
     *  如果你包含了DrawingTool接口的大量成员函数, 那么模版代码的数量(在CanvasSession)中将会变得更多,然而,这是可选的 。。
     *  在Kotlin中,你能够委派接口实现到一个类实例,通过 by 关键字
     *
     *  这里 tool 是PenTool 类实例的名称(成员函数实现所委派的对象)
     *
     *  注意到你不需要增加成员函数的实现到CanvasSession中, 编译器会自动的委派到PenTool class.
     *
     *  这会节约大量编写模版代码的时间, 相反,你仅仅增加你想要在子类进行更改行为的代码即可 ..
     * 例如, 如果我想要改变color属性的值
     *
     * 如果你想要,你也能够在CanvasSession类中覆盖继承的成员函数的行为, 但是现在你不需要为每一个继承的成员函数增加新代码.
     *
     * 有关更多信息,查看[委派](https://kotlinlang.org/docs/delegation.html)
     */
    @Test
    fun delegateBy() {
         // class CanvasSession(val tool: DrawingTool) : DrawingTool by tool
        class CanvasSession(val tool: DrawingTool) : DrawingTool by tool {
            override val color: String = "blue"
        }
    }

    /**
     * 想想你工作在一个智能家庭系统, 一个智能的桌面通常具有不同类型的设备(但是它们全都有一些基础特性 但是也有独一无二的行为).
     * 在下面的代码示例中, 完善 abstract 类(SmartDevice) 这样子类 SmartLight 能够成功编译 ..
     *
     * 然后创建另一个子类叫做 SmartThermostat 它从SmartDevice 继承 并且实现 turnOn / turnOff 函数(返回打印语句  描述  thermostat(恒温器) 是
     * 加热还是关闭), 最终增加另一个函数叫做 adjustTemperature() 接收一个温度估测值作为输入 并且打印 "$name thermostat set to $temperature °C"
     */
    @Test
    fun exercise1() {
        // abstract class // Write your code here
        abstract class SmartDevice(val name: String) {
            abstract fun turnOn();

            abstract fun turnOff();
        }

        open class SmartLight(name: String) : SmartDevice(name) {
            override fun turnOn() {
                println("$name is now ON.")
            }

            override fun turnOff() {
                println("$name is now OFF.")
            }

            fun adjustBrightness(level: Int) {
                println("Adjusting $name brightness to $level%.")
            }
        }

        // class SmartThermostat // Write your code here
        class SmartThermostat(name: String): SmartDevice(name)  {
            override fun turnOn() {
                println("$name thermostat is now heating.")
            }

            override fun turnOff() {
                println("$name thermostat is now off.")
            }

            fun adjustTemperature(temperature: Int) {
                println("$name thermostat set to $temperature °C")
            }
        }

        val livingRoomLight = SmartLight("Living Room Light")
        val bedroomThermostat = SmartThermostat("Bedroom Thermostat")

        livingRoomLight.turnOn()
        // Living Room Light is now ON.
        livingRoomLight.adjustBrightness(10)
        // Adjusting Living Room Light brightness to 10%.
        livingRoomLight.turnOff()
        // Living Room Light is now OFF.

        bedroomThermostat.turnOn()
        // Bedroom Thermostat thermostat is now heating.
        bedroomThermostat.adjustTemperature(5)
        // Bedroom Thermostat thermostat set to 5°C.
        bedroomThermostat.turnOff()
        // Bedroom Thermostat thermostat is now off.
    }

    /**
     * 创建一个叫做Media的接口 让你能够用来实现特定的媒体类(例如 Audio, Video,Podcast),你的接口必须包含:
     *
     * 1. title 属性 来表示媒体的标题
     * 2. play() 函数被用来播放媒体
     *
     * 然后,创建一个叫做Audio实现Media 接口的类(在它的构造器中必须使用title 属性 以及额外的叫做String类型的composer属性)
     *
     * 在这个类中, 实现play() 函数去打印以下内容: "Playing audio,$title,composed by $composer";
     */
    interface Media {
        val title: String;
        fun play();
    }
    @Test
    fun exercise2() {
        // interface // Write your code here

        // class // Write your code here
        // You can use the override keyword in class headers to implement a property from an interface in the constructor.
        class Audio(override val title: String,val composer: String): Media {
            override fun play() {
                println("Playing audio,$title,composed by $composer")
            }
        }
        val audio = Audio("Symphony No. 5", "Beethoven")
        audio.play()
        // Playing audio: Symphony No. 5, composed by Beethoven
    }

    /**
     * 你正在构建一个支付处理系统(为一个电子商务(commerce 商业/贸易)应用),每一个支付方法需要能够去授权一种支付并处理一个交易, 某些支付也需要能够处理退款 ..
     *
     * 1. 在Refundable 接口中, 增加一个叫做`refund()` 的接口去处理退款
     * 2. 在PaymentMethod 抽象类中
     *  1. 增加一个叫做 authorize()的函数(它需要一个金额 并打印包含金额/数量的消息
     *  2. 增加一个抽象函数叫做processPayment - 它也需要一个数量(账目)
     *
     * 3. 创建一个类叫做CreditCard 实现Refundable 接口 以及 PaymentMethod抽象类 ..
     * 在这个类中, 增加一个refund/ processPayment的实现  这样他们打印下面的语句:
     * 1. "Refunding $amount to the credit card .."
     * 2. "Processing credit card payment of $amount."
     */
    interface Refundable {
        // Write your code here
        // 退款接口
        fun refund(amount: Double);
    }
    @Test
    fun exercise3() {
        abstract class PaymentMethod(val name: String) {
            // Write your code here
            fun authorize(amount: Double) {
                println("Authorizing payment of $amount.")
            }
            abstract fun processPayment(amount: Double);
        }

        // class CreditCard // Write your code here
        class CreditCard(type: String): Refundable,PaymentMethod(type) {
            override fun refund(amount: Double) {
                println("Refunding $amount to the credit card ..")
            }

            override fun processPayment(amount: Double) {
                println("Processing credit card payment of $amount.")
            }
        }

        val visa = CreditCard("Visa")

        visa.authorize(100.0)
        // Authorizing payment of $100.0.
        visa.processPayment(100.0)
        // Processing credit card payment of $100.0.
        visa.refund(50.0)
        // Refunding $50.0 to the credit card.
    }

    /**
     * 你有一个简单的消息app(具有某些基本的功能, 但是你想要增加某些能力 - 为了智能消息 而不需要极大的重复代码 ..
     *
     * 在下面的代码中, 定义一个叫做SmartMessenger的类 它继承于Messenger接口  但是委派实现到BasicMessenger类的实例
     *
     * 在SmartMessenger类中, 覆写sendMessage 函数去发送smart 消息, 这个函数必须接受message 作为输入 并返回一个可打印的语句"Sending a smart message: $message"
     *
     * 除此之外,调用来自BasicMessenger的sendMessage()函数 并为消息添加**[smart]**前缀 ..
     *
     * 你不需要重写SmartMessenger类中的receiveMessage函数 ...
     */
    interface Messenger {
        fun sendMessage(message: String)
        fun receiveMessage(): String
    }
    @Test
    fun exercise4() {


        class BasicMessenger : Messenger {
            override fun sendMessage(message: String) {
                println("Sending message: $message")
            }

            override fun receiveMessage(): String {
                return "You've got a new message!"
            }
        }

        // class SmartMessenger // Write your code here
        class SmartMessenger(val basicMessenger: BasicMessenger): Messenger by basicMessenger {
            override fun sendMessage(message: String) {
                println("Sending a smart message: $message");
                basicMessenger.sendMessage("[smart] $message")
            }
        }

        fun main() {
            val basicMessenger = BasicMessenger()
            val smartMessenger = SmartMessenger(basicMessenger)

            basicMessenger.sendMessage("Hello!")
            // Sending message: Hello!
            println(smartMessenger.receiveMessage())
            // You've got a new message!
            smartMessenger.sendMessage("Hello from SmartMessenger!")
            // Sending a smart message: Hello from SmartMessenger!
            // Sending message: [smart] Hello from SmartMessenger!
        }
    }
}