package com.starz.statusdownloader.ui.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.starz.statusdownloader.R;
import com.starz.statusdownloader.PlayerActivity;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;

public class AdapterHome extends RecyclerView.Adapter<AdapterHome.Holder> {
    private int oldColor=0 ;
    ArrayList<File> fileArrayList;
    Context context;
    String path;
    public AdapterHome(String path) {
        fileArrayList=new ArrayList<>();
        this.path=path;
        refresh();
    }


    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        View view=LayoutInflater.from(context).inflate(R.layout.row_item_share_activity,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, final int position) {
        File file=fileArrayList.get(position);
        if(file==null){
            holder.imageView.setVisibility(View.VISIBLE);
            holder.videoView.setVisibility(View.GONE);
            holder.imageView.setImageResource(R.drawable.ic_sentiment_dissatisfied_black_24dp);
            holder.imageView.setBackgroundColor(((int) System.currentTimeMillis())%100*99999999);
            holder.btnPlay.setVisibility(View.GONE);
            return;
        }

        String mimeType = URLConnection.guessContentTypeFromName(file.getPath());

        if(mimeType != null && mimeType.startsWith("video")){
            holder.btnPlay.setImageResource(R.drawable.ic_videocam_black_24dp);
            holder.imageView.setVisibility(View.VISIBLE);
            holder.videoView.setVisibility(View.VISIBLE);
            holder.videoView.setVideoPath(file.getPath());
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
            holder.btnPlay.setImageResource(R.drawable.ic_image_black_24dp);
            holder.imageView.setVisibility(View.VISIBLE);
            holder.videoView.setVisibility(View.GONE);
            Picasso.get().load(Uri.fromFile(file)).fit().into(holder.imageView);
        }




        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, PlayerActivity.class).putExtra("position",position);
                intent.putExtra("path",path);
                context.startActivity(intent);
            }
        });
        holder.itemView.setBackgroundColor(((int) System.currentTimeMillis())%100*99999999);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull Holder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public int getItemCount() {
        return fileArrayList.size();
    }

    public void refresh() {
        File file = new File(Environment.getExternalStorageDirectory(), path);
        File[] list=file.listFiles();
        fileArrayList.clear();
        if(list==null || list.length==0){
            fileArrayList.add(null);
            fileArrayList.add(null);
            fileArrayList.add(null);
            notifyDataSetChanged();
            return;
        }
        for(File f:list){
            String mimeType = URLConnection.guessContentTypeFromName(f.getPath());
            if( mimeType != null) {
                if( mimeType.startsWith("video")||mimeType.startsWith("image"))
                    fileArrayList.add(0,f);
            }
        }
        if(fileArrayList.size()==0){
            fileArrayList.add(null);
            fileArrayList.add(null);
            fileArrayList.add(null);
            notifyDataSetChanged();
            return;
        }

        notifyDataSetChanged();

    }



    class Holder extends RecyclerView.ViewHolder{
        ImageView imageView;
        ImageView btnPlay;
        VideoView videoView;
        public Holder(@NonNull View itemView) {
            super(itemView);
            videoView=itemView.findViewById(R.id.videoView);
            imageView=itemView.findViewById(R.id.imageView);
            btnPlay=itemView.findViewById(R.id.btnPlay);

        }
    }
}