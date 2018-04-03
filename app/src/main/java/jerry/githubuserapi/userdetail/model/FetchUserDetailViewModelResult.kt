package jerry.githubuserapi.userdetail.model

import org.kohsuke.github.GHException

sealed class FetchUserDetailViewModelResult {
    data class Success(val userDetailViewModel: UserDetailViewModel) :
        FetchUserDetailViewModelResult()

    data class Failure(val cause: GHException) :
        FetchUserDetailViewModelResult()
}
