package jerry.githubuserapi.login.model

import org.eclipse.egit.github.core.User
import java.io.IOException

sealed class LoginTrialResult {
    data class Success(
        val user: User,
        override val userId: String,
        override val password: String
    ) : LoginTrialResult()

    data class Failure(
        val cause: IOException,
        override val userId: String,
        override val password: String
    ) : LoginTrialResult()

    abstract val userId: String
    abstract val password: String
}
