package com.example.balanja

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log

class BalanjaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            com.google.firebase.database.FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        } catch (e: Exception) {
            // Already initialized or another error
        }
        AppContainer.init(this)

        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                Log.d("Lifecycle", "${activity.localClassName} - onCreate()")
            }

            override fun onActivityStarted(activity: Activity) {
                Log.d("Lifecycle", "${activity.localClassName} - onStart()")
            }

            override fun onActivityResumed(activity: Activity) {
                Log.d("Lifecycle", "${activity.localClassName} - onResume()")
            }

            override fun onActivityPaused(activity: Activity) {
                Log.d("Lifecycle", "${activity.localClassName} - onPause()")
            }

            override fun onActivityStopped(activity: Activity) {
                Log.d("Lifecycle", "${activity.localClassName} - onStop()")
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

            override fun onActivityDestroyed(activity: Activity) {
                Log.d("Lifecycle", "${activity.localClassName} - onDestroy()")
            }
        })
    }
}