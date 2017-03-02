package nirva.mywedding;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ThreadFactory;

import static android.R.attr.button;
import static android.R.attr.targetActivity;
import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * Created by sanje on 28-02-2017.
 */

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    ArrayList<Cardproperty> _myListItems;
    RequestManager _g;
    File storageDir;
    Context context;
    static int staticlike;
    static String dataupdate="intial";
    public CustomAdapter(){}

    public CustomAdapter(ArrayList<Cardproperty> myListItems,RequestManager g,File sdir,Context c) {
        _myListItems=myListItems;
        _g=g;
        storageDir=sdir;
        context=c;
    }

    @Override
    public CustomAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.customviewframe,parent,false);


        return new ViewHolder(v);
    }



    @Override
    public void onBindViewHolder(CustomAdapter.ViewHolder holder, int position) {
        final String img=_myListItems.get(position).getImage();
        final String usr=_myListItems.get(position).getUser();
        final String txt=_myListItems.get(position).getText();
        final ViewHolder vh=holder;

        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference();
        // Reference to an image file in Firebase Storage
        StorageReference storageReference = storageRef.child("light/"+img);


// Load the image using Glide
        _g.using(new FirebaseImageLoader())
                .load(storageReference)
                .into(vh.img);
        vh.usr.setText(usr);
        vh.txt.setText(usr);
        vh.downloadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("downloading:","Strated");
                NewDownload nd=new NewDownload();
                nd.execute( storageDir, img,context);
            }
        });
        vh.likebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addliketo(img);
                staticlike=staticlike+1;
                Uri uri=Uri.parse("hkl://hkl.com/"+img+"/"+staticlike+"/0");

                Log.d("static output waitlater",dataupdate);
                Intent i =new Intent(Intent.ACTION_VIEW,uri);
                context.startActivity(i);


            }
        });
        vh.inlargebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                getlikecount(img);
                Uri uri=Uri.parse("hkl://hkl.com/"+img+"/"+staticlike);
                Intent i =new Intent(Intent.ACTION_VIEW,uri);
                context.startActivity(i);
            }
        });



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
                dataupdate=value;

                try {
                    arr = new JSONArray("["+value.substring(1,value.length() - 1)+"]");
                    for(int i =0 ; i <arr.length() ; i++){

                        JSONObject jsonObj = (JSONObject)arr.get(i); // get the josn object
                        // compare for the key-value
                        //((JSONObject)arr.get(i)).put("lat", "32"); // put the new value for the key
                        if(jsonObj.getString("image").equalsIgnoreCase(image_name))
                        {
                            int  likes=Integer.valueOf(jsonObj.getString("like"));
                            staticlike=likes;

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


    private void  addliketo(final String image_name)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("post");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value,outputvalue="[";
                int  likes=0;
                value = dataSnapshot.getValue(String.class);
                Log.d("Tag", "Value is: " + value);
                JSONArray arr = null;
                Log.d("input",value);



                try {
                    arr = new JSONArray("["+value.substring(1,value.length() - 1)+"]");
                    for(int i =0 ; i <arr.length() ; i++){

                        JSONObject jsonObj = (JSONObject)arr.get(i); // get the josn object
                        // compare for the key-value

                        if(jsonObj.getString("image").equalsIgnoreCase(image_name))
                        {
                            likes=Integer.valueOf(jsonObj.getString("like"));
                            staticlike=likes+1;
                            jsonObj.put("like", String.valueOf(staticlike));
                        }
                        String image=jsonObj.getString("image");
                        String log=jsonObj.getString("log");
                        String lat=jsonObj.getString("lat");
                        String like=jsonObj.getString("like");
                        String user=jsonObj.getString("user");
                        String text=jsonObj.getString("text");
                        outputvalue=outputvalue+"{\"image\":\""+image+"\",\"log\":\""+log+"\",\"lat\":\""+lat+"\",\"like\":\""+like+"\",\"user\":\""+user+"\",\"text\":\""+text+"\"},";

                    }
                    outputvalue=outputvalue.substring(0,outputvalue.length()-1)+"]";
                    dataupdate=outputvalue;


                    Log.d("output",outputvalue);
                    Log.d("static dataup",dataupdate);
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

    @Override
    public int getItemCount() {
        return _myListItems.size();
    }




    class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView txt;
        public TextView usr;
        public ImageView img;
        public ImageButton downloadbtn,inlargebtn,likebtn;

        public ViewHolder(View v)
        {
            super(v);
            txt=(TextView) v.findViewById(R.id.textViewcard);
            usr=(TextView) v.findViewById(R.id.userViewcard);
            img=(ImageView)v.findViewById(R.id.imageViewcard);
            downloadbtn=(ImageButton)v.findViewById(R.id.downloadButton);
            inlargebtn=(ImageButton)v.findViewById(R.id.inlargebtn);
            likebtn=(ImageButton)v.findViewById(R.id.likebtn);


        }

    }


}
