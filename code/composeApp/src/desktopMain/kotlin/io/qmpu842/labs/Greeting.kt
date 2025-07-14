package io.qmpu842.labs

class Greeting {
    private val platform = getPlatform2()

    fun greet(): String {
        return "Hello, ${platform.name}!"
    }
}