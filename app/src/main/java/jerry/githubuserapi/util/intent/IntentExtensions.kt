package jerry.githubuserapi.util.intent

import android.app.Activity
import android.content.Context
import android.content.Intent

inline fun <reified ACTIVITY : Activity> Context.activityIntent(): Intent =
    Intent(this, ACTIVITY::class.java)

inline fun Context.startActivityWith(intentProvider: () -> Intent) =
    startActivity(intentProvider())

inline fun <reified ACTIVITY : Activity> Context.startActivitySimply() =
    startActivityWith { activityIntent<ACTIVITY>() }
