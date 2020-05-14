package com.starz.statusdownloader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.starz.statusdownloader.share.Post;
import com.starz.statusdownloader.utility.Connection;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;

public class PlayerActivity2 extends AppCompatActivity {
    VerticalViewPager viewPager;
    ViewPagerAdapter2 viewPagerAdapter2;
    ArrayList<Post> arrayList;
    Connection connection;
    String path,month,key,comment,name,time,type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player2);
        Intent intent=getIntent();
        int position=intent.getIntExtra("position",0);

        path = intent.getExtras().getString("path");
        month = intent.getExtras().getString("month");
        key = intent.getExtras().getString("key");
        comment = intent.getExtras().getString("comment");
        name = intent.getExtras().getString("name");
        time = intent.getExtras().getString("time");
        type = intent.getExtras().getString("type");


        viewPager =findViewById(R.id.view_pager);

        arrayList=new ArrayList<>();
        arrayList.add(new Post(path,comment,name,time,type,key,month));
        viewPagerAdapter2=new ViewPagerAdapter2(arrayList,this);
        connection=new Connection();
        viewPager.setAdapter(viewPagerAdapter2);
        viewPager.setCurrentItem(position);

    }

    @Override
    protected void onStart() {
        super.onStart();
        connection.getDbPost().child(month).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot dataSnapshot2:dataSnapshot.getChildren()){
                        Post post=dataSnapshot2.getValue(Post.class);
                        post.setKey(dataSnapshot2.getKey());
                        arrayList.add(post);

                    }

                viewPagerAdapter2.notifyDataSetChanged();
                connection.getDbPost().removeEventListener(this);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        connection.getDbPost().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    for(DataSnapshot dataSnapshot2:dataSnapshot1.getChildren()){
                        Post post=dataSnapshot2.getValue(Post.class);
                        post.setKey(dataSnapshot2.getKey());
                        arrayList.add(post);

                    }
                }

                viewPagerAdapter2.notifyDataSetChanged();
                connection.getDbPost().removeEventListener(this);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
