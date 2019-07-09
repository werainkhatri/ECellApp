package com.nitrr.ecell.esummit.ecellapp.fragments;


import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nitrr.ecell.esummit.ecellapp.R;
import com.nitrr.ecell.esummit.ecellapp.adapters.SponsorsRecyclerViewAdapter;
import com.nitrr.ecell.esummit.ecellapp.misc.Utils;
import com.nitrr.ecell.esummit.ecellapp.models.Sponsors.SponsRVData;
import com.nitrr.ecell.esummit.ecellapp.models.Sponsors.SponsorsModel;
import com.nitrr.ecell.esummit.ecellapp.restapi.APIServices;
import com.nitrr.ecell.esummit.ecellapp.restapi.AppClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Sponsors extends Fragment {

    private RecyclerView recycler;
    private SponsorsRecyclerViewAdapter adapter;
    private SponsorsModel model;
    private List<SponsRVData> list = new ArrayList<SponsRVData>();
    private static String type;

    private DialogInterface.OnClickListener refreshlistener= new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            APICall();
        }
    };
    private DialogInterface.OnClickListener cancellistener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            getActivity().finish();
        }
    };



    public Sponsors() {
        // Required empty public constructor
    }

    public static Sponsors newInstance(String title, int position) {

        Bundle args = new Bundle();

        Sponsors fragment = new Sponsors();
        args.putInt("Position",position);
        type = title;
        args.putString("title",title);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public  View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        list.clear();
        View view=inflater.inflate(R.layout.fragment_sponsors,container,false);
        recycler = view.findViewById(R.id.spons_recycler);
        APICall();
        return view;
    }

    void APICall()
    {
        APIServices service = AppClient.getRetrofitInstance();
        Call<SponsorsModel> call =service.getSponsData();
        call.enqueue(new Callback<SponsorsModel>() {
            @Override
            public void onResponse(Call<SponsorsModel> call, Response<SponsorsModel> response) {
                if(response.isSuccessful()){
                model=response.body();
                    if(model!=null){
                        list = model.getList();
                        setRecyclerView();
                    }
                    else{
                        Log.e("response list empty","response is empty and is: "+response.toString());
                    }
                }
                else{
                    Log.e("response failure","resoponse is "+ response.toString());
                    Utils.showDialog(getContext(),null,false,null,getContext().getString(R.string.wasntabletoload),"Retry",refreshlistener,"Cancel",cancellistener);
                }
            }
            @Override
            public void onFailure(Call<SponsorsModel> call, Throwable t) {
                if(!Utils.isNetworkAvailable(getContext()))
                    Utils.showDialog(getContext(),null,false,null,getContext().getString(R.string.wasntabletoload),"Retry",refreshlistener,"Cancel",cancellistener);
                else
                {
                    Utils.showToast(getActivity(),"Something went wrong.");
                    Log.e("onfailure","throable is "+t.toString());
                    getActivity().finish();
                }
            }
        });}

//    private void setList() {
//        SponsRVData data = new SponsRVData("Resonance",type,R.drawable.spons_cardbg_1,null);
//        list.add(data);
//        data = new SponsRVData("Saavan",type,R.drawable.spons_cardbg_2,null);
//        list.add(data);
//        data = new SponsRVData("Nesley",type,R.drawable.spons_cardbg_3,null);
//        list.add(data);
//    }

    void setRecyclerView(){
        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);
        recycler.setLayoutManager(linearLayoutManager);
        adapter =new SponsorsRecyclerViewAdapter(getContext(),list);
        recycler.setAdapter(adapter);
    }

}
