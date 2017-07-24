package com.gnest.remember.layout;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.gnest.remember.R;
import com.gnest.remember.data.Memo;
import com.gnest.remember.data.SelectableMemo;
import com.gnest.remember.db.DatabaseAccess;
import com.gnest.remember.view.ColorSpinnerAdapter;

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
    private EditText memoEditTextView;
    private int textViewBackgroundId = R.drawable.textview_background_yellow;
    private int textViewBackgroundSelectedId = R.drawable.textview_background_select_yellow;
    private Button saveButton;
    private SelectableMemo memo;

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
            memo = (SelectableMemo) arguments.getBinder(MEMO_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_memo, container, false);
        saveButton = view.findViewById(R.id.save_memo_button);
        saveButton.setOnClickListener(this);
        memoEditTextView = view.findViewById(R.id.editTextMemo);
        if (memo != null) {
            memoEditTextView.setText(memo.getMemoText());
        }
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save_memo_button:
                onSaveButtonPressed();
        }
    }

    public void onSaveButtonPressed() {
        String textToSave = memoEditTextView.getText().toString();
        if (!textToSave.isEmpty()) {
            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this.getContext());
            databaseAccess.open();
            if (memo == null) {
                // Add new memo
                Memo temp = new Memo(textToSave, textViewBackgroundId, textViewBackgroundSelectedId);
                databaseAccess.save(temp);
            } else {
                // Update the memo
                memo.setMemoText(textToSave);
                memo.setTextViewBackgroundId(textViewBackgroundId);
                memo.setTextViewBackgroundSelectedId(textViewBackgroundSelectedId);
                databaseAccess.update(memo);
            }
            databaseAccess.close();
        }
        if (mListener != null) {

            mListener.onSaveEditMemoFragmentInteraction(ItemFragment.LM_HORIZONTAL_ORIENTATION, memo.getPosition());
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
        if (memo != null) {
            colorChoiceSpinner.setSelection(ColorSpinnerAdapter.Colors.getColorPositionByMemoBackGroundId(memo.getTextViewBackgroundId()));
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        textViewBackgroundId = ColorSpinnerAdapter.Colors.values()[i].getMemoBackgroundId();
        textViewBackgroundSelectedId = ColorSpinnerAdapter.Colors.values()[i].getMemoBackgroundSelectedId();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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
        void onSaveEditMemoFragmentInteraction(int lmHorizontalOrientation, int position);
    }
}
