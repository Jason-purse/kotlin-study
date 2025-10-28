package coding.babya.me

/**
 * <p>
 *  当前文档最新版本 2.2.20
 *
 *  <br/>
 *  kotlin 是现代化的但已经是一种成熟的编程语言被设计用来使得开发者更轻松。(mature)
 *
 *  它是简明的,安全的能够和Java以及其他语言互操作的,并且提供了许多方式去重用代码(对于跨平台编程效率) ..
 *  <br />
 *  为了开始, 可以游览[kotlin的引导教程](https://kotlinlang.org/docs/kotlin-tour-welcome.html), 它覆盖了kotlin编程语言的基础并且完全能够在浏览器中完成。
 * </p>
 * @author jasonj
 * @date 2025/10/13
 * @time 09:10
 *
 * @description 开始使用 Kotlin
 *
 **/
class GetStarted {
    /**
     * 安装Kotlin
     *
     * - kotlin 已经包含在 intellij idea 以及 android studio 发行版中, 下载这些idea之一即可开始使用Kotlin.
     */
    fun installKotlin() {

    }

    /**
     * - Console (控制台程序)
     * - Backend(后端)
     * - Cross-platform 跨平台
     * - Android  安卓
     * - Data analysis 数据分析
     */
    fun chooseYourKotlinUseCase() {

    }

    /**
     * 将学习如何开发一个控制台程序并为kotlin 创建单元测试 ..
     * - [通过一个intellij idea 项目向导创建基本的Jvm 应用](https://kotlinlang.org/docs/jvm-get-started.html)
     * - [编写你的第一个单元测试](https://kotlinlang.org/docs/jvm-test-using-junit.html)
     */
    fun console() {

    }

    /**
     * 学习如何开发一个具有kotlin 服务端的后端应用
     * - 创建第一个后端应用
     *   [1. 创建使用Spring boot的 restful web service](https://kotlinlang.org/docs/jvm-get-started-spring-boot.html)
     *   [2. 创建 使用 Ktor的 http apis](https://ktor.io/docs/creating-http-apis.html?_gl=1*nq3z6c*_gcl_au*MTE5NjgzNDg1MC4xNzYwMDc5Njg3*_ga*MTU1OTIwMzMxLjE3NDE0MzgxOTc.*_ga_9J976DJZ68*czE3NjAzMTc3MjgkbzgkZzEkdDE3NjAzMTkyNjMkajYwJGwwJGgw)
     * - [了解如何在应用中混写 java 和 kotlin 代码](https://kotlinlang.org/docs/mixing-java-kotlin-intellij.html)
     */
    fun  backend() {

    }

    /**
     * 跨平台
     *
     * - 学习如何使用[Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html?_gl=1*1oea8tm*_gcl_au*MTE5NjgzNDg1MC4xNzYwMDc5Njg3*_ga*MTU1OTIwMzMxLjE3NDE0MzgxOTc.*_ga_9J976DJZ68*czE3NjAzMTc3MjgkbzgkZzEkdDE3NjAzMTkzOTUkajYwJGwwJGgw) 开发跨平台应用。
     * 1. [设置跨平台开发的环境](https://www.jetbrains.com/help/kotlin-multiplatform-dev/quickstart.html?_gl=1*elxs21*_gcl_au*MTE5NjgzNDg1MC4xNzYwMDc5Njg3*_ga*MTU1OTIwMzMxLjE3NDE0MzgxOTc.*_ga_9J976DJZ68*czE3NjAzMTc3MjgkbzgkZzEkdDE3NjAzMTk0NzAkajYwJGwwJGgw)
     * 2. 创建IOS 和安卓的第一个应用
     *  1. 从头创建跨平台应用
     *      - [共享业务逻辑(然而保持UI 原生)](https://www.jetbrains.com/help/kotlin-multiplatform-dev/multiplatform-create-first-app.html?_gl=1*do7wbg*_gcl_au*MTE5NjgzNDg1MC4xNzYwMDc5Njg3*_ga*MTU1OTIwMzMxLjE3NDE0MzgxOTc.*_ga_9J976DJZ68*czE3NjAzMTc3MjgkbzgkZzEkdDE3NjAzMTk0NzAkajYwJGwwJGgw)
     *      - [共享业务逻辑 和UI](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-multiplatform-create-first-app.html?_gl=1*1c840p6*_gcl_au*MTE5NjgzNDg1MC4xNzYwMDc5Njg3*_ga*MTU1OTIwMzMxLjE3NDE0MzgxOTc.*_ga_9J976DJZ68*czE3NjAzMTc3MjgkbzgkZzEkdDE3NjAzMTk0NzAkajYwJGwwJGgw)
     *  2. [让你现存的安卓应用工作在Ios上](https://www.jetbrains.com/help/kotlin-multiplatform-dev/multiplatform-integrate-in-existing-app.html?_gl=1*1anx867*_gcl_au*MTE5NjgzNDg1MC4xNzYwMDc5Njg3*_ga*MTU1OTIwMzMxLjE3NDE0MzgxOTc.*_ga_9J976DJZ68*czE3NjAzMTc3MjgkbzgkZzEkdDE3NjAzMTk0NzAkajYwJGwwJGgw)
     *  3. [使用Ktor 和 SQLdelight 创建跨平台应用](https://www.jetbrains.com/help/kotlin-multiplatform-dev/multiplatform-ktor-sqldelight.html?_gl=1*1anx867*_gcl_au*MTE5NjgzNDg1MC4xNzYwMDc5Njg3*_ga*MTU1OTIwMzMxLjE3NDE0MzgxOTc.*_ga_9J976DJZ68*czE3NjAzMTc3MjgkbzgkZzEkdDE3NjAzMTk0NzAkajYwJGwwJGgw)
     * 3. 探索[示例项目](https://www.jetbrains.com/help/kotlin-multiplatform-dev/multiplatform-samples.html?_gl=1*1a2pbj4*_gcl_au*MTE5NjgzNDg1MC4xNzYwMDc5Njg3*_ga*MTU1OTIwMzMxLjE3NDE0MzgxOTc.*_ga_9J976DJZ68*czE3NjAzMTc3MjgkbzgkZzEkdDE3NjAzMTk0NzAkajYwJGwwJGgw)
     *
     */
    fun crossPlatform() {

    }

    /**
     * [参考官网](https://kotlinlang.org/docs/getting-started.html#android)
     */
    fun android() {

    }

    /**
     * [参考官网](https://kotlinlang.org/docs/getting-started.html#data-analysis)
     */
    fun dataAnalysis() {

    }
}