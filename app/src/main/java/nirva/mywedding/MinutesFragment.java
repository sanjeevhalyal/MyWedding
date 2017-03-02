package nirva.mywedding;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static nirva.mywedding.Setservice.Folder;
import static nirva.mywedding.Setservice.Folderpref;
import static nirva.mywedding.User_content.fab;


public class MinutesFragment extends Fragment {

    private String value;
    private ArrayList<Cardproperty> myListItems;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Activated = "Activated";

    public MinutesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("View:"," started");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef ;
        myListItems = new ArrayList<>();
        Log.d("Tag", " Refrenece captured");

        View rootView = inflater.inflate(R.layout.fragment_minutes, container, false);
        mRecyclerView=(RecyclerView)rootView.findViewById(R.id.my_recycler_view);

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager=new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String channel = (sharedpreferences.getString(Activated, ""));
        Folder= (sharedpreferences.getString(Folderpref, ""));

        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                fab.setVisibility(View.VISIBLE);
                Log.d("Tag","Fab Visible");
                return false;
            }
        });

        if(channel.equalsIgnoreCase(Activated)) {
            myRef = database.getReference(Folder);



            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    value = dataSnapshot.getValue(String.class);
                    extract(value);
                    Log.d("Tag", "Value is: " + value);
                    // specify an adapter (see also next example)
                    mAdapter = new CustomAdapter(myListItems, Glide.with(getContext()),getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES+"/download"),getContext());
                    mRecyclerView.setAdapter(mAdapter);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("Tag", "Failed to read value.", error.toException());
                }
            });
            rootView.findViewById(R.id.opps).setVisibility(View.GONE);
        }


        // Inflate the layout for this fragment



        return rootView;
    }

    void extract(String value)
    {
        Log.d("extracting:"," started");
        myListItems.clear();

        JSONArray arr = null;

        try {
            arr = new JSONArray("["+value.substring(1,value.length() - 1)+"]");
            for(int i = arr.length()-1; i >=0 ; i--){

                JSONObject jsonObj = (JSONObject)arr.get(i); // get the josn object
                // compare for the key-value
                ((JSONObject)arr.get(i)).put("lat", "32"); // put the new value for the key
                String image=jsonObj.getString("image");
                String log=jsonObj.getString("log");
                String lat=jsonObj.getString("lat");
                String like=jsonObj.getString("like");
                String user=jsonObj.getString("user");
                String text=jsonObj.getString("text");
                myListItems.add(new Cardproperty(image, log, lat, like, user, text));


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }



    }



}
