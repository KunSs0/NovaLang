package dynamic

interface KotlinInterfaceFixture {
    companion object {
        val INSTANCE: String = "interface-instance"

        fun plain(): String = "interface-plain"
    }
}
