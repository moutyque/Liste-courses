package small.app.liste_courses.adapters.listeners

import small.app.liste_courses.models.Department
import small.app.liste_courses.room.entities.Item

interface IActions {

    fun onNewDepartment(d : Department)

    fun onItemChange(i : Item)

    fun onItemClassify(i : Item)
}