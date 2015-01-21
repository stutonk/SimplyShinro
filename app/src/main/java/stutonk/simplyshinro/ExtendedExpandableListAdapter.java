package stutonk.simplyshinro;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Needs thorough documentation when there's more time.
 * <p>
 * Copyright 2015 Joseph Eib
 * </p>
 * <p>
 * * Adapted from the tutorial at
 * http://www.dreamincode.net/forums/topic/270612-how-to-get-started-with-expandablelistview/
 * </p>
 * @author Joseph Eib
 * @since January 2015
 */
public class ExtendedExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<ExpandableListGroup> groups;

    public ExtendedExpandableListAdapter(Context context, ArrayList<ExpandableListGroup> groups) {
        this.context = context;
        this.groups = groups;
    }

    /*public void addChild(ExpandableListGroup group, ExpandableListChild child) {
        if (!groups.contains(group)) {
            groups.add(group);
        }
        ArrayList<ExpandableListChild> children
                = groups.get(groups.indexOf(group)).getChildren();
        children.add(child);
        groups.get(groups.indexOf(group)).setChildren(children);
    }*/


    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return groups.get(groupPosition).getChildren().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groups.get(groupPosition).getChildren().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        Object object = getChild(groupPosition, childPosition);
        if (object instanceof ExpandableListChild) {
            ExpandableListChild child = (ExpandableListChild) object;
            return child.getNumber();
        }
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ExpandableListGroup group = (ExpandableListGroup) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater
                    = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.group_item, null);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.group_text);
        textView.setText(group.getName());

        Typeface robotoThin = Typeface.createFromAsset(context.getAssets(), "Roboto-Thin.ttf");
        textView.setTypeface(robotoThin);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        ExpandableListChild child = (ExpandableListChild) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater inflater
                    = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.child_item, null);
        }

        RelativeLayout relativeLayout
                = (RelativeLayout) convertView.findViewById(R.id.child_layout);

        TextView nameText = (TextView) convertView.findViewById(R.id.child_name);
        nameText.setText(child.getName());

        TextView difficultyText = (TextView) convertView.findViewById(R.id.child_difficulty);
        difficultyText.setText("(difficulty: " + Integer.toString(child.getDifficulty()) + "%)");

        if (!child.isCompleted()) {
            relativeLayout.setBackgroundColor(child.getDifficultyColor());
            nameText.setTextColor(context.getResources().getColor(R.color.charcoal));
            difficultyText.setTextColor(context.getResources().getColor(R.color.charcoal));
        }
        else {
            relativeLayout.setBackgroundColor(context.getResources().getColor(R.color.charcoal));
            nameText.setTextColor(child.getDifficultyColor());
            difficultyText.setTextColor(child.getDifficultyColor());
        }

        Typeface robotoLight = Typeface.createFromAsset(context.getAssets(), "Roboto-Light.ttf");
        nameText.setTypeface(robotoLight);
        Typeface robotoLightItalic
                = Typeface.createFromAsset(context.getAssets(), "Roboto-LightItalic.ttf");
        difficultyText.setTypeface(robotoLightItalic);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
