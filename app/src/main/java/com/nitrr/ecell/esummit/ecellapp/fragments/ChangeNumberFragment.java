package com.nitrr.ecell.esummit.ecellapp.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.nitrr.ecell.esummit.ecellapp.R;
import com.nitrr.ecell.esummit.ecellapp.activities.HomeActivity;
import com.nitrr.ecell.esummit.ecellapp.misc.SharedPref;
import com.nitrr.ecell.esummit.ecellapp.misc.Utils;
import com.nitrr.ecell.esummit.ecellapp.models.GenericMessage;
import com.nitrr.ecell.esummit.ecellapp.models.verifyNumber.ChangeNumber;
import com.nitrr.ecell.esummit.ecellapp.restapi.APIServices;
import com.nitrr.ecell.esummit.ecellapp.restapi.AppClient;

import java.io.IOException;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangeNumberFragment extends Fragment {

    private EditText number;
    private TextInputLayout numberLayout;

    private DialogInterface.OnClickListener successfulYes = (dialogInterface, i) -> {
        dialogInterface.dismiss();
        Objects.requireNonNull(getActivity()).getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.home_parent_layout, new OTPDialogFragment())
                .addToBackStack(null)
                .commit();
    };
    private DialogInterface.OnClickListener successfulNo = (dialogInterface, i) -> {
        dialogInterface.dismiss();
        Objects.requireNonNull(getActivity()).getSupportFragmentManager()
                .beginTransaction()
                .remove(this)
                .addToBackStack(null)
                .commit();
    };

    public ChangeNumberFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_change_number, container, false);
        number = v.findViewById(R.id.new_number);
        numberLayout = v.findViewById(R.id.new_number_layout);
        MaterialButton changeNumber = v.findViewById(R.id.change_num_button);
        changeNumber.getBackground().setColorFilter(this.getResources()
                .getColor(R.color.forgot_button), PorterDuff.Mode.MULTIPLY);
        changeNumber.setOnClickListener(view -> {
            if(checkPhone(number, numberLayout))
                apiCall();
        });
        return v;
    }

    private void apiCall() {
        String num = number.getText().toString();
        ChangeNumber object = new ChangeNumber(num);
        SharedPref pref = new SharedPref();

        AlertDialog dialog = Utils.showProgressBar(getContext(), "Changing Number...");

        Call<GenericMessage> call = AppClient.getInstance().createService(APIServices.class)
                .changeNumber(getResources().getString(R.string.app_access_token),
                        pref.getAccessToken(getContext()), object);

        call.enqueue(new Callback<GenericMessage>() {
            @Override
            public void onResponse(@NonNull Call<GenericMessage> call, @NonNull Response<GenericMessage> response) {
                dialog.dismiss();
                if (getContext() != null) {
                    if(response.isSuccessful()) {
                        if(response.body() != null) {
                            Utils.showLongToast(getContext(), "Mobile Number Changed Successfully");
                            pref.setMobileVerified(getActivity(), false);
                            pref.setGreeted(getActivity(), false);
                            Intent i = new Intent(getActivity(), HomeActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                        } else {
                            Log.e("ChangeNumber", "Response Successful, Response Body NULL");
                        }
                    } else {
                        if(response.errorBody() != null) {
                            try {
                                Utils.showLongToast(getContext(), response.errorBody().string().split("\"")[2]);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.e("ChangeNumber", "Response Unsuccessful, Response Error Body NULL");
                        }
                    }
                } else {
                    Utils.showLongToast(getContext(), "Something went wrong!");
                }

            }

            @Override
            public void onFailure(@NonNull Call<GenericMessage> call, @NonNull Throwable t) {
                dialog.dismiss();

            }
        });
    }

    private boolean checkPhone(EditText editText, TextInputLayout layout) {
        String phoneNo = editText.getText().toString();
        if(!isEmpty(editText, layout))
            return false;
        if (phoneNo.length() == 10) {
            if (phoneNo.charAt(0) == '6' || phoneNo.charAt(0) == '7' || phoneNo.charAt(0) == '8' || phoneNo.charAt(0) == '9')
                return true;
            else
                layout.setError("Invalid Number!");
        }
        else
            layout.setError("Enter a 10 digit number");
        return false;
    }

    private boolean isEmpty(EditText editText, TextInputLayout layout){
        if(!TextUtils.isEmpty(editText.getText()))
            return true;
        else
            layout.setError("Field Required!");
        return false;
    }
}

//                            Utils.showLongToast(getContext(), response.body().getMessage());
//                                    Objects.requireNonNull(getActivity()).getSupportFragmentManager()
//                                    .beginTransaction()
//                                    .remove(ChangeNumberFragment.this)
//                                    .commit();