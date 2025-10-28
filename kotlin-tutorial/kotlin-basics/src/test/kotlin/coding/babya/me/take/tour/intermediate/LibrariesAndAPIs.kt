package coding.babya.me.take.tour.intermediate

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.junit.jupiter.api.Test
import kotlin.math.log
import kotlin.math.pow
import kotlin.system.measureTimeMillis
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.measureTime

/**
 *
 * 要充分利用Kotlin, 请使用现有的库和API,这样您就可以将更多的时间花在编码上, 而减少重新发明轮子的时间 ..
 *
 * 库发布可重用的代码(简化大多数任务), 在库中, 它们是分组相关类、函数、工具的相关包和对象. 库暴露了API (应用程序接口) 是一组函数、类或者属性（开发者能够在代码中使用)
 *
 * 库:
 *
 * Package API, Package[API,Object API]
 *
 * 探索Kotlin的无限可能
 *
 * @author jasonj
 * @date 2025/10/22
 * @time 14:09
 *
 * @description
 *
 **/
class LibrariesAndAPIs {
    /**
     * 标准库, Kotlin 有一个提供基础类型、函数、集合以及设施的库让你的代码简练且富有表达力. 标准库的很大一部分(kotlin包中的所有)在任何Kotlin文件中都是
     * 容易找到 而不需要显式导入它 ..
     */
    @Test
    fun standardLibrary() {
        val text = "emosewa si niltoK"

        // Use the reversed() function from the standard library
        val reversedText = text.reversed()

        // Use the print() function from the standard library
        print(reversedText)
        // Kotlin is awesome
    }

    /**
     * 然而 标准库的某些部分仍然在需要你使用它之前导入它们, 例如,如果你想要使用标准库的时间测量特性, 你需要导入 [kotlin.time](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.time/) 包 ..
     *
     * 在文件顶部,  通过import 关键字后跟你需要的包去导入
     * ```kotlin
     *   import kotlin.time.*
     * ```
     *
     * 星号是一个通配符导入 告诉Kotlin 去导入这个包中的所有内容, 你不能够将*与伴生对象一起使用, 相反你需要显式的声明伴生对象的成员(你想要使用的)
     *
     * 这个示例中, 导入Duration类 并且来自它的伴生对象的 hours / minutes 扩展属性
     *
     * 使用minutes 属性去转换30到30分钟的Duration ..
     *
     * 使用 hours 属性去转换为 0.5 到30分钟的Duration(半小时) ..
     *
     * 是否两个持续是相等的 并且打印结果
     */
    @Test
    fun importExample() {
        val thirtyMinutes: Duration = 30.minutes
        val halfHour: Duration = 0.5.hours
        println(thirtyMinutes == halfHour)
        // true
    }

    /**
     * 你决定写自己的代码之前,  检查标准库是否你想要查看的已经存在, 这里有一个区域列表 在标准库中已经提供了大量的类、函数、以及属性 ..
     *
     * 1. [Collections](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/)
     *
     * 2. [Sequences](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.sequences/)
     *
     * 3. [String manipulation](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.text/)
     *
     * 4. [Time management](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.time/)
     *
     * 有关标准库中还有其他什么内容, 探索 [API 参考](https://kotlinlang.org/api/core/kotlin-stdlib/)
     *
     * ## Kotlin 库
     *
     * 标准库覆盖了许多常见使用情况, 但是还有一些没有解决, 幸运的是,Kotlin团队以及社区的其他成员开发了丰富的库去完善这个标准库, 举个例子,
     * [kotlinx-datetime](https://kotlinlang.org/api/kotlinx-datetime/) 帮助你去管理不同平台的时间 ..
     *
     * 你能够发现有用的库(在[搜索平台上](https://klibs.io/)). 为了使用它们, 你需要做额外的步骤, 像增加一个依赖或者插件, 每一个库都有一个仓库
     * 以及一些指南如何包含它在你的Kotlin项目中 ..
     *
     * 一旦你增加了这个库,你能够在项目中导入任何包, 下面是一个示例,如何导入kotlinx-datetime 包去发现当前NewYork的当前时间 ..
     *
     *
     * 导入 kotlinx.datetime 包
     *
     * 使用Clock.System.now() 函数去创建一个Instant 类的实例(包含当前时间以及分配结果到now变量)
     *
     * 打印当前时间
     *
     * 使用TimeZone.of() 函数去发现NewYork的时区 以及 赋值结果到zone变量上 ..
     *
     * 在包含当前时间的实例上调用.toLocalDateTime() 函数, 使用NewYork 时区作为参数 ..
     *
     * 分配结果到localDateTime 变量
     *
     * 打印调整 为NewYork时区的时间 ..
     *
     * 为了探索示例中使用的类和函数的更多细节,查看[API 参考细节](https://kotlinlang.org/api/kotlinx-datetime/kotlinx-datetime/kotlinx.datetime/) ..
     */
    @Test
    @kotlin.time.ExperimentalTime
    fun buildBeforeSearch() {

        val now = Clock.System.now()
        println("Current instant: $now")
        val timeZone = TimeZone.of("America/New_York")
        // 需要使用Kotlinx-datetime :0.7.2
        val localDateTime = now.toLocalDateTime(timeZone)
        println("Local date-time in NY: $localDateTime")
    }

