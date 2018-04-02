package jerry.githubuserapi.login

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import jerry.githubuserapi.BaseActivity
import jerry.githubuserapi.R
import jerry.githubuserapi.login.event.LoginTrialEvent
import jerry.githubuserapi.login.model.LoginFormSnapshot
import jerry.githubuserapi.login.viewcontroller.LoginFormViewController
import jerry.githubuserapi.login.viewcontroller.SignInButtonViewController
import kotlinx.android.synthetic.main.activity_login.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : BaseActivity() {
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private var mAuthTask: UserLoginTask? = null

    private lateinit var loginFormViewController: LoginFormViewController
    private lateinit var signInButtonViewController: SignInButtonViewController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginFormViewController = LoginFormViewController(
            activityScopeEventBus,
            findViewById(R.id.email),
            findViewById(R.id.password)
        )
        signInButtonViewController = SignInButtonViewController(
            activityScopeEventBus,
            findViewById(R.id.email_sign_in_button),
            loginFormViewController
        )
        activityScopeEventBus.register(this)
    }

    override fun onDestroy() {
        activityScopeEventBus.unregister(this)
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLoginTrialEventReceived(event: LoginTrialEvent) {
        val snapshot = event.loginFormSnapshot
        val validationResult = snapshot.validate()
        when {
            validationResult.isEmailAddressFormatInvalid ->
                loginFormViewController.onEmailAddressInvalid(validationResult.errorResId)
            validationResult.isPasswordFormatInvalid ->
                loginFormViewController.onPasswordInvalid(validationResult.errorResId)
            else -> attemptLogin(snapshot)
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private fun attemptLogin(snapshot: LoginFormSnapshot) {
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private fun showProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        val shortAnimTime =
            resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

        login_form.visibility = if (show) View.GONE else View.VISIBLE
        login_form.animate()
            .setDuration(shortAnimTime)
            .alpha((if (show) 0 else 1).toFloat())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    login_form.visibility = if (show) View.GONE else View.VISIBLE
                }
            })

        login_progress.visibility = if (show) View.VISIBLE else View.GONE
        login_progress.animate()
            .setDuration(shortAnimTime)
            .alpha((if (show) 1 else 0).toFloat())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    login_progress.visibility = if (show) View.VISIBLE else View.GONE
                }
            })
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    inner class UserLoginTask internal constructor(
        private val mEmail: String,
        private val mPassword: String
    ) : AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void): Boolean? {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000)
            } catch (e: InterruptedException) {
                return false
            }

            return DUMMY_CREDENTIALS
                .map { it.split(":") }
                .firstOrNull { it[0] == mEmail }
                ?.let {
                    // Account exists, return true if the password matches.
                    it[1] == mPassword
                }
                    ?: true
        }

        override fun onPostExecute(success: Boolean?) {
            mAuthTask = null
            showProgress(false)

            if (success!!) {
                finish()
            } else {
                password.error = getString(R.string.error_incorrect_password)
                password.requestFocus()
            }
        }

        override fun onCancelled() {
            mAuthTask = null
            showProgress(false)
        }
    }

    companion object {

        /**
         * A dummy authentication store containing known user names and passwords.
         * TODO: remove after connecting to a real authentication system.
         */
        private val DUMMY_CREDENTIALS = arrayOf("foo@example.com:hello", "bar@example.com:world")
    }
}
