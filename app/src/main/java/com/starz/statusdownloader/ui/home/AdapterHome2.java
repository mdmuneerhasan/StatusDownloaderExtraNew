package com.starz.statusdownloader.ui.home;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.starz.statusdownloader.PlayerActivity;
import com.starz.statusdownloader.PlayerActivity2;
import com.starz.statusdownloader.R;
import com.starz.statusdownloader.share.Post;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;

public class AdapterHome2 extends RecyclerView.Adapter<AdapterHome2.Holder> {
    ArrayList<Post> fileArrayList;
    Context context;

    public AdapterHome2(ArrayList<Post> fileArrayList) {
        if(fileArrayList.size()==0){
            fileArrayList.add(null);
            fileArrayList.add(null);
            fileArrayList.add(null);
        }
        this.fileArrayList = fileArrayList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        View view=LayoutInflater.from(context).inflate(R.layout.row_item_share_activity,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, final int position) {
        final Post post=fileArrayList.get(position);
        holder.imageView.setBackgroundColor(((int) System.currentTimeMillis())%100*99999999);
        if(post==null){
            holder.imageView.setVisibility(View.VISIBLE);
            holder.videoView.setVisibility(View.GONE);
            holder.imageView.setImageResource(R.drawable.ic_sentiment_dissatisfied_black_24dp);
            holder.imageView.setBackgroundColor(((int) System.currentTimeMillis())%100*99999999);
            holder.btnPlay.setVisibility(View.GONE);
            return;
        }
        holder.progressBar.setVisibility(View.VISIBLE);
        if(post.getType().startsWith("video")){
            Log.e( "onBindViewHolder: ", "video");
            holder.imageView.setVisibility(View.VISIBLE);
            holder.videoView.setVisibility(View.VISIBLE);
            holder.videoView.setVideoPath(post.getUrl());
            holder.videoView.start();

            holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setVolume(0,0);
                    mp.setLooping(true);
                    mp.start();
                    holder.btnPlay.setImageResource(R.drawable.ic_videocam_black_24dp);
                    holder.progressBar.setVisibility(View.GONE);
                }
            });
        }else{
            Log.e( "onBindViewHolder: ", "image");
            holder.imageView.setVisibility(View.VISIBLE);
            holder.videoView.setVisibility(View.GONE);
            Picasso.get().load(post.getUrl()).fit().into(holder.imageView, new Callback() {
                @Override
                public void onSuccess() {
                    holder.btnPlay.setImageResource(R.drawable.ic_image_black_24dp);
                    holder.progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {

                }
            });
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, PlayerActivity2.class).putExtra("path",post.getUrl());
                intent.putExtra("key",post.getKey());
                intent.putExtra("position",position);
                intent.putExtra("month",post.getMonth());
                intent.putExtra("comment",post.getComment());
                intent.putExtra("name",post.getName());
                intent.putExtra("time",post.getTime());
                intent.putExtra("type",post.getType());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull Holder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public int getItemCount() {
        return fileArrayList.size();
    }




    class Holder extends RecyclerView.ViewHolder{
        ImageView imageView;
        ImageView btnPlay;
        VideoView videoView;
        ProgressBar progressBar;
        public Holder(@NonNull View itemView) {
            super(itemView);
            progressBar=itemView.findViewById(R.id.progress_circular);
            videoView=itemView.findViewById(R.id.videoView);
            imageView=itemView.findViewById(R.id.imageView);
            btnPlay=itemView.findViewById(R.id.btnPlay);

        }
    }
}