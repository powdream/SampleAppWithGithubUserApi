package jerry.githubuserapi.application

import android.app.Application
import jerry.githubuserapi.application.data.DataManager

class CustomApplication : Application() {
    lateinit var dataManager: DataManager
        private set

    override fun onCreate() {
        super.onCreate()
        dataManager = DataManager()
    }
}
