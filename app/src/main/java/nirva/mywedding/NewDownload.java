package nirva.mywedding;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by sanje on 01-03-2017.
 */

public class NewDownload extends AsyncTask<Object, ArrayList<String>, String> {
    Context context;
    File fdl;
    @Override
    protected String doInBackground(Object... objects) {

        File storageDir=(File) objects[0];
        String img=(String)objects[1];
        context=(Context)objects[2];

        fdl=new File(storageDir+img);
        if(!fdl.exists()) {

            downloader("Download In-Progress");


        try {
            fdl = File.createTempFile(
                    img.substring(0,img.length()-4),".jpg",
                    storageDir

            );


        } catch (IOException e) {
            e.printStackTrace();
        }

            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference islandRef = storageRef.child("upload/" + img);

            islandRef.getFile(fdl).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been created
                    Log.d("Donwload", " complete");
                    move();
                    downloader("Download Complete");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    Log.d("Donwload", " Failed");
                    downloader("Download Failed");

                }
            });




        }
        else
        {
            Log.d("File","EXSITS");
            downloader("File Exsits");
        }

        return null;
    }

    void move()
    {
        String descPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Pictures/" + fdl.getName();
        File desc = new File(descPath);
        fdl.renameTo(desc);
        /*
        Bitmap b=BitmapFactory.decodeFile (fdl.getAbsolutePath());
        FileOutputStream fos = null;
        ByteArrayOutputStream bos;
        byte[] bitmapdata;
        bos = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 100 , bos);
        bitmapdata = bos.toByteArray();
        try {
            fos = new FileOutputStream(desc);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        */


        Log.d("File "+fdl.getAbsolutePath(),"Moved "+desc.getAbsolutePath());
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

        Uri contentUri = Uri.fromFile(desc);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }
    void downloader(String s)
    {
        Intent intent = new Intent(context, NotificationReceiverActivity.class);
// use System.currentTimeMillis() to have a unique ID for the pending intent
        PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, 0);

// build notification
// the addAction re-use the same intent to keep the example short
        Notification n  = new Notification.Builder(context)
                .setContentTitle("Downloader")
                .setContentText(s)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentIntent(pIntent).setVibrate(new long[] { 1000,100 })
                        .build();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(NOTIFICATION_SERVICE);
        n.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, n);
    }
    protected String onPostExecute()
    {


        return null;
    }
}
