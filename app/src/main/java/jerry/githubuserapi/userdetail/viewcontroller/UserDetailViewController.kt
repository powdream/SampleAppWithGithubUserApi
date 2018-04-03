package jerry.githubuserapi.userdetail.viewcontroller

import android.content.Context
import android.support.annotation.MainThread
import android.support.v7.app.ActionBar
import android.webkit.WebView
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import jerry.githubuserapi.userdetail.model.UserDetailViewModel
import jerry.githubuserapi.util.thread.ensureOnMainThread

@MainThread
class UserDetailViewController(
    private val actionBar: ActionBar,
    private val avatarImageView: ImageView,
    private val webView: WebView
) {
    private val context: Context
        get() = avatarImageView.context

    private val avatarImageRequestOptions = RequestOptions()
        .placeholder(android.R.drawable.sym_def_app_icon)
        .skipMemoryCache(true)

    fun applyUserDetailViewModel(userDetailViewModel: UserDetailViewModel) = ensureOnMainThread {
        actionBar.title = userDetailViewModel.name
        Glide.with(context)
            .load(userDetailViewModel.avatarUrl)
            .apply(avatarImageRequestOptions)
            .into(avatarImageView)
        webView.loadUrl(userDetailViewModel.htmlUrl)
    }
}
