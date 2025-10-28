package coding.babya.me.learningmaterials

import org.junit.jupiter.api.Test
import java.util.PriorityQueue

/**
 *
 *
 * @author jasonj
 * @date 2025/10/24
 * @time 16:41
 *
 * @description
 *
 **/
class AdventOfCodePuzzles {

    // advent Of 2022
    // Calorie counting(卡路里计算)
    /**
     * 了解Kotlin Advent of Code template 以及一些在Kotlin中方便和字符串、集合工作的便利函数,例如 maxOf / sumOf, 查看扩展函数如何帮你组织
     * 你的解决方案(在一种很棒的方式)
     *
     * 1. 读取 puzzle 描述([Advent of Code](Advent of Code))
     *
     * Santa's reindeer typically eat regular reindeer food, but they need a lot of magical energy to deliver presents on Christmas.
     * For that, their favorite snack is a special type of star fruit that only grows deep in the jungle.
     * The Elves have brought you on their annual expedition to the grove where the fruit grows.
     *
     * 圣诞老人的驯鹿通常吃普通的驯鹿食物,但是它们需要大量的魔法能量去在圣诞节去送礼物,为此, 他们喜欢的小零食是一种特殊类型的杨桃(通常仅仅生长在丛林深处)
     *
     * To supply enough magical energy, the expedition needs to retrieve a minimum of fifty stars by December 25th.
     * Although the Elves assure you that the grove has plenty of fruit, you decide to grab any fruit you see along the way,
     * just in case.
     * 为了提供足够的魔法能量，探险队需要在 12 月 25 日之前取回至少 50 颗星星。
     * 尽管精灵向你保证树林里有很多水果，但你决定抓住沿途看到的任何水果，以防万一。
     *
     * Collect stars by solving puzzles. Two puzzles will be made available on each day in the Advent calendar;
     * the second puzzle is unlocked when you complete the first. Each puzzle grants one star. Good luck!
     *
     * 通过解决谜题来收集星星, 两个谜题在advent 日历的每一天会变得可用, 当第一个完成的时候第二个自动解锁, 每一个谜题获得一个星星,好运 ..
     *
     * The jungle must be too overgrown and difficult to navigate in vehicles or access from the air;
     * the Elves' expedition traditionally goes on foot.
     *
     * 丛林必须杂草丛生，难以乘坐车辆或从空中进入;精灵的探险传统上是步行进行的。
     *
     * As your boats approach land, the Elves begin taking inventory of their supplies.
     * One important consideration is food - in particular,
     * the number of Calories each Elf is carrying (your puzzle input).
     *
     * 当你的船接近陆地时，精灵开始盘点他们的补给品。一个重要的考虑因素是食物——特别是每个精灵携带的卡路里数量（你的谜题输入）。
     *
     * The Elves take turns writing down the number of Calories contained by the various meals, snacks, rations, etc.
     * that they've brought with them, one item per line.
     * Each Elf separates their own inventory from the previous Elf's inventory (if any) by a blank line.
     *
     * 精灵们轮流写下他们带来的各种膳食、零食、口粮等所含的卡路里数量，每行一项。
     * 每个精灵都用空行将自己的物品栏与前一个精灵的物品栏（如果有的话）分开。
     * For example, suppose the Elves finish writing their items' Calories and end up with the following list:
     * 例如，假设精灵写完他们物品的卡路里并最终得到以下列表：
     *
     * 1000
     * 2000
     * 3000
     *
     * 4000
     *
     * 5000
     * 6000
     *
     * 7000
     * 8000
     * 9000
     *
     * 10000
     *
     * 这个列表代表了 5个精灵携带的食物的卡路里 ..
     *
     * 1. 第一个精灵携带的卡路里是 1000, 2000, 3000,总计6000卡路里 ..
     * 2. 第二个精灵携带了一个具有4000卡路里的食物
     * 3. 第三个精灵携带了 5/6000卡路里的食物,总计 11000卡路里 ..
     * 4. 第四个精灵携带了 7/8/9000卡路里的食物,总计  24000 卡路里 ..
     * 5. 第五个精灵携带了 10000卡路里的食物
     *
     *
     * 2. [查看视频中的解决方案](https://www.youtube.com/watch?v=ntbsbqLCKDs)
     *
     */
    @Test
    fun day2022_1_calorieCounting() {
        println(javaClass.classLoader.getResourceAsStream("coding/babya/me/learningmaterials/advent1.txt").use {
                it?.bufferedReader()?.readText()
            }?.let {
//                val listOf = mutableListOf<List<Int>>()
//
//                var tempSequence: MutableList<Int>? = mutableListOf();
//                // 通过空格行分割
//                for (text in it.split("\n")) {
//                    if (text.isBlank()) {
//                        if (tempSequence != null) {
//                            listOf.add(tempSequence)
//                        }
//                        tempSequence = mutableListOf();
//                        continue;
//                    }
//
//                    tempSequence!!.add(text.toInt());
//                }
//
//                return@let listOf.maxOfOrNull { it.sum() }

                // 可以变得更简单
                // 最后一行不能有空格(应该是文件复制的时候存在一定的问题)
                it.split("\n\n").maxOf { elf ->
                    elf.lines().sumOf { it -> it.toInt() }
                }
            })
    }

