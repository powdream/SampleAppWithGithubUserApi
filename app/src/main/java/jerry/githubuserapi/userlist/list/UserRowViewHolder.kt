package jerry.githubuserapi.userlist.list

import android.support.annotation.MainThread
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import jerry.githubuserapi.R
import jerry.githubuserapi.userlist.model.UserViewData
import jerry.githubuserapi.util.thread.ensureOnMainThread

@MainThread
class UserRowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val avatarImageView: ImageView = itemView.findViewById(R.id.row_view_all_user_avatar)
    private val loginIdTextView: TextView = itemView.findViewById(R.id.row_view_all_user_login_id)
    private val htmlUrlTextView: TextView = itemView.findViewById(R.id.row_view_all_user_html_url)

    fun applyUserViewData(userViewData: UserViewData) = ensureOnMainThread {
        // TODO: avatarImageView with Glide
        loginIdTextView.text = userViewData.loginId
        htmlUrlTextView.text = userViewData.htmlUrl
    }
}
