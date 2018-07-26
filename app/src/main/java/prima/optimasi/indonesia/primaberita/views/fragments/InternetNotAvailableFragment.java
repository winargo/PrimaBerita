package prima.optimasi.indonesia.primaberita.views.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import prima.optimasi.indonesia.primaberita.R;

/**
 * Created by Constant-Lab LLP on 26-09-2017.
 */

public class InternetNotAvailableFragment extends BaseFragment {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_internet_not_available, container, false);
    }
}
