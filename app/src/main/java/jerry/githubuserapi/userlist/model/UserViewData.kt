package jerry.githubuserapi.userlist.model

import android.net.Uri
import org.kohsuke.github.GHUser

data class UserViewData(
    val id: Long,
    val loginId: String,
    val avatarUrl: String?,
    val htmlUrl: Uri
) {
    companion object {
        fun from(ghUser: GHUser): UserViewData = UserViewData(
            id = ghUser.id,
            loginId = ghUser.login,
            avatarUrl = ghUser.avatarUrl,
            htmlUrl = Uri.parse(ghUser.htmlUrl.toString())
        )
    }
}
