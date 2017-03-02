package nirva.mywedding;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import static nirva.mywedding.Setservice.Folder;
import static nirva.mywedding.Setservice.Folderpref;
import static nirva.mywedding.User_content.fab;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ToastFragment} interface
 * to handle interaction events.
 * Use the {@link ToastFragment# factory method to
 * create an instance of this fragment.
 */
public class ToastFragment extends Fragment {
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String Activated = "Activated";
    ImageButton nav;
    ImageView mainimage;
    TextView date,month,year;
    ProgressBar progressBar;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef ;
        final View rootview = inflater.inflate(R.layout.fragment_toast, container, false);

        nav=(ImageButton) rootview.findViewById(R.id.nav_btn);
        mainimage=(ImageView)rootview.findViewById(R.id.imageViewtoast);
        date=(TextView)rootview.findViewById(R.id.textView5);
        month=(TextView)rootview.findViewById(R.id.textView4);
        year=(TextView)rootview.findViewById(R.id.textView3);
       // progressBar=(ProgressBar)rootview.findViewById(R.id.pbmain);

        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String channel = (sharedpreferences.getString(Activated, ""));
        Folder = (sharedpreferences.getString(Folderpref, ""));


        progressBar=(ProgressBar)rootview.findViewById(R.id.pbmain);
        progressBar.setVisibility(View.GONE);

        if (channel.equalsIgnoreCase(Activated)) {
            myRef=database.getReference().child(Folder+"-detail");
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    ((TextView)rootview.findViewById(R.id.opps)).setVisibility(View.GONE);
                    String img=dataSnapshot.child("img").getValue(String.class);
                    String lat=dataSnapshot.child("lat").getValue(String.class);
                    String log=dataSnapshot.child("log").getValue(String.class);
                    String day=dataSnapshot.child("day").getValue(String.class);
                    String mon=dataSnapshot.child("mon").getValue(String.class);
                    String yr=dataSnapshot.child("year").getValue(String.class);
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();
                    StorageReference storageReference = storageRef.child(Folder +"/"+img);
                    Glide.with(ToastFragment.this)
                            .using(new FirebaseImageLoader())
                            .load(storageReference)
                           .into(mainimage);


                    date.setText(day);
                    month.setText(mon);
                    year.setText(yr);
                    setnav(lat,log);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else
        {
            ((LinearLayout)rootview.findViewById(R.id.toastlinearlayout)).setVisibility(View.GONE);
        }

        rootview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                fab.setVisibility(View.GONE);
                Log.d("Tag","Fab Gone");
                return false;
            }
        });
        return rootview;
    }
    void setnav(String lat,String log)
    {

        final double la=Double.valueOf(lat);
        final double lo=Double.valueOf(log);
        nav.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View view) {
                                       Intent intent = new Intent(Intent.ACTION_VIEW,
                                               Uri.parse("http://ditu.google.cn/maps?f=d&source=s_d&daddr=" + String.valueOf(la) + "," + String.valueOf(lo) + "&hl=zh&t=m&dirflg=d"));
                                       intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK&Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                       intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                                       startActivity(intent);

                                   }
                               });

    }

}
