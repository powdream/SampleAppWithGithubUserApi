package jerry.githubuserapi.userlist

import android.os.Bundle
import android.support.annotation.MainThread
import android.widget.Toast
import jerry.githubuserapi.BaseActivity
import jerry.githubuserapi.R
import jerry.githubuserapi.userlist.model.UserListFetchResult
import jerry.githubuserapi.userlist.model.UserListViewModel
import jerry.githubuserapi.userlist.repository.UserListViewModelRepository
import jerry.githubuserapi.userlist.viewcontroller.UserListViewController
import jerry.githubuserapi.util.thread.ensureOnMainThread
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.launch

class AllUserActivity : BaseActivity() {
    private lateinit var userListViewModel: UserListViewModel

    private lateinit var userListViewController: UserListViewController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_user)

        userListViewController = UserListViewController(findViewById(R.id.all_user_list))

        userListViewModel = UserListViewModelRepository(
            context = this,
            coroutineDispatcher = CommonPool,
            lifecycleOwner = this
        ).userListViewModel
        val userListFetchResultChannel = userListViewModel.refresh()
        launch(UI) {
            userListFetchResultChannel.consumeEach(::onUserListFetchResultReceived)
        }
    }

    @MainThread
    private fun onUserListFetchResultReceived(result: UserListFetchResult) = ensureOnMainThread {
        logd { "onUserListFetchResultReceived(): result=$result" }
        when (result) {
            is UserListFetchResult.NoMoreUser -> return // Do nothing

            is UserListFetchResult.OnePageFetched ->
                userListViewController.onMoreUserViewDataFetched(result.fetchedUserList)

            is UserListFetchResult.Failure -> {
                loge(result.cause) { "onUserListFetchResultReceived(): fetching failed." }
                Toast.makeText(this, R.string.error_fetch_failure, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
