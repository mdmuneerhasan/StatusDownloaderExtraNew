package com.starz.statusdownloader.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.starz.statusdownloader.R;
import com.starz.statusdownloader.share.Post;
import com.starz.statusdownloader.utility.Connection;
import com.starz.statusdownloader.utility.SavedData;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;

public class HomeFragment extends Fragment {

    RecyclerView rvWhatsApp,rvSaved,rvTrends;
    AdapterHome adWhatsApp,adSaved;
    AdapterHome2 adTrends;
    ArrayList<Post> postArrayList;
    Connection connection;
    boolean set=false;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        rvWhatsApp=root.findViewById(R.id.rvWhatsApp);
        rvSaved=root.findViewById(R.id.rvSaved);
        rvTrends=root.findViewById(R.id.rvTrends);

        connection=new Connection();
        postArrayList=new ArrayList<>();
        adWhatsApp=new AdapterHome( "WhatsApp/Media/.Statuses");
        adSaved=new AdapterHome("statusDownloader");
        adTrends=new AdapterHome2(postArrayList);
        rvTrends.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        rvTrends.setAdapter(adTrends);
        rvWhatsApp.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        rvWhatsApp.setAdapter(adWhatsApp);
        rvSaved.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        rvSaved.setAdapter(adSaved);


        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
     adWhatsApp.refresh();
     adSaved.refresh();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(set)return;
        set = true;
        connection.getDbPost().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postArrayList.clear();
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    for(DataSnapshot dataSnapshot2:dataSnapshot1.getChildren()){
                        Post post=dataSnapshot2.getValue(Post.class);
                        post.setKey(dataSnapshot2.getKey());
                        postArrayList.add(0,post);

                    }
                }
                if(postArrayList.size()==0){
                    postArrayList.add(null);
                    postArrayList.add(null);
                    postArrayList.add(null);
                }
                adTrends.notifyDataSetChanged();
                connection.getDbPost().removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
