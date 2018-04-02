package jerry.githubuserapi

import android.support.v7.app.AppCompatActivity
import android.util.Log
import org.greenrobot.eventbus.EventBus

abstract class BaseActivity : AppCompatActivity() {
    val tag: String by lazy {
        javaClass.simpleName
    }

    val activityScopeEventBus: EventBus by lazy {
        EventBus.builder().build()
    }

    inline fun logv(cause: Throwable? = null, messageSupplier: () -> String) {
        if (BuildConfig.DEBUG) {
            if (cause == null) {
                log(Log::v, messageSupplier)
            } else {
                log(Log::v, messageSupplier, cause)
            }
        }
    }

    inline fun logd(cause: Throwable? = null, messageSupplier: () -> String) {
        if (BuildConfig.DEBUG) {
            if (cause == null) {
                log(Log::d, messageSupplier)
            } else {
                log(Log::d, messageSupplier, cause)
            }
        }
    }

    inline fun logi(cause: Throwable? = null, messageSupplier: () -> String) {
        if (BuildConfig.DEBUG) {
            if (cause == null) {
                log(Log::i, messageSupplier)
            } else {
                log(Log::i, messageSupplier, cause)
            }
        }
    }

    inline fun logw(cause: Throwable? = null, messageSupplier: () -> String) {
        if (BuildConfig.DEBUG) {
            if (cause == null) {
                log(Log::w, messageSupplier)
            } else {
                log(Log::w, messageSupplier, cause)
            }
        }
    }

    inline fun loge(cause: Throwable? = null, messageSupplier: () -> String) {
        if (BuildConfig.DEBUG) {
            if (cause == null) {
                log(Log::e, messageSupplier)
            } else {
                log(Log::e, messageSupplier, cause)
            }
        }
    }

    inline fun log(logger: (String, String) -> Any, messageSupplier: () -> String) {
        if (BuildConfig.DEBUG) {
            logger(tag, messageSupplier())
        }
    }

    inline fun log(
        logger: (String, String, Throwable) -> Any,
        messageSupplier: () -> String,
        cause: Throwable
    ) {
        if (BuildConfig.DEBUG) {
            logger(tag, messageSupplier(), cause)
        }
    }
}
