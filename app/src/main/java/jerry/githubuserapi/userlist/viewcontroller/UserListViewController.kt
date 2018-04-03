package jerry.githubuserapi.userlist.viewcontroller

import android.content.Context
import android.support.annotation.MainThread
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import jerry.githubuserapi.userlist.list.UserListAdapter
import jerry.githubuserapi.userlist.model.UserViewData
import jerry.githubuserapi.util.thread.ensureOnMainThread

@MainThread
class UserListViewController(
    private val userList: RecyclerView
) {
    private val context: Context
        get() = userList.context

    private val userListAdapter = UserListAdapter()

    init {
        userList.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            /* reverseLayout = */ false
        )
        userList.adapter = userListAdapter
    }

    fun onMoreUserViewDataFetched(
        fetchedUserViewDataList: List<UserViewData>
    ) = ensureOnMainThread {
        userListAdapter.onMoreUserViewDataFetched(fetchedUserViewDataList)
    }
}
