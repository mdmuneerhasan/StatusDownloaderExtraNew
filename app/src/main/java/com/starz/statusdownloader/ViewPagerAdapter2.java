package com.starz.statusdownloader;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.core.app.ShareCompat;
import androidx.viewpager.widget.PagerAdapter;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.starz.statusdownloader.share.Post;
import com.starz.statusdownloader.utility.SavedData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class ViewPagerAdapter2 extends PagerAdapter {
    ArrayList<Post> files;
    Context context;
    View view;
    VideoView oldVideoView;
    SavedData savedData;
    public ViewPagerAdapter2(ArrayList<Post> files, Context context) {
        this.files = files;
        this.context = context;
        savedData=new SavedData(context);
    }

    @Override
    public int getCount() {
        return files.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view==object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, final int position) {
        LayoutInflater layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view= null;
        final Post post=files.get(position);
        if(post.getType().startsWith("video")){
            view=layoutInflater.inflate(R.layout.row_item_player_video,container,false);
            final ImageView btnPlay =view.findViewById(R.id.btnPlay);
            final VideoView videoView=view.findViewById(R.id.imageView);
            final ProgressBar progressBar=view.findViewById(R.id.progress_circular);
            progressBar.setVisibility(View.VISIBLE);
            btnPlay.setVisibility(View.GONE);
            videoView.setVideoPath(post.getUrl());
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    btnPlay.setVisibility(View.VISIBLE);
                }
            });
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    progressBar.setVisibility(View.GONE);
                    btnPlay.setVisibility(View.VISIBLE);
                    mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                        @Override
                        public boolean onInfo(MediaPlayer mp, int what, int extra) {
                            if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START)
                                progressBar.setVisibility(View.VISIBLE);
                            if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END)
                                progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    });
                }

            });

            btnPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(oldVideoView!=null){
                        oldVideoView.stopPlayback();
                    }
                    oldVideoView=videoView;
                    videoView.start();
                    btnPlay.setVisibility(View.GONE);
                }
            });
        }else{
            view=layoutInflater.inflate(R.layout.row_item_player_image,container,false);
            ImageView imageView=view.findViewById(R.id.imageView);
            final ProgressBar progressBar=view.findViewById(R.id.progress_circular);
            progressBar.setVisibility(View.VISIBLE);
            Log.e( "onBindViewHolder: ", "image");
            Picasso.get().load(post.getUrl()).fit().into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {

                }
            });
        }
        view.setBackgroundColor(((int) System.currentTimeMillis())%100*99999999);


        ImageButton btnDownload=view.findViewById(R.id.btnDownload);
        ImageButton btnShare=view.findViewById(R.id.btnShare);
        ImageButton btnDelete=view.findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        container.addView(view);

        return view;
    }

    public static int getDominantColor(Bitmap bitmap) {
        try{   Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, 1, 1, true);
        final int color = newBitmap.getPixel(0, 0);
        newBitmap.recycle();
        return color;
        }catch (Exception e){
            return (int) (Math.random()*102345347);
        }
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }


}
