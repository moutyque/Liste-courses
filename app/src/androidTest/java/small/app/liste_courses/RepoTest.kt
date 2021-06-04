package small.app.liste_courses

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)


class RepoTest {

    /*   private lateinit var repo: Repository
       lateinit var instrumentationContext: Context
       private val backgroundScope = CoroutineScope(Job() + Dispatchers.IO)

       @Before
       fun createRepo() {
           instrumentationContext = InstrumentationRegistry.getInstrumentation().targetContext
           repo = Repository(instrumentationContext)
       }

       @Test
       fun saveDepartmentWithItems() {
           val list = ArrayList<Item>()
           list.add(Item("POMME"))
           list.add(Item("BANANNE"))
           list.add(Item("POIRE"))
           val newDep = small.app.liste_courses.models.Department("FRUITS", list, 0)

           backgroundScope.launch {
               repo.saveDepartment(newDep)
               val departmentByName = repo.getDepartmentByName("FRUITS")
               for (i in departmentByName.items) {
                   Assert.assertEquals("", i.departmentId, "FRUITS")
                   Assert.assertEquals("", i.isClassified, true)
               }

               Assert.assertEquals("", departmentByName, newDep)
           }

       }

       @Test
       fun notExistingDepartment() {

           backgroundScope.launch { assert(!repo.departmentExist("NULL")) }
       }


       @Test
       @Throws(Exception::class)
       fun classifyItemWithRepo() {
           backgroundScope.launch {
               var order = 0
               val dep = Department("FRUITS", emptyList(), order++)
               repo.saveDepartment(dep)

               repo.saveDepartment(Department("LEGUMES", emptyList(), order++))
               repo.saveDepartment(Department("VIANDES", emptyList(), order++))


               val depa = repo.getDepartmentByName("FRUITS")
               ViewMatchers.assertThat(depa.name, CoreMatchers.equalTo("FRUITS"))

               Assert.assertEquals(
                   "The name of the department does not match",
                   "FRUITS",
                   depa.name
               )

               val item: Item = Item("POMME")
               dep.classify(item)
               repo.useItem(item)

               var it = repo.getUnclassifiedItem()[0]
               Assert.assertEquals(
                   "The item should be unclassified",
                   false,
                   it.isClassified
               )
               Assert.assertEquals(
                   "The item should be used",
                   true,
                   it.isUsed
               )

               val itemsFromDepartment = repo.getDepartmentByName("FRUITS")
               Assert.assertEquals(
                   "The name does not match",
                   "POMME",
                   itemsFromDepartment.items.get(0).name
               )
               Assert.assertEquals(
                   "The classification should be true",
                   true,
                   itemsFromDepartment.items.get(0).isClassified
               )

               Assert.assertEquals(
                   "The usage should be true",
                   true,
                   itemsFromDepartment.items.get(0).isUsed
               )

               val clone: Item = Item("POMME")
               repo.useItem(clone)


               Assert.assertEquals(
                   "There should be no more unclassified item",
                   0,
                   repo.getUnclassifiedItem().size
               )
               Assert.assertEquals(
                   "There should be no more unused item",
                   0,
                   repo.getUnusedItems().size
               )
           }

       }*/
}