package jerry.githubuserapi.login.viewcontroller

import android.content.Context
import android.support.annotation.MainThread
import android.support.annotation.StringRes
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import jerry.githubuserapi.BuildConfig
import jerry.githubuserapi.login.event.LoginTrialEvent
import jerry.githubuserapi.login.model.LoginFormSnapshot
import jerry.githubuserapi.util.thread.ensureOnMainThread
import org.greenrobot.eventbus.EventBus

@MainThread
class LoginFormViewController(
    private val eventBus: EventBus,
    private val emailTextView: TextView,
    private val passwordTextView: TextView
) {
    init {
        passwordTextView.setOnEditorActionListener { _, id, _ ->
            onEditorActionPerformed(id)
        }
    }

    private val context: Context
        get() = emailTextView.context

    fun getLoginFormSnapshot(): LoginFormSnapshot = ensureOnMainThread {
        LoginFormSnapshot(
            emailTextView.text?.toString().orEmpty(),
            passwordTextView.text?.toString().orEmpty()
        )
    }

    fun onEmailAddressInvalid(@StringRes errorResId: Int) = ensureOnMainThread {
        if (BuildConfig.DEBUG) {
            check(errorResId != 0)
        }
        emailTextView.error = context.getString(errorResId)
        emailTextView.requestFocus()
        passwordTextView.error = null
    }

    fun onPasswordInvalid(@StringRes errorResId: Int) = ensureOnMainThread {
        if (BuildConfig.DEBUG) {
            check(errorResId != 0)
        }
        passwordTextView.error = context.getString(errorResId)
        passwordTextView.requestFocus()
        emailTextView.error = null
    }

    private fun onEditorActionPerformed(id: Int): Boolean = ensureOnMainThread {
        val shouldLogin = id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL
        if (shouldLogin) {
            eventBus.post(LoginTrialEvent(getLoginFormSnapshot()))
        }
        shouldLogin
    }
}
