package stutonk.simplyshinro;

import java.util.ArrayList;

/**
 * Created by Joseph Eib on 1/19/15.
 * <p>
 * Adapted from the tutorial at
 * http://www.dreamincode.net/forums/topic/270612-how-to-get-started-with-expandablelistview/
 */
public class ExpandableListGroup {
    private String name;
    private int resourceId;
    private ArrayList<ExpandableListChild> children;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<ExpandableListChild> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<ExpandableListChild> children) {
        this.children = children;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }
}
