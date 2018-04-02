package jerry.githubuserapi.application

import android.content.Context
import jerry.githubuserapi.application.data.DataManager

val Context.customApplication: CustomApplication
    get() = applicationContext as? CustomApplication
            ?: error("CustomApplication cannot be obtained from Context")

val Context.dataManager: DataManager
    inline get() = customApplication.dataManager
