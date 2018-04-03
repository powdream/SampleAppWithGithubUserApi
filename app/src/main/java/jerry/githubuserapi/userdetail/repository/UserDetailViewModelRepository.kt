package jerry.githubuserapi.userdetail.repository

import android.content.Context
import jerry.githubuserapi.application.dataManager
import jerry.githubuserapi.userdetail.model.FetchUserDetailViewModelResult
import jerry.githubuserapi.userdetail.model.UserDetailViewModel
import kotlinx.coroutines.experimental.CoroutineDispatcher
import kotlinx.coroutines.experimental.CoroutineStart
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import org.kohsuke.github.GHException
import org.kohsuke.github.GitHub

class UserDetailViewModelRepository(
    context: Context,
    private val coroutineDispatcher: CoroutineDispatcher
) {
    private val gitHub: GitHub = context.dataManager.sessionData.github

    fun fetchUserDetailViewModel(loginId: String): Deferred<FetchUserDetailViewModelResult> =
        async(coroutineDispatcher, start = CoroutineStart.LAZY) {
            try {
                val userDetailViewModel = UserDetailViewModel.from(gitHub.getUser(loginId))
                FetchUserDetailViewModelResult.Success(userDetailViewModel)
            } catch (e: GHException) {
                FetchUserDetailViewModelResult.Failure(e)
            }
        }
}
