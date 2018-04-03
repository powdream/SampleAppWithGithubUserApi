package jerry.githubuserapi.userlist.model

import org.kohsuke.github.GHUser

data class UserViewData(
    val id: Long,
    val loginId: String,
    val avatarUrl: String?,
    val htmlUrl: String
) {
    companion object {
        fun from(ghUser: GHUser): UserViewData = UserViewData(
            id = ghUser.id,
            loginId = ghUser.login,
            avatarUrl = ghUser.avatarUrl,
            htmlUrl = ghUser.htmlUrl.toString()
        )
    }
}
