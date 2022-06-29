package small.app.shopping.list

import android.content.Context
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withSpinnerText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.adevinta.android.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.adevinta.android.barista.assertion.BaristaVisibilityAssertions.assertNotDisplayed
import com.adevinta.android.barista.interaction.BaristaClickInteractions.clickOn
import com.adevinta.android.barista.rule.cleardata.ClearDatabaseRule
import junit.framework.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import small.app.shopping.list.TestUtils.changeUnit
import small.app.shopping.list.TestUtils.createAndCheckDep
import small.app.shopping.list.TestUtils.createAndCheckItem
import small.app.shopping.list.TestUtils.interactWithItemSubComponent


@LargeTest
@RunWith(AndroidJUnit4::class)
class BaristaTestCase {

    companion object {

    }

    @get:Rule
    var clearDatabaseRule = ClearDatabaseRule()

    @get:Rule
    var mainActivity = ActivityScenarioRule(MainActivity::class.java)

    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext

    private lateinit var scenario: ActivityScenario<MainActivity>

    @After
    fun tornApart() {
        mainActivity.scenario.close()
    }

    @Before
    fun setup() {
        scenario = mainActivity.scenario
    }

    @Test
    fun createDep() {
        assertDisplayed("List")
        clickOn("List")
        createAndCheckDep("Legume")
        clickOn("Parameters")
        assertDisplayed("Legume")
        clickOn("Full Screen View")
        assertDisplayed("Legume")

    }

    @Test
    fun createMultiDep() {
        assertDisplayed("List")
        clickOn("List")
        createAndCheckDep("Legume")
        createAndCheckDep("Boucherie")
    }

    @Test
    fun createOneItem() {
        assertDisplayed("List")
        clickOn("List")
        createAndCheckDep("Legume")
        createAndCheckItem("Carotte", 0)
        clickOn("Parameters")
        assertDisplayed("Carotte")
        clickOn("Full Screen View")
        assertDisplayed("Carotte")
    }

    @Test
    fun createMultiItemsInSameDep() {
        clickOn("List")
        createAndCheckDep("Legume")
        createAndCheckItem("Carotte", 0)
        createAndCheckItem("Courgette", 0)

    }

    @Test
    fun reuseItem() {
        clickOn("List")
        createAndCheckDep("Legume")
        createAndCheckItem("Carotte", 0)
        interactWithItemSubComponent("Carotte", R.id.iv_check_item).perform(click())
        assertNotDisplayed("Legume")
        assertNotDisplayed("Carotte")


    }

    @Test
    fun createMultiItemsInMultiDep() {
        assertDisplayed("List")
        clickOn("List")
        createAndCheckDep("Legume")
        createAndCheckDep("Boucherie")
        createAndCheckItem("Carotte", 0)
        createAndCheckItem("Steak", 1)
    }

    @Test
    fun modifyQty() {
        assertDisplayed("List")
        clickOn("List")
        createAndCheckDep("Legume")
        createAndCheckItem("Carotte", 0)
        createAndCheckItem("Courgette", 0)

        interactWithItemSubComponent("Carotte", R.id.iv_increase_qty).perform(
            click()
        )
        interactWithItemSubComponent("Carotte", R.id.iv_increase_qty).perform(
            click()
        )
        interactWithItemSubComponent("Carotte", R.id.tv_qty).check { view, _ ->
            assert(
                (view as TextView).text.equals(
                    "2"
                )
            )
        }

    }

    @Test
    fun modifyQtyAndUnit() {
        assertDisplayed("List")
        clickOn("List")
        createAndCheckDep("Legume")
        createAndCheckItem("Carotte", 0)
        createAndCheckItem("Courgette", 0)

        interactWithItemSubComponent("Carotte", R.id.iv_increase_qty).perform(
            click()
        )
        interactWithItemSubComponent("Carotte", R.id.iv_increase_qty).perform(
            click()
        )
        interactWithItemSubComponent("Carotte", R.id.tv_qty).check { view, _ ->
            assert(
                (view as TextView).text.equals(
                    "2"
                )
            )
        }

        clickOn("Parameters")

        assertDisplayed("Legume")

        interactWithItemSubComponent("Carotte", R.id.iv_increase_qty).perform(
            click()
        )
        interactWithItemSubComponent("Carotte", R.id.tv_qty).check { view, _ ->
            assert(
                (view as TextView).text.equals(
                    "3"
                )
            )
        }
        changeUnit("Carotte", "cL")
        interactWithItemSubComponent("Carotte", R.id.s_unit).check(matches(withSpinnerText("cL")))


    }

    @Test
    fun checkUtilsMethod() {
        clickOn("List")
        createAndCheckDep("depName")
        //createAndCheckDep("depName2")
        onView(TestUtils.getDepViewMatcher("depName")).check { view, _ ->

            val name = context.resources.getResourceEntryName(view.id)
            assertEquals("Actual resource name : $name", view.id, R.id.ll_departments)
        }

        createAndCheckItem("itemName", 0)
        onView(TestUtils.getItemViewMatcher("depName", "itemName")).check { view, _ ->

            val name = context.resources.getResourceEntryName(view.id)
            assertEquals("Actual resource name : $name", view.id, R.id.ll_container)
        }
    }


}