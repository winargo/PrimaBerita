package prima.optimasi.indonesia.primaberita;

import android.content.Context;
import android.view.MenuItem;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import prima.optimasi.indonesia.primaberita.core.config.Config;
import prima.optimasi.indonesia.primaberita.views.menu.NavItem;

public class generator {
    public static int backhandling = 0;
    public static NavItem navdata =null;
    public static MenuItem menudata=null;

    public static InterstitialAd mInterstitialAd = null;


    public static void createad(Context context){
        if(mInterstitialAd!=null){
            mInterstitialAd.loadAd(new AdRequest.Builder().build());

            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    // Code to be executed when an ad finishes loading.
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    // Code to be executed when an ad request fails.
                }

                @Override
                public void onAdOpened() {
                    // Code to be executed when the ad is displayed.
                }

                @Override
                public void onAdLeftApplication() {
                    // Code to be executed when the user has left the app.
                }

                @Override
                public void onAdClosed() {
                    // Code to be executed when when the interstitial ad is closed.
                }
            });
        }
        else {
            mInterstitialAd = new InterstitialAd(context);
            mInterstitialAd.setAdUnitId(Config.ADMOB_AD_UNIT_ID);
            createad(context);
        }
    }
}
