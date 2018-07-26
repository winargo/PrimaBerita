package prima.optimasi.indonesia.primaberita.views.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import prima.optimasi.indonesia.primaberita.R;
import prima.optimasi.indonesia.primaberita.core.config.Config;
import prima.optimasi.indonesia.primaberita.core.data.Constants;
import prima.optimasi.indonesia.primaberita.views.activities.MainActivity;
import prima.optimasi.indonesia.primaberita.views.adapters.CustomPagerAdapter;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Constant-Lab LLP on 07-09-2016.
 */
public class FavoritesTabsFragment extends BaseFragment {


    String title;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(Constants.TAG_FOR_TITLE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites_tab, container, false);
        if (getActivity() != null) {
            ((MainActivity) getActivity()).appBarLayout.setExpanded(true, true);
            List<Fragment> fragments = new ArrayList<>();
            List<String> titles = new ArrayList<>();

            if (Config.SHOW_WORDPRESS_FAVORITES) {
                fragments.add(new FavoritesFragment());
                titles.add(getString(R.string.favorites_section_wordpress));
            }
            if (Config.SHOW_YOUTUBE_FAVORITES) {
                fragments.add(new YouTubeFavoritesFragment());
                titles.add(getString(R.string.favorites_section_youtube));
            }
            if (Config.SHOW_SOCIAL_FAVORITES) {
                fragments.add(new SocialFavoritesFragment());
                titles.add(getString(R.string.favorites_section_social));
            }
            if (fragments.size() > 0) {
                CustomPagerAdapter customPagerAdapter = new CustomPagerAdapter(getChildFragmentManager(), fragments, titles);
                ViewPager viewPager = view.findViewById(R.id.viewpager);
                viewPager.setAdapter(customPagerAdapter);
                SmartTabLayout viewPagerTab = view.findViewById(R.id.viewpagertab);
                viewPagerTab.setViewPager(viewPager);
            } else {
                view.findViewById(R.id.no_favorites_tv).setVisibility(View.VISIBLE);
                view.findViewById(R.id.viewpagertab).setVisibility(View.INVISIBLE);
                view.findViewById(R.id.viewpager).setVisibility(View.INVISIBLE);
            }
            setTitle();
        }
        return view;
    }

    private void setTitle() {

        ((MainActivity) getActivity()).getSupportActionBar().setTitle(title);
    }
}
