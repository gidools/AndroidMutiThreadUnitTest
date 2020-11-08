package com.gidools.unittest.thread

import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.junit.rules.Timeout
import java.util.concurrent.Semaphore

class MyThreadTest {

    private lateinit var sut: MyThread
    private lateinit var oneLoopCompletionCallbackTd: OneLoopCompletionCallbackTd

    private val semaphore = Semaphore(0)

    @Before
    fun setup() {
        oneLoopCompletionCallbackTd = OneLoopCompletionCallbackTd(semaphore)
        sut = MyThread("my_thread", oneLoopCompletionCallbackTd)
    }

    @Test
    fun thread_success_threadStarted() {
        // Assign
        semaphore.drainPermits()
        oneLoopCompletionCallbackTd.isStarted = true

        // Act
        sut.startThread()
        sut.stopThread()
        sut.join()

        // Assert
        semaphore.acquireUninterruptibly()
    }

    @Test
    fun thread_success_threadStopped() {
        // Assign
        semaphore.drainPermits()
        oneLoopCompletionCallbackTd.isStarted = true
        oneLoopCompletionCallbackTd.isStopped = true

        // Act
        sut.startThread()
        sut.stopThread()
        sut.join()

        // Assert
        semaphore.acquireUninterruptibly()
        semaphore.acquireUninterruptibly()
    }

    @Test
    fun thread_success_walkEventHandled() {
        // Assign
        semaphore.drainPermits()
        oneLoopCompletionCallbackTd.isWalkEventHandled = true

        // Act
        sut.startThread()
        sut.setEvent(WalkEvent())
        Thread.sleep(1)
        sut.stopThread()
        sut.join()

        // Assert
        semaphore.acquireUninterruptibly()
    }

    @Test
    fun thread_success_runEventHandled() {
        // Assign
        semaphore.drainPermits()
        oneLoopCompletionCallbackTd.isRunEventHandled = true

        // Act
        sut.startThread()
        sut.setEvent(RunEvent())
        Thread.sleep(1)
        sut.stopThread()
        sut.join()

        // Assert
        semaphore.acquireUninterruptibly()
    }

    @Test
    fun thread_success_threadStoppedAfterAllEventHandled() {
        // Assign
        semaphore.drainPermits()
        oneLoopCompletionCallbackTd.isStarted = true
        oneLoopCompletionCallbackTd.isStopped = true
        oneLoopCompletionCallbackTd.isRunEventHandled = true
        oneLoopCompletionCallbackTd.isWalkEventHandled = true

        // Act
        sut.startThread()
        sut.setEvent(RunEvent())
        sut.setEvent(WalkEvent())
        Thread.sleep(10)
        sut.stopThread()
        sut.join()

        // Assert
        semaphore.acquireUninterruptibly()
        semaphore.acquireUninterruptibly()
        semaphore.acquireUninterruptibly()
        semaphore.acquireUninterruptibly()
    }

    private class OneLoopCompletionCallbackTd(private val semaphore: Semaphore) : OneLoopCompletionCallback {

        var isWalkEventHandled = false
        var isRunEventHandled = false
        var isStarted = false
        var isStopped = false

        override fun onOneLoopCompleted(event: Any?) {
            if (isStarted) {
                semaphore.release()
            }

            if (isStopped) {
                semaphore.release()
            }

            if (event is WalkEvent && isWalkEventHandled) {
                semaphore.release()
            }

            if (event is RunEvent && isRunEventHandled) {
                semaphore.release()
            }
        }
    }

    companion object {

        private const val TEST_TIMEOUT_MS = 1000

        @ClassRule
        @JvmField
        val timeout: Timeout = Timeout.millis(TEST_TIMEOUT_MS.toLong())
    }
}