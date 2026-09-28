package dynamic

/**
 * Real Kotlin fixture for the companion-object regression described in
 * docs/文档/Kotlin Companion Java 互操作问题.md
 *
 * kotlinc turns a `companion object` into:
 *   - a nested class `StaticFixture$Companion`
 *   - a static field `Companion` on `StaticFixture` of that nested type
 *   - the declared methods as *instance* methods on `StaticFixture$Companion`
 *
 * So `StaticFixture.Companion.getMapping(...)` means: read the static field,
 * then invoke an instance method -- NOT a static call on the nested class.
 */
class StaticFixture {

    companion object {
        fun getMapping(id: String): String = "mapping:$id"

        fun getINSTANCE(): String = "instance"
    }

    fun currentTick(): Long = 42L

    class Nested(val value: String) {
        fun value(): String = value
    }
}
