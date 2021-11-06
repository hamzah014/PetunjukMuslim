package com.example.petunjukmuslim;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.petunjukmuslim.fragment.HomeFragment;
import com.example.petunjukmuslim.fragment.MapsFragment;
import com.example.petunjukmuslim.fragment.ProfileFragment;
import com.example.petunjukmuslim.fragment.QiblatFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.security.acl.Group;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }else
            {
                //set default when fragment when apps start
                getSupportFragmentManager().beginTransaction().replace(R.id.fl_wrapper,new HomeFragment()).commit();
            }

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemReselectedListener(bottomNavMethod);


    }

    private BottomNavigationView.OnNavigationItemReselectedListener bottomNavMethod = new BottomNavigationView.OnNavigationItemReselectedListener() {
        @Override
        public void onNavigationItemReselected(@NonNull MenuItem item) {

            Fragment fragment = null;

            switch (item.getItemId()){

                case R.id.ic_home:
                    fragment = new HomeFragment();
                    break;

                case R.id.ic_map:
                    fragment = new MapsFragment();
                    break;

                case R.id.ic_compass:
                    fragment = new QiblatFragment();
                    break;

                case R.id.ic_profile:
                    fragment = new ProfileFragment();
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fl_wrapper,fragment).commit();

        }
    };


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sign_out) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            return true;
        }

        return true;

    }


}