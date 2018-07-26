package prima.optimasi.indonesia.primaberita.core.ui.presenters;

import prima.optimasi.indonesia.primaberita.core.data.DataManager;
import prima.optimasi.indonesia.primaberita.core.data.model.User;
import prima.optimasi.indonesia.primaberita.core.data.network.RemoteCallback;
import prima.optimasi.indonesia.primaberita.core.ui.base.BasePresenter;
import prima.optimasi.indonesia.primaberita.core.ui.contracts.LoginContract;

import org.json.JSONObject;

import okhttp3.ResponseBody;

/**
 * Created by Constant-Lab LLP on 18-04-2017.
 */

public class LoginPresenter extends BasePresenter<LoginContract.LoginView> implements LoginContract.ViewActions {

    private DataManager mDataManager;

    public LoginPresenter(DataManager mDataManager) {
        this.mDataManager = mDataManager;
    }

    @Override
    public void onRequestLoginWithWordpress(String username, String password) {
        requestWordpressLogin(username, password);
    }

    private void requestWordpressLogin(String username, String password) {
        if (!isViewAttached()) return;
        mView.showProgress();
        mDataManager.loginWithWordress(username, password, new RemoteCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) {
                try {
                    JSONObject object = new JSONObject(responseBody.string());
                    if (object.has("code")) {
                        String code = object.getString("code");
                        if (code.equals("jwt_auth_failed")) {
                            mView.onWordpressLoginResponse(false, null, null);
                        } else {
                            mView.showError("Cannot Login");
                        }
                    } else {
                        String token = object.getString("token");
                        User user = new User();
                        user.setEmail(object.getString("user_email"));
                        user.setUsername(object.getString("user_display_name"));
                        mView.onWordpressLoginResponse(true, user, token);
                    }
                } catch (Exception e) {
                    if (!isViewAttached()) return;
                    mView.hideProgress();
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
