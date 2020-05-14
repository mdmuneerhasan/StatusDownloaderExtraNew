package com.starz.statusdownloader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.VideoView;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {
    VerticalViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    ArrayList<File> arrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        Intent intent=getIntent();
        int position=intent.getIntExtra("position",0);
        String path=intent.getExtras().getString("path");
        viewPager =findViewById(R.id.view_pager);
        arrayList=new ArrayList<>();

//        File file = new File(Environment.getExternalStorageDirectory(), "WhatsApp/Media/.Statuses");
        File file = new File(Environment.getExternalStorageDirectory(), path);

        for(File f:file.listFiles()){
            String mimeType = URLConnection.guessContentTypeFromName(f.getPath());
            if( mimeType != null) {
                if( mimeType.startsWith("video")||mimeType.startsWith("image"))
                    arrayList.add(0,f);
            }
        }
        viewPagerAdapter=new ViewPagerAdapter(arrayList,this);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(position);


    }
}
