package jerry.githubuserapi.login.model

import org.eclipse.egit.github.core.User
import java.io.IOException

sealed class LoginTrialResult {
    class Success(val user: User) : LoginTrialResult()
    data class Failure(
        val cause: IOException,
        val userId: String,
        val password: String
    ) : LoginTrialResult()
}
