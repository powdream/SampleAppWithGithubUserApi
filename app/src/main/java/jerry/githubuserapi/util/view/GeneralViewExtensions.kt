package jerry.githubuserapi.util.view

import android.view.View

/**
 * A property extension to limit the visibility state up to two states (true/false).
 */
var View?.isVisible: Boolean
    get() = this?.visibility == View.VISIBLE
    set(value) {
        this?.visibility = if (value) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }
