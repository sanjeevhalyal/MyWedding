package nirva.mywedding;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Email_method extends AppCompatActivity {

    TextInputLayout signupInputLayoutEmail,signupInputLayoutPassword ;
    EditText signupInputEmail,signupInputPassword;
    ProgressBar progressBar;
    FirebaseAuth auth;
    String value;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_email_method);

        Button signup = (Button) findViewById(R.id.button4);
        Button login = (Button) findViewById(R.id.button5);
        LinearLayout ll = (LinearLayout) findViewById(R.id.emailfrag);
        ll.setVisibility(View.GONE);
        auth = FirebaseAuth.getInstance();

        signupInputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        signupInputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        signupInputEmail = (EditText) findViewById(R.id.input_email);
        signupInputPassword = (EditText) findViewById(R.id.input_password);

        Button btnSignUp = (Button) findViewById(R.id.btn_signup);
        Button btnLinkToLogIn = (Button) findViewById(R.id.btn_login);
        progressBar.setVisibility(View.GONE);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm(0);



            }
        });

        btnLinkToLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                submitForm(1);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout ll = (LinearLayout) findViewById(R.id.emailfrag);
                ll.setVisibility(view.VISIBLE);

                Button signup = (Button) findViewById(R.id.btn_signup);
                signup.setVisibility(view.GONE);
                Button login = (Button) findViewById(R.id.btn_login);
                login.setVisibility(view.VISIBLE);


            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout ll = (LinearLayout) findViewById(R.id.emailfrag);
                ll.setVisibility(view.VISIBLE);
                Button signup = (Button) findViewById(R.id.btn_signup);
                signup.setVisibility(view.VISIBLE);
                Button login = (Button) findViewById(R.id.btn_login);
                login.setVisibility(view.GONE);
            }
        });



    }


    private void submitForm(int i) {
        String email = signupInputEmail.getText().toString().trim();
        String password = signupInputPassword.getText().toString().trim();

        if(!checkEmail()) {
            return;
        }
        if(!checkPassword()) {
            return;
        }
        signupInputLayoutEmail.setErrorEnabled(false);
        signupInputLayoutPassword.setErrorEnabled(false);

        progressBar.setVisibility(View.VISIBLE);
        if(i==1)
        {
            auth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(Email_method.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE);
                            if(!task.isSuccessful())
                            {
                                Toast.makeText(Email_method.this,getString(R.string.auth_failed),Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                Intent intent=new Intent(Email_method.this,User_content.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
        }
        else
        {
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(Email_method.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d("Tag","createUserWithEmail:onComplete:" + task.isSuccessful());
                            progressBar.setVisibility(View.GONE);
                            // If sign in fails, Log the message to the LogCat. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Log.d("Tag","Authentication failed." + task.getException());

                            } else {
                                startActivity(new Intent(Email_method.this, User_content.class));
                                Toast.makeText(getApplicationContext(), "You are successfully Registered !!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });

        }
    }

    private boolean checkEmail() {
        String email = signupInputEmail.getText().toString().trim();
        if (email.isEmpty() || !isEmailValid(email)) {

            signupInputLayoutEmail.setErrorEnabled(true);
            signupInputLayoutEmail.setError(getString(R.string.err_msg_email));
            signupInputEmail.setError(getString(R.string.err_msg_required));
            requestFocus(signupInputEmail);
            return false;
        }
        signupInputLayoutEmail.setErrorEnabled(false);
        return true;
    }

    private boolean checkPassword() {

        String password = signupInputPassword.getText().toString().trim();
        if (password.isEmpty() || !isPasswordValid(password)) {

            signupInputLayoutPassword.setError(getString(R.string.err_msg_password));
            signupInputPassword.setError(getString(R.string.err_msg_required));
            requestFocus(signupInputPassword);
            return false;
        }
        signupInputLayoutPassword.setErrorEnabled(false);
        return true;
    }

    private static boolean isEmailValid(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private static boolean isPasswordValid(String password){
        return (password.length() >= 6);
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Email_method.this, MainActivity.class));
        finish();
    }
}
