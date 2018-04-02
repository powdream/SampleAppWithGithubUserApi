package jerry.githubuserapi.application.model

import org.eclipse.egit.github.core.User
import org.eclipse.egit.github.core.UserPlan
import java.util.*

class ImmutableUser(private val delegate: User) : User() {
    private fun notSupported(): Nothing = throw UnsupportedOperationException()

    override fun getDiskUsage(): Int = delegate.diskUsage

    override fun getLogin(): String? = delegate.login

    override fun getCompany(): String? = delegate.company

    override fun getId(): Int = delegate.id

    override fun getCollaborators(): Int = delegate.collaborators

    override fun getPublicGists(): Int = delegate.publicGists

    override fun isHireable(): Boolean = delegate.isHireable

    override fun getLocation(): String? = delegate.location

    override fun getOwnedPrivateRepos(): Int = delegate.ownedPrivateRepos

    override fun getPlan(): UserPlan? = delegate.plan

    override fun getEmail(): String? = delegate.email

    override fun getName(): String? = delegate.name

    override fun getTotalPrivateRepos(): Int = delegate.totalPrivateRepos

    override fun getAvatarUrl(): String? = delegate.avatarUrl

    override fun getPublicRepos(): Int = delegate.publicRepos

    override fun getBlog(): String? = delegate.blog

    override fun getUrl(): String? = delegate.url

    override fun getType(): String? = delegate.type

    override fun getGravatarId(): String? = delegate.gravatarId

    override fun getHtmlUrl(): String? = delegate.htmlUrl

    override fun getFollowers(): Int = delegate.followers

    override fun getCreatedAt(): Date = delegate.createdAt

    override fun getPrivateGists(): Int = delegate.privateGists

    override fun getFollowing(): Int = delegate.following

    override fun setId(id: Int): User = notSupported()

    override fun setType(type: String?): User = notSupported()

    override fun setFollowing(following: Int): User = notSupported()

    override fun setFollowers(followers: Int): User = notSupported()

    override fun setBlog(blog: String?): User = notSupported()

    override fun setPublicRepos(publicRepos: Int): User = notSupported()

    override fun setUrl(url: String?): User = notSupported()

    override fun setTotalPrivateRepos(totalPrivateRepos: Int): User = notSupported()

    override fun setCollaborators(collaborators: Int): User = notSupported()

    override fun setPrivateGists(privateGists: Int): User = notSupported()

    override fun setCreatedAt(createdAt: Date?): User = notSupported()

    override fun setAvatarUrl(avatarUrl: String?): User = notSupported()

    override fun setEmail(email: String?): User = notSupported()

    override fun setGravatarId(gravatarId: String?): User = notSupported()

    override fun setPlan(plan: UserPlan?): User = notSupported()

    override fun setPublicGists(publicGists: Int): User = notSupported()

    override fun setHtmlUrl(htmlUrl: String?): User = notSupported()

    override fun setLocation(location: String?): User = notSupported()

    override fun setOwnedPrivateRepos(ownedPrivateRepos: Int): User = notSupported()

    override fun setLogin(login: String?): User = notSupported()

    override fun setDiskUsage(diskUsage: Int): User = notSupported()

    override fun setName(name: String?): User = notSupported()

    override fun setCompany(company: String?): User = notSupported()

    override fun setHireable(hireable: Boolean): User = notSupported()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val otherUser = other as? User ?: return false
        return id == otherUser.id
    }

    override fun hashCode(): Int = id.hashCode()
}
