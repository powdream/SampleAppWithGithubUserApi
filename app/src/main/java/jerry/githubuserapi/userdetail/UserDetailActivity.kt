package jerry.githubuserapi.userdetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import jerry.githubuserapi.BaseActivity
import jerry.githubuserapi.R
import jerry.githubuserapi.util.intent.activityIntent
import kotlinx.android.synthetic.main.activity_user_detail.*

class UserDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail)
        setSupportActionBar(toolbar)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    companion object {
        private const val EXTRA_LOGIN_ID = "extra-login-id"

        fun createIntent(context: Context, loginId: String): Intent =
            context.activityIntent<UserDetailActivity>().putExtra(EXTRA_LOGIN_ID, loginId)
    }
}
