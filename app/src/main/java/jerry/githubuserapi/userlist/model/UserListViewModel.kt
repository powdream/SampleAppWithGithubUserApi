package jerry.githubuserapi.userlist.model

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

    data class OnePageFetched(val fetchedUserList: List<GHUser>) : UserListFetchResult()

    data class Failure(val cause: GHException) : UserListFetchResult()
}

@MainThread
class UserListViewModel(
    private val coroutineDispatcher: CoroutineDispatcher,
    private val iteratorSupplier: () -> PagedIterator<GHUser>
) {
    private lateinit var userPagedIterator: PagedIterator<GHUser>

    private var currentChannel: Channel<UserListFetchResult>? = null
    private var hasMoreUser: Boolean = true
    private var fetchMoreJob: Job? = null

    @Throws(IOException::class)
    fun refresh(): ReceiveChannel<UserListFetchResult> = ensureOnMainThread {
        fetchMoreJob?.cancel()
        fetchMoreJob = null
        currentChannel?.close()
        currentChannel = null

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
        fetchMoreJob = launch(UI) {
            val userListFetchResult = innerFetchMore()
            currentChannel
                ?.takeUnless(Channel<UserListFetchResult>::isClosedForSend)
                ?.send(userListFetchResult)
        }.clearOnCompletion()
    }

    private suspend fun innerFetchMore(): UserListFetchResult {
        if (!hasMoreUser) return UserListFetchResult.NoMoreUser

        return try {
            hasMoreUser = async(coroutineDispatcher) { userPagedIterator.hasNext() }.await()
            if (!hasMoreUser) {
                UserListFetchResult.NoMoreUser
            } else {
                UserListFetchResult.OnePageFetched(userPagedIterator.nextPage())
            }
        } catch (e: GHException) {
            UserListFetchResult.Failure(e)
        }
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

    companion object {
        private const val TAG = "UserListViewModel"
    }
}
