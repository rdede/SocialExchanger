package com.example.contactexchange.MyCode;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.contactexchange.Cards.Card;
import com.example.contactexchange.R;
import com.example.contactexchange.SocialIds;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class FacebookFragment extends Fragment {

    private static final String TAG = FacebookFragment.class.getName();

    private LoginButton btnFbLogin;
    private CallbackManager callbackManager;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public FacebookFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_facebook, container, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        callbackManager = CallbackManager.Factory.create();

        btnFbLogin = view.findViewById(R.id.btnFbLogin);
        btnFbLogin.setReadPermissions("email");
        btnFbLogin.setFragment(this);

        btnFbLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                URL url = createURL("https://graph.facebook.com/me?access_token=" + loginResult.getAccessToken().getToken());
                new AsyncTaskGet().execute(url, url, url);
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "cancel");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d(TAG, exception.toString());
            }
        });


        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    public void addToDb(String username, String facebookUid) {
        //((AddActivity)getActivity()).setProgressBarVisibility(ProgressBar.VISIBLE);
        db.collection("socials").whereEqualTo("uid", mAuth.getUid()).whereEqualTo("socialId", SocialIds.FACEBOOK.ordinal())
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
                                        .add(new Social(SocialIds.FACEBOOK.ordinal(), username, facebookUid, mAuth.getUid()))
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
                                                                        list.set(SocialIds.FACEBOOK.ordinal(), true);
                                                                        document.getReference().update("socials", list);
                                                                    }
                                                                }
                                                            }
                                                        });
                                                getActivity().setResult(SocialIds.FACEBOOK.ordinal(), new Intent().putExtra("username", username));
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

    public String readStream(InputStream in) throws IOException {
        StringBuilder output = new StringBuilder();
        if (in != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(in, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


    public URL createURL(String string) {
        URL url = null;
        try {
            url = new URL(string);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public String makeHttpRequest(URL url) throws IOException {
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        String jsonResponse = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(1000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readStream(inputStream);
            } else {
                Log.d(TAG, Integer.toString(urlConnection.getResponseCode()));
                return "error";
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private class AsyncTaskGet extends AsyncTask<URL, String, String> {

        @Override
        protected String doInBackground(URL... urls) {
            try {
                parseJson(makeHttpRequest(urls[0]));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void parseJson(String json) {
        try {
            JSONObject user = new JSONObject(json);
            String name = user.getString("name");
            String id = user.getString("id");
            addToDb(name, id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
