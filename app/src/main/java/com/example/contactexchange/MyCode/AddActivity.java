package com.example.contactexchange.MyCode;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.example.contactexchange.R;

public class AddActivity extends AppCompatActivity {

    EditText etTwitter;
    ProgressBar pb;

    final Fragment fragmentTwitter = new TwitterFragment();
    final Fragment fragmentInstagram = new InstagramFragment();
    final Fragment fragmentFacebook = new FacebookFragment();
    final android.support.v4.app.FragmentManager fm = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        pb = findViewById(R.id.pbLoading);

        Intent intent = getIntent();
        final String fragment = intent.getStringExtra("fragment");

        switch(fragment) {
            case "twitter" : fm.beginTransaction().add(R.id.add_container, fragmentTwitter, "Twitter").commit();
            break;
            case "instagram" : fm.beginTransaction().add(R.id.add_container, fragmentInstagram, "Instagram").commit();
            break;
            case "facebook" : fm.beginTransaction().add(R.id.add_container, fragmentFacebook, "Facebook").commit();
            break;
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageButton btnClose = toolbar.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(0);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragmentTwitter = getSupportFragmentManager().findFragmentByTag("Twitter");
        Fragment fragmentFacebook = getSupportFragmentManager().findFragmentByTag("Facebook");
        if (fragmentTwitter != null) {
            fragmentTwitter.onActivityResult(requestCode, resultCode, data);
        }
        if (fragmentFacebook != null) {
            fragmentFacebook.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void setProgressBarVisibility(int visibility) {
        pb.setVisibility(visibility);
    }
}
