package prima.optimasi.indonesia.primaberita.views.menu;

import java.util.List;

/**
 * Created by Constant-Lab LLP on 07-09-2017.
 */

public class Section {
    private String sectionName;
    private List<NavItem> navItemList;

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public List<NavItem> getNavItemList() {
        return navItemList;
    }

    public void setNavItemList(List<NavItem> navItemList) {
        this.navItemList = navItemList;
    }
}
