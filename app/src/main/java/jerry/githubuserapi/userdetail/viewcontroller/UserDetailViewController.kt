package jerry.githubuserapi.userdetail.viewcontroller

import android.content.Context
import android.support.annotation.MainThread
import android.support.annotation.StringRes
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.ActionBar
import android.webkit.WebView
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import jerry.githubuserapi.R
import jerry.githubuserapi.userdetail.event.UserDetailActivityUserEvent
import jerry.githubuserapi.userdetail.model.UserDetailViewModel
import jerry.githubuserapi.util.thread.ensureOnMainThread
import org.greenrobot.eventbus.EventBus

@MainThread
class UserDetailViewController(
    private val eventBus: EventBus,
    private val actionBar: ActionBar,
    private val avatarImageView: ImageView,
    private val webView: WebView,
    emailActionButton: FloatingActionButton
) {
    private val context: Context
        get() = avatarImageView.context

    private val avatarImageRequestOptions = RequestOptions()
        .placeholder(android.R.drawable.sym_def_app_icon)
        .skipMemoryCache(true)

    init {
        emailActionButton.setOnClickListener {
            eventBus.post(UserDetailActivityUserEvent.EMAIL_BUTTON_CLICKED)
        }
    }

    fun applyUserDetailViewModel(userDetailViewModel: UserDetailViewModel) = ensureOnMainThread {
        actionBar.title = userDetailViewModel.name ?: userDetailViewModel.loginId
        Glide.with(context)
            .load(userDetailViewModel.avatarUrl)
            .apply(avatarImageRequestOptions)
            .into(avatarImageView)
        webView.loadUrl(userDetailViewModel.htmlUrl)
    }

    fun onFetchFailure() =
        makeSnackbar(R.string.error_fetch_failure)
            .setAction(R.string.action_retry) {
                eventBus.post(UserDetailActivityUserEvent.FETCH_RETRY_ACTION_CLICKED)
            }
            .show()

    fun onInvalidEmailButtonClicked() =
        makeSnackbar(R.string.error_invalid_email_button_clicked).show()

    private fun makeSnackbar(@StringRes resId: Int): Snackbar =
        Snackbar.make(webView, resId, Snackbar.LENGTH_LONG)
}
