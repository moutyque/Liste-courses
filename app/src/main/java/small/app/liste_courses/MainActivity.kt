package small.app.liste_courses

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import small.app.liste_courses.room.Repository
import small.app.liste_courses.room.entities.Item

class MainActivity : AppCompatActivity() {

    lateinit var repo: Repository


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        repo = Repository(context = this)
        val scope = CoroutineScope(Job() + Dispatchers.IO)


    }
}