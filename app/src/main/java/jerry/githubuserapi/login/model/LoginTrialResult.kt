package jerry.githubuserapi.login.model

import org.eclipse.egit.github.core.User
import java.io.IOException

sealed class LoginTrialResult {
    class Success(val user: User) : LoginTrialResult()
    class Failure(val cause: IOException) : LoginTrialResult()
}
