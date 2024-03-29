package small.app.shopping.list

import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.*
import com.adevinta.android.barista.assertion.BaristaVisibilityAssertions
import com.adevinta.android.barista.interaction.BaristaAutoCompleteTextViewInteractions
import com.adevinta.android.barista.interaction.BaristaClickInteractions
import com.adevinta.android.barista.internal.performAction
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher


//https://developer.android.com/static/images/training/testing/espresso-cheatsheet.png
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
        allOf(
            isDescendantOfA(
                allOf(
                    hasDescendant(withText(itemName)),
                    withId(R.id.ll_complet_line)
                )
            ), withId(subComponentId)
        )

    )

    fun interactWithDisplayedItemSubComponent(
        itemName: String,
        subComponentId: Int
    ): ViewInteraction = onView(
        allOf(
            isDescendantOfA(allOf(hasDescendant(withText(itemName)), withId(R.id.ll_complet_line))),
            withId(subComponentId),
            isDisplayed()
        )

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

    fun createAndCheckItem(name: String, depName: String) {
        createItemFromDep(name, depName)
        BaristaVisibilityAssertions.assertDisplayed(name)
    }

    fun createAndCheckDep(name: String) {
        createDep(name)
        BaristaVisibilityAssertions.assertDisplayed(name)
    }

    fun createAndCheckStore(name: String) {
        createStore(name)
        BaristaVisibilityAssertions.assertDisplayed(name)
    }

    private fun createStore(storeName: String) {
        val appCompatImageButton = onView(
            allOf(
                withId(R.id.ib_add_store),
                childAtPosition(
                    allOf(
                        withId(R.id.ll_stores),
                        childAtPosition(
                            withId(R.id.frameLayout),
                            0
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatImageButton.perform(click())

        val materialAutoCompleteTextView = onView(
            allOf(
                withId(R.id.act_store_name),
                childAtPosition(
                    allOf(
                        withId(R.id.cl_rv_dp_header),
                        childAtPosition(
                            withId(android.R.id.content),
                            0
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        materialAutoCompleteTextView.perform(
            replaceText(storeName),
            closeSoftKeyboard()
        )

        val materialButton = onView(
            allOf(
                withId(R.id.b_valid_item_name),
                childAtPosition(
                    allOf(
                        withId(R.id.cl_rv_dp_header),
                        childAtPosition(
                            withId(android.R.id.content),
                            0
                        )
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        materialButton.perform(click())
    }

    private fun createItemFromDep(itemName: String, depName: String) {
        //View with id, ancestor has an other child with depName
        val depViewMatcher = withChild(allOf(withId(R.id.tv_dep_name), withText(depName)))
        allOf(
            isDescendantOfA(depViewMatcher),
            withId(R.id.ib_newItems)
        ).performAction(click())
        BaristaAutoCompleteTextViewInteractions.writeToAutoComplete(
            R.id.act_item_name,
            itemName
        )
        BaristaClickInteractions.clickOn(R.id.b_valid_item_name)
    }

    private fun createDep(name: String) {
        BaristaAutoCompleteTextViewInteractions.writeToAutoComplete(
            R.id.act_departmentName,
            name
        )
        BaristaClickInteractions.clickOn(R.id.ib_add_department)
    }

    fun assertDepDoesNotExist(depName: String) {
        onView(
            allOf(
                withId(R.id.tv_dep_name), withText(depName),
                withParent(
                    allOf(
                        withParent(withId(R.id.ll_complet_line))
                    )
                ),
                isDisplayed()
            )
        ).check(ViewAssertions.doesNotExist())
    }

    fun assertItemDoesNotExist(itemName: String) {
        onView(
            allOf(
                withId(R.id.tv_name), withText(itemName),
                withParent(
                    allOf(
                        withId(R.id.ll_complet_line),
                        withParent(withId(R.id.ll_container))
                    )
                ),
                isDisplayed()
            )
        ).check(ViewAssertions.doesNotExist())
    }

    fun triggerProposition(id: Int, text: String) {
        triggerProposition(
            allOf(
                withId(id),
                isDisplayed()
            ), text
        )
    }

    fun triggerProposition(matcher: Matcher<View>, text: String) {
        onView(
            matcher
        ).perform(
            replaceText(text),
            closeSoftKeyboard()
        )
    }
}
