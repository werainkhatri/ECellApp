package com.nitrr.ecell.esummit.ecellapp.fragments.forgotPassword;

import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.nitrr.ecell.esummit.ecellapp.R;

import java.util.Objects;

public class ChangePasswordFragment extends Fragment {

    private MaterialButton verify;

    public ChangePasswordFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_change_pass, container, false);
        verify = v.findViewById(R.id.forgot_submit);
        verify.getBackground().setColorFilter(this.getResources().getColor(R.color.forgot_button), PorterDuff.Mode.MULTIPLY);
        ((TextView)v.findViewById(R.id.forgot_title)).setTypeface(Typeface.createFromAsset(Objects.requireNonNull(getContext()).getAssets(), "fonts/Oswald-Regular.ttf"));
        return v;
    }
}
