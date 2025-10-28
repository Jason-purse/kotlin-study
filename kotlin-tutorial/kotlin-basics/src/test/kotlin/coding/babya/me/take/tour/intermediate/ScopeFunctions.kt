package coding.babya.me.take.tour.intermediate

import org.junit.jupiter.api.Test

/**
 * 作用域函数
 *
 * 具有接受者的Lambda 表达式 [LambdaExpressionWithReceiver]
 * @author jasonj
 * @date 2025/10/15
 * @time 11:06
 *
 * @description
 *
 **/
class ScopeFunctions {

    /**
     * 理解扩展函数并学习如何使用作用域函数写出更加自然的代码
     *
     * 在程序语言中,scope 是一个区域,用于识别变量或者对象,最常见的范围指的是 全局范围 以及本地范围 ..(global,local)
     *
     * 1. Global  scope  表示在程序任何地方都能够访问一个变量或者对象
     * 2. Locale scope  表示在块或者函数内所定义中仅可访问的变量或者对象
     *
     * Kotlin中, scope函数允许你在一个对象附近创建临时作用域并执行某些代码 ..
     *
     * 范围函数使得你的代码更加简洁 因为它不需要你在临时范围内引用对象的名称, 依赖于作用域函数,你能够访问对象(要么通过 this关键字 或者通过 it(关键字) 引用它作为一个参数 ..
     *
     * Kotlin 有5个作用域函数, `let`,`apply`,`run`,`also`,`with` ..
     *
     * 每一个范围函数需要一个lambda表达式并且返回当前对象或者lambda表达式的结果, 在这个教程中,将解释每一个范围函数以及如何使用它 ..
     *
     * 你能够查看 [Back to the Stdlib: Making the Most of Kotlin's Standard Library](https://youtu.be/DdvgvSHrN9g?feature=shared&t=1511)
     * 谈论作用域函数 ..
     *
     *
     * let 作用域函数让你想要在代码中执行空检查的时候 以及在返回的对象上执行后续动作是有用的 ..
     */
    @Test
    fun let() {
        fun sendNotification(recipientAddress: String): String {
            println("Yo $recipientAddress!")
            return "Notification sent!"
        }

        fun getNextAddress(): String {
            return "sebastian@jetbrains.com"
        }

        // 这个示例有两个函数, sendNotification(), 它有一个recipientAddress 函数参数并且返回字符串
        // getNextAddress 它没有函数参数并且返回字符串

        // 它创建了一个变量 address 且是可空String, 但是遇到了一个问题,当调用sendNotification函数的时候,因为
        // 函数不期待 address 是一个null 值,编译器因此为报告一个错误
        // val address: String? = getNextAddress()
        // sendNotification(address)

        // 从初级教程中可以知道,我们可以通过if 或者elvis 操作符来进行空检查
        // 但是如果我后续我想要在代码中使用返回的对象呢?
        // 你能够通过if 以及 else 分支实现

        val address: String? = getNextAddress()
        var confirm = if(address != null) {
            sendNotification(address)
        } else { null }

        // 但是有一种更加方便的方式,那就是使用Let 作用域函数

        confirm = address?.let(::sendNotification);
        confirm = address?.let{ sendNotification(it) }
        confirm = address?.let{ t ->
            sendNotification(t)
        }

        // 这个示例使用了let 作用域函数进行安全调用, 通过it 引用address变量(使用临时范围)
        // 通过这种方式能够处理可能为null的 address变量 ..
        // 你能够在后续代码中confirm
    }

    /**
     * 使用apply作用域函数去初始化对象, 例如类实例， 在创建而不是之后, 这种方式使得你的代码更容易阅读和管理
     */
    @Test
    fun  apply() {
        class Client() {
            var token: String? = null
            fun connect() = println("connected!")
            fun authenticate() = println("authenticated!")
            fun getData(): String = "Mock data"
        }

        val client = Client()

        fun main() {
            client.token = "asdf"
            client.connect()
            // connected!
            client.authenticate()
            // authenticated!
            client.getData()
        }

        // 在这个示例中 Client 包含了一个叫做token的属性但是有三个成员函数,connect / authenticate / getData

        // 示例创建 client 作为Client类的实例 在初始化token属性之前 并且在main函数中调用它的成员函数 ..

        // 尽管这个示例看起来已经很紧凑了,但是在真实世界中，尽管你能够在创建之后 在你能够配置和使用类实例(包括成员函数)之前需要一段时间,
        // 然而如果你使用apply作用域函数,你能够直接在代码的相同位置（作用域)上创建、配置以及使用成员函数 ..

    }

    @Test
    fun applyUse() {
        class Client() {
            var token: String? = null
            fun connect() = println("connected!")
            fun authenticate() = println("authenticated!")
            fun getData(): String = "Mock data"
        }

        // 也就说明了 apply 使用this
        val client = Client().apply {
            token = "asdf"
            connect()
            authenticate()
        }

        fun main() {
            client.getData()
            // connected!
            // authenticated!
        }
    }

