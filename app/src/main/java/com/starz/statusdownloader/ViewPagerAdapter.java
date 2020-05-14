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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.core.app.ShareCompat;
import androidx.viewpager.widget.PagerAdapter;

import com.starz.statusdownloader.utility.SavedData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class ViewPagerAdapter extends PagerAdapter {
    ArrayList<File> files;
    Context context;
    View view;
    VideoView oldVideoView;
    public ViewPagerAdapter(ArrayList<File> files, Context context) {
        this.files = files;
        this.context = context;
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
        final File file=files.get(position);
        String mimeType = URLConnection.guessContentTypeFromName(file.getPath());
        if( mimeType != null && mimeType.startsWith("video")) {
            Log.e(TAG, "instantiateItem: " );
            view=layoutInflater.inflate(R.layout.row_item_player_video,container,false);
            view.findViewById(R.id.progress_circular).setVisibility(View.GONE);
            final VideoView imageView=view.findViewById(R.id.imageView);
            final ImageView btnPlay =view.findViewById(R.id.btnPlay);
            imageView.setVideoPath(file.getPath());
            imageView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    btnPlay.setVisibility(View.VISIBLE);

                }
            });
            imageView.seekTo(2000);
            btnPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(oldVideoView!=null){
                        oldVideoView.stopPlayback();
                    }
                    imageView.seekTo(0);
                    imageView.start();
                    oldVideoView=imageView;
                    btnPlay.setVisibility(View.GONE);
                }
            });
            Bitmap myBitmap = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(),
                    MediaStore.Video.Thumbnails.MICRO_KIND);
            view.setBackgroundColor(getDominantColor(myBitmap));

        }else if(mimeType != null && mimeType.startsWith("image")){
            view=layoutInflater.inflate(R.layout.row_item_player_image,container,false);
            view.findViewById(R.id.progress_circular).setVisibility(View.GONE);
            ImageView imageView=view.findViewById(R.id.imageView);
            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
            view.setBackgroundColor(getDominantColor(myBitmap));
        }
        ImageButton btnDownload=view.findViewById(R.id.btnDownload);
        ImageButton btnShare=view.findViewById(R.id.btnShare);
        ImageButton btnDelete=view.findViewById(R.id.btnDelete);
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyFileOrDirectory(file.getPath(),"/storage/emulated/0/statusDownloader");
                Toast.makeText(context,file.getName()+" downloaded successfully",Toast.LENGTH_SHORT).show();
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                file.delete();
                Activity activity= (Activity) context;
                activity.finish();
                Toast.makeText(context,file.getName()+" deleted successfully",Toast.LENGTH_SHORT).show();

            }
        });
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File videoFile=copyFileOrDirectory(file.getPath(),"/storage/emulated/0/statusDownloader");
                if(videoFile!=null){
                    String type ;
                    Uri videoURI=getUri(context,videoFile);
                    String mimeType = URLConnection.guessContentTypeFromName(videoFile.getPath());
                    if( mimeType != null && mimeType.startsWith("video")) {
                        type="video";
                    }else if(mimeType != null && mimeType.startsWith("image")){
                        type="image";
                    }else{
                        type="audio";
                    }

                    ShareCompat.IntentBuilder.from((Activity) context)
                            .setStream(videoURI)
                            .setType(type+"/*")
                            .setChooserTitle("Share")
                            .startChooser();
                }
            }
        });
        container.addView(view);
        return view;
    }

    public static Uri getUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    public File copyFileOrDirectory(String srcDir, String dstDir) {

        try {
            File src = new File(srcDir);
            if(srcDir.compareTo(dstDir+'/'+src.getName())==0){
                Log.e(TAG, "copyFileOrDirectory: " );
                return src;
            }
            File dst = new File(dstDir, src.getName());

            if (src.isDirectory()) {

                String files[] = src.list();
                int filesLength = files.length;
                for (int i = 0; i < filesLength; i++) {
                    String src1 = (new File(src, files[i]).getPath());
                    String dst1 = dst.getPath();
                    return copyFileOrDirectory(src1, dst1);
                }
            } else {
                return copyFile(src, dst);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public File copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
        return destFile;
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
