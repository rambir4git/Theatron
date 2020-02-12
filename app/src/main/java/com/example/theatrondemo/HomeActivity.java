package com.example.theatrondemo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.karan.churi.PermissionManager.PermissionManager;

public class HomeActivity extends AppCompatActivity {

    private HomeFragment homeFragment;
    private ProfileFragment profileFragment;
    private FirebaseAuth homeAuth;
    private PermissionManager permissionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.home_toolbar);
        FloatingActionButton cameraBtn = findViewById(R.id.cameraBtn);

        permissionManager = new PermissionManager() {
        };
        permissionManager.checkAndRequestPermissions(this);

        setSupportActionBar(toolbar);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // FRAGMENTS
        homeFragment = new HomeFragment();
        profileFragment = new ProfileFragment();
        ChangeFragment(homeFragment);
        homeAuth = FirebaseAuth.getInstance();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        ChangeFragment(homeFragment);
                        return true;
                    case R.id.profile:
                        ChangeFragment(profileFragment);
                        return true;
                    default:
                        return false;
                }

            }
        });
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, 123);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == RESULT_OK) {
            Uri selectedMediaUri = data.getData();
            Bundle args = new Bundle();
            HomeFragment fragment = new HomeFragment();
            if (selectedMediaUri.toString().contains("image")) {
                args.putParcelable("Uri", selectedMediaUri);
                fragment.setArguments(args);
                ChangeFragment(fragment);
            }
        }
    }

    @Override
    protected void onResume() {
        ChangeFragment(homeFragment);
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_logout) {
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.putExtra("LOGOUT", true);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    private void ChangeFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frames, fragment);
        fragmentTransaction.commit();
    }

}
