package com.permission;

import android.Manifest;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    String[] PERMISSIONS_ARRAY = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS};


    private static final int MULTI_PERMISSION_CALLBACK_CODE = 102;
    private static final int EXTERNAL_STORAGE_PERMISSION_CODE = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    Button btnSinglePermission;
    Button btnMultiplePermission;
    Button btnCallFragment;
    private boolean sentToSettings = false;
    private SharedPreferences permissionStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissionStatus = getSharedPreferences("permissionStatus",MODE_PRIVATE);

        btnSinglePermission = (Button)findViewById(R.id.btn_single_permission);
        btnMultiplePermission = findViewById(R.id.btn_multiple_permission);
        btnCallFragment=findViewById(R.id.btn_call_Fragment);
        btnCallFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new FragmentPermission();
                android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
                transaction.add(R.id.fragment_container,fragment,"");
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        btnSinglePermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(checkWriteExternalStoragePermission()){
                        Toast.makeText(MainActivity.this," Permission is already granted : ",Toast.LENGTH_SHORT).show();
                    }else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            //Show Information about why you need the permission
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("Need Storage Permission");
                            builder.setMessage("This app needs storage permission.");
                            builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    requestWriteExternalStoragePermission();
                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            builder.show();
                        } else if (permissionStatus.getBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE,false)) {
                            //Previously Permission Request was cancelled with 'Dont Ask Again',
                            // Redirect to Settings after showing Information about why you need the permission
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("Need Storage Permission");
                            builder.setMessage("This app needs storage permission.");
                            builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    sentToSettings = true;
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                                    Toast.makeText(getBaseContext(), "Go to Permissions to Grant Storage", Toast.LENGTH_LONG).show();
                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            builder.show();
                        }else {
                            requestWriteExternalStoragePermission();
                        }

                    }
            }
        });
        btnMultiplePermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(checkMultiPermission()) {
                        Toast.makeText(MainActivity.this," All Permission are already granted : ",Toast.LENGTH_SHORT).show();
                    }else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, PERMISSIONS_ARRAY[0])
                                || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, PERMISSIONS_ARRAY[1])
                                || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, PERMISSIONS_ARRAY[2])
                                || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, PERMISSIONS_ARRAY[3])
                                || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, PERMISSIONS_ARRAY[4])) {
                            //Show Information about why you need the permission
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("Need Multiple Permissions");
                            builder.setMessage("This app needs Camera and Location permissions.");
                            builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_ARRAY, MULTI_PERMISSION_CALLBACK_CODE);
                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            builder.show();
                        } else if (permissionStatus.getBoolean(PERMISSIONS_ARRAY[0], false)) {
                            //Previously Permission Request was cancelled with 'Dont Ask Again',
                            // Redirect to Settings after showing Information about why you need the permission
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("Need Multiple Permissions");
                            builder.setMessage("This app needs Camera and Location permissions.");
                            builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    sentToSettings = true;
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                                    Toast.makeText(getBaseContext(), "Go to Permissions to Grant  Camera and Location", Toast.LENGTH_LONG).show();
                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            builder.show();
                        } else {
                            //just request the permission
                            requestMultiplePermission();
                        }
                        SharedPreferences.Editor editor = permissionStatus.edit();
                        editor.putBoolean(PERMISSIONS_ARRAY[0],true);
                        editor.commit();
                    }

            }
        });
    }
    private boolean checkWriteExternalStoragePermission(){
        int result = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(Build.VERSION.SDK_INT >= 23) {
            if (result == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        }else {
            return true;
        }
    }
    private boolean checkMultiPermission(){
        if(Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, PERMISSIONS_ARRAY[0]) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(MainActivity.this, PERMISSIONS_ARRAY[1]) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(MainActivity.this, PERMISSIONS_ARRAY[2]) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(MainActivity.this, PERMISSIONS_ARRAY[3]) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(MainActivity.this, PERMISSIONS_ARRAY[4]) != PackageManager.PERMISSION_GRANTED) {
                    return false;
            }else {
                return true;
            }
        }else {
            return true;
        }
    }

    private void requestWriteExternalStoragePermission(){
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CODE);
    }
    private void requestMultiplePermission(){
        ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_ARRAY, MULTI_PERMISSION_CALLBACK_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == EXTERNAL_STORAGE_PERMISSION_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this,"Permission is now granted ",Toast.LENGTH_SHORT).show();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    //Show Information about why you need the permission
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Need Storage Permission");
                    builder.setMessage("This app needs storage permission");
                    builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            requestWriteExternalStoragePermission();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else {
                    requestWriteExternalStoragePermission();
                    Toast.makeText(getBaseContext(),"Unable to get Permission",Toast.LENGTH_LONG).show();
                }
            }
        }
        else if(requestCode == MULTI_PERMISSION_CALLBACK_CODE){
            //check if all permissions are granted
            boolean allgranted = false;
            for(int i=0;i<grantResults.length;i++){
                if(grantResults[i]==PackageManager.PERMISSION_GRANTED){
                    allgranted = true;
                } else {
                    allgranted = false;
                    break;
                }
            }

            if(allgranted){
                Toast.makeText(MainActivity.this,"All permission are granted",Toast.LENGTH_SHORT).show();
            } else if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,PERMISSIONS_ARRAY[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,PERMISSIONS_ARRAY[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,PERMISSIONS_ARRAY[2])
                    || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,PERMISSIONS_ARRAY[3])
                    || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,PERMISSIONS_ARRAY[4])){

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Need Multiple Permissions");
                        builder.setMessage("This app needs Camera and Location permissions.");
                        builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                ActivityCompat.requestPermissions(MainActivity.this,PERMISSIONS_ARRAY,MULTI_PERMISSION_CALLBACK_CODE);
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.show();
            } else {
                Toast.makeText(getBaseContext(),"Unable to get Permission",Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                Toast.makeText(MainActivity.this,"Permission got from setting :",Toast.LENGTH_SHORT).show();
            }
        }
    }

}
