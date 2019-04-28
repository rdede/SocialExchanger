package com.example.contactexchange.MyCode;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.contactexchange.Cards.Card;
import com.example.contactexchange.R;
import com.example.contactexchange.SocialIds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CreationFragment extends Fragment implements SocialAdapter.OnListItemClickListener {

    /*private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;*/

    private static final String TAG = CreationFragment.class.getName();

    private ArrayList<Social> mList;
    private RecyclerView mRecyclerView;
    private SocialAdapter mAdapter;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    ProgressBar pb;
    TextView tvNoSocials;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_creation, container, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        mRecyclerView = view.findViewById(R.id.rv);

        tvNoSocials = view.findViewById(R.id.tvNoSocials);

        mList = new ArrayList<Social>();

        pb = view.findViewById(R.id.pbLoading);

        pb.setVisibility(ProgressBar.VISIBLE);
        db.collection("socials")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Boolean isEmpty = true;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Social doc = document.toObject(Social.class);
                                if (doc.getUid() != null && doc.getUid().equals(mAuth.getUid())) {
                                    mList.add(doc);
                                    isEmpty = false;
                                }
                            }
                            if(isEmpty){
                                tvNoSocials.setVisibility(TextView.VISIBLE);
                            }
                            pb.setVisibility(ProgressBar.INVISIBLE);
                            mAdapter = new SocialAdapter(mList, CreationFragment.this, getActivity());
                            mAdapter.setDeleteBtnVisibility(true);
                            mRecyclerView.setAdapter(mAdapter);
                            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        } else {
                            pb.setVisibility(ProgressBar.INVISIBLE);
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        FloatingActionButton fabAdd = view.findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSocial();
            }
        });

        return view;
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        switch (mList.get(clickedItemIndex).getSocialId()) {
            case 0:
                Uri uriTwitter = Uri.parse("twitter://user?user_id=" + mList.get(clickedItemIndex).getId());
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
                Log.d("coucou", mList.get(clickedItemIndex).getId());
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
    public void onDeleteClick(final int clickerItemIndex) {
        pb.setVisibility(ProgressBar.VISIBLE);
        db.collection("socials")
                .whereEqualTo("username", mList.get(clickerItemIndex).getUsername())
                .whereEqualTo("socialId", mList.get(clickerItemIndex).getSocialId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                pb.setVisibility(ProgressBar.INVISIBLE);
                                document.getReference().delete();
                                int socialIdDeleted = mList.get(clickerItemIndex).getSocialId();
                                mList.remove(clickerItemIndex);
                                mAdapter.notifyItemRemoved(clickerItemIndex);
                                db.collection("socials").whereEqualTo("uid", mAuth.getUid())
                                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            ArrayList<Boolean> socials = new ArrayList<>();
                                            socials.add(false);
                                            socials.add(false);
                                            socials.add(false);
                                            for (QueryDocumentSnapshot document : task.getResult())  {
                                                Social social = document.toObject(Social.class);
                                                switch(social.getSocialId()){
                                                    case 0 : socials.set(SocialIds.TWITTER.ordinal(), true);
                                                        break;
                                                    case 1 : socials.set(SocialIds.INSTAGRAM.ordinal(), true);
                                                        break;
                                                    case 2 : socials.set(SocialIds.FACEBOOK.ordinal(), true);
                                                        break;
                                                }
                                            }
                                            db.collection("cards").document(mAuth.getUid())
                                                    .update("socials", socials);
                                        }
                                    }
                                });
                                if (mList.isEmpty()) {
                                    tvNoSocials.setVisibility(TextView.VISIBLE);
                                }
                            }
                        } else {
                            pb.setVisibility(ProgressBar.INVISIBLE);
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
        CodeViewerFragment.getInstance().deleteElement(mList.get(clickerItemIndex).getSocialId(), mList.get(clickerItemIndex).getUsername());
    }


    public void addSocial() {
        tvNoSocials.setVisibility(TextView.GONE);

        final String[] listSocials = new String[]{"Twitter", "Instagram", "Facebook"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Choose")
                .setItems(listSocials, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(getActivity(), AddActivity.class);
                        switch (listSocials[which]) {
                            case "Twitter":
                                i.putExtra("fragment", "twitter");
                                startActivityForResult(i, SocialIds.TWITTER.ordinal());
                                break;
                            case "Instagram":
                                i.putExtra("fragment", "instagram");
                                startActivityForResult(i, SocialIds.INSTAGRAM.ordinal());
                                break;
                            case "Facebook":
                                i.putExtra("fragment", "facebook");
                                startActivityForResult(i, SocialIds.FACEBOOK.ordinal());
                                break;
                        }
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String username;
        switch (resultCode) {
            case 0:
                username = data.getStringExtra("username");
                mList.add(new Social(SocialIds.TWITTER.ordinal(), username, ""));
                mAdapter.notifyItemInserted(mList.size());
                CodeViewerFragment.getInstance().addElement(1, username);
                break;
            case 1:
                username = data.getStringExtra("username");
                mList.add(new Social(SocialIds.INSTAGRAM.ordinal(), username, ""));
                mAdapter.notifyItemInserted(mList.size());
                CodeViewerFragment.getInstance().addElement(2, username);
                break;
            case 2:
                username = data.getStringExtra("username");
                mList.add(new Social(SocialIds.FACEBOOK.ordinal(), username, ""));
                mAdapter.notifyItemInserted(mList.size());
                CodeViewerFragment.getInstance().addElement(3, username);
                break;
        }
    }
}