    /**
     * 库作者也许标记API 需要选择加入(在你能够使用它们之前), 这种API 通常仍然处于开发者也许在未来会被改变,如果你不选择加入,那么你会看到警告和错误:
     * This declaration needs opt-in. Its usage should be marked with '@...' or '@OptIn(...)'
     *
     * 为了加入, 编写@OptIn 注解后跟(包含分类此API的类名::class)
     *
     * 例如， 来自标准库的uintArrayOf()函数属于 @ExperimentalUnsignedTypes,正如 API 参考中展示的那样 ..
     * ```kotlin
     * @ExperimentalUnsignedTypes
     * inline fun uintArrayOf(vararg elements: UInt): UIntArray
     * ```
     *
     * 在代码中, opt-in 使用uintArrayOf()函数去创建一个无符号整数的数组并且修改它的元素之一 ..
     *
     * 这是最简单的方式去加入了, 但是也有其他方式, 了解更多,查看[Opt-in](https://kotlinlang.org/docs/opt-in-requirements.html)
     */
    @Test
    fun optInToApis() {
        val unsignedArray = uintArrayOf(1u, 2u, 3u, 4u, 5u)
        unsignedArray[2] = 42u;
        println("Updated array: ${unsignedArray.joinToString()}")
        // Updated array: 1,2,42,4,5
    }

    /**
     * exercise1
     *
     * 如果你开发了财政的app(帮助用户计算它们投资的未来价值), 复利的计算公式是:
     * ```text
     *  A = P x (1 + r / n)nt
     * ```
     *
     * A 是在利息累积之后的钱的总量( 本金 + 利息)
     *
     * r 是年利率(小数, 10进制)
     *
     * n 是每年复利的次数
     *
     * t 钱是投资的时间(以年为单位)
     *
     * 更新代码为:
     * 1. 导入kotlin.math包的必要函数
     * 2. 完善calculateCompoundInterest() 函数去计算应用了复利之后的最终金额
     *
     */
    @Test
    fun practice() {
// Write your code here

        fun calculateCompoundInterest(P: Double, r: Double, n: Int, t: Int): Double {
            // Write your code here
            return P *  (1 + r / n).pow(n * t)
        }

        fun main() {
            val principal = 1000.0
            val rate = 0.05
            val timesCompounded = 4
            val years = 5
            val amount = calculateCompoundInterest(principal, rate, timesCompounded, years)
            println("The accumulated amount is: $amount")
            // The accumulated amount is: 1282.0372317085844
        }
    }

    /**
     * 你想要测量 在你程序中执行多个数据处理任务的时间, 更新代码去增加正确的导入语句以及函数(来自kotlin.time包的)
     */
    @Test
    fun exercise2() {
        val timeTaken = /* Write your code here */ measureTime {
            // Simulate some data processing
            val data = List(1000) { it * 2 }
            val filteredData = data.filter { it % 3 == 0 }

            // Simulate processing the filtered data
            val processedData = filteredData.map { it / 2 }
            println("Processed data")
        }

        println("Time taken: $timeTaken") // e.g. 16 ms
    }

    /**
     * 在最新Kotlin发行版中的标准库中的新特性. 你想要尝试它,但是它需要Opt-in, 这个特性归属于 [@ExperimentalStdlibApi](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-experimental-stdlib-api/) ..
     * 那么加入应该看起来像:
     * ```kotlin
     *   @OptIn(ExperimentalStdlibApi::class)
     * ```
     */
    @Test
    fun exercise3() {

    }

    /**
     * 下一步是什么呢?
     *
     * 现在已经完成了中级教程, 下一步,检查受欢迎的Kotlin应用的教程:
     *
     * 1. [创建一个使用SpringBoot和kotlin的后端应用](https://kotlinlang.org/docs/jvm-create-project-with-spring-boot.html)
     *
     * 2. 创建针对Android 和 IOS的跨平台应用(从头开始)
     *      1. [共享业务逻辑  然而保持UI 原生](https://www.jetbrains.com/help/kotlin-multiplatform-dev/multiplatform-create-first-app.html?_gl=1*oozjax*_gcl_au*MTE5NjgzNDg1MC4xNzYwMDc5Njg3*_ga*MTU1OTIwMzMxLjE3NDE0MzgxOTc.*_ga_9J976DJZ68*czE3NjExODkwNDMkbzIzJGcxJHQxNzYxMTkxMzcwJGozNSRsMCRoMA..)
     *      2. [共享业务逻辑和UI](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-multiplatform-create-first-app.html?_gl=1*oozjax*_gcl_au*MTE5NjgzNDg1MC4xNzYwMDc5Njg3*_ga*MTU1OTIwMzMxLjE3NDE0MzgxOTc.*_ga_9J976DJZ68*czE3NjExODkwNDMkbzIzJGcxJHQxNzYxMTkxMzcwJGozNSRsMCRoMA..)
     */
    @Test
    fun whatSNext() {

    }

}