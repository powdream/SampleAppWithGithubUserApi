package jerry.githubuserapi.application.data

import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class DataManager {
    @Volatile
    private var _sessionData: SessionData = SessionData()

    private val sessionDataLock: ReentrantReadWriteLock = ReentrantReadWriteLock()

    /**
     * immutable [SessionData].
     */
    val sessionData: SessionData
        get() = sessionDataLock.read { _sessionData }

    fun updateSessionData(sessionDataGenerator: (SessionData) -> SessionData) {
        sessionDataLock.write {
            _sessionData = sessionDataGenerator(_sessionData)
        }
    }
}
