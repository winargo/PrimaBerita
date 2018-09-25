package prima.optimasi.indonesia.primaberita.views.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import prima.optimasi.indonesia.primaberita.R;
import prima.optimasi.indonesia.primaberita.core.config.Config;
import prima.optimasi.indonesia.primaberita.core.data.Constants;
import prima.optimasi.indonesia.primaberita.core.data.DataManager;
import prima.optimasi.indonesia.primaberita.core.data.model.User;
import prima.optimasi.indonesia.primaberita.core.ui.contracts.LoginContract;
import prima.optimasi.indonesia.primaberita.core.ui.presenters.LoginPresenter;

public class LoginActivity extends BaseActivity implements LoginContract.LoginView {

    TextView forgotPassword, skip;
    Button login, createAccount;
    AppCompatEditText username, password;
    ProgressBar progressBar;
    private LoginPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mPresenter = new LoginPresenter(DataManager.getInstance(this));

        FirebaseMessaging.getInstance().subscribeToTopic("News");
        if(getSharedPreferences("primaberita",MODE_PRIVATE).getString("privacy","0").equals("0")){
            AlertDialog.Builder build = new AlertDialog.Builder(this).setTitle("Terms And Condition");

            View v = LayoutInflater.from(this).inflate(R.layout.layout_privacy,null);
            CheckBox cb = v.findViewById(R.id.privacychecked);

            LinearLayout l = v.findViewById(R.id.layoutcheckprivacy);

            l.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(cb.isChecked()){
                        cb.setChecked(false);
                    }
                    else{
                        cb.setChecked(true);
                    }
                }
            });

            build.setView(v);


            build.setCancelable(false);

            build.setPositiveButton("Proceed",null);
            build.setNegativeButton("No,Thanks",null);

            AlertDialog builds = build.create();
            builds.setOnShowListener(new DialogInterface.OnShowListener() {

                @Override
                public void onShow(DialogInterface dialogInterface) {

                    Button button = ((AlertDialog) builds).getButton(AlertDialog.BUTTON_POSITIVE);
                    button.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            if(cb.isChecked()){
                                getSharedPreferences("primaberita",MODE_PRIVATE).edit().putString("privacy","1").apply();
                                builds.dismiss();
                            }
                            else {
                                Toast.makeText(LoginActivity.this, "You must agree to Term and Condition", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    Button button1 = ((AlertDialog) builds).getButton(AlertDialog.BUTTON_NEGATIVE);
                    button1.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            builds.dismiss();
                            LoginActivity.this.finish();
                        }
                    });
                }
            });

            builds.show();

        }


        initView();
        mPresenter.attachView(this);
        setupClicks();
    }

    private void setupClicks() {
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, WebActivity.class);
                intent.putExtra(WebActivity.URL, Config.REGISTER_URL);
                intent.putExtra(WebActivity.CSS, "custom.css");
                intent.putExtra(WebActivity.TITLE, "Register");
                intent.putExtra(WebActivity.MESSAGE, "Email Terkirim , Cek Email Anda ");
                startActivity(intent);
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!username.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
                    mPresenter.onRequestLoginWithWordpress(username.getText().toString().trim(), password.getText().toString().trim());
                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);

                    try {
                        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                    }catch (Exception e){
                        Log.e("Input token error",e.getMessage().toString());
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Username or Password should not be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });


        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, WebActivity.class);
                intent.putExtra(WebActivity.URL, Config.LOST_PASSWORD_URL);
                intent.putExtra(WebActivity.CSS, "custom.css");
                intent.putExtra(WebActivity.TITLE, "Forgot Password");
                intent.putExtra(WebActivity.MESSAGE, "Password Sent to your email. Check Email");
                startActivity(intent);
            }
        });


        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initView() {
        login = findViewById(R.id.login);
        createAccount = findViewById(R.id.create_account);
        username = findViewById(R.id.username);
        skip = findViewById(R.id.skip);
        forgotPassword = findViewById(R.id.forgot_password);
        password = findViewById(R.id.password);
        progressBar = findViewById(R.id.progress_bar);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        username.setEnabled(false);
        password.setEnabled(false);
        login.setEnabled(false);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.INVISIBLE);
        login.setEnabled(true);
    }

    @Override
    public void showError(String errorMessage) {
        username.setEnabled(true);
        password.setEnabled(true);

        login.setEnabled(true);
        if (errorMessage.equals("Default 403 Forbidden")) {
            Toast.makeText(getApplicationContext(), R.string.username_password_not_correct, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onWordpressLoginResponse(boolean success, User user, String token) {
        if (success) {
            applicationMain.user = user;
            Gson gson = new Gson();
            String json = gson.toJson(user);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            sharedPreferences.edit().putString(Constants.KEY_FOR_TOKEN, token).apply();
            sharedPreferences.edit().putString(Constants.KEY_FOR_USER, json).apply();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        } else {
            username.setEnabled(true);
            password.setEnabled(true);
            login.setEnabled(true);
            Toast.makeText(getApplicationContext(), R.string.username_password_not_correct, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }
}
