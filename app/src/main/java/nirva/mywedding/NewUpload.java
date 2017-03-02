package nirva.mywedding;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

import static nirva.mywedding.Setservice.Folder;
import static nirva.mywedding.User_content.Activated;

/**
 * Created by sanje on 01-03-2017.
 */

public class NewUpload extends AsyncTask<Object, ArrayList<String>, String> {
    StorageReference storageRef;
    StorageReference riversRef;
    String text,filename;
    String value;
    String username;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    @Override
    protected String doInBackground(Object... objects) {

        filename=(String)objects[2];
        text=(String)objects[3];
        username=(String)objects[4];
        storageRef = FirebaseStorage.getInstance().getReference();
        riversRef = storageRef.child(Folder +"/upload/"+((Uri)objects[0]).getLastPathSegment());


        UploadTask uploadTasklight = riversRef.putFile(((Uri)objects[0]));

// Register observers to listen for when the download is done or if it fails
        uploadTasklight.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Log.d("tag","Light upload complete");

            }
        });
        riversRef = storageRef.child(Folder +"/light/"+((Uri)objects[1]).getLastPathSegment());
        UploadTask uploadTaskup = riversRef.putFile(((Uri)objects[0]));

// Register observers to listen for when the download is done or if it fails
        uploadTaskup.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Log.d("tag","normal upload complete");
                DatabaseReference mDatabase;
//
                mDatabase = FirebaseDatabase.getInstance().getReference();


                mDatabase.child("post").setValue(value);
                Log.d("tag","value added: "+value);



            }
        });


        return null;
    }

    private void up(Object o)
    {
        UploadTask uploadTask = riversRef.putFile(((Uri)o));

// Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Log.d("tag","upload complete");



            }
        });

    }
    @Override
    protected void onPostExecute(String result) {
        // execution of result of Long time consuming operation

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("post");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                value = dataSnapshot.getValue(String.class);
                value=value.substring(0,value.length()-1);
                Log.d("Tag", "Value is: " + value);
                String image=filename;
                String log="0";
                String lat="0";
                String like="1";
                String user=getuser();
                value=value+",{\"image\":\""+image+"\",\"log\":\""+log+"\",\"lat\":\""+lat+"\",\"like\":\""+like+"\",\"user\":\""+user+"\",\"text\":\""+text+"\"}]";

                Log.d("text: ",value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Tag", "Failed to read value.", error.toException());
            }
        });


    }
    String getuser()
    {

        return username;

    }
}
