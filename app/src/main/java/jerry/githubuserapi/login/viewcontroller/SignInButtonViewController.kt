package jerry.githubuserapi.login.viewcontroller

import android.support.annotation.MainThread
import android.widget.Button
import jerry.githubuserapi.login.event.LoginTrialEvent
import jerry.githubuserapi.util.thread.ensureOnMainThread
import org.greenrobot.eventbus.EventBus

@MainThread
class SignInButtonViewController(
    private val eventBus: EventBus,
    signInButton: Button,
    private val loginFormViewController: LoginFormViewController
) {
    init {
        signInButton.setOnClickListener { onSignInButtonClicked() }
    }

    private fun onSignInButtonClicked() = ensureOnMainThread {
        eventBus.post(LoginTrialEvent(loginFormViewController.getLoginFormSnapshot()))
    }
}
