package jerry.githubuserapi.userlist.repository

import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import jerry.githubuserapi.application.dataManager
import jerry.githubuserapi.userlist.model.UserListViewModel
import kotlinx.coroutines.experimental.CoroutineDispatcher
import org.kohsuke.github.GHUser
import org.kohsuke.github.GitHub
import org.kohsuke.github.PagedIterable
import java.io.IOException

class UserListViewModelRepository(
    context: Context,
    coroutineDispatcher: CoroutineDispatcher,
    lifecycleOwner: LifecycleOwner,
    private val pageSize: Int = DEFAULT_PAGE_SIZE
) {
    private val gitHub: GitHub = context.dataManager.sessionData.github

    val userListViewModel: UserListViewModel =
        UserListViewModel(coroutineDispatcher, lifecycleOwner) {
            createPagedUserList().iterator()
        }

    @Throws(IOException::class)
    private fun createPagedUserList(): PagedIterable<GHUser> =
        gitHub.listUsers().withPageSize(pageSize)

    companion object {
        private const val DEFAULT_PAGE_SIZE = 30
    }
}
