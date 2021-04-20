package small.app.liste_courses.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.SortedList
import small.app.liste_courses.objects.Utils
import small.app.liste_courses.room.entities.Item

class ListFragmentViewModel(application: Application) : AndroidViewModel(application) {


    fun getUnclassifiedItems(): LiveData<List<Item>> {
        return Utils.repo.getUnclassifiedItem()
    }

}