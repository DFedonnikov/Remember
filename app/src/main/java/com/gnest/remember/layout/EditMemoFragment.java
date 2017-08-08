package com.gnest.remember.layout;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
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
import com.gnest.remember.view.ColorSpinnerAdapter;

import java.text.ParseException;
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
    private static final String DATE_PARSE_EXC_TAG = "date_parse_exc_tag";

    private static Calendar selectedDate;

    private View view;
    private EditText mMemoEditTextView;
    private String mColor = ColorSpinnerAdapter.Colors.values()[0].toString();
    private Button mSaveButton;
    private ClickableMemo mMemo;

    private SimpleDateFormat calendarDateFormat = new SimpleDateFormat("d MMMM yyyy", /*Locale.getDefault()*/Locale.ENGLISH);

    private String selectedDateFormatted;

    private AppBarLayout mAppBarLayout;

    private boolean isCalendarExpanded = false;


    private CompactCalendarView mCompactCalendarView;

    private OnEditMemoFragmentInteractionListener mListener;

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
        view = inflater.inflate(R.layout.fragment_edit_memo, container, false);
        mSaveButton = view.findViewById(R.id.save_memo_button);
        mSaveButton.setOnClickListener(this);
        mMemoEditTextView = view.findViewById(R.id.editTextMemo);
        if (mMemo != null) {
            mMemoEditTextView.setText(mMemo.getMemoText());
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.setSupportActionBar(toolbar);

        mAppBarLayout = view.findViewById(R.id.app_bar_layout);

        // Set up the CompactCalendarView


        mCompactCalendarView = view.findViewById(R.id.compactcalendar_view);

        // Force English
        mCompactCalendarView.setLocale(TimeZone.getDefault(), /*Locale.getDefault()*/Locale.ENGLISH);

        mCompactCalendarView.setShouldDrawDaysHeader(true);

        mCompactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                selectedDate = Calendar.getInstance();
                selectedDate.setTime(dateClicked);
                setSubtitle(calendarDateFormat.format(dateClicked));
                selectedDateFormatted = calendarDateFormat.format(dateClicked);
                isCalendarExpanded = !isCalendarExpanded;
                mAppBarLayout.setExpanded(isCalendarExpanded, true);
                DialogFragment timePickFragment = new TimePickerFragment();
                timePickFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                setSubtitle(calendarDateFormat.format(firstDayOfNewMonth));
            }
        });

        if (mMemo == null) {
            setCurrentDate(new Date());
        } else {
            try {
                setCurrentDate(calendarDateFormat.parse(mMemo.getDate()));
            } catch (ParseException e) {
                Log.d(DATE_PARSE_EXC_TAG, "date parse exception");
            }
        }


        final ImageView arrow = view.findViewById(R.id.date_picker_arrow);

        RelativeLayout datePickerButton = view.findViewById(R.id.date_picker_button);

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
            Toast.makeText(getContext(), selectedDate.getTime().toString(), Toast.LENGTH_SHORT).show();
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
        if (!textToSave.isEmpty()) {
            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this.getContext());
            databaseAccess.open();
            if (mMemo == null) {
                // Add new mMemo
                Memo temp = new Memo(textToSave, mColor, selectedDateFormatted);
                databaseAccess.save(temp);
            } else {
                // Update the mMemo
                mMemo.setMemoText(textToSave);
                mMemo.setColor(mColor);
                mMemo.setDate(selectedDateFormatted);
                databaseAccess.update(mMemo);
            }
            databaseAccess.close();
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
        TextView datePickerTextView = view.findViewById(R.id.date_picker_text_view);

        if (datePickerTextView != null) {
            datePickerTextView.setText(subtitle);
        }
    }

    public void setCurrentDate(Date date) {
        String currentDate = calendarDateFormat.format(date);
        selectedDateFormatted = currentDate;
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
