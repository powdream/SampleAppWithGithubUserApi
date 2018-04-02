package jerry.githubuserapi.login.repository

import jerry.githubuserapi.login.model.LoginTrialResult
import kotlinx.coroutines.experimental.CoroutineStart
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import org.eclipse.egit.github.core.client.GitHubClient
import org.eclipse.egit.github.core.service.UserService
import java.io.IOException
import kotlin.coroutines.experimental.CoroutineContext

class AuthenticatedUserRepository {
    fun getAuthenticatedUser(
        context: CoroutineContext,
        userId: String,
        password: String
    ): Deferred<LoginTrialResult> = async(context = context, start = CoroutineStart.LAZY) {
        val userService = UserService(GitHubClient().setCredentials(userId, password))
        try {
            LoginTrialResult.Success(userService.user)
        } catch (e: IOException) {
            LoginTrialResult.Failure(e, userId, password)
        }
    }
}
