package jerry.githubuserapi.userlist

import android.os.Bundle
import android.support.annotation.MainThread
import android.widget.Toast
import jerry.githubuserapi.BaseActivity
import jerry.githubuserapi.R
import jerry.githubuserapi.userlist.event.MoreUserDataRequiredEvent
import jerry.githubuserapi.userlist.model.UserListFetchResult
import jerry.githubuserapi.userlist.model.UserListViewModel
import jerry.githubuserapi.userlist.repository.UserListViewModelRepository
import jerry.githubuserapi.userlist.viewcontroller.UserListViewController
import jerry.githubuserapi.util.thread.ensureOnMainThread
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class AllUserActivity : BaseActivity() {
    private lateinit var userListViewController: UserListViewController
    private lateinit var userListViewModel: UserListViewModel

    private var hasMoreUser: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_user)

        userListViewController = UserListViewController(
            activityScopeEventBus,
            findViewById(R.id.all_user_list),
            DEFAULT_PAGE_SIZE
        )

        val repository = UserListViewModelRepository(
            context = this,
            coroutineDispatcher = CommonPool,
            lifecycleOwner = this,
            pageSize = DEFAULT_PAGE_SIZE
        )
        userListViewModel = repository.userListViewModel
        enableUserListViewModel()

        activityScopeEventBus.register(this)
    }

    override fun onDestroy() {
        activityScopeEventBus.unregister(this)
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMoreUserDataRequiredEvent(
        @Suppress("UNUSED_PARAMETER")
        ignore: MoreUserDataRequiredEvent
    ) {
        if (hasMoreUser) {
            userListViewModel.fetchMore()
        }
    }

    private fun enableUserListViewModel() {
        hasMoreUser = true
        val userListFetchResultChannel = userListViewModel.refresh()
        launch(UI) {
            userListFetchResultChannel.consumeEach(::onUserListFetchResultReceived)
        }
    }

    @MainThread
    private fun onUserListFetchResultReceived(result: UserListFetchResult) = ensureOnMainThread {
        logd { "onUserListFetchResultReceived(): result=$result" }
        when (result) {
            is UserListFetchResult.NoMoreUser -> {
                hasMoreUser = false
                return
            }

            is UserListFetchResult.OnePageFetched ->
                userListViewController.provideFetchedUserViewDataList(result.fetchedUserList)

            is UserListFetchResult.Failure -> {
                loge(result.cause) { "onUserListFetchResultReceived(): fetching failed." }
                Toast.makeText(this, R.string.error_fetch_failure, Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val DEFAULT_PAGE_SIZE = 30
    }
}
