package com.example.contactexchange.MyCode;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.contactexchange.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;

public class CodeViewerFragment extends Fragment {

    private static final String TAG = CodeViewerFragment.class.getName();

    ImageView ivQr;
    ProgressBar pb;

    RelativeLayout layout;
    TextView tvNoSocials;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private ArrayList<String> listStrings = new ArrayList<>();

    private static CodeViewerFragment instance = null;

    public CodeViewerFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_code_viewer, container, false);

        instance = this;

        ivQr = view.findViewById(R.id.ivQrCode);
        pb = view.findViewById(R.id.pbLoading);

        layout = view.findViewById(R.id.layoutRelative);
        tvNoSocials = view.findViewById(R.id.tvNoSocials);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(mAuth.getUid(), BarcodeFormat.QR_CODE, 500, 500);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            ivQr.setVisibility(ImageView.VISIBLE);
            ivQr.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        /*pb.setVisibility(ProgressBar.VISIBLE);
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
                                    isEmpty = false;
                                    switch (doc.getSocialId()) {
                                        case 1:
                                            listStrings.add("twitter:" + doc.getUsername() + ';');
                                            break;
                                        case 2:
                                            listStrings.add("instagram:" + doc.getUsername() + ';');
                                            break;
                                        case 3:
                                            listStrings.add("facebook:" + doc.getUsername() + ';');
                                            break;
                                    }
                                }
                            }
                            pb.setVisibility(ProgressBar.INVISIBLE);
                            if(!isEmpty) {
                                generateQr();
                            } else {
                                tvNoSocials.setVisibility(TextView.VISIBLE);
                            }
                        } else {
                            pb.setVisibility(ProgressBar.INVISIBLE);
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });*/

        return view;
    }

    public void generateQr() {
        /*if (!listStrings.isEmpty()) {
            String qrString = "";
            for (String string : listStrings) {
                qrString = qrString.concat(string);
            }
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            try {
                BitMatrix bitMatrix = multiFormatWriter.encode(qrString, BarcodeFormat.QR_CODE, 500, 500);
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                ivQr.setVisibility(ImageView.VISIBLE);
                ivQr.setImageBitmap(bitmap);
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }*/
    }

    public void deleteElement(int socialId, String username) {
        /*String social = "";
        switch (socialId) {
            case 1:
                social = "twitter";
                break;
            case 2:
                social = "instagram";
                break;
            case 3:
                social = "facebook";
                break;
        }
        String string = "";
        for(int i = 0; i < listStrings.size(); i++) {
            string = listStrings.get(i);
            if (string.equals(social + ':' + username + ';')) {
                listStrings.remove(string);
            }
        }
        if(listStrings.isEmpty()) {
            layout.setVisibility(RelativeLayout.GONE);
            tvNoSocials.setVisibility(TextView.VISIBLE);
        } else {
            generateQr();
        }*/
    }

    public void addElement(int socialId, String username) {
        /*tvNoSocials.setVisibility(TextView.GONE);
        String social = "";
        switch (socialId) {
            case 1:
                social = "twitter";
                break;
            case 2:
                social = "instagram";
                break;
            case 3:
                social = "facebook";
                break;
        }
        listStrings.add(social + ':' + username + ';');
        generateQr();*/
    }

    public static CodeViewerFragment getInstance() {
        return instance;
    }
}
