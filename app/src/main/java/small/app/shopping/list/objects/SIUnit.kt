package small.app.shopping.list.objects

enum class SIUnit(val value: String, val mutliplicator: Int) {
    EMPTY("", 1),
    KG("kg", 1),
    G("g", 100),
    L("L", 1),
    CL("cL", 10);

    companion object {
        private val map = SIUnit.values().associateBy(SIUnit::value)
        fun fromValue(value: String): SIUnit = map[value]!!
    }
}