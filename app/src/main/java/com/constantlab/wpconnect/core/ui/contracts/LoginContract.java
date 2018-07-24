package com.constantlab.wpconnect.core.ui.contracts;

import com.constantlab.wpconnect.core.data.model.User;
import com.constantlab.wpconnect.core.ui.base.RemoteView;

/**
 * Created by Constant-Lab LLP on 18-04-2017.
 */

public interface LoginContract {
    interface ViewActions {
        void onRequestLoginWithWordpress(String username, String password);
    }

    interface LoginView extends RemoteView {

        void onWordpressLoginResponse(boolean success, User user, String token);
    }
}
