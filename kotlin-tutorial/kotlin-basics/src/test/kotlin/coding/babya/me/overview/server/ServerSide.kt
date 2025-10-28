package coding.babya.me.overview.server

import org.junit.jupiter.api.Test

/**
 * 服务端应用
 *
 * Kotlin 非常适合开发服务器端应用
 *
 * 它允许你编写简洁且富有表现力的代码 同时保持和现有基于java的技术栈完全兼容性, 所有都是具有平滑的学习曲线:
 *
 * 1. 表现力
 *
 *  Kotlin的创新语言特性, 例如支持[类型安全的构建器](https://kotlinlang.org/docs/type-safe-builders.html) 以及[委派属性](https://kotlinlang.org/docs/delegated-properties.html),帮助去构建强大并且很容易使用的抽象
 *
 * 2. 弹性(可扩展性)
 *
 *  Kotlin支持携程帮助服务端应用(缩放具有适度硬件要求的庞大数量的客户端)
 *
 *  3. 互操作性
 *
 *   Kotlin能够完全和所有基于java的框架兼容, 因此你能够使用你熟悉的技术栈 同样享受到更加现代化语言的优势 ..
 *
 *  4. 迁移
 *
 *   Kotlin 支持逐步迁移java大型代码库到Kotlin, 你能够在Kotlin中编写新代码 同时将系统的旧部分保持为java ..
 *
 *  5. 工具
 *
 *      除了一般的极好的IDE 支持,Kotlin 提供了特定框架的工具(例如,Spring以及Ktor)(在IDEA中插件)
 *
 *  6. 学习曲线
 *
 *      对于Java开发者, 开始Kotlin非常容易, 在Kotlin插件中包含了 自动转换java到Kotlin的转换器(作为你的第一步),[Kotlin Koans](https://kotlinlang.org/docs/koans.html) 能够指导
 *      你了解关键语言特性以及一系列的交互性练习 .. Kotlin特定的框架像Ktor 提供了一个简单、直截了当的方式而没有隐藏大型框架的复杂性 ...
 *
 *
 * @author jasonj
 * @date 2025/10/23
 * @time 14:34
 *
 * @description
 *
 **/
