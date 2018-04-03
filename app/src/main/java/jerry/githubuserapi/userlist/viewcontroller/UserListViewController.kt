package jerry.githubuserapi.userlist.viewcontroller

import android.content.Context
import android.support.annotation.MainThread
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import jerry.githubuserapi.userlist.event.MoreUserDataRequiredEvent
import jerry.githubuserapi.userlist.list.UserListAdapter
import jerry.githubuserapi.userlist.model.UserViewData
import jerry.githubuserapi.util.thread.ensureOnMainThread
import org.greenrobot.eventbus.EventBus

@MainThread
class UserListViewController(
    private val eventBus: EventBus,
    private val userList: RecyclerView,
    pageSize: Int
) {
    private val context: Context
        get() = userList.context

    private val userListAdapter = UserListAdapter(eventBus)
    private val layoutManager = LinearLayoutManager(
        context,
        LinearLayoutManager.VERTICAL,
        /* reverseLayout = */ false
    )

    private val twoPages: Int = pageSize shl 1

    init {
        userList.layoutManager = layoutManager
        userList.adapter = userListAdapter
        userList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) =
                onUserListScrolling()
        })
    }

    fun provideFetchedUserViewDataList(
        fetchedUserViewDataList: List<UserViewData>
    ) = ensureOnMainThread {
        userListAdapter.onMoreUserViewDataFetched(fetchedUserViewDataList)
    }

    /**
     * Attempts to fetch more user data reaching to the bottom of [userList].
     */
    private fun onUserListScrolling() = ensureOnMainThread {
        if (isNearToBottomOfList()) {
            eventBus.post(MoreUserDataRequiredEvent)
        }
    }

    private fun isNearToBottomOfList(): Boolean = ensureOnMainThread {
        val itemCount = userListAdapter.itemCount
        val lastVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition()
        return itemCount - lastVisibleItemPosition in 0..twoPages
    }
}
