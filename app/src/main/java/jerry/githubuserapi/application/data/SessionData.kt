package jerry.githubuserapi.application.data

import jerry.githubuserapi.application.model.ImmutableUser
import org.eclipse.egit.github.core.client.GitHubClient

data class SessionData(
    val currentUser: CurrentUser = UnsignedIn
) {
    val isSignedIn: Boolean
        get() = currentUser is SignedIn

    val gitHubClient: GitHubClient by lazy {
        (currentUser as? SignedIn)?.gitHubClient ?: GitHubClient()
    }
}

sealed class CurrentUser

data class SignedIn(
    val authenticatedUser: ImmutableUser,
    val gitHubClient: GitHubClient
) : CurrentUser()

object UnsignedIn : CurrentUser()
