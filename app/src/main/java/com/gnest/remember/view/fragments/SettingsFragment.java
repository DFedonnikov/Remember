package com.gnest.remember.view.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int REQUEST_CODE_NOTIFICATION_SOUND = 1;
    @BindView(R.id.settings_fragment_toolbar)
    Toolbar toolbar;

    private static SharedPreferences SHARED_PREFERENCES;

    private View mView;
    private OnSettingsFragmentInteractionListener mListener;
    private DrawerLayout drawerLayout;
    private Unbinder unbinder;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, mView);
        drawerLayout = getActivity().findViewById(R.id.drawer_layout);
        return mView;
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
        activity.setSupportActionBar(toolbar);
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            mListener.syncDrawerToggleState();
        }
        SHARED_PREFERENCES = PreferenceManager.getDefaultSharedPreferences(activity);
        SHARED_PREFERENCES.registerOnSharedPreferenceChangeListener(this);
        setFontPrefSummary();
        setNotificationSoundPrefSummary();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        SHARED_PREFERENCES.unregisterOnSharedPreferenceChangeListener(this);
        SHARED_PREFERENCES = null;
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (App.NOTIFICATION_SOUND_KEY.equals(preference.getKey())) {
            openRingtonePicker();
            return true;
        } else {
            return super.onPreferenceTreeClick(preference);
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
                drawerLayout.openDrawer(GravityCompat.START);
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
                String uriPath = setNotificationSoundPrefSummary();
                App.setNotificationSound(uriPath != null ? Uri.parse(uriPath) : Settings.System.DEFAULT_NOTIFICATION_URI);
        }
    }

    @NonNull
    private String setFontPrefSummary() {
        Preference fontSizePref = findPreference(App.FONT_SIZE_KEY);
        String fontSize = SHARED_PREFERENCES.getString(App.FONT_SIZE_KEY, "");
        fontSizePref.setSummary(fontSize);
        return fontSize;
    }

    private String setNotificationSoundPrefSummary() {
        Preference notificationSoundPref = findPreference(App.NOTIFICATION_SOUND_KEY);
        String uriPath = SHARED_PREFERENCES.getString(App.NOTIFICATION_SOUND_KEY, null);
        String summary = uriPath == null || uriPath.isEmpty() ? "" : RingtoneManager.getRingtone(getContext(), Uri.parse(uriPath)).getTitle(getContext());
        notificationSoundPref.setSummary(summary);
        return uriPath;
    }

    public interface OnSettingsFragmentInteractionListener {
        void syncDrawerToggleState();
    }
}