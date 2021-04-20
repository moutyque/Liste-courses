package small.app.liste_courses.objects

enum class SIUnit(val value: String, val mutliplicator: Int) {
    EMPTY("", 1),
    KG("kg", 1),
    G("g", 100),
    L("L", 1),
    CL("cL", 10)

}