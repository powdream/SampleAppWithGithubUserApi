package jerry.githubuserapi.userlist.repository

import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import jerry.githubuserapi.application.dataManager
import jerry.githubuserapi.userlist.model.UserListViewModel
import kotlinx.coroutines.experimental.CoroutineDispatcher
import org.kohsuke.github.GitHub

class UserListViewModelRepository(
    context: Context,
    coroutineDispatcher: CoroutineDispatcher,
    lifecycleOwner: LifecycleOwner,
    private val pageSize: Int
) {
    private val gitHub: GitHub = context.dataManager.sessionData.github

    val userListViewModel: UserListViewModel = UserListViewModel(
        coroutineDispatcher,
        lifecycleOwner,
        iteratorSupplier = {
            gitHub.listUsers().withPageSize(pageSize).iterator()
        }
    )
}
