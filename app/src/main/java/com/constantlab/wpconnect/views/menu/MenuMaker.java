package com.constantlab.wpconnect.views.menu;

import android.content.Context;
import android.content.res.Resources;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

import com.constantlab.wpconnect.R;
import com.constantlab.wpconnect.views.fragments.FavoritesTabsFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Constant-Lab LLP on 07-09-2017.
 */

public class MenuMaker {
    protected MenuItemCallback callback;
    private List<MenuItem> menuItemList;
    private Context context;
    private NavItem firstNavItem;

    public MenuMaker(Context context, MenuItemCallback callback) {
        this.context = context;
        this.callback = callback;
        menuItemList = new ArrayList<>();
    }

    public NavItem getFirstNavItem() {
        return firstNavItem;
    }

    public void setFirstNavItem(NavItem firstNavItem) {
        this.firstNavItem = firstNavItem;
    }

    public List<MenuItem> getMenuItemList() {
        return menuItemList;
    }

    public void generateMenu(Menu menu, List<Section> sectionList, boolean showSectionNameIfSingle) {
        if (sectionList.size() == 1) {
            Section section = sectionList.get(0);
            if (!showSectionNameIfSingle) {
                for (final NavItem navItem : section.getNavItemList()) {
                    MenuItem item = menu.add(R.id.main_group, Menu.NONE, Menu.NONE, navItem.getTitle()).setCheckable(true).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (callback != null)
                                callback.menuItemClicked(navItem, item);
                            return true;
                        }
                    });
                    item.setIcon(getDrawableByName(navItem.getDrawableName()));
                    menuItemList.add(item);
                }

            } else {
                SubMenu subMenu = menu.addSubMenu(R.id.main_group, Menu.NONE, Menu.NONE, section.getSectionName());
                subMenu.setGroupCheckable(0, true, true);
                for (final NavItem navItem : section.getNavItemList()) {
                    MenuItem item = subMenu.add(navItem.getTitle()).setCheckable(true).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (callback != null)
                                callback.menuItemClicked(navItem, item);
                            return true;
                        }
                    });
                    item.setIcon(getDrawableByName(navItem.getDrawableName()));
                    menuItemList.add(item);
                }
            }
        } else if (sectionList.size() > 1) {
            for (Section section : sectionList) {
                SubMenu subMenu = menu.addSubMenu(R.id.main_group, Menu.NONE, Menu.NONE, section.getSectionName());
                subMenu.setGroupCheckable(0, true, true);
                for (final NavItem navItem : section.getNavItemList()) {
                    MenuItem item = subMenu.add(navItem.getTitle()).setCheckable(true).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (callback != null)
                                callback.menuItemClicked(navItem, item);
                            return true;
                        }
                    });
                    item.setIcon(getDrawableByName(navItem.getDrawableName()));
                    menuItemList.add(item);
                }
            }
        }

        /*
            This item is fixed item for the menu. You can alter it here if you want.
         */

        MenuItem favorites = menu.add(R.id.main_group, MenuExtras.FAVORITES_ITEM_ID, Menu.NONE, R.string.favorites).setCheckable(true).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (callback != null) {
                    NavItem navItem = new NavItem(context.getString(R.string.favorites), FavoritesTabsFragment.class, null);
                    callback.menuItemClicked(navItem, item);
                }
                return true;
            }
        });
        favorites.setIcon(R.drawable.ic_favorite_filled);
        menuItemList.add(favorites);
        MenuItem extra = menu.add(R.id.main_group, MenuExtras.LOGOUT_ITEM_ID, Menu.NONE, R.string.logout).setCheckable(true).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (callback != null)
                    callback.menuItemClicked(null, item);
                return false;
            }
        });
        extra.setIcon(R.drawable.ic_exit_to_app);
        menuItemList.add(extra);
    }


    private int getDrawableByName(String name) {
        Resources resources = context.getResources();
        return resources.getIdentifier(name, "drawable",
                context.getPackageName());
    }


}
