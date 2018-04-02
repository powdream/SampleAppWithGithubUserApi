package jerry.githubuserapi

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import jerry.githubuserapi.login.LoginActivity
import jerry.githubuserapi.util.intent.startActivitySimply

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Temporary: open [LoginActivity] immediately.
        startActivitySimply<LoginActivity>()
        finish()
    }
}
