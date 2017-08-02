package com.gnest.remember.layout;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.gnest.remember.data.ClickableMemo;
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
    private EditText mMemoEditTextView;
    private String mColor = ColorSpinnerAdapter.Colors.values()[0].toString();
    private Button mSaveButton;
    private ClickableMemo mMemo;

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
        View view = inflater.inflate(R.layout.fragment_edit_memo, container, false);
        mSaveButton = view.findViewById(R.id.save_memo_button);
        mSaveButton.setOnClickListener(this);
        mMemoEditTextView = view.findViewById(R.id.editTextMemo);
        if (mMemo != null) {
            mMemoEditTextView.setText(mMemo.getMemoText());
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
        String textToSave = mMemoEditTextView.getText().toString();
        if (!textToSave.isEmpty()) {
            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this.getContext());
            databaseAccess.open();
            if (mMemo == null) {
                // Add new mMemo
                Memo temp = new Memo(textToSave, mColor);
                databaseAccess.save(temp);
            } else {
                // Update the mMemo
                mMemo.setMemoText(textToSave);
                mMemo.setColor(mColor);
                databaseAccess.update(mMemo);
            }
            databaseAccess.close();
        }
        if (mListener != null) {
            Bundle bundle = null;
            if (mMemo != null) {
                bundle = new Bundle();
                bundle.putInt(ItemFragment.LM_SCROLL_ORIENTATION_KEY, ItemFragment.LM_HORIZONTAL_ORIENTATION);
                bundle.putInt(ItemFragment.POSITION_KEY, mMemo.getPosition());
                bundle.putBoolean(ItemFragment.EXPANDED_KEY, true);
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
