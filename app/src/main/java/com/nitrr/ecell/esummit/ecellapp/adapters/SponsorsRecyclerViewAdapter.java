package com.nitrr.ecell.esummit.ecellapp.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.nitrr.ecell.esummit.ecellapp.R;
import com.nitrr.ecell.esummit.ecellapp.misc.Utils;
import com.nitrr.ecell.esummit.ecellapp.models.sponsors.SponsRVData;

import java.util.List;

import jp.wasabeef.glide.transformations.CropSquareTransformation;


public class SponsorsRecyclerViewAdapter extends RecyclerView.Adapter<SponsorsRecyclerViewAdapter.MyViewHolder>{
    private List<SponsRVData> list;
    private Context context;
    private int pos;

    public SponsorsRecyclerViewAdapter(Context context,List<SponsRVData> list,int i) {
        this.context = context;
        this.list = list;
        pos = i;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_card_sponsors,viewGroup,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        SponsRVData data = list.get(i);
        Glide.with(context).load(data.getImg()).transform(new CircleCrop()).into(holder.loadingimg);
        switch(pos){
            case 0:{
                holder.card.setBackgroundResource(R.drawable.spons_cardbg_5);
                break;
            }
            case 1:{
                holder.card.setBackgroundResource(R.drawable.spons_cardbg_4);
                break;
            }
            case 2:{
                holder.card.setBackgroundResource(R.drawable.spons_cardbg_3);
                break;
            }
            case 3:{
                holder.card.setBackgroundResource(R.drawable.spons_cardbg_1);
                break;
            }
            case 4:{
                holder.card.setBackgroundResource(R.drawable.spons_cardbg_2);
                break;
            }
        }
        holder.name.setText(data.getName());
        holder.category.setText(data.getType());
        Glide.with(context).load(data.getImg()).transform(new CircleCrop()).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                holder.loadingimg.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                holder.loadingimg.setVisibility(View.GONE);
                return false;
            }
        }).into(holder.image);

        holder.card.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View view = LayoutInflater.from(context).inflate(R.layout.layout_spons_alertdialog,null);
            builder.setView(view);
            builder.setCancelable(true);
            TextView sponsname = view.findViewById(R.id.sponors_name);
            ImageView sponsimg = view.findViewById(R.id.sponsor_image);
            sponsname.setText(data.getName());
            Glide.with(context).load(data.getImg()).into(sponsimg);
            builder.create().show();
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        CardView card;
        TextView name;
        TextView category;
        ImageView loadingimg;
        ImageView image;

        public MyViewHolder(@NonNull View view) {
            super(view);
            card = view.findViewById(R.id.spons_card);
            loadingimg = view.findViewById(R.id.spons_loading);
            image = view.findViewById(R.id.spons_img);
            name = view.findViewById(R.id.spons_name);
            category = view.findViewById(R.id.spons_type);
        }
    }
}