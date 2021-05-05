package small.app.liste_courses.objects

import small.app.liste_courses.models.Department

class DepartmentComparator : Comparator<Department> {
    override fun compare(o1: Department?, o2: Department?): Int {
        if (o1 == null && o2 == null) return 0
        if (o1 == null) return -1
        if (o2 == null) return 1

        return o1.order.compareTo(o2.order)
    }
}