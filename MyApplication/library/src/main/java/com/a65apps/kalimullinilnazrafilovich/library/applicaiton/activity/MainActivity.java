package com.a65apps.kalimullinilnazrafilovich.library.applicaiton.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.a65apps.kalimullinilnazrafilovich.library.applicaiton.fragments.ContactDetailsFragment;
import com.a65apps.kalimullinilnazrafilovich.library.applicaiton.fragments.ContactListFragment;
import com.a65apps.kalimullinilnazrafilovich.myapplication.R;

import moxy.MvpAppCompatActivity;

public class MainActivity extends MvpAppCompatActivity {
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    private transient boolean firstCreateMainActivity;
    private transient String id;
    private transient String details;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firstCreateMainActivity = savedInstanceState == null;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onStart() {
        super.onStart();

        requestPermission();

        details = getIntent().getStringExtra("contactDetail");
        id = getIntent().getStringExtra("id");
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermission() {
        int permissionStatus = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS);
        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            openFragment();
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                    PERMISSIONS_REQUEST_READ_CONTACTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openFragment();
            } else {
                Toast.makeText(this, R.string.deny_permission_message, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void addFragmentListContact() {
        ContactListFragment contactListFragment = new ContactListFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.content, contactListFragment).commit();
    }

    private void addFragmentContactDetail(@NonNull String id) {
        ContactDetailsFragment contactDetailsFragment = ContactDetailsFragment.newInstance(id);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, contactDetailsFragment).addToBackStack(null).commit();
    }

    void openFragment() {
        if (firstCreateMainActivity) {
            addFragmentListContact();
        }
        if (details != null) {
            addFragmentContactDetail(id);
        }
    }
}
