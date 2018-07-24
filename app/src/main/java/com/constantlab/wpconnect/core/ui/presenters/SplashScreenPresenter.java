package com.constantlab.wpconnect.core.ui.presenters;

import com.constantlab.wpconnect.core.data.DataManager;
import com.constantlab.wpconnect.core.data.network.RemoteCallback;
import com.constantlab.wpconnect.core.ui.base.BasePresenter;
import com.constantlab.wpconnect.core.ui.contracts.SplashScreenContract;

import org.json.JSONObject;

import okhttp3.ResponseBody;

/**
 * Created by Constant-Lab LLP on 17-04-2017.
 */

public class SplashScreenPresenter extends BasePresenter<SplashScreenContract.SplashScreenView> implements SplashScreenContract.ViewActions {
    private DataManager mDataManager;


    public SplashScreenPresenter(DataManager mDataManager) {
        this.mDataManager = mDataManager;
    }


    @Override
    public void onValidateUser(String token) {
        requestValidation(token);
    }

    private void requestValidation(String token) {
        if (!isViewAttached()) return;
        mView.showProgress();
        mDataManager.validateUser(token, new RemoteCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) {
                if (!isViewAttached()) return;
                mView.hideProgress();
                try {
                    JSONObject object = new JSONObject(responseBody.string());
                    if (object.has("code")) {
                        String code = object.getString("code");
                        if (code.equals("jwt_auth_valid_token")) {
                            mView.onValidationResponse(true);
                        } else if (code.equals("jwt_auth_invalid_token")) {
                            mView.onValidationResponse(false);
                        }
                    }
                } catch (Exception e) {
                    mView.showError(e.getMessage());
                }
            }

            @Override
            public void onUnauthorized() {
                if (!isViewAttached()) return;
                mView.hideProgress();
                mView.showError("Unauthorized");
            }

            @Override
            public void onFailed(Throwable throwable) {
                if (!isViewAttached()) return;
                mView.hideProgress();
                mView.showError(throwable.getMessage());
            }
        });
    }
}
