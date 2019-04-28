package com.example.contactexchange.Cards;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.example.contactexchange.MyCode.CreationFragment;
import com.example.contactexchange.MyCode.Social;
import com.example.contactexchange.MyCode.SocialAdapter;
import com.example.contactexchange.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CardDetailsActivity extends AppCompatActivity implements SocialAdapter.OnListItemClickListener {

    private ArrayList<Social> mList;
    private RecyclerView mRecyclerView;
    private SocialAdapter mAdapter;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_details);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        pb = findViewById(R.id.pbLoading);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String uid = intent.getStringExtra("uid");

        mRecyclerView = findViewById(R.id.rv);
        mList = new ArrayList<Social>();

        pb.setVisibility(ProgressBar.VISIBLE);
        db.collection("socials").whereEqualTo("uid", uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Social doc = document.toObject(Social.class);
                                mList.add(doc);
                            }
                            pb.setVisibility(ProgressBar.INVISIBLE);
                            mAdapter = new SocialAdapter(mList, CardDetailsActivity.this, CardDetailsActivity.this);
                            mAdapter.setDeleteBtnVisibility(false);
                            mRecyclerView.setAdapter(mAdapter);
                            mRecyclerView.setLayoutManager(new LinearLayoutManager(CardDetailsActivity.this));
                        }
                    }
                });

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView tvToolbarTitle = toolbar.findViewById(R.id.toolbar_title);
        tvToolbarTitle.setText(name);
        setSupportActionBar(toolbar);

        ImageButton btnClose = toolbar.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        switch (mList.get(clickedItemIndex).getSocialId()) {
            case 0:Uri uriTwitter = Uri.parse("twitter://user?user_id=" + mList.get(clickedItemIndex).getId());
                Intent twitterIntent = new Intent(Intent.ACTION_VIEW, uriTwitter);

                twitterIntent.setPackage("com.twitter.android");

                try {
                    startActivity(twitterIntent);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://twitter.com/" + mList.get(clickedItemIndex).getUsername())));
                }
                break;
            case 1:
                Uri uriInstagram = Uri.parse("http://instagram.com/_u/" + mList.get(clickedItemIndex).getUsername());
                Intent instagramIntent = new Intent(Intent.ACTION_VIEW, uriInstagram);

                instagramIntent.setPackage("com.instagram.android");

                try {
                    startActivity(instagramIntent);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://instagram.com/" + mList.get(clickedItemIndex).getUsername())));
                }
                break;
            case 2 :
                Uri uriFacebook = Uri.parse("fb://profile?app_scoped_user_id=%@"+mList.get(clickedItemIndex).getId());
                Intent facebookIntent = new Intent(Intent.ACTION_VIEW, uriFacebook);

                facebookIntent.setPackage("com.facebook.katana");

                try {
                    startActivity(facebookIntent);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://facebook.com/" + mList.get(clickedItemIndex).getUsername())));
                }
                break;
        }
    }

    @Override
    public void onDeleteClick(int clickerItemIndex) {
    }
}
