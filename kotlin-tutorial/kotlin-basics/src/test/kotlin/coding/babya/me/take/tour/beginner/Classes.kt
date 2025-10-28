package coding.babya.me.take.tour.beginner

import org.junit.jupiter.api.Test
import kotlin.math.abs
import kotlin.math.min
import kotlin.random.Random

/**
 *
 * 类相关的支持
 *
 * Kotlin 支持面向对象的编程(使用类和对象),对象是有用的存储数据到你的程序中, 类允许你为一个对象声明一组特征.
 * 当你创建来自一个类的对象时, 你能够节约时间和精力,因为你不需要每次声明这些特征 ..
 *
 * [NullSafety]
 *
 * @author jasonj
 * @date 2025/10/14
 * @time 14:38
 *
 * @description
 *
 **/
class Classes {
    /**
     * 为了声明一个类,使用class 关键字
     */
    class Customer

    /**
     * 属性, 类的对象的特征能够在属性中声明, 你能够声明一个类的属性
     *
     * 1. 在类名之后的括号中
     */
    class Contact(val id: Int, var email: String)

    /**
     * 在花括号中定义类体
     */
    class Contact1(val id: Int, var email: String) {
        val category: String = ""
    }

    /**
     * 推荐使用val 声明只读属性 除非你需要在类的实例创建之后进行属性改变
     *
     * 能够声明属性 不用val / var(当你在括号中声明属性时),但是当你这样做了之后,对象实例创建之后无法访问这些属性
     *
     * 在括号中包含的内容叫做类头(class header)
     *
     * 就像函数参数那样,类属性也可以有默认值
     */
    class Contact2(val id: Int, var email: String = "example@gmail.com") {
        val category: String = "work"
    }

    /**
     * 为了创建一个类的实例, 你需要通过构造器声明一个类实例
     *
     * 默认情况下, Kotlin 自动创建了一个使用声明在类头中的参数的构造器
     *
     * 在这个示例中, Contact 是类, contact 是Contact类的实例
     *
     * id 以及 email 是属性
     *
     * id 以及 email 将作为默认构造器来创建 contact ..
     *
     * Kotlin类可以有多个构造器,包括开发者自己定义的. [更多构造器相关的信息](https://kotlinlang.org/docs/classes.html#constructors-and-initializer-blocks)
     */
    @Test
    fun createInstance() {
        // 也就是说它本身就是一个构造器
        class Contact(val id: Int, var email: String)

        Contact(1, "mary@gmail.com")
    }

    /**
     * 为了访问一个实例的属性, 在实例名后面通过.追加属性名访问
     *
     * > 如果需要连接属性值作为字符串的一部分,可以使用字符串模版, 例如:
     * ```kotlin
     *  println("Their email address is: ${contact.email}")
     * ```
     */
    @Test
    fun accessProperties() {
        val contact = Contact(1, "mary@gmail.com")

        // Prints the value of the property: email
        println(contact.email)
        // mary@gmail.com

        // Updates the value of the property: email
        contact.email = "jane@gmail.com"

        // Prints the new value of the property: email
        println(contact.email)
        // jane@gmail.com
    }

    /**
     * 成员函数, 除了声明属性作为对象特征的一部分,你可以定义对象的行为(通过成员函数)
     *
     * 在Kotlin中,成员函数必须在类体中声明, 为了在对象实例上调用成员函数, 在实例名.函数名调用..
     */
    @Test
    fun memberFunctions() {
        class Contact(val id: Int, var email: String) {
            fun printId() {
                println(id)
            }
        }

        val contact = Contact(1, "mary@gmail.com")
        // Calls member function printId()
        contact.printId()
        // 1
    }

    /**
     * 数据类
     *
     * Kotlin 具有数据类 它是特定于存储数据的结构 ..
     *
     * 数据类和普通类是具有相同能力的, 但是它们自动增加了一些额外的成员函数..
     *
     * 这些成员函数允许你很容易的打印实例到可读输出,比较类的实例,复制实例以及更多 ..
     *
     * 因为这些函数都是自动可用的,你不需要花费时间写相关的模版代码(为你的每一个类)
     *
     * 为了声明一个数据类, 使用 data 关键字
     */
    @Test
    fun dataClasses() {
        /**
         * 它包含了一些预定义的成员函数
         *
         * 1. toString()  打印类实例和属性为可读形式的字符串
         * 2. equals() or ==  比较类的实例
         * 3. copy()  通过复制其他实例来创建一个类实例,  可能潜在具有一些不同的属性
         *
         */
        data class User(val name: String, val id: Int)
    }

