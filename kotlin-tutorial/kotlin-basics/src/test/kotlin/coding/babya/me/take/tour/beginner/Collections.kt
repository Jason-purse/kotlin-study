package coding.babya.me.take.tour.beginner

import org.junit.jupiter.api.Test

/**
 * 当编程的时候, 分组数据到集合结构来进行后续处理是非常有用的.
 *
 * Kotlin 提供集合来表达这个目的 ..
 *
 * Kotlin 具有以下集合 - 用于分组数据:
 * ```txt
 *  Lists           有序集合
 *  Sets            唯一无需集合
 *  Maps            key-value 对 Set集合 (key 是唯一的并且仅映射到唯一的一个值)
 * ```
 *
 * 每一个集合类型能够被修改或者仅只读 ..
 *
 * 下一个章节[控制流](https://kotlinlang.org/docs/kotlin-tour-control-flow.html)
 *
 * 下一个章节 [ControlFlow]
 *
 * @author jasonj
 * @date 2025/10/13
 * @time 14:41
 *
 * @description
 *
 **/
class Collections {
    /**
     * list 根据它们增加的顺序存储数据, 并且允许数据重复 ..
     *
     * 为了创建只读列表(List), 使用**listOf()** 函数 ..
     *
     * 为了创建可变列表(MutableList), 使用**mutableListOf()** 函数 ..
     *
     * 当创建列表的时候,Kotlin 能够推断它存储项的类型, 为了显式的声明类型, 增加类型在三角方括号中**<>** (在列表声明之后)
     */
    @Test
    fun list() {
        // read only list
        val readOnlyShapes = listOf("triangle","square","circle");
        println(readOnlyShapes);

        // [triangle,square,circle]

        // Mutable list with explicit type declaration
        val shapes: MutableList<String> = mutableListOf("triangle","square","circle");

        println(shapes);
        // [triangle,square,circle]
    }

    /**
     * 为了阻止不必要的修改,你能够创建仅只读的可变列表的视图 并赋值到**List** ..
     *
     * 下面这种方式叫做 强转 **casting** ..(仅仅对变量进行类型控制)
     */
    @Test
    fun listView() {
        val shapes: MutableList<String> = mutableListOf("triangle","square","circle")

        val shapesLocked: List<String> = shapes;
    }

    /**
     * 列表是有序的,因此如果访问列表中的项, 使用下标访问操作符(索引操作符)**[]**:
     *
     * 当然对于列表中的第一个或者最后一项,有分别的便利方法: **first / last()**
     *
     * 这两个函数是**extension** 扩展函数的示例, 为了调用对象中的一个扩展函数, 通过对象之后.函数名即可 ..
     *
     * 扩展函数将会中级教程中包含, 现在仅仅只需要如何调用它们 ..
     */
    @Test
    fun orderedList() {
        val readOnlyShapes = listOf("triangle","square","circle")
        println("The first item in the list is: ${readOnlyShapes[0]}")

        println("The first item in the list is: ${readOnlyShapes.first()}")

        println("The last item in the list is: ${readOnlyShapes.last()}")
    }

    /**
     * 获取列表的项数量
     */
    @Test
    public fun  countItemsOfList() {
        println("This list has ${listOf("triangle","square","circle").count()} items")
    }

    /**
     * 为了检查列表中是否存在某个项, 使用**in** 操作符
     */
    @Test
    public fun containsOf() {
        val listOf = listOf("triangle", "square", "circle")
        println("circle" in listOf)
        // 同样也是true
        println(String(charArrayOf('c','i','r','c','l','e')) in listOf)
    }

    /**
     * 为了从一个可变列表中增加或者移除项目, 使用**add / remove** 函数
     */
    @Test
    public fun addOrRemoveMutableList() {
        val shapes: MutableList<String> = mutableListOf("triangle","square","circle")

        // 增加 "pentagon" to the list
        shapes.add("pentagon")

        println(shapes);

        shapes.remove("pentagon")
        println(shapes)

    }

