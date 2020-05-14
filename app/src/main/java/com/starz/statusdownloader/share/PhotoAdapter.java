package com.starz.statusdownloader.share;


import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.starz.statusdownloader.R;

import java.util.ArrayList;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.Holder>{
    ArrayList<Uri> arrayList;
    Context context;
    Intent intent;
    public PhotoAdapter(ArrayList<Uri> arrayList, Intent intent) {
        this.arrayList = arrayList;
        this.intent=intent;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context=viewGroup.getContext();
        return new Holder(LayoutInflater.from(context).inflate(R.layout.row_item_share_activity,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int i) {
        if(intent.getType().startsWith("video")){
            holder.imageView.setVisibility(View.VISIBLE);
            holder.videoView.setVisibility(View.VISIBLE);
            holder.videoView.setVideoURI(arrayList.get(i));
            holder.videoView.start();
            holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setVolume(0,0);
                    mp.setLooping(true);
                    mp.start();
                }
            });
        }else{
            holder.imageView.setVisibility(View.VISIBLE);
            holder.videoView.setVisibility(View.GONE);
            Picasso.get().load(arrayList.get(i)).into(holder.imageView);
        }
        holder.btnDelete.setImageResource(R.drawable.ic_delete_black_24dp);
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayList.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void setArrayList(ArrayList<Uri> uriArrayList) {
        this.arrayList=uriArrayList;
    }

    class  Holder extends RecyclerView.ViewHolder{
        public ImageView imageView,btnDelete;
        VideoView videoView;
        public Holder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.imageView);
            videoView=itemView.findViewById(R.id.videoView);
            btnDelete=itemView.findViewById(R.id.btnPlay);
        }
    }
}