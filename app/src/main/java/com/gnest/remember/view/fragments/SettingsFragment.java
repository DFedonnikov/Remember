package com.gnest.remember.view.fragments;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.gnest.remember.App;
import com.gnest.remember.R;
import com.squareup.leakcanary.RefWatcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int REQUEST_CODE_NOTIFICATION_SOUND = 1;
    private static final int REQUEST_CODE_CHANNEL_SETUP = 2;

    @BindView(R.id.settings_fragment_toolbar)
    Toolbar toolbar;

    private static SharedPreferences SHARED_PREFERENCES;

    private OnSettingsFragmentInteractionListener mListener;
    private Unbinder mUnbinder;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    public SettingsFragment() {
    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        RefWatcher refWatcher = App.getRefWatcher();
        refWatcher.watch(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view != null) {
            mUnbinder = ButterKnife.bind(this, view);
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSettingsFragmentInteractionListener) {
            mListener = (OnSettingsFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSettingsFragmentInteractionListener");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
            ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
            SHARED_PREFERENCES = PreferenceManager.getDefaultSharedPreferences(activity);
            SHARED_PREFERENCES.registerOnSharedPreferenceChangeListener(this);
        }
        setFontPrefSummary();
        setNotificationSoundPrefSummary(getRingtoneUri());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
        SHARED_PREFERENCES.unregisterOnSharedPreferenceChangeListener(this);
        SHARED_PREFERENCES = null;
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (App.NOTIFICATION_SOUND_KEY.equals(preference.getKey())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                openNotificationChanelSetup();
            } else {
                openRingtonePicker();
            }
            return true;
        } else {
            return super.onPreferenceTreeClick(preference);
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void openNotificationChanelSetup() {
        Context context = getContext();
        if (context != null) {
            Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
            intent.putExtra(Settings.EXTRA_CHANNEL_ID, App.NOTIFICATION_CHANNEL_ID);
            startActivityForResult(intent, REQUEST_CODE_CHANNEL_SETUP);
        }
    }

    private void openRingtonePicker() {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, Settings.System.DEFAULT_NOTIFICATION_URI);

        String notificationSoundPath = SHARED_PREFERENCES.getString(App.NOTIFICATION_SOUND_KEY, null);
        if (notificationSoundPath != null) {
            if (notificationSoundPath.length() == 0) {
                // Select "Silent"
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, "");
            } else {
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(notificationSoundPath));
            }
        } else {
            // No ringtone has been selected, set to the default
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Settings.System.DEFAULT_NOTIFICATION_URI);
        }

        startActivityForResult(intent, REQUEST_CODE_NOTIFICATION_SOUND);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_NOTIFICATION_SOUND && data != null) {
            Uri ringtone = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            SharedPreferences.Editor editor = SHARED_PREFERENCES.edit();
            if (ringtone != null) {
                editor.putString(App.NOTIFICATION_SOUND_KEY, ringtone.toString());
            } else {
                // "Silent" was selected
                editor.putString(App.NOTIFICATION_SOUND_KEY, "");
            }
            editor.apply();
        } else if (requestCode == REQUEST_CODE_CHANNEL_SETUP) {
            setNotificationSoundPrefSummary(getRingtoneUri());
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mListener.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case App.FONT_SIZE_KEY:
                String fontSize = setFontPrefSummary();
                App.setFontSize(Integer.parseInt(fontSize));
                break;
            case App.NOTIFICATION_SOUND_KEY:
                Uri ringtoneUri = getRingtoneUri();
                setNotificationSoundPrefSummary(ringtoneUri);
                App.setNotificationSound(ringtoneUri);
        }
    }

    @NonNull
    private String setFontPrefSummary() {
        Preference fontSizePref = findPreference(App.FONT_SIZE_KEY);
        String fontSize = SHARED_PREFERENCES.getString(App.FONT_SIZE_KEY, "");
        fontSizePref.setSummary(fontSize);
        return fontSize;
    }

    private void setNotificationSoundPrefSummary(Uri ringtoneUri) {
        Preference notificationSoundPref = findPreference(App.NOTIFICATION_SOUND_KEY);
        Context context = getContext();
        if (context != null) {
            String summary = RingtoneManager.getRingtone(context, ringtoneUri).getTitle(context);
            notificationSoundPref.setSummary(summary);
        }
    }

    private Uri getRingtoneUri() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            FragmentActivity activity = getActivity();
            if (activity != null) {
                NotificationManager manager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
                if (manager != null) {
                    NotificationChannel channel = manager.getNotificationChannel(App.NOTIFICATION_CHANNEL_ID);
                    if (channel != null) {
                        return channel.getSound();
                    }
                }
            }
        }
        String uriPath = SHARED_PREFERENCES.getString(App.NOTIFICATION_SOUND_KEY, "");
        return uriPath.isEmpty() ? Settings.System.DEFAULT_NOTIFICATION_URI : Uri.parse(uriPath);
    }


    public interface OnSettingsFragmentInteractionListener {
        void onBackPressed();
    }
}
