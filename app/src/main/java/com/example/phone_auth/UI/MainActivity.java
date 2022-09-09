package com.example.phone_auth.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.phone_auth.R;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUserStatus();
    }

    private void checkUserStatus() {
        SharedPreferences sharedPreferences=getSharedPreferences("logindata",MODE_PRIVATE);
        Boolean counter=sharedPreferences.getBoolean("logincounter",Boolean.valueOf(String.valueOf(MODE_PRIVATE)));
        if(!counter){
            startActivity(new Intent(MainActivity.this, Login.class));
            finish();
        }
    }


    // code for Logout option
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu , menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            mAuth=FirebaseAuth.getInstance();
            mAuth.signOut();

            SharedPreferences sharedPreferences=getSharedPreferences("logindata",MODE_PRIVATE);
            sharedPreferences.edit().clear().commit();

            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            finish();
        }
        return true;
    }
}