    /**
     * 列表是有序且允许重复,集合是无序且仅唯一的 .
     *
     * 为了创建只读集合(Set), 使用**setOf()** 函数 ..
     *
     * 为了创建可变集合(MutableSet), 使用**mutableSetOf()** 函数
     *
     * 当创建集合的时候,Kotlin 能够推断存储项的类型, 为了显式声明类型,
     * 增加类型在集合声明之后的三角括号中.
     *
     * 你能够看到重复的"cherry" 被删除了 ..
     */
    @Test
    fun set() {
        // Read-only set
        val readOnlyFruit = setOf("apple", "banana", "cherry", "cherry")
        // Mutable set with explicit type declaration
        val fruit: MutableSet<String> = mutableSetOf("apple", "banana", "cherry", "cherry")

        println(readOnlyFruit)
        // [apple, banana, cherry]
    }

    /**
     * 同list 原理
     *
     * 由于集合是无序的,所以不能通过特定下标来访问他们 ..
     */
    @Test
    fun setView() {
        val fruit: MutableSet<String> = mutableSetOf("apple", "banana", "cherry", "cherry")
        val fruitLocked: Set<String> = fruit
    }

    @Test
    fun countItemsOfSet() {
        val readOnlyFruit = setOf("apple", "banana", "cherry", "cherry")
        println("This set has ${readOnlyFruit.count()} items")
        // This set has 3 items
    }

    @Test
    fun setInOperator() {
        val readOnlyFruit = setOf("apple", "banana", "cherry", "cherry")
        println("banana" in readOnlyFruit)
        // true
    }

    /**
     * add / remove function for set
     */
    @Test
    fun setAddOrRemove() {
        val fruit: MutableSet<String> = mutableSetOf("apple", "banana", "cherry", "cherry")
        fruit.add("dragonfruit")    // Add "dragonfruit" to the set
        println(fruit)              // [apple, banana, cherry, dragonfruit]

        fruit.remove("dragonfruit") // Remove "dragonfruit" from the set
        println(fruit)              // [apple, banana, cherry]
    }

    /**
     * map 存储项为 key-value 键值对, 你能够引用key 去访问值 ..
     *
     * 你能够想象一个map 就像食物菜单, 能能够发现价格, 通过食物(key) - 你想吃的食物 ..
     *
     * map 是有用的 如果你想要在不使用数字索引查看一个值,就像列表那样
     *
     * 1. map中的每一个key都是独一无二的(因此Kotlin 能够理解你想要获取的值)
     * 2. map中的value是可以重复的
     *
     * 创建只读map(Map), 使用**mapOf()** 函数 ..
     *
     * 创建可变map(MutableMap) 使用**mutableMapOf()** 函数
     *
     * 当创建map的时候,同样能够推断项的类型, 为了显式声明,只需要在map声明之后的 三角括号中增加key,value的类型..
     *
     * 例如,MutableMap<String,Int>. key表示String,value 是Int ..
     *
     * 创建map的最容易的方式是使用 **to** 关键字 在key和相关的值之间..
      */
    @Test
    fun map() {
        // Read-only map
        val readOnlyJuiceMenu = mapOf("apple" to 100, "kiwi" to 190, "orange" to 100)
        println(readOnlyJuiceMenu)
        // {apple=100, kiwi=190, orange=100}

        // Mutable map with explicit type declaration
        val juiceMenu: MutableMap<String, Int> = mutableMapOf("apple" to 100, "kiwi" to 190, "orange" to 100)
        println(juiceMenu)
        // {apple=100, kiwi=190, orange=100}
    }

    /**
     * 同list, 能够创建一个可变map的只读视图, 通过将变量分配到Map ..
     */
    @Test
    fun mapView() {
        val juiceMenu: MutableMap<String, Int> = mutableMapOf("apple" to 100, "kiwi" to 190, "orange" to 100)
        val juiceMenuLocked: Map<String, Int> = juiceMenu
    }

    /**
     * 通过使用map key和索引访问操作符 ..
     */
    @Test
    fun accessValueOfMap() {
        // Read-only map
        val readOnlyJuiceMenu = mapOf("apple" to 100, "kiwi" to 190, "orange" to 100)
        println("The value of apple juice is: ${readOnlyJuiceMenu["apple"]}")
        // The value of apple juice is: 100
    }

    /**
     * 如果访问一个不存在于map中的key-value对, 将会访问**null** 值 ..
     *
     * 在[Null safety 章节](https://kotlinlang.org/docs/kotlin-tour-null-safety.html) 会解释空值
     */
    @Test
    fun accessNoValueOfMap() {
        // Read-only map
        val readOnlyJuiceMenu = mapOf("apple" to 100, "kiwi" to 190, "orange" to 100)
        println("The value of pineapple juice is: ${readOnlyJuiceMenu["pineapple"]}")
        // The value of pineapple juice is: null
    }

