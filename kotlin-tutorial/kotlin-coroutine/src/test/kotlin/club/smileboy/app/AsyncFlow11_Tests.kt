package club.smileboy.app

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

/**
 * @author JASONJ
 * @date 2022/6/19
 * @time 20:14
 * @description 异步流 ....
 *
 * contents
 *  - flow
 *  - sequence / normal iterator
 *  - type evolve
 *  - flow is cold / lazy
 *  - flow is cancellable
 *
 *
 **/
class AsyncFlow11_Tests {

    /**
     * 包含多个值的流
     */
    @Test
    fun multipleValues() {

        runBlocking {
            flow<Int> {
                repeat(100) {
                    emit(it)
                }
            }.collect {
                println(it)
            }
        }
    }

    // ------------------------------------- sequence ------------------------
    /**
     * cpu 暂停性计算 ...
     */
    @Test
    fun sequenceTests() {
        sequence<Int> {
            repeat(100) {
                TimeUnit.SECONDS.sleep(1)
                yield(it)
            }
        }
    }

    // ---------------------------------------- 类型演变 ----------------------------
    /**
     * 为了异步流,就像使用同步sequence<?> 一样,使用flow<?> 即可,只是它们的值是异步返回的 ..
     * 由于流是异步的,那么一般使用在协程中 ...
     * 它的大多数函数都是可暂停的 ...
     * flow 流构建器 内部是 可暂停的 ...
     */
    @Test
    fun flowUse() {
        fun simple(): Flow<Int> = flow { // flow builder
            for (i in 1..3) {
                delay(100) // pretend we are doing something useful here
                emit(i) // emit next value
            }
        }
    }

    // ------------------------------------ 流是冷的,惰性的 ------------------------
    @Test
    fun flowIsCold() {
        fun simple(): Flow<Int> = flow {
            println("Flow started")
            for (i in 1..3) {
                delay(100)
                emit(i)
            }
        }
        // invoke ,but action is not invoke ..
        simple().let {
            println("waiting for collec function invoke .... begin flow handle ...")
            runBlocking {
                // util collect invoke
                it.collect {
                    println(it)
                }

                println("again collect result was same ...")
                it.collect {
                    println(it)
                }
            }
        }

    }

    /**
     * 流正常的退出,而没有异常 ...
     */
    @Test
    fun flowIsCanncel() {
        fun simple(): Flow<Int> = flow {
            for (i in 1..3) {
                delay(100)
                println("Emitting $i")
                emit(i)
            }
        }
        runBlocking {
            simple().let {
                withTimeoutOrNull(60) {
                    it.collect {
                        println(it)
                    }
                }
            }
        }
    }
}