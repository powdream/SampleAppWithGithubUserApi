package jerry.githubuserapi

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import jerry.githubuserapi.application.dataManager
import jerry.githubuserapi.login.LoginActivity
import jerry.githubuserapi.userlist.AllUserActivity
import jerry.githubuserapi.util.intent.startActivitySimply

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Route activities
        if (!dataManager.isSignedIn) {
            startActivitySimply<LoginActivity>()
            finish()
        } else {
            startActivitySimply<AllUserActivity>()
            finish()
        }
    }
}
