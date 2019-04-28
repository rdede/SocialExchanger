package com.example.contactexchange;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.provider.DocumentsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.contactexchange.Cards.Card;
import com.example.contactexchange.Cards.CardsFragment;
import com.example.contactexchange.MyCode.Social;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getName();

    final Fragment fragment1 = new CardsFragment();
    final Fragment fragment3 = new CodeFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragment1;

    BottomNavigationView navigation;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fm.beginTransaction().add(R.id.main_container, fragment3, "code").hide(fragment3).commit();
        fm.beginTransaction().add(R.id.main_container, fragment1, "cards").commit();





        toolbar.findViewById(R.id.btnSettings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filePath = getApplication().getFilesDir() + "/" + mAuth.getUid();
                File file = new File(filePath);
                try {
                    FileInputStream fin = new FileInputStream(file);
                    String string = convertStreamToString(fin);
                    Log.d("coucou", string);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(i);
            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_cards:
                    fm.beginTransaction().hide(active).show(fragment1).commit();
                    active = fragment1;
                    return true;

                case R.id.navigation_reader:
                    startQRScanner();
                    navigation.setSelectedItemId(R.id.navigation_cards);
                    return false;

                case R.id.navigation_code:
                    fm.beginTransaction().hide(active).show(fragment3).commit();
                    active = fragment3;
                    return true;
            }
            return false;
        }
    };

    private void startQRScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Scan a code");
        integrator.setCameraId(0);
        integrator.setOrientationLocked(true);
        integrator.setBeepEnabled(true);
        integrator.setCaptureActivity(CaptureActivityPortrait.class);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            final String resultUid = result.getContents();
            if (resultUid != null) {
                if (resultUid.equals(mAuth.getUid())) {
                    Toast.makeText(this, "You cannot add yourself", Toast.LENGTH_SHORT).show();
                } else {
                    final CardsFragment fragment = (CardsFragment) getSupportFragmentManager().findFragmentByTag("cards");
                    fragment.setPbVisibility(true);
                    db.collection("cards")
                            .document(resultUid)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()){
                                        DocumentSnapshot document = task.getResult();
                                        if(document.exists()){
                                            Card doc = document.toObject(Card.class);

                                            DocumentReference cardsRef = db.collection("cards").document(resultUid);

                                            ArrayList<Integer> androidColors = new ArrayList<>();
                                            androidColors.add(R.drawable.shape_cards_blue);
                                            androidColors.add(R.drawable.shape_cards_green);
                                            androidColors.add(R.drawable.shape_cards_red);
                                            androidColors.add(R.drawable.shape_cards_pink);//getResources().getStringArray(R.array.colors);
                                            int randomAndroidColor = androidColors.get(new Random().nextInt(androidColors.size()));

                                            String filename = mAuth.getUid();
                                            String fileContents = resultUid + ':' + Integer.toString(randomAndroidColor) + '\n';
                                            FileOutputStream outputStream;

                                            try {
                                                outputStream = openFileOutput(filename, getApplication().MODE_PRIVATE);
                                                outputStream.write(fileContents.getBytes());
                                                outputStream.close();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }


                                            cardsRef.update("uidsAdded", FieldValue.arrayUnion(mAuth.getUid()));
                                            CardsFragment fragment = (CardsFragment) getSupportFragmentManager().findFragmentByTag("cards");
                                            fragment.addNewCard(doc);
                                        }
                                    }
                                }
                            });
                }
            } else {
                Toast.makeText(this, "Scan canceled", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }
}
