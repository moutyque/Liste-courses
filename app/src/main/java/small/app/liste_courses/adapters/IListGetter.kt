package small.app.liste_courses.adapters

interface IListGetter<T> {

    fun getList(): MutableList<T>
}