package io.qmpu842.labs

class JVMPlatform {
    val name: String = "Java ${System.getProperty("java.version")}"
}

fun getPlatform2() = JVMPlatform()
