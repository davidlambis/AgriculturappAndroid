package com.interedes.agriculturappv3.activities.chat.chat_sms

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.ListPreference
import android.preference.Preference
import android.preference.PreferenceActivity
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import android.preference.RingtonePreference
import android.text.TextUtils
import android.view.MenuItem
import com.interedes.agriculturappv3.R

/**
 * A [PreferenceActivity] that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 *
 * See [Android Design: Settings](http://developer.android.com/design/patterns/settings.html)
 * for design guidelines and the [Settings API Guide](http://developer.android.com/guide/topics/ui/settings.html)
 * for more information on developing a Settings UI.
 */
class SettingsActivity : AppCompatPreferenceActivity() {

    // Keys as defined in settings.xml

    //public static String VIBRATION_KEY = "pref_vibration";

    companion object {
        var NOTIFICATIONS_KEY = "pref_notifications"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Create a default empty ActionBar
        val actionBar = supportActionBar
        fragmentManager.beginTransaction().replace(android.R.id.content, SettingsFragment()).commit()
    }

    /**
     * SettingsFragment
     * Builds a Preferences screen based on the content in settings.xml
     */
    class SettingsFragment : PreferenceFragment() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            addPreferencesFromResource(R.xml.settings)
        }
    }
}
