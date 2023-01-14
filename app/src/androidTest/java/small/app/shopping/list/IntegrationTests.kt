package small.app.shopping.list

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.adevinta.android.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.adevinta.android.barista.assertion.BaristaVisibilityAssertions.assertNotDisplayed
import com.adevinta.android.barista.assertion.BaristaVisibilityAssertions.assertNotExist
import com.adevinta.android.barista.interaction.BaristaClickInteractions.clickOn
import com.adevinta.android.barista.interaction.BaristaScrollInteractions.scrollTo
import com.adevinta.android.barista.rule.cleardata.ClearDatabaseRule
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import small.app.shopping.list.TestUtils.assertDepDoesNotExist
import small.app.shopping.list.TestUtils.assertItemDoesNotExist
import small.app.shopping.list.TestUtils.createAndCheckDep
import small.app.shopping.list.TestUtils.createAndCheckItem
import small.app.shopping.list.TestUtils.createAndCheckStore
import small.app.shopping.list.TestUtils.getDepViewMatcher
import small.app.shopping.list.TestUtils.interactWithDisplayedItemSubComponent


@LargeTest
@RunWith(AndroidJUnit4::class)
class IntegrationTests {

    @get:Rule
    var clearDatabaseRule = ClearDatabaseRule()

    @get:Rule
    var mainActivity = ActivityScenarioRule(MainActivity::class.java)

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
    fun createStore() {
        assertDisplayed("List")
        clickOn("List")
        createAndCheckStore("Store")
    }

    @Test
    fun createDep() {
        assertDisplayed("List")
        clickOn("List")
        createAndCheckStore("Store")
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
        createAndCheckStore("Store")
        createAndCheckDep("Legume")
        createAndCheckDep("Boucherie")
    }

    @Test
    fun createOneItem() {
        assertDisplayed("List")
        clickOn("List")
        createAndCheckStore("Store")
        createAndCheckDep("Legume")
        createAndCheckItem("Carotte", "Legume")
        clickOn("Parameters")
        assertDisplayed("Carotte")
        clickOn("Full Screen View")
        assertDisplayed("Carotte")
    }

    @Test
    fun createMultiItemsInSameDep() {
        clickOn("List")
        createAndCheckStore("Store")
        createAndCheckDep("Legume")
        createAndCheckItem("Carotte", "Legume")
        createAndCheckItem("Courgette", "Legume")
    }

    @Test
    fun createMultipleStore() {
        clickOn("List")
        createAndCheckStore("Store")
        createAndCheckDep("Legume")
        createAndCheckItem("Carotte", "Legume")
        createAndCheckItem("Courgette", "Legume")

        createAndCheckDep("Boucherie")
        scrollTo(getDepViewMatcher("Boucherie"))
        createAndCheckItem("Steak", "Boucherie")

        createAndCheckStore("Store2")
        assertNotExist("Store")
        assertNotDisplayed("Legume")
        assertNotDisplayed("Boucherie")
        assertNotDisplayed("Courgette")
        assertNotDisplayed("Carotte")
        assertNotDisplayed("Steak")
    }

    @Test
    fun switchBetweenStore() {
        clickOn("List")
        createAndCheckStore("Store")
        createAndCheckDep("Legume")
        createAndCheckItem("Carotte", "Legume")
        createAndCheckItem("Courgette", "Legume")

        createAndCheckDep("Boucherie")
        scrollTo(getDepViewMatcher("Boucherie"))
        createAndCheckItem("Steak", "Boucherie")

        createAndCheckStore("Store2")
        createAndCheckDep("Fruits")
        createAndCheckItem("Pommes", "Fruits")

        createAndCheckDep("Sec")
        scrollTo(getDepViewMatcher("Sec"))
        createAndCheckItem("Riz", "Sec")

        clickOn(R.id.s_store_dropdown)
        clickOn("Store")
        assertDisplayed("Legume")


    }

    @Test
    fun reuseItem() {
        clickOn("List")
        createAndCheckStore("Store")
        createAndCheckDep("Legume")
        createAndCheckItem("Carotte", "Legume")
        createAndCheckItem("Courgette", "Legume")
        interactWithDisplayedItemSubComponent("Carotte", R.id.iv_check_item).perform(click())
        assertNotDisplayed("Carotte")

    }

    @Test
    fun createMultiItemsInMultiDep() {
        assertDisplayed("List")
        clickOn("List")
        createAndCheckStore("Store")
        createAndCheckDep("Legume")
        createAndCheckDep("Boucherie")
        createAndCheckItem("Carotte", "Legume")
        scrollTo(getDepViewMatcher("Boucherie"))
        createAndCheckItem("Steak", "Boucherie")
    }


    @Test
    fun createAndUseItemFromFullView() {
        assertDisplayed("List")
        clickOn("List")
        createAndCheckStore("Store")
        createAndCheckDep("Legume")
        createAndCheckItem("Carotte", "Legume")
        createAndCheckItem("Courgette", "Legume")

        assertDisplayed("Full Screen View")
        clickOn("Full Screen View")
        assertDisplayed("Carotte")
        interactWithDisplayedItemSubComponent("Carotte", R.id.iv_check_item).perform(click())
        getDepViewMatcher("Legume")
        assertNotDisplayed("Carotte")
        clickOn("List")
        assertNotDisplayed(R.id.tv_name, "Carotte")
        assertItemDoesNotExist("Carotte")
    }

    @Test
    fun verfifyDepartmentDisapearFromFullScreen() {
        assertDisplayed("List")
        clickOn("List")
        createAndCheckStore("Store")
        createAndCheckDep("Legume")
        createAndCheckItem("Carotte", "Legume")
        assertDisplayed("Full Screen View")
        clickOn("Full Screen View")
        assertDisplayed("Carotte")
        interactWithDisplayedItemSubComponent("Carotte", R.id.iv_check_item).perform(click())
        assertItemDoesNotExist("Carotte")
        clickOn("List")
        assertDepDoesNotExist("Legumes")
    }

    @Test
    fun verfifyDepartmentFilteredByStoreInParamView() {
        assertDisplayed("List")
        clickOn("List")
        createAndCheckStore("Store")
        createAndCheckDep("Legume")
        createAndCheckItem("Carotte", "Legume")
        createAndCheckDep("Viandes")
        createAndCheckStore("Store2")
        assertDisplayed("Parameters")
        clickOn("Parameters")
        assertDepDoesNotExist("Legume")
    }

    @Test
    fun deleteStore() {
        clickOn("List")
        createAndCheckStore("Store")
        createAndCheckDep("Legume")
        createAndCheckItem("Carotte", "Legume")
        createAndCheckItem("Courgette", "Legume")

        createAndCheckDep("Boucherie")
        scrollTo(getDepViewMatcher("Boucherie"))
        createAndCheckItem("Steak", "Boucherie")

        clickOn("Parameters")
        clickOn(R.id.ib_delete_store)
        assertDepDoesNotExist("Boucherie")

    }

}
