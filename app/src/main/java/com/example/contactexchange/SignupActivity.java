package com.example.contactexchange;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.contactexchange.Cards.Card;
import com.example.contactexchange.MyCode.Social;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = SignupActivity.class.getName();

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    ProgressBar pb;
    ScrollView viewLayout;

    EditText etEmail;
    EditText etPassword;
    EditText etName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etName = findViewById(R.id.etName);

        pb = findViewById(R.id.pbLoading);
        viewLayout = findViewById(R.id.viewScroll);
    }

    public void signup(View view) {
        viewLayout.setVisibility(ScrollView.GONE);
        pb.setVisibility(ProgressBar.VISIBLE);

        final String email = etEmail.getText().toString();
        final String password = etPassword.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            db = FirebaseFirestore.getInstance();
                            ArrayList<Boolean> socials = new ArrayList<>();
                            socials.add(false);
                            socials.add(false);
                            socials.add(false);
                            db.collection("cards").document(mAuth.getUid())
                                    .set(new Card(etName.getText().toString(), mAuth.getUid(), socials, new ArrayList<>()));
                            String filename = mAuth.getUid();
                            new File(getApplication().getFilesDir(), filename);
                            login(email, password);
                        } else {
                            viewLayout.setVisibility(ScrollView.VISIBLE);
                            pb.setVisibility(ProgressBar.GONE);
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignupActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);
                        } else {
                            viewLayout.setVisibility(ScrollView.GONE);
                            pb.setVisibility(ProgressBar.VISIBLE);
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(SignupActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
}
