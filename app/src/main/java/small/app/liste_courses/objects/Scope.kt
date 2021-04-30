package small.app.liste_courses.objects

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import small.app.liste_courses.fragments.ListFragment
import small.app.liste_courses.fragments.ParamsFragment

object Scope {

    val backgroundScope = CoroutineScope(Job() + Dispatchers.IO)
    val mainScope = CoroutineScope(Job() + Dispatchers.Main)

    val listFrag = ListFragment()
    val paramFrag = ParamsFragment()
}