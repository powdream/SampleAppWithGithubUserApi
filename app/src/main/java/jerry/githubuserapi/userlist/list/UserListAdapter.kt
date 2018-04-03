package jerry.githubuserapi.userlist.list

import android.support.annotation.MainThread
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import jerry.githubuserapi.R
import jerry.githubuserapi.userlist.model.UserViewData
import jerry.githubuserapi.util.thread.ensureOnMainThread
import org.greenrobot.eventbus.EventBus

@MainThread
class UserListAdapter(private val eventBus: EventBus) : RecyclerView.Adapter<UserRowViewHolder>() {
    private val userViewDataList: MutableList<UserViewData> = mutableListOf()

    fun onMoreUserViewDataFetched(fetchedUserViewDataList: List<UserViewData>) =
        ensureOnMainThread {
            val insertionStart = userViewDataList.size
            userViewDataList += fetchedUserViewDataList
            notifyItemRangeInserted(insertionStart, fetchedUserViewDataList.size)
        }

    override fun getItemId(position: Int): Long = ensureOnMainThread {
        userViewDataList.getOrNull(position)?.id ?: RecyclerView.NO_ID
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserRowViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.row_view_all_user, parent, /* attachToRoot = */ false)
        return UserRowViewHolder(itemView, eventBus)
    }

    override fun getItemCount(): Int = ensureOnMainThread(userViewDataList::size)

    override fun onBindViewHolder(
        holder: UserRowViewHolder,
        position: Int
    ) = ensureOnMainThread<Unit> {
        userViewDataList.getOrNull(position)?.let(holder::applyUserViewData)
    }
}
