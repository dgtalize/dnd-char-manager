package com.dgtalize.dndcharmanager.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dgtalize.dndcharmanager.R;
import com.dgtalize.dndcharmanager.model.Character;
import com.dgtalize.dndcharmanager.ui.PointsDialog;

import java.security.InvalidParameterException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CharacterHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CharacterHomeFragment extends CharacterFragment {
    private static final String LOG_TAG = "CharacterHomeFragment";

    private TextView xpTextView;
    private ImageButton xpSubsButton;
    private ImageButton xpAddButton;
    private TextView levelTextView;
    private ProgressBar hpProgressBar;
    private ImageButton hpDamageButton;
    private ImageButton hpSubsButton;
    private ImageButton hpAddButton;
    private ImageButton hpHealButton;
    private TextView hpTextView;
    private TextView acTextView;
    private TextView babTextView;
    private TextView svFortText;
    private TextView svReflexText;
    private TextView svWillText;
    private TextView grappleTextView;

    public CharacterHomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param character Character
     * @return A new instance of fragment CharacterHomeFragment.
     */
    public static CharacterHomeFragment newInstance(Character character) {
        CharacterHomeFragment fragment = new CharacterHomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_character_home, container, false);

        xpTextView = (TextView) view.findViewById(R.id.xpTextView);
        xpSubsButton = (ImageButton) view.findViewById(R.id.xpSubsButton);
        xpAddButton = (ImageButton) view.findViewById(R.id.xpAddButton);
        levelTextView = (TextView) view.findViewById(R.id.levelTextView);
        hpProgressBar = (ProgressBar) view.findViewById(R.id.hpProgressBar);
        hpTextView = (TextView) view.findViewById(R.id.hpTextView);
        hpDamageButton = (ImageButton) view.findViewById(R.id.hpDamageButton);
        hpSubsButton = (ImageButton) view.findViewById(R.id.hpSubsButton);
        hpAddButton = (ImageButton) view.findViewById(R.id.hpAddButton);
        hpHealButton = (ImageButton) view.findViewById(R.id.hpHealButton);
        acTextView = (TextView) view.findViewById(R.id.acTextView);
        babTextView = (TextView) view.findViewById(R.id.babTextView);
        svFortText = (TextView) view.findViewById(R.id.svFortText);
        svReflexText = (TextView) view.findViewById(R.id.svReflexText);
        svWillText = (TextView) view.findViewById(R.id.svWillText);
        grappleTextView = (TextView) view.findViewById(R.id.grappleTextView);

        //Add/Substract XP buttons
        xpSubsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askForXpPoints(-1);
            }
        });
        xpAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askForXpPoints(1);
            }
        });

        //Add/Substract HP buttons
        hpSubsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askForHpPoints(-1);
            }
        });
        hpAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askForHpPoints(1);
            }
        });
        hpDamageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askForCurrentHpPoints(true);
            }
        });
        hpHealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askForCurrentHpPoints(false);
            }
        });


        //fill the view with data from Character
        fillCharacterData();

        return view;
    }

    private void fillCharacterData() {
        updateCharacterXp();
        updateCharacterHp();
        acTextView.setText(String.valueOf(getCharacter().getArmorClass()));
        babTextView.setText(String.valueOf(getCharacter().getBAB()));
        //Savings
        svFortText.setText(String.valueOf(getCharacter().getSavingFortitude()));
        svReflexText.setText(String.valueOf(getCharacter().getSavingReflex()));
        svWillText.setText(String.valueOf(getCharacter().getSavingWill()));

        grappleTextView.setText(String.valueOf(getCharacter().getGrappleModifier()));
    }

    private void updateCharacterXp() {
        xpTextView.setText(String.valueOf(getCharacter().getXp()));
        levelTextView.setText(String.format(getString(R.string.level_number), getCharacter().getLevel()));
    }

    private void updateCharacterHp() {
        hpProgressBar.setMax(getCharacter().getHp());
        hpProgressBar.setProgress(getCharacter().getCurrentHp());
        hpTextView.setText(String.format(getString(R.string.hp_current_status), getCharacter().getCurrentHp(), getCharacter().getHp()));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void askForXpPoints(final int multiplier) {
        PointsDialog pointsDialog = new PointsDialog(new PointsDialog.OnPointsListener() {
            @Override
            public void onPointsEntered(int points) {
                try {
                    getCharacter().addXp(multiplier * points);
                    updateCharacterXp();
                    //Notify the character data has changed
                    notifyCharacterChange();
                } catch (InvalidParameterException ex) {
                    Toast.makeText(getContext(), R.string.xp_cannot_be_negative, Toast.LENGTH_SHORT).show();
                }
            }
        });
        pointsDialog.show(this.getActivity(), R.string.experience);
    }

    private void askForHpPoints(final int multiplier) {
        PointsDialog pointsDialog = new PointsDialog(new PointsDialog.OnPointsListener() {
            @Override
            public void onPointsEntered(int points) {
                getCharacter().addBaseHp(multiplier * points);
                updateCharacterHp();
                //Notify the character data has changed
                notifyCharacterChange();
            }
        });
        pointsDialog.show(this.getActivity(), R.string.hit_points);
    }

    private void askForCurrentHpPoints(final boolean isDamage) {
        PointsDialog pointsDialog = new PointsDialog(new PointsDialog.OnPointsListener() {
            @Override
            public void onPointsEntered(int points) {
                if (isDamage) {
                    getCharacter().addDamage(points);
                } else {
                    getCharacter().heal(points);
                }
                updateCharacterHp();
                //Notify the character data has changed
                notifyCharacterChange();
            }
        });
        pointsDialog.show(this.getActivity(), R.string.heal);
    }
}
