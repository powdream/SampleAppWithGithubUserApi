package jerry.githubuserapi.application.data

import org.eclipse.egit.github.core.User
import java.util.concurrent.atomic.AtomicReference

class DataManager {
    private val _authenticatedUser: AtomicReference<User> = AtomicReference()

    var authenticatedUser: User?
        get() = _authenticatedUser.get()
        set(value) = _authenticatedUser.set(value)

    val isSignedIn: Boolean
        get() = authenticatedUser != null
}