    data class User(val name: String, val id: Int)

    /**
     * 打印为字符串
     *
     * 为了打印一个类实例的可读形式字符串, 你能够显式的调用toString() 函数,或者使用print 函数(println() 或者print()) 它们会自动的为你调用toString() ..
     */
    @Test
    fun printAsString() {
        val user = User("Alex", 1)
        // 自动使用toString() 函数让输出更容易阅读,当你调试或者创建日志的时候尤其有用 .
        println(user)
    }

    /**
     * 比较数据类实例, 可以使用相等操作符 ==
     */
    @Test
    fun compareInstances() {
        val user = User("Alex", 1)
        val secondUser = User("Alex", 1)
        val thirdUser = User("Max", 2)

// Compares user to second user
        println("user == secondUser: ${user == secondUser}")
// user == secondUser: true

// Compares user to third user
        println("user == thirdUser: ${user == thirdUser}")
// user == thirdUser: false

    }

    /**
     * 为了创建一个数据类实例的复制品, 使用copy函数即可 ..
     *
     * 为了创建一个数据类实例的复制品 并改变某些属性,  在实例上调用copy函数 并增加需要替代给的那个属性的值作为函数参数 ..
     *
     * 创建实例的复制品 往往比修改原始实例更加安全 因为任何依赖于原始对象实例的代码并不会被复制品影响以及对复制品所做事情影响 ..
     *
     * 有关数据类的更多信息,查看[数据类](https://kotlinlang.org/docs/data-classes.html)
     */
    @Test
    fun copyInstance() {
        val user = User("Alex", 1)

        // Creates an exact copy of user
        println(user.copy())
        // User(name=Alex, id=1)

        // Creates a copy of user with name: "Max"
        println(user.copy("Max"))
        // User(name=Max, id=1)

        // Creates a copy of user with id: 3
        println(user.copy(id = 3))
        // User(name=Alex, id=3)
    }

    /**
     * 定义数据类 Employee 具有两个属性,一个名称,另一个salary 薪水
     *
     * 确保薪水的属性是可变的, 否则你无法在年底进行底薪提升, 下面是示例 完善它
     */
    @Test
    fun exercise1() {

        class Employee(name: String, var salary: Int)

        val emp = Employee("Mary", 20)
        println(emp)
        emp.salary += 10
        println(emp)
    }

    /**
     * 练习2
     *
     * 声明额外的数据类(为了让你的代码进行编译所需要的)
     */
    @Test
    fun exercise2() {
        data class Name(val name: String, val region: String)
        data class City(val name: String, val locale: String)
        data class Address(val street: String, val city: City)
        data class Person(val name: Name, val address: Address, val ownsAPet: Boolean = true)
        // Write your code here
        // data class Name(...)


        Person(
            Name("John", "Smith"), Address("123 Fake Street", City("Springfield", "US")), ownsAPet = false
        )
    }

    /**
     * 为了测试你的代码,你需要有一个生成器(创建随机员工), 定义一个RandomEmployeeGenerator 类(具有可能名称的固定列表) - 在类体中定义
     *
     * 使用最小值和最大值薪水配置这个类(在类头中), 在类体中, 定义 generateEmployee() 函数
     * > 在这个示例中, 你导入一个包 来使用Random.nextInt() 函数 .. 有关导入包的更多信息,查看[Pacakges and imports](https://kotlinlang.org/docs/packages.html)
     */
    @Test
    fun exercise3() {

        data class Employee(val name: String, var salary: Int)

        class RandomEmployeeGenerator(var minSalary: Int, var maxSalary: Int) {
            val employees = listOf("A", "B", "C","D","E","F","G");

            fun generateEmployee(): Employee {
//                val salaries = (minSalary .. maxSalary).toList();
//                return Employee(employees[abs(Random.nextInt()) % employees.size],salaries[abs(Random.nextInt()) % salaries.size])

                return Employee(employees.random(), Random.nextInt(from = minSalary, until = maxSalary))
            }
        }

        // Write your code here

        val empGen = RandomEmployeeGenerator(10, 30)
        println(empGen.generateEmployee())
        println(empGen.generateEmployee())
        println(empGen.generateEmployee())
        empGen.minSalary = 50
        empGen.maxSalary = 100
        println(empGen.generateEmployee())
    }
}