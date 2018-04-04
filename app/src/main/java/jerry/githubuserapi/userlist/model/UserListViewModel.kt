package jerry.githubuserapi.userlist.model

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.support.annotation.MainThread
import android.util.Log
import jerry.githubuserapi.BuildConfig
import jerry.githubuserapi.util.thread.ensureOnMainThread
import kotlinx.coroutines.experimental.CoroutineDispatcher
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.launch
import org.kohsuke.github.GHException
import org.kohsuke.github.GHUser
import org.kohsuke.github.PagedIterator
import java.io.IOException

sealed class UserListFetchResult {
    object NoMoreUser : UserListFetchResult()

    data class OnePageFetched(val fetchedUserList: List<UserViewData>) : UserListFetchResult()

    data class Failure(val cause: GHException) : UserListFetchResult()
}

@MainThread
class UserListViewModel(
    private val coroutineDispatcher: CoroutineDispatcher,
    lifecycleOwner: LifecycleOwner,
    private val iteratorSupplier: () -> PagedIterator<GHUser>
) {
    private lateinit var userPagedIterator: PagedIterator<GHUser>

    private var currentChannel: Channel<UserListFetchResult>? = null
    private var hasMoreUser: Boolean = true
    private var fetchMoreJob: Job? = null

    init {
        lifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy(source: LifecycleOwner) {
                closeChannel()
            }
        })
    }

    @Throws(IOException::class)
    fun refresh(): ReceiveChannel<UserListFetchResult> = ensureOnMainThread {
        closeChannel()

        hasMoreUser = true
        userPagedIterator = iteratorSupplier()
        Channel<UserListFetchResult>(Channel.UNLIMITED).also {
            currentChannel = it
            fetchMore()
        }
    }

    fun fetchMore() = ensureOnMainThread {
        // Do not send duplicated fetch requests
        if (fetchMoreJob != null) {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "fetchMoreJob exists.")
            }
            return
        }
        fetchMoreJob = createFetchMoreJob().clearOnCompletion()
    }

    private fun createFetchMoreJob(): Job = launch(UI) {
        val userListFetchResult = innerFetchMore()
        currentChannel?.takeUnless(Channel<*>::isClosedForSend)
            ?.send(userListFetchResult)
    }

    private suspend fun innerFetchMore(): UserListFetchResult {
        if (!hasMoreUser) return UserListFetchResult.NoMoreUser

        return try {
            hasMoreUser = userPagedIterator.hasMoreUser()
            if (!hasMoreUser) {
                UserListFetchResult.NoMoreUser
            } else {
                UserListFetchResult.OnePageFetched(userPagedIterator.nextUserViewDataPage())
            }
        } catch (e: GHException) {
            UserListFetchResult.Failure(e)
        }
    }

    private fun closeChannel() {
        fetchMoreJob?.cancel()
        fetchMoreJob = null
        currentChannel?.close()
        currentChannel = null
        hasMoreUser = false
    }

    private fun Job.clearOnCompletion(): Job = apply {
        invokeOnCompletion(onCancelling = true) {
            if (this === fetchMoreJob) {
                fetchMoreJob = null
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "Running fetchMoreJob completes.")
                }
            }
        }
    }

    private fun PagedIterator<GHUser>.nextUserViewDataPage(): List<UserViewData> =
        nextPage().map { UserViewData.from(it) }

    private suspend fun PagedIterator<GHUser>.hasMoreUser(): Boolean =
        async(coroutineDispatcher) { hasNext() }.await()

    companion object {
        private const val TAG = "UserListViewModel"
    }
}
