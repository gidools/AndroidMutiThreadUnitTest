package com.gidools.unittest.thread

class MyThread(name: String, private val loopCompletionCallback: OneLoopCompletionCallback? = null)
    : Thread(name) {

    private val eventList = arrayListOf<Any>()
    private var keepAlive = true

    fun startThread() {
        keepAlive = true
        start()
    }

    fun stopThread() {
        keepAlive = false
    }

    override fun run() {
        loopCompletionCallback?.onOneLoopCompleted(null)

        while(keepAlive) {
            if (eventList.size > 0) {
                val event = eventList.removeAt(0)
                loopCompletionCallback?.onOneLoopCompleted(event)
            }

            sleep(1)
        }

        loopCompletionCallback?.onOneLoopCompleted(null)
    }

    fun setEvent(event: Any) {
        eventList.add(event)
    }
}

interface OneLoopCompletionCallback {
    fun onOneLoopCompleted(event: Any?)
}
