package jerry.githubuserapi.util.thread

import android.os.Build
import android.os.Looper
import jerry.githubuserapi.BuildConfig

fun isOnMainThread(): Boolean =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        Looper.getMainLooper().isCurrentThread
    } else {
        // Looper.equals() isn't overridden
        Looper.getMainLooper() === Looper.myLooper()
    }

inline fun <T> ensureOnMainThread(action: () -> T): T {
    if (BuildConfig.DEBUG) {
        check(isOnMainThread())
    }
    return action()
}
