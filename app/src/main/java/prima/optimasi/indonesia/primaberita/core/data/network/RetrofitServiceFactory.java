package prima.optimasi.indonesia.primaberita.core.data.network;

import android.content.Context;
import android.support.annotation.NonNull;

import prima.optimasi.indonesia.primaberita.BuildConfig;
import prima.optimasi.indonesia.primaberita.core.config.Config;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

/**
 * Created by Constant-Lab LLP on 17-04-2017.
 */

public class RetrofitServiceFactory {
    private static final int HTTP_READ_TIMEOUT = 10000;
    private static final int HTTP_CONNECT_TIMEOUT = 6000;

    public static WpConnectService makePartysanService(@NonNull Context context) {
        return makePartysanService(makeOkHttpClient(context));
    }

    private static WpConnectService makePartysanService(OkHttpClient okHttpClient) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.PRIMARY_BASE_URL)
                .client(okHttpClient)
                .build();

        return retrofit.create(WpConnectService.class);
    }

    private static OkHttpClient makeOkHttpClient(Context context) {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient().newBuilder();
        httpClientBuilder.connectTimeout(HTTP_CONNECT_TIMEOUT, TimeUnit.SECONDS);
        httpClientBuilder.readTimeout(HTTP_READ_TIMEOUT, TimeUnit.SECONDS);
        if (BuildConfig.DEBUG)
            httpClientBuilder.addInterceptor(makeLoggingInterceptor());


        return httpClientBuilder.build();
    }


    private static HttpLoggingInterceptor makeLoggingInterceptor() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY
                : HttpLoggingInterceptor.Level.NONE);
        return logging;
    }
}
