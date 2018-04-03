package jerry.githubuserapi.userlist

import android.os.Bundle
import android.support.annotation.MainThread
import jerry.githubuserapi.BaseActivity
import jerry.githubuserapi.R
import jerry.githubuserapi.userlist.model.UserListFetchResult
import jerry.githubuserapi.userlist.model.UserListViewModel
import jerry.githubuserapi.userlist.repository.UserListViewModelRepository
import jerry.githubuserapi.util.thread.ensureOnMainThread
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch

class AllUserActivity : BaseActivity() {
    private lateinit var userListViewModel: UserListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_user)

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

    private var received = 0

    @MainThread
    private fun onUserListFetchResultReceived(
        userListFetchResult: UserListFetchResult
    ) = ensureOnMainThread<Unit> {
        logd {
            "onUserListFetchResultReceived(): count=$received, result=$userListFetchResult"
        }

        launch(UI) {
            delay(200)
            if (received++ < 10) {
                userListViewModel.fetchMore()
                logi {
                    "next fetch requested!"
                }
            }
        }
    }
}
