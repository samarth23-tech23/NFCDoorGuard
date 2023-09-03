package com.example.nfcconnect;
import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.OkHttpClient;

public class Homepage extends AppCompatActivity {
    TextView Tmonth, Tday, Tyear, tname;
    FirebaseDatabase rootNode;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    OkHttpClient okHttpClient = new OkHttpClient();
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        Tmonth = findViewById(R.id.month);
        Tday = findViewById(R.id.date);
        Tyear = findViewById(R.id.year);
        tname = findViewById(R.id.tvName);
        Date currentTime = Calendar.getInstance().getTime();

        // Format the date using SimpleDateFormat with desired format
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM, dd, yyyy", Locale.US);
        String formattedDate = sdf.format(currentTime);

        String[] splitDate = formattedDate.split(",");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            name = extras.getString("username");
        }

        tname.setText("Welcome back, " + name);

        Tmonth.setText(splitDate[0].trim()); // Month
        Tday.setText(splitDate[1].trim());   // Day
        Tyear.setText(splitDate[2].trim());  // Year
    }

    public void logout(View c) {
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
    }

    public void reset(View v) {
        Intent intent = new Intent(Homepage.this, reset.class);
        startActivity(intent);
    }

    public void log(View r) {
        Intent intent = new Intent(getApplicationContext(), LogActivity.class);
        intent.putExtra("username", name);
        startActivity(intent);
    }
}
