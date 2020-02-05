package com.example.theatrondemo;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.VideoResult;
import com.otaliastudios.cameraview.controls.Facing;
import com.otaliastudios.cameraview.controls.Flash;
import com.otaliastudios.cameraview.controls.Mode;

import java.io.File;
import java.util.Calendar;

public class CameraActivity extends AppCompatActivity {

    private CameraView cameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        Toolbar toolbar = findViewById(R.id.cameraToolbar);
        FloatingActionButton cameraBtn = findViewById(R.id.startBtn);
        setSupportActionBar(toolbar);
        cameraView = findViewById(R.id.cameraView);
        cameraView.setMode(Mode.VIDEO);
        cameraView.setFlash(Flash.OFF);
        cameraView.setFacing(Facing.BACK);
        cameraView.setUseDeviceOrientation(true);
        cameraView.setLifecycleOwner(this);

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cameraView.isTakingVideo()) {
                    cameraView.stopVideo();
                } else {
                    cameraView.takeVideoSnapshot(new File(getExternalFilesDir(null), Calendar.getInstance().getTimeInMillis() + ".MP4"));
                    //cameraView.takeVideo(new File(getExternalFilesDir(null), Calendar.getInstance().getTimeInMillis()+".MP4"));
                }
            }
        });


        cameraView.addCameraListener(new CameraListener() {
            @Override
            public void onVideoTaken(@NonNull VideoResult result) {
                super.onVideoTaken(result);
                Toast.makeText(CameraActivity.this, result.getFile().getAbsolutePath(), Toast.LENGTH_SHORT).show();
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.camera_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.flash:
                if (item.getIcon().equals(ContextCompat.getDrawable(CameraActivity.this, R.drawable.ic_flash_off_black_24dp))) {
                    item.setIcon(R.drawable.ic_flash_on_black_24dp);
                    cameraView.setFlash(Flash.ON);
                    Toast.makeText(this, "Flash ON", Toast.LENGTH_SHORT).show();
                } else {
                    item.setIcon(R.drawable.ic_flash_off_black_24dp);
                    cameraView.setFlash(Flash.OFF);
                    Toast.makeText(this, "Flash OFF", Toast.LENGTH_SHORT).show();
                }
                break;
            /*
            case R.id.mode:
                if(item.getIcon().equals(ContextCompat.getDrawable(CameraActivity.this,R.drawable.ic_camera_alt_black_24dp))){
                    item.setIcon(R.drawable.ic_videocam_black_24dp);
                    cameraView.setMode(Mode.VIDEO);
                }
                else {
                    item.setIcon(R.drawable.ic_camera_alt_black_24dp);
                    cameraView.setMode(Mode.PICTURE);
                }
             */
            case R.id.flip:
                if (item.getIcon().equals(ContextCompat.getDrawable(CameraActivity.this, R.drawable.ic_camera_front_black_24dp))) {
                    item.setIcon(R.drawable.ic_camera_rear_black_24dp);
                    cameraView.setFacing(Facing.BACK);
                } else {
                    item.setIcon(R.drawable.ic_camera_front_black_24dp);
                    cameraView.setFacing(Facing.FRONT);
                }
        }
        return super.onOptionsItemSelected(item);
    }

}
