package com.dgtalize.dndcharmanager.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.dgtalize.dndcharmanager.R;
import com.dgtalize.dndcharmanager.model.Character;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CharacterMoneyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CharacterMoneyFragment extends CharacterFragment {

    private EditText cpEditText;
    private EditText spEditText;
    private EditText gpEditText;
    private EditText ppEditText;

    public CharacterMoneyFragment() {
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param character Character
     * @return A new instance of fragment CharacterHomeFragment.
     */
    public static CharacterMoneyFragment newInstance(Character character) {
        CharacterMoneyFragment fragment = new CharacterMoneyFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CHARACTER, character);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_character_money, container, false);

        this.cpEditText = (EditText) view.findViewById(R.id.cpEditText);
        this.spEditText = (EditText) view.findViewById(R.id.spEditText);
        this.gpEditText = (EditText) view.findViewById(R.id.gpEditText);
        this.ppEditText = (EditText) view.findViewById(R.id.ppEditText);

        initializeValues();

        //region Events
        this.cpEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(charSequence) && TextUtils.isDigitsOnly(charSequence)) {
                    getCharacter().getMoney().setCp(Integer.parseInt(charSequence.toString()));
                    notifyCharacterChange();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //SP
        this.spEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(charSequence) && TextUtils.isDigitsOnly(charSequence)) {
                    getCharacter().getMoney().setSp(Integer.parseInt(charSequence.toString()));
                    notifyCharacterChange();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        this.gpEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(charSequence) && TextUtils.isDigitsOnly(charSequence)) {
                    getCharacter().getMoney().setGp(Integer.parseInt(charSequence.toString()));
                    notifyCharacterChange();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //PP
        this.ppEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(charSequence) && TextUtils.isDigitsOnly(charSequence)) {
                    getCharacter().getMoney().setPp(Integer.parseInt(charSequence.toString()));
                    notifyCharacterChange();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //endregion

        return view;
    }

    private void initializeValues() {
        this.cpEditText.setText(String.valueOf(getCharacter().getMoney().getCp()));
        this.spEditText.setText(String.valueOf(getCharacter().getMoney().getSp()));
        this.gpEditText.setText(String.valueOf(getCharacter().getMoney().getGp()));
        this.ppEditText.setText(String.valueOf(getCharacter().getMoney().getPp()));
    }
}
