package small.app.shopping.list

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.IdlingPolicies
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.adevinta.android.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.adevinta.android.barista.interaction.BaristaAutoCompleteTextViewInteractions.writeToAutoComplete
import com.adevinta.android.barista.interaction.BaristaClickInteractions.clickOn
import com.adevinta.android.barista.interaction.BaristaListInteractions.clickListItemChild
import com.adevinta.android.barista.rule.cleardata.ClearDatabaseRule
import org.junit.*
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit


@LargeTest
@RunWith(AndroidJUnit4::class)
class BaristaTestCase {

    companion object{
        @BeforeClass
        fun beforeClass() {
            IdlingPolicies.setMasterPolicyTimeout(10, TimeUnit.SECONDS)
            IdlingPolicies.setIdlingResourceTimeout(10, TimeUnit.SECONDS)
        }

    }
    @get:Rule
    var clearDatabaseRule = ClearDatabaseRule()

    @get:Rule
    var mainActivity = ActivityScenarioRule(MainActivity::class.java)


    lateinit var scenario: ActivityScenario<MainActivity>

    @After
    fun tornApart(){
        mainActivity.scenario.close()
    }

    @Before
    fun setup(){
        scenario = mainActivity.scenario
    }


   /* @get:Rule
    var chain: RuleChain = RuleChain.outerRule(clearDatabaseRule)
        .around(mActivityTestRule)*/

    @Test
    fun createDep(){
        assertDisplayed("List")
        clickOn("List")
        createAndCheckDep("Legume")
        clickOn("Parameters")
        assertDisplayed("Legume")
        clickOn("Full Screen View")
        assertDisplayed("Legume")

    }

    @Test
    fun createMultiDep(){
        assertDisplayed("List")
        clickOn("List")
        createAndCheckDep("Legume")
        createAndCheckDep("Boucherie")
    }

    @Test
    fun createOneItem(){
        assertDisplayed("List")
        clickOn("List")
        createAndCheckDep("Legume")
        createAndCheckItem("Carotte",0)
        clickOn("Parameters")
        assertDisplayed("Carotte")
        clickOn("Full Screen View")
        assertDisplayed("Carotte")
    }

    @Test
    fun createMultiItemsInSameDep(){
        clickOn("List")
        createAndCheckDep("Legume")
        createAndCheckItem("Carotte",0)
        createAndCheckItem("Courgette",0)

    }

    @Test
    fun createMultiItemsInMultiDep(){
        assertDisplayed("List")
        clickOn("List")
        createAndCheckDep("Legume")
        createAndCheckDep("Boucherie")
        createAndCheckItem("Carotte",0)
        createAndCheckItem("Steak",1)
    }

    @Test
    fun createModifyQty(){
        assertDisplayed("List")
        clickOn("List")
        createAndCheckDep("Legume")
        createItemFromDep("Carotte",0)
        assertDisplayed("Carotte")


    }
    private fun createAndCheckItem(name : String,dep_position: Int) {
        createItemFromDep(name,dep_position)
        assertDisplayed(name)
    }
    private fun createAndCheckDep(name : String) {
        createDep(name)
        assertDisplayed(name)
    }


    private fun createItemFromDep(item_name: String, dep_position : Int){
        clickListItemChild(R.id.rv_department,dep_position,R.id.ib_newItems)
        writeToAutoComplete(
            R.id.act_item_name_in_dep,
            item_name
        )
        clickOn(R.id.b_valid_item_name)
    }

    private fun createDep(name : String) {
        writeToAutoComplete(
            R.id.act_departmentName,
            name
        )
        clickOn(R.id.ib_add_department)
    }
}