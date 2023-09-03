
package com.example.nfcconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class loginActivity extends AppCompatActivity {
    EditText mUsername,mPassword;
    Button mLoginBtn;
    ProgressBar progressBar;
    FirebaseDatabase rootNode;
    DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
    OkHttpClient okHttpClient=new OkHttpClient();
    String txtResponse,getKey;
    String encPassword,encNFCpass,getPassword,pass, uName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mUsername=findViewById(R.id.l_uName);
        mPassword=findViewById(R.id.l_password);
        mLoginBtn=findViewById(R.id.loginBtn);





        mPassword.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    String l_Name=mUsername.getText().toString();
                    uName=l_Name;
                    reference.child("app").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild(l_Name)) {
                                getPassword=snapshot.child(l_Name).child("password").getValue(String.class);
                                getKey=snapshot.child(l_Name).child("key").getValue(String.class);


                                ///flask part
                                RequestBody formbody=new FormBody.Builder().add("password",getPassword).add("enckey",getKey).build();
                                Request request=new Request.Builder().url("https://karthik022.pythonanywhere.com/dcrypt").post(formbody).build();
                                Toast.makeText(loginActivity.this, "Decrypting...", Toast.LENGTH_SHORT).show();
                                okHttpClient.newCall(request).enqueue(new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(loginActivity.this, "Flask response error", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {

                                                    txtResponse= (response.body().string());
                                                    Toast.makeText(loginActivity.this, txtResponse, Toast.LENGTH_SHORT).show();
                                                    String in=txtResponse;
                                                    JSONObject reader = new JSONObject(in);
                                                    encPassword = reader.getString("password");
                                                    pass=encPassword.toString();
                                                } catch (IOException | JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });

                                    }
                                });


                            }
                            else{
                                Toast.makeText(loginActivity.this, "Wrong Username !", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                    return true;
                }
                return false;
            }
        });



        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String l_password=mPassword.getText().toString();
                if(l_password.equals(pass))
                {
                    Toast.makeText(loginActivity.this, "Successfully logged in!", Toast.LENGTH_SHORT).show();
                    //opening new Activity
                    Intent intent=new Intent(loginActivity.this,Homepage.class);
                    intent.putExtra("username",uName);
                    startActivity(intent);

                    finish();
                }
                else {
                    Toast.makeText(loginActivity.this, "Wrong Password !", Toast.LENGTH_SHORT).show();
                }

                if(uName.isEmpty()){
                    Toast.makeText(loginActivity.this, "Please enter username !", Toast.LENGTH_SHORT).show();
                }
                else{}
                if(l_password.isEmpty()){
                    Toast.makeText(loginActivity.this,"Please enter password !",Toast.LENGTH_SHORT).show();
                }
                else{}

            }
        });
    }
}