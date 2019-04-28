package com.example.contactexchange.MyCode;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.contactexchange.Cards.Card;
import com.example.contactexchange.R;
import com.example.contactexchange.SocialIds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class TwitterFragment extends Fragment {

    private static final String TAG = TwitterFragment.class.getName();

    private TwitterLoginButton btnTwitterLogin;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public TwitterFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        TwitterConfig config = new TwitterConfig.Builder(getContext())
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(getResources().getString(R.string.com_twitter_sdk_android_CONSUMER_KEY), getResources().getString(R.string.com_twitter_sdk_android_CONSUMER_SECRET)))
                .debug(true)
                .build();

        Twitter.initialize(config);
        View view = inflater.inflate(R.layout.fragment_twitter, container, false);


        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        btnTwitterLogin = view.findViewById(R.id.btnTwitterLogin);

        btnTwitterLogin.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a TwitterSession for making API calls
                TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                TwitterAuthToken authToken = session.getAuthToken();
                addToDb(session.getUserName(), Long.toString(session.getUserId()));
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
                Toast.makeText(getActivity(), "Login fail", Toast.LENGTH_LONG).show();
            }
        });


        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        btnTwitterLogin.onActivityResult(requestCode, resultCode, data);
    }

    public void addToDb(String username, String twitterUid) {
        ((AddActivity) getActivity()).setProgressBarVisibility(ProgressBar.VISIBLE);
        db.collection("socials").whereEqualTo("uid", mAuth.getUid()).whereEqualTo("socialId", SocialIds.TWITTER.ordinal())
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                ((AddActivity) getActivity()).setProgressBarVisibility(ProgressBar.INVISIBLE);
                                Toast.makeText(getActivity(), "Social media already added", Toast.LENGTH_SHORT).show();
                                getActivity().setResult(100);
                                getActivity().finish();
                            } else {
                                db.collection("socials")
                                        .add(new Social(SocialIds.TWITTER.ordinal(), username, twitterUid, mAuth.getUid()))
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                ((AddActivity) getActivity()).setProgressBarVisibility(ProgressBar.INVISIBLE);
                                                Toast.makeText(getActivity(), "Added successfully.", Toast.LENGTH_SHORT).show();
                                                db.collection("cards").document(mAuth.getUid())
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    DocumentSnapshot document = task.getResult();
                                                                    if (document.exists()) {
                                                                        Card doc = document.toObject(Card.class);
                                                                        ArrayList<Boolean> list = doc.getSocials();
                                                                        list.set(SocialIds.TWITTER.ordinal(), true);
                                                                        document.getReference().update("socials", list);
                                                                    }
                                                                }
                                                            }
                                                        });
                                                getActivity().setResult(SocialIds.TWITTER.ordinal(), new Intent().putExtra("username", username));
                                                getActivity().finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                ((AddActivity) getActivity()).setProgressBarVisibility(ProgressBar.INVISIBLE);
                                                Toast.makeText(getActivity(), "Adding failed.", Toast.LENGTH_SHORT).show();
                                                getActivity().setResult(100);
                                                getActivity().finish();
                                            }
                                        });
                            }
                        }
                    }
                });
    }
}
