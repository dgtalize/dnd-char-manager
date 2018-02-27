package com.dgtalize.dndcharmanager.ui.adapter;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.dgtalize.dndcharmanager.R;
import com.dgtalize.dndcharmanager.model.Character;
import com.dgtalize.dndcharmanager.model.Spell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Expandable list adapter for Spell Levels
 */

public class SpellLevelListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private Character character;
    private List<LevelEntry> levelsList; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<Spell>> levelSpellsList;

    public SpellLevelListAdapter(Context context, List<LevelEntry> levelsList,
                                 HashMap<String, List<Spell>> levelSpellsList,
                                 Character character) {
        this.context = context;
        this.levelsList = levelsList;
        this.levelSpellsList = levelSpellsList;
        this.character = character;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        LevelEntry levelEntry = this.levelsList.get(groupPosition);
        return this.levelSpellsList.get(String.valueOf(levelEntry.getNumber())).get(childPosititon);
    }

    public void addChild(int level, Spell child) {
        String groupKey = String.valueOf(level);
        this.addChild(groupKey, child);
    }

    public void addChild(String groupKey, Spell child) {
        this.levelSpellsList.get(groupKey).add(child);
        this.notifyDataSetChanged();
    }

    /**
     * Searches for a spell by key, and then replace it with a new one
     *
     * @param keyToRemove Key (UID) of the spell to replace
     * @param newChild    New spell to replace with
     */
    public void replaceChild(String keyToRemove, Spell newChild) {
        //Search through all the levels for the spell
        for (Map.Entry<String, List<Spell>> spellsEntry : this.levelSpellsList.entrySet()) {
            List<Spell> spells = spellsEntry.getValue();
            //remove the old spell
            Spell spellToRemove = null;
            for (Spell spell : spells) {
                if (spell.getUid().equals(keyToRemove)) {
                    spellToRemove = spell;
                    break;
                }
            }
            if (spellToRemove != null) {
                //remove
                spells.remove(spellToRemove);
                //add the new one
                spells.add(newChild);
                return;
            }
        }
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final Spell spell = (Spell) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.spell_levels_item, null);
        }

        TextView spellNameTextView = (TextView) convertView.findViewById(R.id.spellNameTextView);
        spellNameTextView.setText(spell.getName());
        spellNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSpellView(view.getContext(), spell);
            }
        });
        return convertView;
    }


    private void openSpellView(Context context, Spell item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(item.getName())
                .setMessage(item.getDescription())
                .show();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        LevelEntry levelEntry = this.levelsList.get(groupPosition);
        return this.levelSpellsList.get(String.valueOf(levelEntry.getNumber())).size();
    }

    public void addGroup(LevelEntry levelEntry) {
        this.levelsList.add(levelEntry);
        levelSpellsList.put(String.valueOf(levelEntry.getNumber()), new ArrayList<Spell>());
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.levelsList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.levelsList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        LevelEntry levelEntry = (LevelEntry) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.spell_levels_group, null);
        }

        TextView spellLevelHeaderTextView = (TextView) convertView.findViewById(R.id.spellLevelHeaderTextView);
        spellLevelHeaderTextView.setText(levelEntry.getName());

        TextView spellLevelEnabledTextView = (TextView) convertView.findViewById(R.id.spellLevelEnabledTextView);
        Integer spellsCanCast = character.spellsAvailableLevel(levelEntry.getNumber());
        spellLevelEnabledTextView.setText(spellsCanCast == null ? "-" : String.valueOf(spellsCanCast));

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
