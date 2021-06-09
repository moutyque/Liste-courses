package small.app.liste_courses.callback

interface IMovableAdapter {

    fun canMove(): Boolean

    fun setMove(b: Boolean)

    fun getAdapterList(): List<Any>

    fun onItemMove(initialPosition: Int, targetPosition: Int)
}