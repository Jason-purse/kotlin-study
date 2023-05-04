package club.smileboy.app.test

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.coroutineContext
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.junit.jupiter.api.Test
import kotlin.coroutines.EmptyCoroutineContext

class FlowTests {

    @Test
    fun emptyJobCoroutineContext() {
        runBlocking {
            println(
                // not empty
                CoroutineScope(newSingleThreadContext("flowOn"))
                    .coroutineContext.job
            )
        }
    }


    @Test
    fun test() = runBlocking {
        println(this.coroutineContext)
        // 相当于外面的 作用域还在 ..
        withContext(this.coroutineContext) {
            val context = this.coroutineContext
            flow {
                for (i in 1..10) {

                    launch {
                        println(context)
                        // 这个时候尝试切换,还可以用 ..
                        withContext(context) {
                            emit(i)
                        }
                    }
                }
            }
                .collect { println(it) }

        }
    }
}