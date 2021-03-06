package com.nitrr.ecell.esummit.ecellapp.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.nitrr.ecell.esummit.ecellapp.BuildConfig;
import com.nitrr.ecell.esummit.ecellapp.R;
import com.nitrr.ecell.esummit.ecellapp.fragments.BQuizQnAFragment;
import com.nitrr.ecell.esummit.ecellapp.fragments.LeaderBoardFragment;
import com.nitrr.ecell.esummit.ecellapp.misc.SharedPref;
import com.nitrr.ecell.esummit.ecellapp.misc.Utils;
import com.nitrr.ecell.esummit.ecellapp.models.bquiz.BquizLiveCheckResponse;
import com.nitrr.ecell.esummit.ecellapp.restapi.APIServices;
import com.nitrr.ecell.esummit.ecellapp.restapi.AppClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BQuizActivity extends BaseActivity {

    private ProgressBar progressBar;
    private TextView proceedTextView,leaderBoardTextview;
    private BQuizQnAFragment fragment;
    private LeaderBoardFragment leaderBoardFragment;
    SharedPref pref;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_bquiz;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImageView bQuizBG = findViewById(R.id.bquiz_bg);
        Glide.with(this)
                .load(R.drawable.bquizbg)
                .apply(new RequestOptions().centerCrop())
                .into(bQuizBG);

        pref = new SharedPref();
        proceedTextView = findViewById(R.id.bquiz_proceed);
        progressBar = findViewById(R.id.bquiz_progess_bar);
        leaderBoardTextview = findViewById(R.id.bq_leader_text);

        proceedTextView.setOnClickListener(v -> {
            proceedTextView.setText("");
            progressBar.setVisibility(View.VISIBLE);

            apiCall();
        });

        leaderBoardTextview.setOnClickListener(v -> {

            leaderBoardFragment = new LeaderBoardFragment();
           leaderBoardFragment.show(getSupportFragmentManager(), "LeaderBoard");


        });
    }


    private void apiCall() {
        APIServices services = AppClient.getInstance().createService(APIServices.class, BuildConfig.BQUIZ_URL);
        Call<BquizLiveCheckResponse> responseCall = services.isLiveRequest(new SharedPref().getAccessToken(BQuizActivity.this));

        responseCall.enqueue(new Callback<BquizLiveCheckResponse>() {
            @Override
            public void onResponse(Call<BquizLiveCheckResponse> call, Response<BquizLiveCheckResponse> response) {
                if (response.body() != null && response.isSuccessful()) {

                    if (response.body().live) {
                        fragment = new BQuizQnAFragment();
                        fragment.show(getSupportFragmentManager(), "BQnA");

                    } else {
                        Utils.showLongToast(BQuizActivity.this, "Bquiz isn't live right now.");
                    }

                    progressBar.setVisibility(View.GONE);
                    proceedTextView.setText(getResources().getText(R.string.proceed));
                }
            }

            @Override
            public void onFailure(Call<BquizLiveCheckResponse> call, Throwable t) {
                Log.e("onFailure", String.valueOf(t));

            }
        });
    }

//    @Override
//    public void onBackPressed() {
//        if(pref.getIsBquizCancelable(this)) {
//            Utils.showLongToast(this, "Please wait for ruuning time to finish to quit Bquiz.");
//        }
//        else
//            super.onBackPressed();
//    }

    @Override
    protected void onDestroy() {
        HomeActivity.setSelected(false);
        super.onDestroy();
    }
}
