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
import android.support.v4.app.FragmentManager;
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
import com.gnest.remember.App;
import com.gnest.remember.R;
import com.gnest.remember.services.AlarmReceiver;
import com.gnest.remember.presenter.EditMemoPresenter;
import com.gnest.remember.presenter.IEditMemoPresenter;
import com.gnest.remember.view.adapters.ColorSpinnerAdapter;
import com.gnest.remember.view.IEditMemoView;
import com.gnest.remember.view.layoutmanagers.MyGridLayoutManager;
import com.hannesdorfmann.mosby3.mvp.MvpFragment;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class EditMemoFragment extends MvpFragment<IEditMemoView, IEditMemoPresenter>
        implements AdapterView.OnItemSelectedListener, IEditMemoView, TimeSetListener {
    public static final String MEMO_ID_KEY = "memo_param";
    private static final String TEXT_KEY = "Text key";

    private Unbinder mUnbinder;

    @BindView(R.id.editTextMemo)
    EditText memoEditTextView;
    @BindView(R.id.bt_remove_alert)
    ImageView removeAlert;
    @BindView(R.id.date_picker_arrow)
    ImageView arrow;
    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.compactcalendar_view)
    CompactCalendarView compactCalendarView;
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

    private AppCompatSpinner colorChoiceSpinner;
    private DrawerLayout mDrawerLayout;
    private TimePickerFragment timePickFragment;

    private int mMemoId;
    private String mColor;
    private OnEditMemoFragmentInteractionListener mListener;

    public static EditMemoFragment newInstance(Bundle bundle) {
        EditMemoFragment fragment = new EditMemoFragment();
        if (bundle == null) {
            bundle = new Bundle();
            bundle.putInt(MEMO_ID_KEY, -1);
        }
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() != null) {
            Bundle arguments = getArguments();
            mMemoId = arguments.getInt(MEMO_ID_KEY, -1);
        }
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_memo, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        timePickFragment = new TimePickerFragment();
        timePickFragment.setTimeSetListener(this);
        memoEditTextView.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                hideKeyboard(v);
            }
        });
        memoEditTextView.setTypeface(App.FONT);
        memoEditTextView.setTextSize(App.FONT_SIZE);
        removeAlert.setOnClickListener(alertView -> {
            presenter.processRemoveAlarm(alarmRemoveText);
            setAlarmVisibility(false);
        });
        compactCalendarView.setLocale(TimeZone.getDefault(), Locale.getDefault());
        compactCalendarView.setShouldDrawDaysHeader(true);
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
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

        presenter.loadData();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        toolbar.setTitle("");
        AppCompatActivity mActivity = ((AppCompatActivity) getActivity());
        if (mActivity != null) {
            mActivity.setSupportActionBar(toolbar);
            ActionBar actionBar = mActivity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(true);
                mListener.syncDrawerToggleState();
            }
            mDrawerLayout = mActivity.findViewById(R.id.drawer_layout);
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
        mListener = null;
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        colorChoiceSpinner.setOnItemSelectedListener(null);
        mUnbinder.unbind();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (memoEditTextView != null) {
            outState.putString(TEXT_KEY, memoEditTextView.getText().toString());
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.getString(TEXT_KEY) != null) {
            memoEditTextView.setText(savedInstanceState.getString(TEXT_KEY));
        }
    }

    @Override
    @NonNull
    public IEditMemoPresenter createPresenter() {
        return new EditMemoPresenter(mMemoId);
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

    @Override
    public void setData(String memoText, String color, boolean alarmSet) {
        memoEditTextView.setText(memoText);
        mColor = color;
        setAlarmVisibility(alarmSet);
    }

    public void saveMemo() {
        presenter.saveData();
    }

    public void onBackButtonPressed() {
        saveMemo();
        presenter.processPressBackButton();
    }

    @Override
    public String getMemoText() {
        return memoEditTextView.getText().toString();
    }

    @Override
    public String getMemoColor() {
        return mColor;
    }

    @Override
    public String getAlarmSetText() {
        return alarmSetText;
    }

    @Override
    public void returnFromEdit(int memoPosition) {
        if (mListener != null) {
            Bundle bundle = null;
            if (memoPosition != -1) {
                bundle = new Bundle();
                bundle.putInt(MyGridLayoutManager.LM_SCROLL_ORIENTATION_KEY, MyGridLayoutManager.HORIZONTAL);
                bundle.putInt(MyGridLayoutManager.POSITION_KEY, memoPosition);
                bundle.putBoolean(ListItemFragment.EXPANDED_KEY, true);
            }
            mListener.onReturnFromEditFragmentInteraction(bundle);
        }
    }

    @Override
    public void setAlarm(boolean isSet, long alarmDate, String notificationText, int id) {
        Intent intent = AlarmReceiver.getReceiverIntent(getContext(), id, notificationText, alarmDate, isSet);
        mListener.sendBroadcast(intent);
    }

    @Override
    public void showAlarmToast(String alarmText) {
        Toast.makeText(getContext(), alarmText, Toast.LENGTH_LONG).show();
    }

    @Override
    public void setCalendarExpanded(boolean isCalendarExpanded) {
        appBarLayout.setExpanded(isCalendarExpanded, true);
    }

    @Override
    public void showTimePicker(int hour, int minute) {
        Bundle bundle = new Bundle();
        bundle.putInt(TimePickerFragment.HOUR_KEY, hour);
        bundle.putInt(TimePickerFragment.MINUTE_KEY, minute);
        timePickFragment.setArguments(bundle);
        timePickFragment.show(mListener.getSupportFragmentManager(), "timePicker");
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
        colorChoiceSpinner = (AppCompatSpinner) item.getActionView();
        colorChoiceSpinner.setBackgroundColor(primaryColor);
        ColorSpinnerAdapter colorSpinnerAdapter = new ColorSpinnerAdapter(getContext());
        colorChoiceSpinner.setAdapter(colorSpinnerAdapter);
        colorChoiceSpinner.setOnItemSelectedListener(this);
        if (mColor != null) {
            colorChoiceSpinner.setSelection(ColorSpinnerAdapter.Colors.valueOf(mColor).ordinal());
        }
    }

    @Override
    public void onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
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
        if (compactCalendarView != null) {
            compactCalendarView.setCurrentDate(date);
        }
    }

    @Override
    public void onTimeSet(int hour, int minute) {
        presenter.processTimeSet(hour, minute);
    }

    @Override
    public void setAlarmVisibility(boolean visible) {
        removeAlert.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    private void hideKeyboard(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager) App.self().getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the mActivity and potentially other fragments contained in that
     * mActivity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

    public interface OnEditMemoFragmentInteractionListener {
        void onReturnFromEditFragmentInteraction(Bundle bundle);

        void syncDrawerToggleState();

        void sendBroadcast(Intent intent);

        FragmentManager getSupportFragmentManager();
    }
}
