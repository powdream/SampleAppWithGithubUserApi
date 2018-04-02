package jerry.githubuserapi.application.data

import jerry.githubuserapi.application.model.ImmutableUser

data class SessionData(
    val authenticatedUser: ImmutableUser? = null
) {
    val isSignedIn: Boolean
        get() = authenticatedUser != null
}
