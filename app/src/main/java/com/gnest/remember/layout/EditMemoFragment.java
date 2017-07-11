package com.gnest.remember.layout;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.gnest.remember.R;
import com.gnest.remember.data.Memo;
import com.gnest.remember.data.SelectableMemo;
import com.gnest.remember.db.DatabaseAccess;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnEditMemoFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditMemoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditMemoFragment extends Fragment implements View.OnClickListener {
    public static final String MEMO_KEY = "memo_param";
    private EditText memoEditTextView;
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
                Memo temp = new Memo(textToSave);
                databaseAccess.save(temp);
            } else {
                // Update the memo
                memo.setMemoText(textToSave);
                databaseAccess.update(memo);
            }
            databaseAccess.close();
        }
        if (mListener != null) {
            mListener.onSaveEditMemoFragmentInteraction();
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
        void onSaveEditMemoFragmentInteraction();
    }
}
