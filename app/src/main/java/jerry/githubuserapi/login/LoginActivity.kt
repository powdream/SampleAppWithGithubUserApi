package jerry.githubuserapi.login

import android.os.Bundle
import android.support.annotation.MainThread
import android.widget.Toast
import jerry.githubuserapi.BaseActivity
import jerry.githubuserapi.BuildConfig
import jerry.githubuserapi.R
import jerry.githubuserapi.application.dataManager
import jerry.githubuserapi.application.model.ImmutableUser
import jerry.githubuserapi.login.event.LoginTrialEvent
import jerry.githubuserapi.login.model.LoginFormSnapshot
import jerry.githubuserapi.login.model.LoginTrialResult
import jerry.githubuserapi.login.repository.AuthenticatedUserRepository
import jerry.githubuserapi.login.viewcontroller.LoginFormViewController
import jerry.githubuserapi.login.viewcontroller.LoginProgressViewController
import jerry.githubuserapi.login.viewcontroller.SignInButtonViewController
import jerry.githubuserapi.userlist.AllUserActivity
import jerry.githubuserapi.util.intent.startActivitySimply
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
    private lateinit var loginProgressViewController: LoginProgressViewController

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
        loginProgressViewController = LoginProgressViewController(
            findViewById(R.id.login_form),
            findViewById(R.id.login_progress)
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
                loginProgressViewController.showProgress()
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
                is LoginTrialResult.Success -> onLoginSucceeded(loginTrialResult)
                is LoginTrialResult.Failure -> onLoginFailed(loginTrialResult)
            }
            loginProgressViewController.hideProgress()
        }

        // Set-up to clear ``loginJob`` on the job finished.
        job.invokeOnCompletion(onCancelling = true) {
            loginJob.compareAndSet(job, null)
        }

        return job
    }

    @MainThread
    private fun onLoginSucceeded(loginSuccess: LoginTrialResult.Success) {
        // Set the session information and exit from this activity.
        dataManager.updateSessionData {
            it.copy(authenticatedUser = ImmutableUser(loginSuccess.user))
        }
        startActivitySimply<AllUserActivity>()
        finish()
        if (BuildConfig.DEBUG) {
            Toast.makeText(this, R.string.sign_in_success, Toast.LENGTH_SHORT).show()
        }
    }

    @MainThread
    private fun onLoginFailed(loginFailure: LoginTrialResult.Failure) {
        val (cause, userId, password) = loginFailure
        loginFormViewController.onPasswordInvalid(R.string.error_incorrect_password)
        loge(cause) { "Login failure: userId=$userId, password=$password" }
    }
}
