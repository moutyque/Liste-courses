package small.app.shopping.list.objects

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

object Scope {

    val backgroundScope = CoroutineScope(Job() + Dispatchers.IO)
    val mainScope = CoroutineScope(Job() + Dispatchers.Main)

}