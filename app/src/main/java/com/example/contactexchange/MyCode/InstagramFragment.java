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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class InstagramFragment extends Fragment {

    private static final String TAG = InstagramFragment.class.getName();

    EditText etInstagram;
    Button btnSend;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public InstagramFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_instagram, container, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        etInstagram = view.findViewById(R.id.etInstagram);
        btnSend = view.findViewById(R.id.btnSend);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToDb();
            }
        });
        return view;
    }

    public void addToDb() {
        ((AddActivity) getActivity()).setProgressBarVisibility(ProgressBar.VISIBLE);
        db.collection("socials").whereEqualTo("uid", mAuth.getUid()).whereEqualTo("socialId", SocialIds.INSTAGRAM.ordinal())
                .whereEqualTo("username", etInstagram.getText().toString())
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
                                        .add(new Social(SocialIds.INSTAGRAM.ordinal(), etInstagram.getText().toString(), mAuth.getUid()))
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
                                                                        list.set(SocialIds.INSTAGRAM.ordinal(), true);
                                                                        document.getReference().update("socials", list);
                                                                    }
                                                                }
                                                            }
                                                        });
                                                getActivity().setResult(SocialIds.INSTAGRAM.ordinal(), new Intent().putExtra("username", etInstagram.getText().toString()));
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
