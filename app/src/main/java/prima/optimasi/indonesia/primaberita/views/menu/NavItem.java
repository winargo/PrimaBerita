package prima.optimasi.indonesia.primaberita.views.menu;

import android.support.v4.app.Fragment;

import java.io.Serializable;

public class NavItem implements Serializable {

    private String drawableName;
    private String title;
    private String[] mData;
    private Class<? extends Fragment> mFragment;
    private String provider;

    public NavItem(String text, Class<? extends Fragment> fragment, String[] data) {
        title = text;
        mFragment = fragment;
        mData = data;
    }

    public NavItem() {
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getDrawableName() {
        return drawableName;
    }

    public void setDrawableName(String drawableName) {
        this.drawableName = drawableName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String[] getmData() {
        return mData;
    }

    public void setmData(String[] mData) {
        this.mData = mData;
    }

    public Class<? extends Fragment> getmFragment() {
        return mFragment;
    }

    public void setmFragment(Class<? extends Fragment> mFragment) {
        this.mFragment = mFragment;
    }


}
