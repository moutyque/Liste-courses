package small.app.shopping.list

import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.*
import com.adevinta.android.barista.assertion.BaristaVisibilityAssertions
import com.adevinta.android.barista.interaction.BaristaAutoCompleteTextViewInteractions
import com.adevinta.android.barista.interaction.BaristaClickInteractions
import com.adevinta.android.barista.interaction.BaristaListInteractions
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher

object TestUtils {

    /**
     * Find a view child at {@position} inside the view that match {@parentMatcher]
     */
    fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }


    fun interactWithItemSubComponent(
        itemName: String,
        subComponentId: Int
    ): ViewInteraction = onView(
        allOf(isDescendantOfA(allOf(hasDescendant(withText(itemName)), withId(R.id.ll_complet_line))),withId(subComponentId),
            isDisplayed())

    )

    internal fun getItemViewMatcher(
        depName: String,
        itemName: String
    ): Matcher<View> = allOf(
        isDescendantOfA(getDepViewMatcher(depName)),
        hasDescendant(withText(itemName)),
        withId(R.id.ll_container),
        isDisplayed()
    )

    internal fun getDepViewMatcher(
        depName: String
    ): Matcher<View> =
        allOf(
            isDescendantOfA(withId(R.id.rv_department)),
            hasDescendant(withText(depName)),
            withId(R.id.ll_departments),
            isDisplayed()
        )

    fun changeUnit(itemName: String,unit : String){
        interactWithItemSubComponent(itemName, R.id.s_unit).perform(
            click()
        )

        onView(
            allOf(
                isDescendantOfA(
                    allOf(
                        withId(R.id.select_dialog_listview),
                        childAtPosition(
                            withId(R.id.contentPanel),
                            0
                        )
                    )
                ),
                withText(unit)
            )
        ).perform(click())
    }

    fun createAndCheckItem(name: String, dep_position: Int) {
        createItemFromDep(name, dep_position)
        BaristaVisibilityAssertions.assertDisplayed(name)
    }

    fun createAndCheckDep(name: String) {
        createDep(name)
        BaristaVisibilityAssertions.assertDisplayed(name)
    }


    fun createItemFromDep(item_name: String, dep_position: Int) {
        BaristaListInteractions.clickListItemChild(
            R.id.rv_department,
            dep_position,
            R.id.ib_newItems
        )
        BaristaAutoCompleteTextViewInteractions.writeToAutoComplete(
            R.id.act_item_name_in_dep,
            item_name
        )
        BaristaClickInteractions.clickOn(R.id.b_valid_item_name)
    }

    fun createDep(name: String) {
        BaristaAutoCompleteTextViewInteractions.writeToAutoComplete(
            R.id.act_departmentName,
            name
        )
        BaristaClickInteractions.clickOn(R.id.ib_add_department)
    }
}