    /**
     * 类似于apply,你能够使用run 范围初始化一个对象,但是更棒的是它能够在特定时候初始化对象并且立即计算一个结果 ..
     *
     * 让我们继续之前的apply 函数示例, 但是此时, 你想要connect() 以及 authenticate() 函数能够分组 这样它们能够在每个请求上调用 ..
     */
    @Test
    fun run() {
        class Client() {
            var token: String? = null
            fun connect() = println("connected!")
            fun authenticate() = println("authenticated!")
            fun getData(): String = "Mock data"
        }
        val client: Client = Client().apply {
            token = "asdf"
        }

        fun main() {
            val result: String = client.run {
                connect()
                // connected!
                authenticate()
                // authenticated!
                // 结果将作为返回值 返回给 result
                getData()
            }
        }
    }

    /**
     * also 同样能够使用一个对象完成额外的动作 然后返回对象继续在代码中使用,就像写一个日志
     *
     * 有用的是在函数调用之间增加某些日志 去查看到medals变量发现了什么, also 函数能够去做类似的事情:
     */
    @Test
    fun also() {
        val medals: List<String> = listOf("Gold", "Silver", "Bronze")
        val reversedLongUppercaseMedals: List<String> =
            medals
                // 通过 it 关键字并调用uppercase() 扩展函数并返回
                .map { it.uppercase() }
                // 同样是 it 检查
                .filter { it.length > 4 }
                .reversed()
        println(reversedLongUppercaseMedals)
        // [BRONZE, SILVER]
    }

    // also 其实就是 使用this 并无返回值 ..
    /**
     * 1. 在medals 变量上使用also 范围函数
     * 2. 在also 范围函数中创建了一个临时范围,因此不需要显式引用medals 变量 - 当使用 it 作为函数参数时 ..
     * 3. 传递一个lambda表达式到also 作用域函数调用println()  - 通过`it` 关键字使用medals 变量
     *
     * 自从also 函数可以返回对象的时候,它不仅仅用于日志 而是可以调试,链式多个操作, 并且执行其他副作用操作(但是不影响你代码的主要流程) ..
     */
    @Test
    fun alsoDoSomething() {
        val medals: List<String> = listOf("Gold", "Silver", "Bronze")
        val reversedLongUppercaseMedals: List<String> =
            medals
                .map { it.uppercase() }
                .also { println(it) }
                // [GOLD, SILVER, BRONZE]
                .filter { it.length > 4 }
                .also { println(it) }
                // [SILVER, BRONZE]
                .reversed()
        println(reversedLongUppercaseMedals)
        // [BRONZE, SILVER]
    }

    /**
     * with 不像其他范围函数,with 并不是一个扩展函数,因此语法有所不同, 你能够传递接受者给with作为一个参数 ..
     *
     * 使用with 范围函数 - 当你想要在一个对象上调用多个函数时 ..
     *
     * 考虑这个示例:
     *
     */
    @Test
    fun with() {
        class Canvas {
            fun rect(x: Int, y: Int, w: Int, h: Int): Unit = println("$x, $y, $w, $h")
            fun circ(x: Int, y: Int, rad: Int): Unit = println("$x, $y, $rad")
            fun text(x: Int, y: Int, str: String): Unit = println("$x, $y, $str")
        }

        val mainMonitorPrimaryBufferBackedCanvas = Canvas()

        mainMonitorPrimaryBufferBackedCanvas.text(10, 10, "Foo")
        mainMonitorPrimaryBufferBackedCanvas.rect(20, 30, 100, 50)
        mainMonitorPrimaryBufferBackedCanvas.circ(40, 60, 25)
        mainMonitorPrimaryBufferBackedCanvas.text(15, 45, "Hello")
        mainMonitorPrimaryBufferBackedCanvas.rect(70, 80, 150, 100)
        mainMonitorPrimaryBufferBackedCanvas.circ(90, 110, 40)
        mainMonitorPrimaryBufferBackedCanvas.text(35, 55, "World")
        mainMonitorPrimaryBufferBackedCanvas.rect(120, 140, 200, 75)
        mainMonitorPrimaryBufferBackedCanvas.circ(160, 180, 55)
        mainMonitorPrimaryBufferBackedCanvas.text(50, 70, "Kotlin");

        // 这个示例 创建了Canvas(此类具有三个成员函数,`react()`,`cir()`,`text()` ..
        // 这些成员函数打印一个语句(根据提供的函数参数结构化打印)
        // 示例 创建 mainMonitorPrimaryBufferBackedCanvas 作为Canvas的实例 在通过不同函数参数调用一系列成员函数之前
        // 你能够看到这个代码难以阅读, 如果你有with函数,这个代码可以流水线化为:
        with(mainMonitorPrimaryBufferBackedCanvas) {
            text(10, 10, "Foo")
            rect(20, 30, 100, 50)
            circ(40, 60, 25)
            text(15, 45, "Hello")
            rect(70, 80, 150, 100)
            circ(90, 110, 40)
            text(35, 55, "World")
            rect(120, 140, 200, 75)
            circ(160, 180, 55)
            text(50, 70, "Kotlin")
        }

        // 但是它同样可以被apply 替换 ..
        mainMonitorPrimaryBufferBackedCanvas.apply {
            text(10, 10, "Foo")
            rect(20, 30, 100, 50)
            circ(40, 60, 25)
            text(15, 45, "Hello")
            rect(70, 80, 150, 100)
            circ(90, 110, 40)
            text(35, 55, "World")
            rect(120, 140, 200, 75)
            circ(160, 180, 55)
            text(50, 70, "Kotlin")
        }

        // 又或者是 run

        mainMonitorPrimaryBufferBackedCanvas.run {
            text(10, 10, "Foo")
            rect(20, 30, 100, 50)
            circ(40, 60, 25)
            text(15, 45, "Hello")
            rect(70, 80, 150, 100)
            circ(90, 110, 40)
            text(35, 55, "World")
            rect(120, 140, 200, 75)
            circ(160, 180, 55)
            text(50, 70, "Kotlin")
        }
    }

