package nirva.mywedding;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.renderscript.Script;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class User_content extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private File f;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    String mCurrentPhotoPath;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Activated = "Activated";
    public static final String User= "User";
    static FloatingActionButton fab;

    private void dispatchTakePictureIntent() throws IOException {
        Toast.makeText(User_content.this, "Starting camera", Toast.LENGTH_SHORT).show();
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                f=photoFile;
                Uri photoURI = FileProvider.getUriForFile(this,
                        "nirva.mywedding.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name


        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        Toast.makeText(User_content.this, mCurrentPhotoPath, Toast.LENGTH_SHORT).show();
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }


    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("Tag","Permission is granted");
                return true;
            } else {

                Log.v("Tag","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("Tag","Permission is granted");
            return true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_content);
        isStoragePermissionGranted();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);



        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                String channel = (sharedpreferences.getString(Activated, ""));
                String userstring = (sharedpreferences.getString(User, ""));
                if(channel.equalsIgnoreCase(Activated)) {
                    if (userstring.equalsIgnoreCase(User)) {
                        LayoutInflater li = LayoutInflater.from(User_content.this);
                        View getListItemView = li.inflate(R.layout.dialog_get_list_item, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(User_content.this);
                        alertDialogBuilder.setView(getListItemView);
                        Snackbar.make(view, "Starting Camera: Enter your display name", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        final EditText userInput = (EditText) getListItemView.findViewById(R.id.editTextDialogUserInput);
                        alertDialogBuilder
                                .setTitle("Enter your Display name ")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // get user input and set it to result
                                        // edit text
                                        String listItemText = userInput.getText().toString();

                                        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedpreferences.edit();
                                        editor.putString(User, listItemText);
                                        editor.commit();

                                        try {
                                            dispatchTakePictureIntent();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }).create()
                                .show();


                    } else {

                        try {
                            Snackbar.make(view, "Starting Camera", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            dispatchTakePictureIntent();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
                else
                {
                    Snackbar.make(view, "Your App is not Activated", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Toast.makeText(this, String.valueOf(resultCode), Toast.LENGTH_SHORT).show();
        galleryAddPic();

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Toast.makeText(this, "Running result", Toast.LENGTH_SHORT).show();
            final File f = new File(mCurrentPhotoPath);
            Bitmap b = BitmapFactory.decodeFile(mCurrentPhotoPath);
            String filename = f.getName() ;
            String filenameArray[] = filename.split("\\.");

            File fpng=null;
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES+"/light");
            try {
                 fpng = File.createTempFile(
                        f.getName().substring(0,f.getName().length()-4),".jpg",
                         storageDir

                );


            } catch (IOException e) {
                e.printStackTrace();
            }

            Toast.makeText(this, fpng.getName()+" - "+filenameArray[0]+" - "+f.getName(), Toast.LENGTH_LONG).show();
            FileOutputStream fos = null;
            ByteArrayOutputStream bos;
            byte[] bitmapdata;
            bos = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.JPEG, 60 , bos);
            bitmapdata = bos.toByteArray();
            try {
                fpng.createNewFile();
                fos = new FileOutputStream(fpng);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();
                fpng.renameTo(new File(storageDir,f.getName().substring(0,f.getName().length()-4)+".jpg"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            LayoutInflater li = LayoutInflater.from(User_content.this);
            View getListItemView = li.inflate(R.layout.dialog_get_list_item, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(User_content.this);
            alertDialogBuilder.setView(getListItemView);
            final EditText userInput = (EditText) getListItemView.findViewById(R.id.editTextDialogUserInput);
            alertDialogBuilder
                    .setTitle("Comments.. ")
                    .setCancelable(false)
                    .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            // get user input and set it to result
                            // edit text
                            String listItemText = userInput.getText().toString();
                            Toast.makeText(User_content.this, "Entry: "+listItemText, Toast.LENGTH_LONG).show();
                            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES+"/light");
                            File fpng= new File(storageDir,f.getName().substring(0,f.getName().length()-4)+".jpg");
                            Log.d("file name: ",fpng.getAbsolutePath());
                            Uri uri = Uri.fromFile(f);
                            Uri uripng = Uri.fromFile(fpng);
                            String filename=f.getName();
                            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                            String username = (sharedpreferences.getString(User, ""));
                            NewUpload up=new NewUpload();
                            up.execute(uri,uripng,filename,listItemText,username);




                        }
                    }).create()
                    .show();






        }
    }



            @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_content, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.Contact_Developer) {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"sanjeevhalyal@gmail.com"});
            i.putExtra(Intent.EXTRA_SUBJECT, "Subject:Contacting You;)");

            try {
                startActivity(Intent.createChooser(i, "Send mail..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_user_content, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:

                    return new ToastFragment();
                case 1:
                    return new MinutesFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Toast";
                case 1:
                    return "Minutes";
            }
            return null;
        }
    }
}
