package application.pooja.com.vedioplayer.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import application.pooja.com.vedioplayer.LoginActivity;
import application.pooja.com.vedioplayer.R;
import application.pooja.com.vedioplayer.adapter.myAdapter;
import application.pooja.com.vedioplayer.application.myapplication;
import application.pooja.com.vedioplayer.model.Example;

public class VedioActivity extends AppCompatActivity implements myAdapter.ItemClickListener {

    boolean doubleBackToExitPressedOnce = false;

        Toolbar toolbar;
    FirebaseAuth mAuth;
    RequestQueue requestQueue;
    ArrayList<Example> posts;
    myAdapter mAdapter;
    GoogleApiClient mGoogleApiClient;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vedio);
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        recyclerView =findViewById(R.id.recyclerView);
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(VedioActivity.this,"You got an error",Toast.LENGTH_LONG).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
        Log.d("VT","After mGoogleApiClient");
        mGoogleApiClient.connect();

        requestQueue = Volley.newRequestQueue(this);
        mAuth = FirebaseAuth.getInstance();
        if(myapplication.getExamples()==null)
        {
            new getVediosFromServer().execute();
        }
        else
        {
            setAdapterOnRecyclerView();
        }
       
        Log.d("VT", "inside oncreate: ");
    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.menu_logout:
//                Toast.makeText(this, "Logout", Toast.LENGTH_LONG).show();
//                mAuth.signOut();
//
//                // Google sign out
//                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
//                        new ResultCallback<Status>() {
//                            @Override
//                            public void onResult(@NonNull Status status) {
//
//                                Toast.makeText(VedioActivity.this, "User Logout", Toast.LENGTH_LONG).show();
//                            }
//                        });
//                finish();
//                startActivity(new Intent(VedioActivity.this,LoginActivity.class));
//                break;
//            default:
//                break;
//        }
//        return true;
//    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main_manu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
    private void setAdapterOnRecyclerView() {
        mAdapter = new myAdapter(myapplication.getExamples(),this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter.setClickListener(this);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
    }

    @Override
    public void onItemClick(View view,String id) {
        Bundle bundle = new Bundle();
        bundle.putString("PLAYVEDIOID",id);
        Log.d("VT","id of vedio in vedioActivity: "+id);

        Intent intent = new Intent(VedioActivity.this,ViewVedioActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);


    }

    private class getVediosFromServer extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog = new ProgressDialog(VedioActivity.this);

        @Override
        protected void onPreExecute() {
            Log.d("VT", "inside pre Response: ");
            super.onPreExecute();
            dialog.setMessage("Please wait.");
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
            Log.d("VT", "inside post Response: ");
        }

        @Override
        protected Void doInBackground(Void... voids) {

            String url = "https://interview-e18de.firebaseio.com/media.json?print=pretty";
            Log.d("VT", "inside do Response: " + url);
            final JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    Log.d("VT", "jsonresponse: " + response);

                    Type listType = new TypeToken<List<Example>>() {
                    }.getType();
                    posts = new Gson().fromJson(response.toString(), listType);

                    myapplication.setExamples(posts);
                    for (int i = 0; i < posts.size(); i++) {
                        Log.d("VT", "response one by one :" + posts.get(i).getDescription());
                    }
                    setAdapterOnRecyclerView();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("VT", "inside error Response: " + error);
                    error.printStackTrace();
                }
            });
            requestQueue.add(request);
            return null;
        }
    }

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
