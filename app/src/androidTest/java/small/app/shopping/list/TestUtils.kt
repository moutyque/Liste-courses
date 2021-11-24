package small.app.shopping.list

import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.matcher.ViewMatchers.withId
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

    /*
    onView(
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
     */


    fun viewInRecyclerView(parent_id: Int, child_id: Int, position: Int): Matcher<View> {
        return allOf(
            withId(child_id),
            childAtPosition(
                withId(parent_id),
                position
            )
        )
    }

    fun viewInRecyclerView(parent: Matcher<View>, child_id: Int, position: Int): Matcher<View> {
        return allOf(
            withId(child_id),
            childAtPosition(
                parent,
                position
            )
        )
    }

    fun childAtPositionInRecyclerView(parent: Matcher<View>, position: Int): Matcher<View> {
        return childAtPosition(
            parent,
            position
        )
    }

    fun childInRecyclerView(parent: Matcher<View>, child_id: Int): Matcher<View> {
        return allOf(
            withId(child_id),
            parent
        )
    }
}