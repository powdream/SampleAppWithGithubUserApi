package jerry.githubuserapi.login.viewcontroller

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.res.Resources
import android.view.View
import android.view.ViewPropertyAnimator
import jerry.githubuserapi.util.view.isVisible

class LoginProgressViewController(
    private val loginFormView: View,
    private val loginProgressView: View
) {
    private val resources: Resources
        get() = loginFormView.resources

    private val shortAnimTimeInMillis: Long =
        resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

    fun showProgress() {
        loginFormView.visibleToGone()
        loginProgressView.goneToVisible()
    }

    fun hideProgress() {
        loginFormView.goneToVisible()
        loginProgressView.visibleToGone()
    }

    private fun View.visibleToGone() {
        this.isVisible = true
        this.animate()
            .setDuration(shortAnimTimeInMillis)
            .alpha(0f)
            .doOnAnimationEnd {
                this@visibleToGone.isVisible = false
            }
    }

    private fun View.goneToVisible() {
        this.isVisible = false
        this.animate()
            .setDuration(shortAnimTimeInMillis)
            .alpha(1f)
            .doOnAnimationEnd {
                this@goneToVisible.isVisible = true
            }
    }

    private inline fun ViewPropertyAnimator.doOnAnimationEnd(
        crossinline action: () -> Unit
    ): ViewPropertyAnimator =
        setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) = action()
        })
}
