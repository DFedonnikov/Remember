package com.gnest.remember.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.CalendarContract
import android.view.MenuItem
import android.widget.Toast
import androidx.core.content.edit
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.gnest.remember.R
import com.gnest.remember.extensions.toast
import com.google.android.material.navigation.NavigationView
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val rxPermissions by lazy { RxPermissions(this) }
    private val compositeDisposable = CompositeDisposable()

    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navController = findNavController(this, R.id.navHostFragment)
        bottomNavView.setupWithNavController(navController)
        bottomNavView.setOnNavigationItemSelectedListener {
            var isProcessed = true
            when (it.itemId) {
                R.id.mainList -> navController.navigate(R.id.mainList)
                R.id.archive -> navController.navigate(R.id.archive)
                R.id.editMemo -> navController.navigate(R.id.editMemo)
                R.id.settings -> navController.navigate(R.id.settings)
                else -> isProcessed = false
            }
            isProcessed
        }
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mainList -> navController.navigate(R.id.mainList)
            R.id.archive -> navController.navigate(R.id.archive)
            R.id.editMemo -> navController.navigate(R.id.editMemo)
            R.id.settings -> navController.navigate(R.id.settings)
            else -> return false
        }
        return false
    }

    fun addToCalendar(memoId: Int, description: String, timeInMillis: Long) {
        val strategy = if (getEventId(memoId) == -1L) CalendarUpdateStrategy.ADD else CalendarUpdateStrategy.UPDATE
        processCalendar(memoId, description, timeInMillis, strategy)
    }

    fun removeFromCalendar(memoId: Int) {
        processCalendar(memoId, null, -1, CalendarUpdateStrategy.DELETE)
    }

    private fun processCalendar(memoId: Int, description: String?, timeInMillis: Long, strategy: CalendarUpdateStrategy) {
        val contentResolver = contentResolver
        rxPermissions.requestEachCombined(Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR)
                .subscribe { permission ->
                    when {
                        permission.granted -> when (strategy) {
                            CalendarUpdateStrategy.ADD -> addToCalendarInternal(memoId, description, timeInMillis, contentResolver)
                            CalendarUpdateStrategy.UPDATE -> updateCalendarInternal(memoId, description, timeInMillis, contentResolver)
                            CalendarUpdateStrategy.DELETE -> removeFromCalendarInternal(memoId, contentResolver)
                        }
                        permission.shouldShowRequestPermissionRationale -> Toast.makeText(this, R.string.calendar_perm_denied_toast,
                                Toast.LENGTH_LONG).show()
                        else -> Toast.makeText(this, R.string.calendar_perm_denied_without_ask_again_toast,
                                Toast.LENGTH_LONG).show()
                    }
                }.addTo(compositeDisposable)

    }

    private fun addToCalendarInternal(memoId: Int, description: String?, timeInMillis: Long, contentResolver: ContentResolver) {
        val calendarId = getCalendarId(contentResolver)
        val contentValues = getCalendarEventContentValues(description, timeInMillis, calendarId)
        @SuppressLint("MissingPermission") val uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, contentValues)
        uri?.lastPathSegment?.let {
            val eventId = java.lang.Long.parseLong(it)
            processCalendarEventIdInSharedPref(memoId, eventId, CalendarUpdateStrategy.ADD)
        }
        toast(R.string.calendar_event_added_toast)
    }

    private fun updateCalendarInternal(memoId: Int, description: String?, timeInMillis: Long, contentResolver: ContentResolver) {
        val calendarId = getCalendarId(contentResolver)
        val eventId = getEventId(memoId)
        val contentValues = getCalendarEventContentValues(description, timeInMillis, calendarId)
        val updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId)
        contentResolver.update(updateUri, contentValues, null, null)
        toast(R.string.calendar_event_updated_toast)
    }

    private fun getCalendarEventContentValues(description: String?, timeInMillis: Long, calendarId: Long): ContentValues {
        val contentValues = ContentValues()
        val endDate = timeInMillis + 1000 * 60 * 60
        contentValues.put(CalendarContract.Events.DTSTART, timeInMillis)
        contentValues.put(CalendarContract.Events.DTEND, endDate)
        contentValues.put(CalendarContract.Events.TITLE, resources.getString(R.string.notification_title))
        contentValues.put(CalendarContract.Events.DESCRIPTION, description)
        contentValues.put(CalendarContract.Events.CALENDAR_ID, calendarId)
        contentValues.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().displayName)
        return contentValues
    }

    @SuppressLint("MissingPermission")
    private fun getCalendarId(contentResolver: ContentResolver): Long {
        var calendarId: Long = 0
        val selection = (CalendarContract.Calendars.VISIBLE + " = 1 AND "
                + CalendarContract.Calendars.IS_PRIMARY + " = 1")
        val calendarUri = CalendarContract.Calendars.CONTENT_URI
        var cur = contentResolver.query(calendarUri, null, selection, null, null)
        if (cur != null && cur.count <= 0) {
            cur = contentResolver.query(calendarUri, null, null, null, null)
        }
        if (cur != null && cur.moveToFirst()) {
            calendarId = cur.getLong(cur.getColumnIndex(CalendarContract.Calendars._ID))
        }

        cur?.close()
        return calendarId
    }

    private fun processCalendarEventIdInSharedPref(memoId: Int, eventId: Long, strategy: CalendarUpdateStrategy) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        preferences.edit {
            val key = getCalendarEventKey(memoId)
            when (strategy) {
                CalendarUpdateStrategy.ADD -> putLong(key, eventId)
                CalendarUpdateStrategy.DELETE -> remove(key)
                else -> {
                }
            }
        }
    }

    private fun getCalendarEventKey(memoId: Int): String {
        return resources.getString(R.string.calendar_event_base_key) + memoId
    }

    private fun removeFromCalendarInternal(memoId: Int, contentResolver: ContentResolver) {
        val eventId = getEventId(memoId)
        if (eventId != -1L) {
            val deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId)
            contentResolver.delete(deleteUri, null, null)
            processCalendarEventIdInSharedPref(memoId, eventId, CalendarUpdateStrategy.DELETE)
        }
    }

    private fun getEventId(memoId: Int): Long {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val key = getCalendarEventKey(memoId)
        return preferences.getLong(key, -1)
    }

    private enum class CalendarUpdateStrategy {
        ADD,
        DELETE,
        UPDATE
    }
}