package dynamic

// Common Kotlin JVM shapes, used to verify how NovaLang resolves each of them.

object Singleton {
    fun greet(): String = "singleton-greet"

    @JvmStatic
    fun staticGreet(): String = "singleton-static"

    @JvmField
    val FIELD: String = "singleton-field"
}

class Holder(val name: String) {
    companion object {
        @JvmStatic
        fun jvmStatic(): String = "holder-jvm-static"

        fun plain(): String = "holder-plain"

        @JvmField
        val CONST: String = "holder-const"
    }
}

fun topLevel(): String = "top-level-fn"

data class Point(val x: Int, val y: Int)