    /**
     *
     */
    @Test
    fun AdventOfCodePuzzles_Part2() {
        println(javaClass.classLoader.getResourceAsStream("coding/babya/me/learningmaterials/advent1.txt").use {
            it?.bufferedReader()?.readText()
        }?.let {
            it.split("\n\n").map { it.lines().sumOf { it.toInt() } }.sortedDescending().take(3)
                .sum()
        })

        println(javaClass.classLoader.getResourceAsStream("coding/babya/me/learningmaterials/advent1.txt").use {
            it?.bufferedReader()?.readText()
        }?.let {
            // 给一个有序集合
            // 因为是一个set,所以第二次添加 相同值的情况下,会被忽略 ..
            val set =  sortedSetOf<Int>();
            for (element in it.split("\n\n").map { it.lines().sumOf { it.toInt() } }) {
                // 所以如果用来处理这个场景 可能没问题,但是在忽略相同值的情况下 需要注意 ..
                set.add(element);
                // 大于3的情况下(也就是由于相同值没有添加进去,会导致这段逻辑存在缺陷)
                if(set.size > 3) {
                    set.remove(set.first());
                }
            }
            set.sum()
        })

        println(javaClass.classLoader.getResourceAsStream("coding/babya/me/learningmaterials/advent1.txt").use {
            it?.bufferedReader()?.readText()
        }?.let {
          // 尝试处理
            val data = it.split("\n\n").map { it.lines().sumOf { it.toInt() } }

            // 现在尝试下一种,用优先级队列
            val priorityQueue = PriorityQueue<Int>(4);
            for (element in data) {
                priorityQueue.add(element)
                if(priorityQueue.size > 3) {
                    priorityQueue.poll()
                }
            }

            priorityQueue.sum()
        })


        // 还有一种就是随机读取, 然后找到最大的三个,而不需要管其他元素的排序 ..
    }

    fun findTopN(n: Int,elements: List<Int>): List<Int> {
        if(elements.size == n) return elements;

        val x = elements.random()
        val gt = elements.filter { it > x }
        val eq = elements.filter { it == x }
        val lt = elements.filter { it < x }

        if(gt.size >= n) {
            return findTopN(n,gt)
        }
        else if(gt.size + eq.size >= n) {
            return (eq + gt).takeLast(3)
        }
        // 大于 或者等于的 不超过三个,所以在从小的集合中选择其余的一个,加上 gt + eq
        return findTopN(n - gt.size - eq.size, lt) + gt + eq
    }

}