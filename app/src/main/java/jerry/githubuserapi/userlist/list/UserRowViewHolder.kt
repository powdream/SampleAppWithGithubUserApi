package jerry.githubuserapi.userlist.list

import android.support.annotation.MainThread
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import jerry.githubuserapi.R
import jerry.githubuserapi.userlist.event.UserRowViewClickedEvent
import jerry.githubuserapi.userlist.model.UserViewData
import jerry.githubuserapi.util.thread.ensureOnMainThread
import org.greenrobot.eventbus.EventBus

@MainThread
class UserRowViewHolder(
    itemView: View,
    private val eventBus: EventBus
) : RecyclerView.ViewHolder(itemView) {
    private val avatarImageView: ImageView = itemView.findViewById(R.id.row_view_all_user_avatar)
    private val loginIdTextView: TextView = itemView.findViewById(R.id.row_view_all_user_login_id)
    private val htmlUrlTextView: TextView = itemView.findViewById(R.id.row_view_all_user_html_url)

    private val glideRequestOptions by lazy(LazyThreadSafetyMode.NONE) {
        RequestOptions()
            .placeholder(android.R.drawable.sym_def_app_icon)
            .override(avatarImageView.layoutParams.width, avatarImageView.layoutParams.height)
            .fitCenter()
    }

    private var currentUserViewData: UserViewData? = null

    init {
        itemView.setOnClickListener {
            mayPostUserRowViewClickedEvent()
        }
    }

    fun applyUserViewData(userViewData: UserViewData) = ensureOnMainThread {
        Glide.with(itemView)
            .load(userViewData.avatarUrl)
            .apply(glideRequestOptions)
            .into(avatarImageView)
        loginIdTextView.text = userViewData.loginId
        htmlUrlTextView.text = userViewData.htmlUrl

        currentUserViewData = userViewData
    }

    private fun mayPostUserRowViewClickedEvent() =
        currentUserViewData?.let(::UserRowViewClickedEvent)?.let(eventBus::post)
}
