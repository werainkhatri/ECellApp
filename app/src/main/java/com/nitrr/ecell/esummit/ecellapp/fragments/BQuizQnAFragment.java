package com.nitrr.ecell.esummit.ecellapp.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nitrr.ecell.esummit.ecellapp.R;
import com.nitrr.ecell.esummit.ecellapp.misc.Utils;
import com.nitrr.ecell.esummit.ecellapp.models.bquiz.BquizAnswerModel;
import com.nitrr.ecell.esummit.ecellapp.rxsocket.WebSocket;

import org.json.JSONObject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class BQuizQnAFragment extends DialogFragment {

    private WebSocket webSocket;
    private TextView response;
    private EditText resText;
    private Gson gson;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bquiz, container, false);

        view.findViewById(R.id.connect).setOnClickListener(v -> setUpWebSocket("wss://258c974e.ngrok.io/bquiz/live/question/"));
        // view.findViewById(R.id.send).setOnClickListener(v -> sendMessage(new BquizAnswerModel(resText.getText().toString().trim())));

        response = view.findViewById(R.id.response);
        resText = view.findViewById(R.id.res_text);

        gson = new Gson();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (webSocket != null)
            webSocket.closeConnection()
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();
    }

    @SuppressLint("CheckResult")
    private void setUpWebSocket(String URL) {
        webSocket = new WebSocket(URL);

        webSocket.onOpen()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(socketEventOpen -> Utils.showLongToast(getContext(), "B-Quiz is live."), Throwable::printStackTrace);

        webSocket.onClosed()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(socketEventClosed -> Utils.showLongToast(getContext(), "B-Quiz closed."), Throwable::printStackTrace);

        webSocket.onClosing()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(socketEventClosing -> Utils.showLongToast(getContext(), "B-Quiz is closing."), Throwable::printStackTrace);

        webSocket.onMessage()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(socketEventMessage -> {
                    Utils.showLongToast(getContext(), socketEventMessage.getMessage());
                    BquizAnswerModel model = gson.fromJson(socketEventMessage.getMessage(), BquizAnswerModel.class);
                    response.setText(model.toString());

                }, Throwable::printStackTrace);

        webSocket.onFailure()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(socketEventFailure -> Utils.showLongToast(getContext(), "Some exception occurred. Contact Technical TeamList."),
                        Throwable::printStackTrace);

        webSocket.setupConnection();
    }

//    @SuppressLint("CheckResult")
//    private void sendMessage(Object message){
//        if (webSocket != null){
//            webSocket.sendMessage(message)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(
//                            success -> Utils.showLongToast(getContext(), "Answer submitted successfully."),
//                            throwable -> Utils.showLongToast(getContext(), throwable.getMessage()));
//
//        } else
//            throw new RuntimeException("Socket not Initialized.");
//    }
}
