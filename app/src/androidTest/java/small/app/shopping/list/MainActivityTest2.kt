package small.app.shopping.list


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.adevinta.android.barista.interaction.BaristaClickInteractions
import com.adevinta.android.barista.rule.cleardata.ClearDatabaseRule
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import small.app.shopping.list.TestUtils.createAndCheckDep
import small.app.shopping.list.TestUtils.createAndCheckItem

@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest2 {

    @get:Rule
    var clearDatabaseRule = ClearDatabaseRule()

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun mainActivityTest2() {
        BaristaClickInteractions.clickOn("List")
        createAndCheckDep("dep")
        createAndCheckItem("i1", 0)

        val materialAutoCompleteTextView3 = onView(
            allOf(
                withId(R.id.actv_selection_item),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.ll_items),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        materialAutoCompleteTextView3.perform(click())

        val appCompatImageButton3 = onView(
            allOf(
                withId(R.id.ib_newItems),
                childAtPosition(
                    allOf(
                        withId(R.id.cl_rv_dp_header),
                        childAtPosition(
                            withId(R.id.ll_departments),
                            0
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatImageButton3.perform(click())

        val materialAutoCompleteTextView4 = onView(
            allOf(
                withId(R.id.act_item_name_in_dep),
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
        materialAutoCompleteTextView4.perform(replaceText("i3"), closeSoftKeyboard())

        val materialButton2 = onView(
            allOf(
                withId(R.id.b_valid_item_name), withText("Ok"),
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
        materialButton2.perform(click())

        val materialAutoCompleteTextView5 = onView(
            allOf(
                withId(R.id.actv_selection_item),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.ll_items),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        materialAutoCompleteTextView5.perform(click())

        val materialAutoCompleteTextView6 = onView(
            allOf(
                withId(R.id.actv_selection_item),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.ll_items),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        materialAutoCompleteTextView6.perform(replaceText("i2"), closeSoftKeyboard())

        val appCompatImageButton4 = onView(
            allOf(
                withId(R.id.ib_add_item),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.ll_items),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatImageButton4.perform(click())

        val appCompatImageView = onView(
            allOf(
                withId(R.id.iv_dd),
                childAtPosition(
                    allOf(
                        withId(R.id.ll_complet_line),
                        childAtPosition(
                            withId(R.id.ll_container),
                            0
                        )
                    ),
                    4
                ),
                isDisplayed()
            )
        )
        appCompatImageView.perform(longClick())

        val materialAutoCompleteTextView7 = onView(
            allOf(
                withId(R.id.actv_selection_item),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.ll_items),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        materialAutoCompleteTextView7.perform(replaceText("i4"), closeSoftKeyboard())

        val appCompatImageButton5 = onView(
            allOf(
                withId(R.id.ib_add_item),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.ll_items),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatImageButton5.perform(click())

        val appCompatImageView2 = onView(
            allOf(
                withId(R.id.iv_dd),
                childAtPosition(
                    allOf(
                        withId(R.id.ll_complet_line),
                        childAtPosition(
                            withId(R.id.ll_container),
                            0
                        )
                    ),
                    4
                ),
                isDisplayed()
            )
        )
        appCompatImageView2.perform(longClick())
    }

    private fun childAtPosition(
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
}
