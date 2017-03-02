package nirva.mywedding;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;
import static nirva.mywedding.Setservice.Folder;

public class inlarge extends AppCompatActivity {

    TextView likesdisplay,textviewtext,textviewuser;
    String like;
    String img;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Uri data = intent.getData();
        List<String> al= data.getPathSegments();
        img=al.get(0);
        like=al.get(1);

        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        like=String.valueOf(CustomAdapter.staticlike);
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("post").setValue(CustomAdapter.dataupdate);
        /*
        int liked=getIntent().getIntExtra("liked",0);
        int likecount=getIntent().getIntExtra("like",0);;

        String image_name=getIntent().getStringExtra("image");
        */


       // img=image_name;



        setContentView(R.layout.activity_inlarge);
       Log.d("File name",img);
        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference();

        // Reference to an image file in Firebase Storage
        StorageReference storageReference = storageRef.child(Folder +"/upload/"+img);

        likesdisplay=(TextView)findViewById(R.id.Likenumber);
        ImageView largeimg=(ImageView)findViewById(R.id.largeimageViewcard);
        String tmp=like+" likes";
        likesdisplay.setText( tmp);
        textviewtext=(TextView)findViewById(R.id.textViewcard);
        textviewuser=(TextView)findViewById(R.id.userViewcard);
        getlikecount(img);
        ImageButton download=(ImageButton)findViewById(R.id.downloadButton) ;

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewDownload nd=new NewDownload();
                nd.execute( getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES+"/download"), img,getApplicationContext());
            }
        });



// Load the image using Glide
        Glide.with(this)
                .using(new FirebaseImageLoader())
                .load(storageReference)
                .into(largeimg);




    }

    private void getlikecount(final String image_name) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("post");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value;
                value = dataSnapshot.getValue(String.class);
                Log.d("Tag", "Value is: " + value);
                JSONArray arr = null;


                try {
                    arr = new JSONArray("["+value.substring(1,value.length() - 1)+"]");
                    for(int i =0 ; i <arr.length() ; i++){

                        JSONObject jsonObj = (JSONObject)arr.get(i); // get the josn object
                        // compare for the key-value
                        //((JSONObject)arr.get(i)).put("lat", "32"); // put the new value for the key
                        if(jsonObj.getString("image").equalsIgnoreCase(image_name))
                        {
                            textviewtext.setText(jsonObj.getString("text"));
                            textviewuser.setText(jsonObj.getString("user"));

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }



            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Tag", "Failed to read value.", error.toException());
            }
        });
    }



}
