package com.nitrr.ecell.esummit.ecellapp.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.textfield.TextInputLayout;
import com.nitrr.ecell.esummit.ecellapp.R;
import com.nitrr.ecell.esummit.ecellapp.fragments.OTPDialogFragment;
import com.nitrr.ecell.esummit.ecellapp.fragments.forgotPassword.EmailFragment;
import com.nitrr.ecell.esummit.ecellapp.misc.Animation.LoginAnimation;
import com.nitrr.ecell.esummit.ecellapp.misc.CustomTextWatcher;
import com.nitrr.ecell.esummit.ecellapp.misc.SharedPref;
import com.nitrr.ecell.esummit.ecellapp.misc.Utils;
import com.nitrr.ecell.esummit.ecellapp.models.auth.AuthResponse;
import com.nitrr.ecell.esummit.ecellapp.models.auth.LoginDetails;
import com.nitrr.ecell.esummit.ecellapp.models.auth.RegisterDetails;
import com.nitrr.ecell.esummit.ecellapp.restapi.APIServices;
import com.nitrr.ecell.esummit.ecellapp.restapi.AppClient;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {

    private Button signIn, register;
    private TextView toSignIn, toRegister, forgotPassword;
    private EditText loginEmail, loginPassword;
    private EditText firstName, lastName, registerPassword, registerEmail, registerNumber;
    private TextInputLayout loginEmailLayout, loginPasswordLayout, registerEmailLayout,
            registerPasswordLayout, firstNameLayout, lastNameLayout, registerNumberLayout;
    private LoginAnimation loginanimation;
    private AuthResponse authResponse;
    private EmailFragment emailFragment;
    SharedPref pref;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_login;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeViews();

        loginanimation = new LoginAnimation(this);
        loginanimation.toSignInScreen();

        loginEmail.clearFocus();

        signIn.setOnClickListener((View v) -> {
            if(TextUtils.isEmpty(loginEmail.getText()))
                loginEmailLayout.setError("Required Field!");
            if (TextUtils.isEmpty(loginPassword.getText()))
                loginPasswordLayout.setError("Required Field!");
            if (loginEmailLayout.getError() == null && loginPasswordLayout.getError() == null)
                LoginApiCall();
        });

        forgotPassword.setOnClickListener(view -> {
            emailFragment = new EmailFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.login_outer_constraint, emailFragment, "verify_email")
                    .addToBackStack(null)
                    .commit();
        });

        register.setOnClickListener((View v) -> {
            firstNameLayout.setError(TextUtils.isEmpty(firstName.getText()) ? "Field Required!" : null);
            lastNameLayout.setError(TextUtils.isEmpty(lastName.getText()) ? "Field Required!" : null);
            registerEmailLayout.setError(TextUtils.isEmpty(lastName.getText()) ? "Field Required!" : null);
            registerPasswordLayout.setError((TextUtils.isEmpty(registerPassword.getText())) ? "Field Required!" : null);
            registerNumberLayout.setError(TextUtils.isEmpty(lastName.getText()) ? "Field Required!" : null);

            if (firstNameLayout.getError() == null &&
                    lastNameLayout.getError() == null &&
                    registerEmailLayout.getError() == null &&
                    registerPasswordLayout.getError() == null &&
                    registerNumberLayout.getError() == null) {

                try {
                    showAlertDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        toRegister.setOnClickListener((View v) -> loginanimation.toRegisterScreen());
        toSignIn.setOnClickListener((View v) -> loginanimation.toSignInScreen());

        if(pref.getIsVerifying(LoginActivity.this))
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.login_outer_constraint, new OTPDialogFragment())
                    .commit();
    }

    private void initializeViews() {
        pref = new SharedPref();

        toSignIn = findViewById(R.id.to_sign_in);
        toRegister = findViewById(R.id.to_register);
        forgotPassword = findViewById(R.id.forgot);

        signIn = findViewById(R.id.sign_in_button);
        register = findViewById(R.id.register_button);

        loginEmailLayout = findViewById(R.id.login_email_layout);
        loginPasswordLayout = findViewById(R.id.login_password_layout);
        firstNameLayout = findViewById(R.id.register_first_name_layout);
        lastNameLayout = findViewById(R.id.register_last_name_layout);
        registerEmailLayout = findViewById(R.id.register_email_layout);
        registerPasswordLayout = findViewById(R.id.register_password_layout);
        registerNumberLayout = findViewById(R.id.register_number_layout);

        firstName = findViewById(R.id.register_first_name);
        lastName = findViewById(R.id.register_last_name);
        registerPassword = findViewById(R.id.register_password);
        registerEmail = findViewById(R.id.register_email);
        registerNumber = findViewById(R.id.register_number);

        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);

        firstName.addTextChangedListener(new CustomTextWatcher(LoginActivity.this, firstName, firstNameLayout, CustomTextWatcher.FIRST_NAME));
        lastName.addTextChangedListener(new CustomTextWatcher(LoginActivity.this, lastName, lastNameLayout, CustomTextWatcher.LAST_NAME));
        loginEmail.addTextChangedListener(new CustomTextWatcher(LoginActivity.this, loginEmail, loginEmailLayout, CustomTextWatcher.EMAIL));
        registerNumber.addTextChangedListener(new CustomTextWatcher(LoginActivity.this, registerNumber, registerNumberLayout, CustomTextWatcher.MOBILE_NO));
        registerEmail.addTextChangedListener(new CustomTextWatcher(LoginActivity.this, registerEmail, registerEmailLayout, CustomTextWatcher.EMAIL));
        registerPassword.addTextChangedListener(new CustomTextWatcher(LoginActivity.this, registerPassword, registerPasswordLayout, CustomTextWatcher.PASSWORD));
    }

    private void LoginApiCall() {
        AlertDialog loginDialog = Utils.showProgressBar(this, "Signing In...");

        LoginDetails details = new LoginDetails(loginEmail.getText().toString().trim(), loginPassword.getText().toString());

        Call<AuthResponse> call = AppClient.getInstance().createService(APIServices.class).postLoginUser(details);

        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse> call, @NonNull Response<AuthResponse> response) {
                loginDialog.dismiss();
                try {
                    if (getApplicationContext() != null) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                AuthResponse authResponse = response.body();
                                pref.setSharedPref(LoginActivity.this, authResponse.getToken(), authResponse.getFirstName(),
                                        authResponse.getLastName(), loginEmail.getText().toString());
                                pref.setIsLoggedIn(LoginActivity.this, true);
                                Log.e("LoginActivity Login", response.body().getMessage());
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            } else
                                Utils.showLongToast(LoginActivity.this, "Couldn't log you in. Please try again.");
                        } else if(response.code() == 400){
                            Utils.showLongToast(LoginActivity.this, "Invalid Credentials!");
                        } else
                            Utils.showLongToast(LoginActivity.this, "Couldn't log you in. Please try again later.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponse> call, @NonNull Throwable t) {
                loginDialog.dismiss();
                Utils.showLongToast(LoginActivity.this, "An Error occurred while logging you in. Please try again in a while!");
            }
        });
    }

    private void RegisterApiCall() {
        AlertDialog registerDialog = Utils.showProgressBar(this, "Registering User...");

        RegisterDetails details = new RegisterDetails(firstName.getText().toString().trim(),
                lastName.getText().toString().trim(),
                registerEmail.getText().toString().trim(),
                registerPassword.getText().toString(),
                registerNumber.getText().toString().trim(),
                null, null, null);

        Call<AuthResponse> call = AppClient.getInstance().createService(APIServices.class).postRegisterUser(details);

        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse> call, @NonNull Response<AuthResponse> response) {
                registerDialog.dismiss();
                try {
                    if (getApplicationContext() != null) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                authResponse = response.body();
                                SharedPref pref = new SharedPref();
                                pref.setSharedPref(LoginActivity.this,
                                        authResponse.getToken(),
                                        details.getFirstName(),
                                        details.getLastName(),
                                        details.getEmail());
                                pref.setMobileNumber(LoginActivity.this, details.getContact());
                                pref.setIsVerifying(LoginActivity.this, true);
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.login_outer_constraint, new OTPDialogFragment(), "otp_register")
                                        .addToBackStack(null)
                                        .commit();
                            } else {
                                Utils.showLongToast(LoginActivity.this, "Sorry! Couldn't Register You. Please try again.");
                                Log.e("LoginActivity Register", "Response Successful, Response Body NULL");
                            }
                        } else if (response.code() == 400) {
                            Utils.showLongToast(getApplicationContext(), "User with this email already exists.");
                        } else
                            Utils.showLongToast(LoginActivity.this, "Sorry! Couldn't Register You. Please try again in some time.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponse> call, @NonNull Throwable t) {
                registerDialog.dismiss();
                Utils.showLongToast(getApplicationContext(), "Registration Failed! Please try again in some time...");
            }
        });
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        View view = LayoutInflater.from(LoginActivity.this).inflate(R.layout.alert_dialog_privacy_policy, null);

        builder.setView(view);

        AlertDialog dialog = builder.create();

        view.findViewById(R.id.alert_privacy_accept).setOnClickListener(v -> {
            RegisterApiCall();

            if (dialog.isShowing())
                dialog.dismiss();
        });

        view.findViewById(R.id.alert_privacy_decline).setOnClickListener(v -> {
            if (dialog.isShowing())
                dialog.dismiss();
        });

        TextView privacyText = view.findViewById(R.id.text_privacy);
        Spanned policy;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            policy = Html.fromHtml(getString(R.string.agree_terms_privacy), Html.FROM_HTML_MODE_LEGACY);
        } else {
            policy = Html.fromHtml(getString(R.string.agree_terms_privacy));
        }

        privacyText.setText(policy);
        privacyText.setMovementMethod(LinkMovementMethod.getInstance());

        dialog.setCancelable(false);

        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();
    }

    @Override
    public void onBackPressed() {
        if(pref.getIsVerifying(this)) {
            Utils.showLongToast(this, "Please complete the registration process to proceed.");
        }
        else
            super.onBackPressed();
    }
}
