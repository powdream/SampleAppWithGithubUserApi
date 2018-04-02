package jerry.githubuserapi.login

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.support.annotation.MainThread
import android.view.View
import android.widget.Toast
import jerry.githubuserapi.BaseActivity
import jerry.githubuserapi.BuildConfig
import jerry.githubuserapi.R
import jerry.githubuserapi.application.dataManager
import jerry.githubuserapi.login.event.LoginTrialEvent
import jerry.githubuserapi.login.model.LoginFormSnapshot
import jerry.githubuserapi.login.model.LoginTrialResult
import jerry.githubuserapi.login.repository.AuthenticatedUserRepository
import jerry.githubuserapi.login.viewcontroller.LoginFormViewController
import jerry.githubuserapi.login.viewcontroller.SignInButtonViewController
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.atomic.AtomicReference

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : BaseActivity() {
    private val loginJob: AtomicReference<Job> = AtomicReference()

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
        if (loginJob.get() != null) {
            // There is a running log-in job.
            return
        }

        val snapshot = event.loginFormSnapshot
        val validationResult = snapshot.validate()
        when {
            validationResult.isEmailAddressFormatInvalid ->
                loginFormViewController.onEmailAddressInvalid(validationResult.errorResId)
            validationResult.isPasswordFormatInvalid ->
                loginFormViewController.onPasswordInvalid(validationResult.errorResId)
            else -> if (loginJob.compareAndSet(null, attemptLogin(snapshot))) {
                showProgress(true)
            }
        }
    }

    @MainThread
    private fun attemptLogin(snapshot: LoginFormSnapshot): Job {
        val job = launch(UI) {
            val (userId, password) = snapshot
            val loginTrialResult = AuthenticatedUserRepository()
                .getAuthenticatedUser(CommonPool, userId, password)
                .await()
            when (loginTrialResult) {
                is LoginTrialResult.Success -> {
                    this@LoginActivity.dataManager.authenticatedUser = loginTrialResult.user
                    if (BuildConfig.DEBUG) {
                        Toast
                            .makeText(
                                this@LoginActivity,
                                R.string.sign_in_success,
                                Toast.LENGTH_SHORT
                            )
                            .show()
                    }
                }
                is LoginTrialResult.Failure -> {
                    loge(loginTrialResult.cause) { "Login failure: userId=$userId, password=$password" }
                    loginFormViewController.onPasswordInvalid(R.string.error_incorrect_password)
                }
            }
            showProgress(false)
        }

        // Set-up to clear ``loginJob`` on the job finished.
        job.invokeOnCompletion(onCancelling = true) {
            loginJob.compareAndSet(job, null)
        }

        return job
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
}
