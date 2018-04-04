package jerry.githubuserapi.userdetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.MainThread
import android.support.annotation.Size
import jerry.githubuserapi.BaseActivity
import jerry.githubuserapi.R
import jerry.githubuserapi.userdetail.event.UserDetailActivityUserEvent
import jerry.githubuserapi.userdetail.model.FetchUserDetailViewModelResult
import jerry.githubuserapi.userdetail.model.UserDetailViewModel
import jerry.githubuserapi.userdetail.repository.UserDetailViewModelRepository
import jerry.githubuserapi.userdetail.viewcontroller.UserDetailViewController
import jerry.githubuserapi.util.intent.activityIntent
import jerry.githubuserapi.util.thread.ensureOnMainThread
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class UserDetailActivity : BaseActivity() {
    private lateinit var repository: UserDetailViewModelRepository
    private lateinit var userDetailViewController: UserDetailViewController

    @Size(min = 1L)
    private lateinit var loginId: String

    private var fetchUserDetailDataJob: Job? = null
    private var userDetailViewModel: UserDetailViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail)
        setSupportActionBar(findViewById(R.id.toolbar))

        userDetailViewController = UserDetailViewController(
            activityScopeEventBus,
            supportActionBar ?: error("actionBar doesn't exist."),
            findViewById(R.id.user_detail_avatar),
            findViewById(R.id.user_detail_web_view),
            findViewById(R.id.fab)
        )

        loginId = parseIntent(intent)

        supportActionBar?.title = ""
        repository = UserDetailViewModelRepository(this, CommonPool)
        fetchUserDetailDataJob = attemptToFetchUserDetail()

        activityScopeEventBus.register(this)
    }

    override fun onDestroy() {
        activityScopeEventBus.unregister(this)
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUserDetailActivityUserEventReceived(
        event: UserDetailActivityUserEvent
    ) {
        logv { "onUserDetailActivityUserEventReceived() - event=$event" }
        when (event) {
            UserDetailActivityUserEvent.FETCH_RETRY_ACTION_CLICKED -> {
                fetchUserDetailDataJob?.cancel()
                fetchUserDetailDataJob = attemptToFetchUserDetail()
            }

            UserDetailActivityUserEvent.EMAIL_BUTTON_CLICKED -> {
                val captureUserDetailViewModel = userDetailViewModel
                if (captureUserDetailViewModel?.email?.isNotEmpty() != true) {
                    userDetailViewController.onInvalidEmailButtonClicked()
                } else {
                    // TODO: Try to send e-mail.
                }
            }
        }
    }

    @MainThread
    private fun attemptToFetchUserDetail(): Job = launch(UI) {
        val fetchResult = repository.fetchUserDetailViewModel(loginId).await()
        onFetchUserDetailViewModelResultReceived(fetchResult)
    }

    @MainThread
    private fun onFetchUserDetailViewModelResultReceived(
        fetchResult: FetchUserDetailViewModelResult
    ) = ensureOnMainThread {
        when (fetchResult) {
            is FetchUserDetailViewModelResult.Success -> {
                userDetailViewController.applyUserDetailViewModel(fetchResult.userDetailViewModel)
                userDetailViewModel = fetchResult.userDetailViewModel
            }
            is FetchUserDetailViewModelResult.Failure -> {
                loge(fetchResult.cause) {
                    "onFetchUserDetailViewModelResultReceived() - fetch failed."
                }
                supportActionBar?.title = loginId
                userDetailViewController.onFetchFailure()
            }
        }
    }

    companion object {
        private const val EXTRA_LOGIN_ID = "extra-login-id"

        fun createIntent(context: Context, loginId: String): Intent =
            context.activityIntent<UserDetailActivity>().putExtra(EXTRA_LOGIN_ID, loginId)

        @Size(min = 1L)
        private fun parseIntent(intent: Intent): String =
            intent.getStringExtra(EXTRA_LOGIN_ID)?.takeIf(String::isNotEmpty)
                    ?: error("invalid loginId")
    }
}