class ServerSide {
    /**
     * 服务端框架开发
     *
     * 下面是一些示例
     *
     * 1. [Spring 使用Kotlin的语言特性提供了更简洁的API](https://spring.io/blog/2017/01/04/introducing-kotlin-support-in-spring-framework-5-0),从spring 5.0开始,
     * [在线的项目生成器](https://start.spring.io/#!language=kotlin)允许你快速的生成一个kotlin支持的新项目 ..
     *
     * 2. [Ktor](https://github.com/kotlin/ktor) 是一个由JetBrains构件的框架 为了用Kotlin创建web应用, 使用携程来实现高扩展性并提供了容易使用和惯用的API ..
     *
     * 3. [Quarkus](https://quarkus.io/guides/kotlin) 提供了使用Kotlin的一类支持,此框架开源并且由红帽维护,Quarkus  为Kubernetes从头开始构建的并且提供了有凝聚力的全栈框架 - 通过利用数百个不断成长的同类最佳库列表 ..
     *
     * 4. [Vert.x](https://vertx.io/) 是一个在jvm上构建响应式web应用的框架, 提供了对Kotlin的[专有支持](https://github.com/vert-x3/vertx-lang-kotlin),包括[完整的文档](https://vertx.io/docs/vertx-core/kotlin/) 。。
     *
     * 5. [kotlinx.html](https://github.com/kotlin/kotlinx.html) 是一个DSL(被用来在web应用中构建HTML), 它提供了传统模版系统例如JSP / FreeMarker的一种替代物 ..
     *
     * 6. [Micronaut](https://micronaut.io/) 是一种现代化的基于Jvm的全栈框架(用来构建模块化、容易测试的微服务和serverless 应用), 它携带了大量的有用内置特性 。。
     *
     * 7. [http4k](https://http4k.org/) 是一个函数性工具箱(具有很少内存占用) 的Kotlin Http应用, 纯kotlin编写,此库基于Twitter上的"你的服务器是一个函数"文章以及表示了一种模型(http服务器和客户端作为能够相互结合的简单Kotlin 函数)
     *
     * 8. [Javalin](https://javalin.io/) 是一个非常轻量级的web框架(支持 websockets, http2,异步请求)
     *
     * 对于数据库也有一些可用的选项,例如直接jdbc访问,jpa 以及 使用noSQL 数据源(通过它们的java驱动), 对于jpa [kotlin-jpa](https://kotlinlang.org/docs/no-arg-plugin.html#jpa-support) 编译器插件能够适配kotlin编译的类去满足框架的要求 ..
     *
     * 在[kotlin.link](https://kotlin.link/resources)上发现更多框架 ..
     *
     * ## 部署Kotlin 服务端应用
     *
     * Kotlin 应用能够部署到任何支持javaweb应用的主机,包括Amazon Web服务, Google 云平台, 以及其他 ..
     *
     * 为了在Heroku上部署Kotlin应用, 能能够参考 官方的[Heroku 指南](https://devcenter.heroku.com/articles/getting-started-with-kotlin)
     *
     * AWS Labs 提供了一个[示例项目](https://github.com/awslabs/serverless-photo-recognition)(展示了如何使用Kotlin 编写[AWS lambda函数](https://aws.amazon.com/lambda/)
     *
     * google 云平台提供了一系列教程去部署kotlin 应用到gcp,同样 [Ktor 以及 App Engine](https://cloud.google.com/community/tutorials/kotlin-ktor-app-engine-java8) 以及 [Spring和 App engine(https://cloud.google.com/community/tutorials/kotlin-springboot-app-engine-java8],除此之外, 这里有一个[交互性的code lab](https://codelabs.developers.google.com/codelabs/cloud-spring-cloud-gcp-kotlin) 来部署一个kotlin Spring应用 ..
     *
     * ## 在服务端使用kotlin的产品
     *
     * [Corda](https://www.corda.net/) 是一个开源的分布式账本平台(支持主要银行,完全基于Kotlin构建)
     *
     * [JetBrains Account](https://account.jetbrains.com/?_gl=1*1xueztl*_gcl_au*MTE5NjgzNDg1MC4xNzYwMDc5Njg3*_ga*MTU1OTIwMzMxLjE3NDE0MzgxOTc.*_ga_9J976DJZ68*czE3NjEyMDQwODUkbzI1JGcxJHQxNzYxMjA0NDY5JGo2MCRsMCRoMA..), 此系统负责完整的许可证销售和验证流程, 100% kotlin 编写并且已经从2015运行在生产并且没有出现重大的问题 ..
     *
     *
     * [Chess.com](https://www.chess.com/) 是一个专注于国际象棋以及全球热爱国际象棋的玩家的网站, Chess.com 使用Ktor 无缝配置多个Http客户端 ..
     *
     * 在[Adobe](https://blog.developer.adobe.com/streamlining-server-side-app-development-with-kotlin-be8cf9d8b61a)的设计师 使用Kotlin进行服务端应用开发并且 Ktor来在Adobe练习平台中进行原型设计, 这让组织去集中并标准化顾客数据(在应用数据科学和机器学习之前)
     */
    @Test
    fun  serverSideDevelopment() {

    }

    /**
     * 下一步, 对于更深度的语言介绍, 查看Kotlin 文档 以及   [Kotlin Koans](https://kotlinlang.org/docs/koans.html)
     *
     * 探索如何使用Kotor 构建异步服务器应用, 使用Kotlin携程的框架
     *
     *
     * 观看 [Micronaut for microservices with Kotlin](https://micronaut.io/2020/12/03/webinar-micronaut-for-microservices-with-kotlin/)的 网络研讨会 以及探索一个[详细的指南](https://guides.micronaut.io/latest/micronaut-kotlin-extension-fns.html)
     * 展示了你应该如何使用[Kotlin 扩展函数](https://kotlinlang.org/docs/extensions.html#extension-functions)(在 Micronaut 框架中)
     *
     * http4k 提供了 [CLI](https://toolbox.http4k.org/) 生成完全合法项目,以及一个[starter 仓库](https://start.http4k.org/)去生成一个完整的CD 管道(使用GitHub,Travis, 以及HeroKu) - 通过单个bash 命令
     *
     * 想要从java 迁移到kotlin , 学习如何在[java和Kotlin中通过字符串执行特定任务](https://kotlinlang.org/docs/java-to-kotlin-idioms-strings.html) ..
     */
    @Test
    fun next() {

    }
}