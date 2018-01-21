package com.gnest.remember.view.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.gnest.remember.R;
import com.gnest.remember.model.services.AlarmReceiver;
import com.gnest.remember.presenter.EditMemoPresenter;
import com.gnest.remember.presenter.IEditMemoPresenter;
import com.gnest.remember.view.adapters.ColorSpinnerAdapter;
import com.gnest.remember.view.IEditMemoView;
import com.gnest.remember.view.layoutmanagers.MyGridLayoutManager;
import com.hannesdorfmann.mosby.mvp.MvpFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnEditMemoFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditMemoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditMemoFragment extends MvpFragment<IEditMemoView, IEditMemoPresenter>
        implements AdapterView.OnItemSelectedListener, IEditMemoView, TimeSetListener {
    public static final String MEMO_ID_KEY = "memo_param";
    private static final String TEXT_KEY = "Text key";

    private AppCompatActivity activity;
    private Unbinder unbinder;

    @BindView(R.id.editTextMemo)
    EditText mMemoEditTextView;
    @BindView(R.id.bt_remove_alert)
    ImageView mRemoveAlert;
    @BindView(R.id.date_picker_arrow)
    ImageView arrow;
    @BindView(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.compactcalendar_view)
    CompactCalendarView mCompactCalendarView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.date_picker_button)
    RelativeLayout datePickerButton;
    @BindView(R.id.date_picker_text_view)
    TextView datePickerTextView;

    @BindString(R.string.alarm_remove_text)
    String alarmRemoveText;
    @BindString(R.string.alarm_set_text)
    String alarmSetText;

    @BindColor(R.color.colorPrimary)
    int primaryColor;

    private DrawerLayout drawerLayout;

    private int memoId = -1;
    private String mColor;
    private OnEditMemoFragmentInteractionListener mListener;

    public static EditMemoFragment newInstance() {
        return new EditMemoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            Bundle arguments = getArguments();
            memoId = arguments.getInt(MEMO_ID_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_edit_memo, container, false);
        unbinder = ButterKnife.bind(this, mView);
        mMemoEditTextView.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                hideKeyboard(v);
            }
        });
        mRemoveAlert.setOnClickListener(view -> {
            presenter.processRemoveAlarm(alarmRemoveText);
            setAlarmVisibility(false);
        });
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        toolbar.setTitle("");
        activity = ((AppCompatActivity) getActivity());
        activity.setSupportActionBar(toolbar);
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            mListener.syncDrawerToggleState();
        }

        drawerLayout = getActivity().findViewById(R.id.drawer_layout);

        mCompactCalendarView.setLocale(TimeZone.getDefault(), Locale.getDefault());

        mCompactCalendarView.setShouldDrawDaysHeader(true);

        mCompactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                presenter.processDayClicked(dateClicked);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                presenter.processMonthScroll(firstDayOfNewMonth);
            }
        });

        datePickerButton.setOnClickListener(v -> presenter.processDatePicker());

        if (memoId != -1) {
            presenter.loadData();
        } else {
            presenter.processSetCurrentDate(Calendar.getInstance());
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnEditMemoFragmentInteractionListener) {
            mListener = (OnEditMemoFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnEditMemoFragmentInteractionListener");
        }
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
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TEXT_KEY, mMemoEditTextView.getText().toString());
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            mMemoEditTextView.setText(savedInstanceState.getString(TEXT_KEY));
        }
    }

    @Override
    @NonNull
    public IEditMemoPresenter createPresenter() {
        return new EditMemoPresenter(memoId);
    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        public static final String HOUR_KEY = "HOUR_KEY";

        public static final String MINUTE_KEY = "MINUTE_KEY";
        private TimeSetListener mListener;

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int hour = 0;
            int minute = 0;
            if (getArguments() != null) {
                hour = getArguments().getInt(HOUR_KEY);
                minute = getArguments().getInt(MINUTE_KEY);
            }
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            if (mListener != null) {
                mListener.onTimeSet(hourOfDay, minute);
            }
        }

        public void setTimeSetListener(TimeSetListener listener) {
            this.mListener = listener;
        }

    }

    public void onBackButtonPressed() {
        saveMemo(false);
    }

    @Override
    public void setData(String memoText, String color, boolean alarmSet) {
        mMemoEditTextView.setText(memoText);
        mColor = color;
        setAlarmVisibility(alarmSet);
    }

    public void saveMemo(boolean isTriggeredByDrawerItem) {
        presenter.processSaveMemo(mMemoEditTextView.getText().toString(), mColor, alarmSetText, isTriggeredByDrawerItem);
    }

    @Override
    public void memoSavedInteraction(int memoPosition, boolean isTriggeredByDrawerItem) {
        if (mListener != null) {
            Bundle bundle = null;
            if (memoPosition != -1) {
                bundle = new Bundle();
                bundle.putInt(MyGridLayoutManager.LM_SCROLL_ORIENTATION_KEY, MyGridLayoutManager.HORIZONTAL);
                bundle.putInt(MyGridLayoutManager.POSITION_KEY, memoPosition);
                bundle.putBoolean(ListItemFragment.EXPANDED_KEY, true);
            }
            mListener.onSaveEditMemoFragmentInteraction(bundle, isTriggeredByDrawerItem);
        }
    }

    @Override
    public void setAlarm(boolean isSet, long alarmDate, String notificationText, int id) {
        Intent intent = AlarmReceiver.getReceiverIntent(getContext(), id, notificationText, alarmDate, isSet);
        activity.sendBroadcast(intent);
    }

    @Override
    public void showAlarmToast(String alarmText) {
        Toast.makeText(activity, alarmText, Toast.LENGTH_LONG).show();
    }

    @Override
    public void setCalendarExpanded(boolean isCalendarExpanded) {
        mAppBarLayout.setExpanded(isCalendarExpanded, true);
    }

    @Override
    public void showTimePicker(int hour, int minute) {
        TimePickerFragment timePickFragment = new TimePickerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(TimePickerFragment.HOUR_KEY, hour);
        bundle.putInt(TimePickerFragment.MINUTE_KEY, minute);
        timePickFragment.setArguments(bundle);
        timePickFragment.setTimeSetListener(this);
        timePickFragment.show(activity.getSupportFragmentManager(), "timePicker");
    }

    @Override
    public void animateArrow(boolean isCalendarExpanded) {
        int rotation = isCalendarExpanded ? 0 : 180;
        ViewCompat.animate(arrow).rotation(rotation).start();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.ab_editfragment, menu);
        MenuItem item = menu.findItem(R.id.item_color_choice_spinner);
        AppCompatSpinner colorChoiceSpinner = (AppCompatSpinner) item.getActionView();
        colorChoiceSpinner.setBackgroundColor(primaryColor);
        ColorSpinnerAdapter colorSpinnerAdapter = new ColorSpinnerAdapter(getContext());
        colorChoiceSpinner.setAdapter(colorSpinnerAdapter);
        colorChoiceSpinner.setOnItemSelectedListener(this);
        if (mColor != null) {
            colorChoiceSpinner.setSelection(ColorSpinnerAdapter.Colors.valueOf(mColor).ordinal());
        }
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
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        mColor = ColorSpinnerAdapter.Colors.values()[i].name();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void setSubtitle(String subtitle) {
        if (datePickerTextView != null) {
            datePickerTextView.setText(subtitle);
        }
    }

    @Override
    public void setCurrentDate(Date date) {
        if (mCompactCalendarView != null) {
            mCompactCalendarView.setCurrentDate(date);
        }
    }

    @Override
    public void onTimeSet(int hour, int minute) {
        presenter.processTimeSet(hour, minute);
    }

    @Override
    public void setAlarmVisibility(boolean visible) {
        mRemoveAlert.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    private void hideKeyboard(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

    public interface OnEditMemoFragmentInteractionListener {
        void onSaveEditMemoFragmentInteraction(Bundle bundle, boolean isTriggeredByDrawerItem);

        void syncDrawerToggleState();
    }
}
