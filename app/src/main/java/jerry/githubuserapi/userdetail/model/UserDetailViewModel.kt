package jerry.githubuserapi.userdetail.model

import org.kohsuke.github.GHException
import org.kohsuke.github.GHUser

data class UserDetailViewModel(
    val loginId: String,
    val name: String?,
    val avatarUrl: String?,
    val htmlUrl: String,
    val email: String?
) {
    companion object {
        @Throws(GHException::class)
        fun from(ghUser: GHUser): UserDetailViewModel =
            UserDetailViewModel(
                ghUser.login,
                ghUser.name,
                ghUser.avatarUrl,
                ghUser.htmlUrl.toString(),
                ghUser.email
            )
    }
}
