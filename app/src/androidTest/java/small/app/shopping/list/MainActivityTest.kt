package small.app.shopping.list


import android.util.Log
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.adevinta.android.barista.rule.cleardata.ClearDatabaseRule
import org.hamcrest.Matchers.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import small.app.shopping.list.TestUtils.childAtPosition
import small.app.shopping.list.TestUtils.childAtPositionInRecyclerView
import small.app.shopping.list.TestUtils.childInRecyclerView
import small.app.shopping.list.TestUtils.viewInRecyclerView

@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @get:Rule
    var clearDatabaseRule = ClearDatabaseRule()


    @Test
    fun mainActivityTest() {
        val tabView = onView(
            allOf(
                withContentDescription("List"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.tabLayout),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        tabView.perform(click())

        val tabView2 = onView(
            allOf(
                withContentDescription("List"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.tabLayout),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        tabView2.perform(click())

        val materialAutoCompleteTextView = onView(
            allOf(
                withId(R.id.act_departmentName),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.ll_departments),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        materialAutoCompleteTextView.perform(replaceText("legumes"), closeSoftKeyboard())

        val appCompatImageButton = onView(
            allOf(
                withId(R.id.ib_add_department),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.ll_departments),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatImageButton.perform(click())

        val appCompatImageButton2 = onView(
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
        appCompatImageButton2.perform(click())

        val materialAutoCompleteTextView2 = onView(
            allOf(
                withId(R.id.act_item_name_in_dep),
                childAtPosition(
                    allOf(
                        withId(R.id.constraintLayout),
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
        materialAutoCompleteTextView2.perform(click())

        val materialAutoCompleteTextView3 = onView(
            allOf(
                withId(R.id.act_item_name_in_dep),
                childAtPosition(
                    allOf(
                        withId(R.id.constraintLayout),
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
        materialAutoCompleteTextView3.perform(replaceText("carottes"), closeSoftKeyboard())

        val materialButton = onView(
            allOf(
                withId(R.id.b_valid_item_name), withText("Ok"),
                childAtPosition(
                    allOf(
                        withId(R.id.constraintLayout),
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
                        withId(R.id.constraintLayout),
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
        materialAutoCompleteTextView4.perform(replaceText("courgette"), closeSoftKeyboard())

        val materialButton2 = onView(
            allOf(
                withId(R.id.b_valid_item_name), withText("Ok"),
                childAtPosition(
                    allOf(
                        withId(R.id.constraintLayout),
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


/*        val appCompatImageView = onView(
            allOf(
                withId(R.id.iv_increase_qty),
                childAtPosition(
                    allOf(
                        withId(R.id.ll_qty_modifiers),
                        childAtPosition(
                            withId(R.id.ll_always_visible),
                            1
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatImageView.perform(click())*/

        val appCompatImageView2 = onView(
            allOf(
                withId(R.id.iv_increase_qty),
                childAtPosition(
                    allOf(
                        withId(R.id.ll_qty_modifiers),
                        childAtPosition(
                            withId(R.id.ll_always_visible),
                            1
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        /*  appCompatImageView2 = onView(
              allOf(
                  withId(R.id.iv_increase_qty),
                  childAtPosition(
                      TestUtils.viewInRecyclerView(R.id.ll_always_visible, R.id.ll_qty_modifiers, 1),
                      0
                  ),
                  isDisplayed()
              )
          )*/

        onView(
            childAtPosition(
                withId(R.id.rv_department),
                0//Dep id
            )
        ).check { view, noViewFoundException ->

            val name = mActivityTestRule.activity.resources.getResourceEntryName(view.id)
            Log.d(
                "GET_VIEW", name
            )
        }

        //Check we get the items view
        onView(
            allOf(
                withId(R.id.iv_increase_qty),
                childAtPosition(
                    withId(R.id.rv_department),
                    0//Dep id
                ),
                childAtPosition(withId(R.id.rv_items), 0)
            )

        ).check { view, noViewFoundException ->

            val name = mActivityTestRule.activity.resources.getResourceEntryName(view.id)
            Log.d(
                "GET_VIEW",
                name
            )
        }
        onView(
            childInRecyclerView(
                childAtPositionInRecyclerView(
                    viewInRecyclerView(
                        R.id.ll_qty_modifiers,
                        R.id.ll_always_visible,
                        1
                    ), 0
                ),
                R.id.iv_increase_qty
            )
        ).perform(click())


        val appCompatImageView3 = onView(
            allOf(
                withId(R.id.iv_increase_qty),
                childAtPosition(
                    allOf(
                        withId(R.id.ll_qty_modifiers),
                        childAtPosition(
                            withId(R.id.ll_always_visible),
                            1
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatImageView3.perform(click())

        val appCompatImageView4 = onView(
            allOf(
                withId(R.id.iv_decrease_qty),
                childAtPosition(
                    allOf(
                        withId(R.id.ll_qty_modifiers),
                        childAtPosition(
                            withId(R.id.ll_always_visible),
                            1
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatImageView4.perform(click())
    }

}
