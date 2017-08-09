package com.gnest.remember.layout;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.gnest.remember.R;
import com.gnest.remember.data.Memo;
import com.gnest.remember.data.ClickableMemo;
import com.gnest.remember.db.DatabaseAccess;
import com.gnest.remember.services.AlarmService;
import com.gnest.remember.view.ColorSpinnerAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnEditMemoFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditMemoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditMemoFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    public static final String MEMO_KEY = "memo_param";

    private static Calendar selectedDate = Calendar.getInstance();
    private AppCompatActivity activity;
    private static boolean isAlarmSet;

    private View mView;
    private EditText mMemoEditTextView;
    private Button mSaveButton;
    private ImageView mAddAlert;
    private ClickableMemo mMemo;
    private String mColor;
    private AppBarLayout mAppBarLayout;
    private CompactCalendarView mCompactCalendarView;
    private boolean isCalendarExpanded;

    private OnEditMemoFragmentInteractionListener mListener;

    private SimpleDateFormat mCalendarDateFormat = new SimpleDateFormat("d MMMM yyyy", /*Locale.getDefault()*/Locale.ENGLISH);
    private SimpleDateFormat mCalendarAlarmSetFormat = new SimpleDateFormat("d MMMM yyyy hh:mm");


    private String selectedDateFormatted;

    public EditMemoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment EditMemoFragment.
     */
    public static EditMemoFragment newInstance() {
        return new EditMemoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            Bundle arguments = getArguments();
            mMemo = (ClickableMemo) arguments.getBinder(MEMO_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_edit_memo, container, false);
        mSaveButton = mView.findViewById(R.id.save_memo_button);
        mSaveButton.setOnClickListener(this);
        mMemoEditTextView = mView.findViewById(R.id.editTextMemo);
        if (mMemo != null) {
            mMemoEditTextView.setText(mMemo.getMemoText());
        }
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Toolbar toolbar = mView.findViewById(R.id.toolbar);
        toolbar.setTitle("");
        activity = ((AppCompatActivity) getActivity());
        activity.setSupportActionBar(toolbar);

        mAppBarLayout = mView.findViewById(R.id.app_bar_layout);

        // Set up the CompactCalendarView


        mCompactCalendarView = mView.findViewById(R.id.compactcalendar_view);
        mAddAlert = mView.findViewById(R.id.bt_add_alert);

        // Force English
        mCompactCalendarView.setLocale(TimeZone.getDefault(), /*Locale.getDefault()*/Locale.ENGLISH);

        mCompactCalendarView.setShouldDrawDaysHeader(true);

        mCompactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                selectedDate.setTime(dateClicked);
                setSubtitle(mCalendarDateFormat.format(dateClicked));
                selectedDateFormatted = mCalendarDateFormat.format(dateClicked);
                isCalendarExpanded = !isCalendarExpanded;
                mAppBarLayout.setExpanded(isCalendarExpanded, true);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                setSubtitle(mCalendarDateFormat.format(firstDayOfNewMonth));
            }
        });

        setCurrentDate(Calendar.getInstance().getTime());

        mAddAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment timePickFragment = new TimePickerFragment();
                timePickFragment.show(activity.getSupportFragmentManager(), "timePicker");
            }
        });

        final ImageView arrow = mView.findViewById(R.id.date_picker_arrow);

        RelativeLayout datePickerButton = mView.findViewById(R.id.date_picker_button);

        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCalendarExpanded) {
                    ViewCompat.animate(arrow).rotation(0).start();
                } else {
                    ViewCompat.animate(arrow).rotation(180).start();
                }

                isCalendarExpanded = !isCalendarExpanded;
                mAppBarLayout.setExpanded(isCalendarExpanded, true);
            }
        });
    }


    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = selectedDate;
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            selectedDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
            selectedDate.set(Calendar.MINUTE, minute);
            Calendar now = Calendar.getInstance();
            if (selectedDate.after(now)) {
                isAlarmSet = true;
            }
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save_memo_button:
                onSaveButtonPressed();
        }
    }

    public void onSaveButtonPressed() {
        String textToSave = mMemoEditTextView.getText().toString();
        long savedId = -1;
        if (!textToSave.isEmpty()) {
            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this.getContext());
            databaseAccess.open();
            if (mMemo == null) {
                // Add new mMemo
                Memo temp = new Memo(textToSave, mColor);
                savedId = databaseAccess.save(temp);
            } else {
                // Update the mMemo
                mMemo.setMemoText(textToSave);
                mMemo.setColor(mColor);
                databaseAccess.update(mMemo);
                savedId = mMemo.getId();
            }
            databaseAccess.close();
        }

        if (isAlarmSet) {
            AlarmManager manager = (AlarmManager)activity.getSystemService(Context.ALARM_SERVICE);
            String notificationText = textToSave;
            if (notificationText.length() > 10) {
                notificationText = notificationText.substring(0, 10).concat("...");
            }
            Intent intent = AlarmService.getServiceIntent(activity, notificationText, savedId);
            PendingIntent pendingIntent = PendingIntent.getService(activity, (int) savedId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            manager.set(AlarmManager.RTC_WAKEUP, selectedDate.getTimeInMillis(), pendingIntent);
            isAlarmSet = false;
            StringBuilder alarmSetToast = new StringBuilder();
            alarmSetToast
                    .append(getString(R.string.alarm_set_text))
                    .append(" ")
                    .append(mCalendarAlarmSetFormat.format(selectedDate.getTime()));
            Toast.makeText(getActivity(), alarmSetToast, Toast.LENGTH_LONG).show();
        }

        if (mListener != null) {
            Bundle bundle = null;
            if (mMemo != null) {
                bundle = new Bundle();
                bundle.putInt(ListItemFragment.LM_SCROLL_ORIENTATION_KEY, ListItemFragment.LM_HORIZONTAL_ORIENTATION);
                bundle.putInt(ListItemFragment.POSITION_KEY, mMemo.getPosition());
                bundle.putBoolean(ListItemFragment.EXPANDED_KEY, true);
            }
            mListener.onSaveEditMemoFragmentInteraction(bundle);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.ab_editfragment, menu);
        MenuItem item = menu.findItem(R.id.item_color_choice_spinner);
        Spinner colorChoiceSpinner = (Spinner) item.getActionView();
        ColorSpinnerAdapter colorSpinnerAdapter = new ColorSpinnerAdapter(getContext());
        colorChoiceSpinner.setAdapter(colorSpinnerAdapter);
        colorChoiceSpinner.setOnItemSelectedListener(this);
        if (mMemo != null) {
            colorChoiceSpinner.setSelection(ColorSpinnerAdapter.Colors.valueOf(mMemo.getColor()).ordinal());
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        mColor = ColorSpinnerAdapter.Colors.values()[i].name();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void setSubtitle(String subtitle) {
        TextView datePickerTextView = mView.findViewById(R.id.date_picker_text_view);

        if (datePickerTextView != null) {
            datePickerTextView.setText(subtitle);
        }
    }

    public void setCurrentDate(Date date) {
        selectedDate.setTime(date);
        String currentDate = mCalendarDateFormat.format(date);
        setSubtitle(currentDate);
        if (mCompactCalendarView != null) {
            mCompactCalendarView.setCurrentDate(date);
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
        void onSaveEditMemoFragmentInteraction(Bundle bundle);
    }
}
