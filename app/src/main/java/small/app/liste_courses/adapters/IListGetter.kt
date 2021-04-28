package small.app.liste_courses.adapters

interface IList<T> {


    fun contains(i: T): Boolean

    fun findIndex(i: T): Int
}