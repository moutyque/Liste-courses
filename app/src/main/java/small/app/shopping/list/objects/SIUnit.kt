package small.app.shopping.list.objects

enum class SIUnit(val value: String, val mutliplicator: Int) {
    EMPTY("", 1),
    KG("kg", 1),
    G("g", 100),
    L("L", 1),
    CL("cL", 10);

    companion object {
        private val map = values().associateBy(SIUnit::value)
        fun fromValue(value: String): SIUnit = map[value]!!


        fun unitList() : ArrayList<String> {
           return arrayListOf<String>().apply {
               add(EMPTY.value)
               add(CL.value)
               add(L.value)
               add(G.value)
               add(KG.value)}
        }
    }
}