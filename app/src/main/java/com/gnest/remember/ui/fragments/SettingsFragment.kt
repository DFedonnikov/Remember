package com.gnest.remember.ui.fragments

import android.annotation.TargetApi
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.core.content.edit
import androidx.navigation.fragment.findNavController

import com.gnest.remember.App
import com.gnest.remember.R

import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.gnest.remember.extensions.setSupportActionBar
import com.gnest.remember.extensions.setupActionBarWithNavController
import com.gnest.remember.extensions.sharedPreferences
import com.gnest.remember.extensions.supportActionBar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val ringtoneUri: Uri
        get() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val activity = activity
                if (activity != null) {
                    val manager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                    manager?.let {
                        val channel = manager.getNotificationChannel(App.NOTIFICATION_CHANNEL_ID)
                        channel?.let { return it.sound }
                    }
                }
            }
            val uriPath = sharedPreferences().getString(App.NOTIFICATION_SOUND_KEY, "")
            return if (uriPath?.isEmpty() != false) Settings.System.DEFAULT_NOTIFICATION_URI else Uri.parse(uriPath)
        }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
//        val refWatcher = App.getRefWatcher()
//        refWatcher.watch(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSupportActionBar(settingsFragmentToolbar)
        supportActionBar()?.setDisplayHomeAsUpEnabled(true)
        supportActionBar()?.setHomeButtonEnabled(true)
        activity?.drawerLayout?.let { setupActionBarWithNavController(it) }
        sharedPreferences().registerOnSharedPreferenceChangeListener(this)
//        activity?.addOnBackPressedCallback(this)
        setFontPrefSummary()
        setNotificationSoundPrefSummary(ringtoneUri)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sharedPreferences().unregisterOnSharedPreferenceChangeListener(this)
//        activity?.removeOnBackPressedCallback(this)
    }

//    override fun handleOnBackPressed(): Boolean {
//        findNavController().navigateUp()
//        return true
//    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        return if (App.NOTIFICATION_SOUND_KEY == preference.key) {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> openNotificationChanelSetup()
                else -> openRingtonePicker()
            }
            true
        } else {
            super.onPreferenceTreeClick(preference)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when {
            requestCode == REQUEST_CODE_NOTIFICATION_SOUND && data != null -> {
                val ringtone = data.getParcelableExtra<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
                sharedPreferences().edit {
                    ringtone?.let { putString(App.NOTIFICATION_SOUND_KEY, ringtone.toString()) }
                            ?: putString(App.NOTIFICATION_SOUND_KEY, "") // "Silent" was selected
                }
            }
            requestCode == REQUEST_CODE_CHANNEL_SETUP -> setNotificationSoundPrefSummary(ringtoneUri)
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            findNavController().navigateUp()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        when (key) {
            App.FONT_SIZE_KEY -> {
                val fontSize = setFontPrefSummary()
                App.setFontSize(Integer.parseInt(fontSize))
            }
            App.NOTIFICATION_SOUND_KEY -> {
                val ringtoneUri = ringtoneUri
                setNotificationSoundPrefSummary(ringtoneUri)
                App.setNotificationSound(ringtoneUri)
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun openNotificationChanelSetup() {
        val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
        intent.putExtra(Settings.EXTRA_CHANNEL_ID, App.NOTIFICATION_CHANNEL_ID)
        startActivityForResult(intent, REQUEST_CODE_CHANNEL_SETUP)
    }

    private fun openRingtonePicker() {
        val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, Settings.System.DEFAULT_NOTIFICATION_URI)

        val notificationSoundPath = sharedPreferences().getString(App.NOTIFICATION_SOUND_KEY, null)

        notificationSoundPath?.let {
            when {
                it.isEmpty() -> // Select "Silent"
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, "")
                else -> intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(notificationSoundPath))
            }
        } ?: run {
            // No ringtone has been selected, set to the default
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Settings.System.DEFAULT_NOTIFICATION_URI)
        }

        startActivityForResult(intent, REQUEST_CODE_NOTIFICATION_SOUND)
    }

    private fun setFontPrefSummary(): String {
        val fontSizePref = findPreference(App.FONT_SIZE_KEY)
        val fontSize = sharedPreferences().getString(App.FONT_SIZE_KEY, "") ?: ""
        fontSizePref.summary = fontSize
        return fontSize
    }

    private fun setNotificationSoundPrefSummary(ringtoneUri: Uri) {
        val notificationSoundPref = findPreference(App.NOTIFICATION_SOUND_KEY)
        val summary = RingtoneManager.getRingtone(requireContext(), ringtoneUri).getTitle(context)
        notificationSoundPref.summary = summary
    }

    companion object {

        private const val REQUEST_CODE_NOTIFICATION_SOUND = 1
        private const val REQUEST_CODE_CHANNEL_SETUP = 2
    }
}