    @Test
    fun indexAccessAddToMap() {
        val juiceMenu: MutableMap<String, Int> = mutableMapOf("apple" to 100, "kiwi" to 190, "orange" to 100)
        juiceMenu["coconut"] = 150 // Add key "coconut" with value 150 to the map
        println(juiceMenu)
        // {apple=100, kiwi=190, orange=100, coconut=150}
    }

    /**
     * remove 函数可以从可变map中移除key-value键值对
     */
    @Test
    fun removeFromMap() {
        val juiceMenu: MutableMap<String, Int> = mutableMapOf("apple" to 100, "kiwi" to 190, "orange" to 100)
        juiceMenu.remove("orange")    // Remove key "orange" from the map
        println(juiceMenu)
        // {apple=100, kiwi=190}
    }

    /**
     * 统计 map 存在多少项
     */
    @Test
    fun countMap() {
        // Read-only map
        val readOnlyJuiceMenu = mapOf("apple" to 100, "kiwi" to 190, "orange" to 100)
        println("This map has ${readOnlyJuiceMenu.count()} key-value pairs")
        // This map has 3 key-value pairs
    }

    /**
     * 检查特定key是否已经包含在map中, 使用`.containsKey()` 函数
     */
    @Test
    fun containsOfMap() {
        val readOnlyJuiceMenu = mapOf("apple" to 100, "kiwi" to 190, "orange" to 100)
        println(readOnlyJuiceMenu.containsKey("kiwi"))
    }

    /**
     * 访问map的 keys 或者 values, 分别使用keys,values函数
     *
     * 这里两个函数其实是对象的属性,为了访问对象的属性,通过对象.属性名即可访问 ..
     *
     * 属性将会在[Classes](https://kotlinlang.org/docs/kotlin-tour-classes.html) 章节讨论细节 .
     *
     */
    @Test
    fun keysOrValuesOfMap() {
        val readOnlyJuiceMenu = mapOf("apple" to 100, "kiwi" to 190, "orange" to 100)
        println(readOnlyJuiceMenu.keys)
        // [apple, kiwi, orange]
        println(readOnlyJuiceMenu.values)
        // [100, 190, 100]
    }

    /**
     * 这里巧妙的使用 in 能够访问list / set中判断是否包含元素的性质 来判断是否map存在key / value
     *
     * 有关能够使用集合做什么的更多信息,查看 [Collections](https://kotlinlang.org/docs/collections-overview.html)
     *
     * 现在你已经知道了基础类型以及如何管理集合,是时候探索程序中的控制流 ..
     */
    @Test
    fun checkKeyOrValueInMap() {
        val readOnlyJuiceMenu = mapOf("apple" to 100, "kiwi" to 190, "orange" to 100)
        println("orange" in readOnlyJuiceMenu.keys)
        // true

        // Alternatively, you don't need to use the keys property
        println("orange" in readOnlyJuiceMenu)
        // true

        println(200 in readOnlyJuiceMenu.values)
        // false
    }

    /**
     * 你有一个列表具有"green"数字,有一个"red"数字的列表, 完善代码去打印总共有多少元素 ..
     *
     */
    @Test
    fun exercise1() {
        val greenNumbers = listOf(1, 4, 23)
        val redNumbers = listOf(17, 2)
        // Write your code here
        println("There is ${greenNumbers.count() + redNumbers.count()} numbers")
    }

    /**
     * 你有一个被你的服务器支持的协议集合(set), 一个用户请求去使用特定的协议, 完善这个程序去检查是否请求的协议被支持或者不支持,**isSupported** 必须是一个boolean 值 ..
     */
    @Test
    fun exercise2() {
        val SUPPORTED = setOf("HTTP", "HTTPS", "FTP")
        val requested = "smtp"
        // val isSupported = // Write your code here
        val isSupported = requested.uppercase() in SUPPORTED
        println("Support for $requested: $isSupported")
    }

    /**
     * 定义一个和数字相关的map,从1 - 3 到他们相对于它们的拼写,使用map去拼写给定的数字
     */
    @Test
    fun exercise3() {
        // val number2word = // Write your code here
        val number2word = mapOf(1 to "ONE",2 to "TWO",3 to "THERE")
        val n = 2
        println("$n is spelt as '${number2word[n]}'")
    }
}