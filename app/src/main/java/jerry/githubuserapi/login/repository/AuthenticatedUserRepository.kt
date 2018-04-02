package jerry.githubuserapi.login.repository

import jerry.githubuserapi.login.model.LoginTrialResult
import kotlinx.coroutines.experimental.CoroutineStart
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import org.kohsuke.github.GitHub
import java.io.IOException
import kotlin.coroutines.experimental.CoroutineContext

class AuthenticatedUserRepository {
    fun getAuthenticatedUser(
        context: CoroutineContext,
        userId: String,
        password: String
    ): Deferred<LoginTrialResult> = async(context = context, start = CoroutineStart.LAZY) {
        try {
            val github = GitHub.connectUsingPassword(userId, password)
            LoginTrialResult.Success(github.myself, github)
        } catch (e: IOException) {
            LoginTrialResult.Failure(e, userId, password)
        }
    }
}