    /**
     * 使用情况概览
     *
     * 这部分覆盖了Kotlin中可用的不同范围函数 以及它们主要的使用情况(为了让你的代码更加自然),你能够将这个表作为快速参考,重要的是不需要完整理解这些函数如何工作 - 为了
     * 在代码中使用它们 ..
     * # Kotlin Scope Functions Summary
     *
     * | Function       | Access to         | Return value      | Use case      |
     * |-----------------|-----------------|---------------------|-----------------|
     * | **let** | `it` | Lambda result | Perform null checks in your code and later perform further actions with the returned object. |
     * | **apply** | `this` | `x` | Initialize objects at the time of creation. |
     * | **run** | `this` | Lambda result | Initialize objects at the time of creation AND compute a result. |
     * | **also** | `it` | `x` | Complete additional actions before returning the object. |
     * | **with** | `this` | Lambda result | Call multiple functions on an object. |
     *
     * @see [作用域函数](https://kotlinlang.org/docs/scope-functions.html)
     */
    @Test
    fun useCaseOverview() {
        // 上面的意思是 let 会返回lambda 表达式的结果
        // apply 会返回this 对象
        // run 会返回 lambda 表达式的结果
        // also 返回 this对象
        // with 返回lambda 表达式结果

        println("1".let {
            return@let 123;
        })

        println("1".apply { return@apply })

        println("1".run { return@run "2" })

        println("1".also { return@also })

        println(with(1) {
            return@with 123;
        })
    }

    /**
     * 重写`.getPriceInEuros()` 函数作为单个表达式函数 去使用安全调用操作符 以及 let 作用域函数
     */
    @Test
    fun exercise1() {
        data class ProductInfo(val priceInDollars: Double?)

        class Product {
            fun getProductInfo(): ProductInfo? {
                return ProductInfo(100.0)
            }
        }

        fun convertToEuros(dollars: Double): Double {
            return dollars * 0.85
        }
        // Rewrite this function
//        fun Product.getPriceInEuros(): Double? {
//            val info = getProductInfo()
//            if (info == null) return null
//            val price = info.priceInDollars
//            if (price == null) return null
//            return convertToEuros(price)
//        }

        fun Product.getPriceInEuros() = getProductInfo()?.priceInDollars?.let { convertToEuros(it) }

        val product = Product()
        val priceInEuros = product.getPriceInEuros()

        if (priceInEuros != null) {
            println("Price in Euros: €$priceInEuros")
            // Price in Euros: €85.0
        } else {
            println("Price information is not available.")
        }
    }

    /**
     * 你有一个updateEmail函数 去更新用户的邮箱地址, 使用`apply` 作用域函数去更新邮箱地址 然后使用also 作用域函数打印日志消息,
     * Updating email for user with ID: ${it.id}
     */
    @Test
    fun exercise2() {
        data class User(val id: Int, var email: String)

        // fun updateEmail(user: User, newEmail: String): User = // Write your code here
        fun updateEmail(user: User, newEmail: String): User = user.apply { email = newEmail }
            .also { println("Updating email for user with ID: ${it.id}") }

        val user = User(1, "old_email@example.com")
        val updatedUser = updateEmail(user, "new_email@example.com")
        // Updating email for user with ID: 1

        println("Updated User: $updatedUser")
        // Updated User: User(id=1, email=new_email@example.com)
    }
}