package com.starz.statusdownloader.share;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;
import com.starz.statusdownloader.R;
import com.starz.statusdownloader.utility.Connection;
import com.starz.statusdownloader.utility.SavedData;
import com.starz.statusdownloader.utility.Storage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ShareActivity extends AppCompatActivity {
    SavedData savedData;
    Intent intent;
    ArrayList<Uri> uriArrayList;
    RecyclerView recyclerView;
    PhotoAdapter photoAdapter;
    Date date;
    Storage storage;
    Connection connection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        savedData=new SavedData(this);
        uriArrayList=new ArrayList<>();
        intent=getIntent();
        recyclerView=findViewById(R.id.recycler);
        GridLayoutManager gridLayoutManager=new GridLayoutManager(this,2);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        photoAdapter=new PhotoAdapter(uriArrayList,intent);
        recyclerView.setAdapter(photoAdapter);
        date=new Date();
        storage=new Storage();
        connection=new Connection();
        if(!savedData.haveValue("uid")){
            savedData.setValue("uid", String.valueOf(System.currentTimeMillis()));
        }
        if(intent.getAction()==Intent.ACTION_SEND){
            Log.e("Single ", "onCreate: " );
            Uri uri=intent.getParcelableExtra(Intent.EXTRA_STREAM);
            uriArrayList.add(uri);
            photoAdapter.notifyDataSetChanged();
        }
        if(intent.getAction()==Intent.ACTION_SEND_MULTIPLE){
            Log.e("Multiple ", "onCreate: " );
            uriArrayList=intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
            photoAdapter.setArrayList(uriArrayList);
            photoAdapter.notifyDataSetChanged();
        }



    }

    public void share(View view) {
        for(Uri uri:uriArrayList){
            //    public Post(String url, String comment, String name, String time, String type, String key, String month) {
            final Post post = new Post("", "no comment", "name", new SimpleDateFormat("hh:mm a dd-MMM-yyyy").format(date), intent.getType(), "", new SimpleDateFormat("MM").format(date));
            post.setLink("link");
            if(uri!=null){
                storage.getPostStorage().child("delete").child(post.getMonth())
                        .child(System.currentTimeMillis()+"."+getExtension(uri)).putFile(uri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl();
                                downloadUrl.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String url = uri.toString();
                                        savedData.toast("Upload Successful");
                                        post.setUrl(url);
                                        post.setUid(savedData.getValue("uid"));
                                        connection.getDbPost().child(post.getMonth()).push().setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                savedData.toast("posted");
                                            }
                                        });
                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        savedData.toast("Upload Failed");

                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        //       savedData.showAlert(String.valueOf(100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount()));
                    }
                });
            }

        }
        savedData.toast("sharing your content");
        onBackPressed();
    }
    private String getExtension(Uri uri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(getContentResolver().getType(uri));
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }
}
