package com.gnest.remember

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Typeface
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.preference.PreferenceManager
import com.crashlytics.android.Crashlytics
import com.gnest.remember.di.modules.appModule
import com.gnest.remember.model.db.data.MemoRealmFields
import com.gnest.remember.model.db.migration.RealmMigration
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import io.fabric.sdk.android.Fabric
import io.realm.Realm
import io.realm.RealmConfiguration
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import java.util.*

class App : Application() {
    private var refWatcher: RefWatcher? = null
    lateinit var numOfLines: IntArray
        private set

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) { // This process is dedicated to LeakCanary for heap analysis.
// You should not init your app in this process.
            return
        }
        refWatcher = LeakCanary.install(this)
        self = this
        Realm.init(this)
        configRealm()
        FONT = Typeface.createFromAsset(assets, FONT_PATH)
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        FONT_SIZE = sharedPref.getString(FONT_SIZE_KEY, FONT_SIZE_DEFAULT)!!.toInt()
        NOTIFICATION_SOUND = Uri.parse(sharedPref.getString(NOTIFICATION_SOUND_KEY, Settings.System.DEFAULT_NOTIFICATION_URI.toString()))
        numOfLines = resources.getIntArray(R.array.numOfLines)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
        Fabric.with(this, Crashlytics())

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(appModule)
        }
    }

    private fun configRealm() {
        val config = RealmConfiguration.Builder()
                .name(MemoRealmFields.DEFAULT_CONFIG_NAME)
                .schemaVersion(1)
                .migration(RealmMigration())
                .build()
        Realm.setDefaultConfiguration(config)
        val archive = RealmConfiguration.Builder()
                .name(MemoRealmFields.ARCHIVE_CONFIG_NAME)
                .schemaVersion(1)
                .migration(RealmMigration())
                .build()
        REALM_CONFIG_MAP[MemoRealmFields.DEFAULT_CONFIG_NAME] = config
        REALM_CONFIG_MAP[MemoRealmFields.ARCHIVE_CONFIG_NAME] = archive
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (notificationManager != null) {
            var channel = notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID)
            if (channel == null) {
                val name: CharSequence = getString(R.string.channel_name)
                val description = getString(R.string.channel_description)
                val importance = NotificationManager.IMPORTANCE_HIGH
                val attributes = AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                        .setContentType(AudioAttributes.CONTENT_TYPE_UNKNOWN)
                        .build()
                channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance)
                channel.description = description
                channel.setSound(Settings.System.DEFAULT_NOTIFICATION_URI, attributes)
                channel.enableVibration(true)
                channel.vibrationPattern = VIBRATE_PATTERN
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    companion object {
        const val FONT_PATH = "fonts/CaviarDreams.ttf"
        const val FONT_SIZE_DEFAULT = "16"
        const val FONT_SIZE_KEY = "font_size"
        const val NOTIFICATION_SOUND_KEY = "notification_sound"
        const val NOTIFICATION_CHANNEL_ID = "com.gnest.remember.NOTIFICATION"
        @JvmField
        val VIBRATE_PATTERN = longArrayOf(300, 300, 300, 300)
        @JvmField
        var FONT = Typeface.DEFAULT
        @JvmField
        var FONT_SIZE = 16
        @JvmField
        var NOTIFICATION_SOUND = Settings.System.DEFAULT_NOTIFICATION_URI
        val REALM_CONFIG_MAP: MutableMap<String, RealmConfiguration> = HashMap()
        lateinit var self: App
        @JvmStatic
        fun getConfigurationByName(name: String?): RealmConfiguration? {
            return REALM_CONFIG_MAP[name]
        }

        fun setFontSize(fontSize: Int) {
            FONT_SIZE = fontSize
        }

        fun setNotificationSound(notificationSoundPath: Uri) {
            NOTIFICATION_SOUND = notificationSoundPath
        }
    }
}