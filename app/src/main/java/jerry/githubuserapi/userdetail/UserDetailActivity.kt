package jerry.githubuserapi.userdetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.MainThread
import android.support.annotation.Size
import android.support.design.widget.Snackbar
import jerry.githubuserapi.BaseActivity
import jerry.githubuserapi.R
import jerry.githubuserapi.userdetail.model.FetchUserDetailViewModelResult
import jerry.githubuserapi.userdetail.repository.UserDetailViewModelRepository
import jerry.githubuserapi.userdetail.viewcontroller.UserDetailViewController
import jerry.githubuserapi.util.intent.activityIntent
import jerry.githubuserapi.util.thread.ensureOnMainThread
import kotlinx.android.synthetic.main.activity_user_detail.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class UserDetailActivity : BaseActivity() {
    private lateinit var userDetailViewController: UserDetailViewController

    @Size(min = 1L)
    private lateinit var loginId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail)
        setSupportActionBar(findViewById(R.id.toolbar))

        userDetailViewController = UserDetailViewController(
            supportActionBar ?: error("actionBar doesn't exist."),
            findViewById(R.id.user_detail_avatar),
            findViewById(R.id.user_detail_web_view)
        )

        loginId = parseIntent(intent)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        supportActionBar?.title = ""

        val repository = UserDetailViewModelRepository(this, CommonPool)
        launch(UI) {
            val fetchResult = repository.fetchUserDetailViewModel(loginId).await()
            onFetchUserDetailViewModelResultReceived(fetchResult)
        }
    }

    @MainThread
    private fun onFetchUserDetailViewModelResultReceived(
        fetchResult: FetchUserDetailViewModelResult
    ) = ensureOnMainThread {
        when (fetchResult) {
            is FetchUserDetailViewModelResult.Success ->
                userDetailViewController.applyUserDetailViewModel(fetchResult.userDetailViewModel)
            is FetchUserDetailViewModelResult.Failure -> {
                loge(fetchResult.cause) {
                    "onFetchUserDetailViewModelResultReceived() - fetch failed."
                }
                Snackbar
                    .make(
                        findViewById(R.id.toolbar),
                        R.string.error_fetch_failure,
                        Snackbar.LENGTH_LONG
                    )
                    .show()

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
