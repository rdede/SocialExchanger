package com.example.contactexchange.Cards;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.contactexchange.MyCode.CreationFragment;
import com.example.contactexchange.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CardsFragment extends Fragment implements CardsAdapter.OnListItemClickListener {

    private static final String TAG = CardsFragment.class.getName();

    private ArrayList<Card> mList;
    private RecyclerView mRecyclerView;
    private CardsAdapter mAdapter;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    ProgressBar pb;
    TextView tvNoCards;

    public CardsFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cards, container, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        mRecyclerView = view.findViewById(R.id.rv);
        tvNoCards = view.findViewById(R.id.tvNoCards);

        mList = new ArrayList<>();

        pb = view.findViewById(R.id.pbLoading);

        pb.setVisibility(ProgressBar.VISIBLE);
        db.collection("cards")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Boolean isEmpty = true;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Card doc = document.toObject(Card.class);
                                if (doc.getUidsAdded() != null && doc.getUidsAdded().contains(mAuth.getUid())) {
                                    mList.add(doc);
                                    isEmpty = false;
                                }
                            }
                            if(isEmpty){
                                tvNoCards.setVisibility(TextView.VISIBLE);
                            }
                            pb.setVisibility(ProgressBar.INVISIBLE);
                            mAdapter = new CardsAdapter(mList, CardsFragment.this, getContext(), mAuth.getUid());
                            mRecyclerView.setAdapter(mAdapter);
                            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        } else {
                            pb.setVisibility(ProgressBar.INVISIBLE);
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        return view;
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Intent intent = new Intent(getActivity(), CardDetailsActivity.class);
        intent.putExtra("name", mList.get(clickedItemIndex).getName());
        intent.putExtra("uid", mList.get(clickedItemIndex).getUid());
        startActivity(intent);
    }

    public void addNewCard(Card card) {
        if(mList.size() == 0){
            tvNoCards.setVisibility(TextView.GONE);
        }
        mList.add(card);
        pb.setVisibility(ProgressBar.GONE);
        mAdapter.notifyItemInserted(mList.size());
        /*db.collection("cards").document(resultUid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Card card = document.toObject(Card.class);
                                if(mList.size() == 0){
                                    tvNoCards.setVisibility(TextView.GONE);
                                }
                                mList.add(card);
                                pb.setVisibility(ProgressBar.GONE);
                                mAdapter.notifyItemInserted(mList.size());
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });*/
    }

    public void setPbVisibility(Boolean bool) {
        if(bool) {
            pb.setVisibility(ProgressBar.VISIBLE);
        } else {
            pb.setVisibility(ProgressBar.GONE);
        }
    }
}
