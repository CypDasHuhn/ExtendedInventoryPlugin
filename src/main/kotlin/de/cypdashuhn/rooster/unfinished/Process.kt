package de.cypdashuhn.rooster.unfinished

import de.cypdashuhn.rooster.core.Rooster.cache
import de.cypdashuhn.rooster.core.Rooster.plugin
import kotlinx.coroutines.*
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import kotlin.coroutines.coroutineContext

/** These functions haven't been tested, better not to use! */

data class RepeatData(
    val repeatCount: Int = 1,
    val offset: Long = 0,
    val infinite: Boolean = false
)

data class TimeOutData(
    val timeOutLength: Long = 0,
    val timeOutError: () -> Unit
)


data class ProgressData<T>(
    val offset: Long,
    val value: MutableValue<T>,
    val progressHandler: suspend (MutableValue<T>) -> Unit,
)

data class MutableValue<T>(var value: T)

suspend fun <T> startWithProgress(
    delay: Long = 0,
    repeatData: RepeatData? = null,
    async: Boolean = false,
    timeOutData: TimeOutData? = null,
    progressData: ProgressData<T>,
    action: suspend (data: MutableValue<T>) -> Unit
): Job {
    return start(delay, repeatData, async, timeOutData) {
        val job = coroutineContext.job
        val currentState = progressData.value

        val progressJob = CoroutineScope(coroutineContext).launch {
            while (job.isActive) {
                val progress = progressData.progressHandler(currentState)
                println("Progress: $progress")
                delay(progressData.offset)
            }
        }

        try {
            action(currentState)
        } finally {
            progressJob.cancel()
        }
    }
}

suspend fun start(
    delay: Long = 0,
    repeatData: RepeatData? = null,
    async: Boolean = false,
    timeOutData: TimeOutData? = null,
    action: suspend () -> Unit
): Job {
    val scope = if (async) CoroutineScope(Dispatchers.Default) else CoroutineScope(Dispatchers.IO)

    delay(delay)
    return scope.launch {
        repeatData?.let { repeatInfo ->
            if (repeatInfo.infinite) {
                while (isActive) {
                    performActionWithTimeout(timeOutData, action)
                    delay(repeatInfo.offset)
                }
            } else {
                repeat(repeatInfo.repeatCount) {
                    performActionWithTimeout(timeOutData, action)
                    delay(repeatInfo.offset)
                }
            }
        } ?: performActionWithTimeout(timeOutData, action)
    }
}

suspend fun performActionWithTimeout(
    timeOutData: TimeOutData? = null,
    action: suspend () -> Unit
) {
    val actionJob = coroutineScope {
        async {
            try {
                withTimeout(timeOutData?.timeOutLength ?: Long.MAX_VALUE) {
                    action()
                }
            } catch (e: TimeoutCancellationException) {
                timeOutData?.timeOutError?.invoke()
                null
            }
        }
    }
    actionJob.await()
}

data class PlayerInputState(
    var isProcessing: Boolean = false,
    val inputQueue: MutableList<(suspend () -> Unit)> = mutableListOf()
)

@OptIn(DelicateCoroutinesApi::class)
fun noOverlap(cacheKey: String, player: Player, action: suspend (Player) -> Unit) {
    GlobalScope.launch {
        val inputState = cache.get(cacheKey, player, {
            PlayerInputState()
        })

        inputState.inputQueue.add {
            Bukkit.getScheduler().runTask(plugin, Runnable {
                launch {
                    action(player)
                } // Ensure the suspend function runs in a coroutine but is triggered on the main thread
            })
        }

        if (!inputState.isProcessing) {
            processQueuedInputs(cacheKey, player, inputState)
        }
    }
}

private suspend fun processQueuedInputs(cacheKey: String, player: Player, inputState: PlayerInputState) {
    inputState.isProcessing = true
    while (inputState.inputQueue.isNotEmpty()) {
        val nextInput = inputState.inputQueue.removeAt(0)
        nextInput()
    }
    inputState.isProcessing = false

    cache.invalidate(cacheKey, player)
}