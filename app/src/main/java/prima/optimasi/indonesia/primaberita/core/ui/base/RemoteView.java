package prima.optimasi.indonesia.primaberita.core.ui.base;


/**
 * Created by Constant-Lab LLP on 17-04-2017.
 */

public interface RemoteView {

    void showProgress();

    void hideProgress();

    void showError(String errorMessage);
}
