package application.pooja.com.vedioplayer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import application.pooja.com.vedioplayer.activities.VedioActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 1;
    private SignInButton signInButton;
    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                Log.d("VT","current user: "+user);
//                if(firebaseAuth.getCurrentUser()!= null) {
//                    Intent intent = new Intent(getApplicationContext(),VedioActivity.class);
//                    startActivity(intent);
//                }
//                if(firebaseAuth.getCurrentUser() == null)
//                {
//                    Toast.makeText(getApplicationContext(),"You have logged out",Toast.LENGTH_LONG).show();
//                }
//            }
//        };

        signInButton = findViewById(R.id.google);
        signInButton.setOnClickListener(this);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        Log.d("VT","After GoogleSignInOptions");

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(LoginActivity.this,"You got an error",Toast.LENGTH_LONG).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
        Log.d("VT","After mGoogleApiClient");
        mGoogleApiClient.connect();

    }



    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.google) {
            signIn();
            Toast.makeText(getApplicationContext(), "Signin Clicked", Toast.LENGTH_LONG).show();
        }

    }


        @Override
    protected void onStart() {
        super.onStart();
            if(mAuth.getCurrentUser()!= null)
            {
                finish();
                startActivity(new Intent(getApplicationContext(),VedioActivity.class));
            }
    }
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN)
        {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess())
            {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }
            else{

            }
        }
    }
//
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        final AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("VT","SignInWithCredential : on Complete: "+task.isSuccessful());
                        if(task.isSuccessful())
                        {
                            Toast.makeText(LoginActivity.this,"User signedIn  ",Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(),VedioActivity.class);
                            startActivity(intent);
                            finish();
                        }
                      else  if(!task.isSuccessful())
                        {
                            Log.d("VT","SignInWithCredential",task.getException());
                            Toast.makeText(LoginActivity.this,"Authentication Failed ",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
//}


}