package jerry.githubuserapi

import android.support.v7.app.AppCompatActivity
import org.greenrobot.eventbus.EventBus

abstract class BaseActivity : AppCompatActivity() {
    val activityScopeEventBus: EventBus by lazy {
        EventBus.builder().build()
    }
}
