package jerry.githubuserapi.application.data

import org.kohsuke.github.GHUser
import org.kohsuke.github.GitHub

data class SessionData(
    val signedOrUnsigned: SignedOrUnsigned = UnsignedIn
) {
    val isSignedIn: Boolean
        get() = signedOrUnsigned is SignedIn

    val github: GitHub by lazy {
        (signedOrUnsigned as? SignedIn)?.gitHub ?: GitHub.offline()
    }
}

sealed class SignedOrUnsigned

data class SignedIn(
    val user: GHUser,
    val gitHub: GitHub
) : SignedOrUnsigned()

object UnsignedIn : SignedOrUnsigned()
