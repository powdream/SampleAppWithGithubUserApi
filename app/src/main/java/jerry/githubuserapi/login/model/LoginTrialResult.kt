package jerry.githubuserapi.login.model

import org.kohsuke.github.GHUser
import org.kohsuke.github.GitHub
import java.io.IOException

sealed class LoginTrialResult {
    data class Success(
        val user: GHUser,
        val gitHub: GitHub
    ) : LoginTrialResult()

    data class Failure(
        val cause: IOException,
        val userId: String,
        val password: String
    ) : LoginTrialResult()
}
