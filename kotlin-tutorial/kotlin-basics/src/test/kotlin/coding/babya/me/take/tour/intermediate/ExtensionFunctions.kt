package coding.babya.me.take.tour.intermediate

import org.junit.jupiter.api.Test
import java.net.http.HttpResponse

/**
 *
 * 这种函数让代码更加简洁并且更具有可读性, 学习如何使用高效设计模式使得项目去到下一个级别 ..
 *
 * [ScopeFunctions]
 * @author jasonj
 * @date 2025/10/13
 * @time 10:11
 *
 * @description
 *
 **/
class ExtensionFunctions {
    /**
     * 软件开发中, 你经常需要修改程序的行为而无需改变原始的源代码 .. 你能够调用扩展函数(和调用类的成员函数的方式那样),使用. 函数名 ..
     *
     * 在介绍扩展函数的完整语法之前,你需要理解接受者是?(receiver), 接受者说的是函数在哪个对象上调用, 换句话说, 接受者是信息共享的位置或者与谁共享 ..
     *
     * 下面示例中, first函数在readOnlyShapes 变量上调用, 所以readOnlyShapes 变量是接受者 ..
     *
     * 为了创建一个扩展函数, 写类的名称(你想要扩展的类名后跟.并增加函数的名称), 继续函数声明的剩余部分, 包括它的参数和返回类型..
     */
    @Test
    fun introduction() {
        val readOnlyShapes = listOf("triangle","square","circle");
        println("The first item in the list is ${readOnlyShapes.first()}")

        // readOnlyShapes 是receiver
    }

    /**
     * 扩展函数声明
     *
     * 1. String 是扩展类
     * 2. bold 是扩展函数的名称
     * 3. .bold() 扩展函数的返回类型是String
     * 4. "hello" 是String的实例,接受者
     * 5. 接受者是在函数体中通过关键字this进行访问的 ..
     * 6. 字符串模版被用来访问this的值 ..
     * 7. bold扩展函数需要一个字符串并且返回它在html <b> 元素内进行文本加粗 ..
     */
    @Test
    fun extensionFunctionDeclare() {
        fun String.bold(): String {
            return "<b>$this</b>"
        }

        println("hello".bold())
    }

    /**
     * 面向扩展的设计
     *
     * 你能够定义扩展函数(在任何位置),这让你能够创建面向扩展的设计, 这种设计独立于核心功能(且有用但非必要的特性),使得代码更容易阅读和维护。
     *
     * 一个好的示例是[HttpClient](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client/-http-client/index.html?_gl=1*6z8ucn*_gcl_au*MTE5NjgzNDg1MC4xNzYwMDc5Njg3*_ga*MTU1OTIwMzMxLjE3NDE0MzgxOTc.*_ga_9J976DJZ68*czE3NjA0OTI0ODQkbzEyJGcxJHQxNzYwNDk1NjEyJGo2MCRsMCRoMA..)
     * 此类来自于 Ktor库, 这帮助去执行网络请求, 它的功能核心是单个功能**request()**, 它需要一个http请求的所有信息 ..
     *
     * 事实上,大多数热门http请求是 get或者 post请求,这是有意义的 让库能够 提供更短的名字(来适配常用使用情况), 然而这不需要写新的网络代码,
     * 仅仅需要一个特定的请求调用, 换句话说,他们是完美的候选 - 通过定义独立的.get() / .post()扩展函数 ..
     */
    @Test
    fun extensionOrientedDesign() {
        class HttpClient {
            fun request(method: String, url: String, headers: Map<String, String>): Any {
                // Network code
                return Any();
            }
        }

        /**
         * get 和Post 请求是 HttpClient的扩展, 它们能够直接的使用来自Httpclient类的request函数,因为它们调用在HttpClient类的实例作为接受者 ..
         *
         * 你能够使用这些扩展函数去调用 request()函数(使用合适的 http方法), 这能够简化你的代码并且使得它更容易去理解 ..
         */
        fun HttpClient.get(url: String): Any = request("GET",url,emptyMap())
        fun HttpClient.post(url: String): Any = request("POST", url, emptyMap())
    }

    /**
     * 简化代码
     *
     * 面向扩展的方式广泛的使用在Kotlin的[标准库](https://kotlinlang.org/api/latest/jvm/stdlib/)中 以及其他库,举个例子,
     * String类具有大量的扩展函数帮你更好的string工作 ..
     *
     * 有关扩展函数的更多信息,查看[扩展](https://kotlinlang.org/docs/extensions.html)
     */
    @Test
    fun simpleExtensionCode() {
        class HttpClient {
            fun request(method: String, url: String, headers: Map<String, String>): Any {
                println("Requesting $method to $url with headers: $headers")
                return String("Response from $url".toByteArray())
            }
        }

        fun HttpClient.get(url: String): Any = request("GET", url, emptyMap())

        fun main() {
            val client = HttpClient()

            // Making a GET request using request() directly
            val getResponseWithMember = client.request("GET", "https://example.com", emptyMap())

            // Making a GET request using the get() extension function
            // The client instance is the receiver
            val getResponseWithExtension = client.get("https://example.com")
        }
    }

    /**
     * 写一个扩展函数叫做 isPositive 它需要一个整数 以及检查是否它是正数还是负数 ..
     */
    @Test
    fun exercise1() {
        fun Int.isPositive() = this > 0;
        println(1.isPositive())
    }

    /**
     * 编写一个扩展函数叫做 toLowercaseString() 需要一个字符串并且返回小写版本 ..
     */
    @Test
    fun exercise2() {
        fun String.toLowercaseString(): String {
            return this.lowercase();
        }

        println("Hello World!".toLowercaseString())
    }

}