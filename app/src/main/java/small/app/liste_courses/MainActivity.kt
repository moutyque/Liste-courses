package small.app.liste_courses

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import small.app.liste_courses.room.Repository

class MainActivity : AppCompatActivity() {

    lateinit var repo: Repository


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        repo = Repository(context = this)


    }
}