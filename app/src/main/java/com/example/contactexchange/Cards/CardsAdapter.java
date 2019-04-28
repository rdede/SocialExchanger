package com.example.contactexchange.Cards;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.contactexchange.R;
import com.example.contactexchange.SocialIds;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static android.provider.Settings.System.getString;

public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.ViewHolder> {

    private ArrayList<Card> mList;
    private OnListItemClickListener mOnListItemClickListener;
    private Context context;
    private String uid;

    public CardsAdapter(ArrayList<Card> mList, OnListItemClickListener mOnListItemClickListener, Context context, String uid) {
        this.mList = mList;
        this.mOnListItemClickListener = mOnListItemClickListener;
        this.context = context;
        this.uid = uid;
    }

    @NonNull
    @Override
    public CardsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.cards_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardsAdapter.ViewHolder viewHolder, int i) {
        try {
            String filePath = context.getFilesDir() + "/" + uid;
            File file = new File(filePath);
            try {
                FileInputStream fin = new FileInputStream(file);
                String string = convertStreamToString(fin);
                String[] lines = string.split(System.getProperty("line.separator"));
                for(int y = 0; y < lines.length; y++) {
                    String[] element = lines[y].split(":");
                    if(element[0].equals(mList.get(i).getUid())) {
                        Log.d("coucou", "element"+element[1]);
                        Drawable ressource = context.getResources().getDrawable(Integer.parseInt(element[1]));
                        viewHolder.layout.setBackgroundDrawable(ressource);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        viewHolder.tvName.setText(mList.get(i).getName());
        if(!mList.get(i).getSocials().isEmpty()) {
            if (mList.get(i).getSocials().get(SocialIds.TWITTER.ordinal())) {
                viewHolder.ivTwitter.setVisibility(ImageView.VISIBLE);
            }
            if (mList.get(i).getSocials().get(SocialIds.INSTAGRAM.ordinal())) {
                viewHolder.ivInstagram.setVisibility(ImageView.VISIBLE);
            }
            if (mList.get(i).getSocials().get(SocialIds.FACEBOOK.ordinal())) {
                viewHolder.ivFacebook.setVisibility(ImageView.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvName;
        ImageView ivTwitter;
        ImageView ivInstagram;
        ImageView ivFacebook;
        RelativeLayout layout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            ivTwitter = itemView.findViewById(R.id.ivTwitter);
            ivInstagram = itemView.findViewById(R.id.ivInstagram);
            ivFacebook = itemView.findViewById(R.id.ivFacebook);
            layout = itemView.findViewById(R.id.layout);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnListItemClickListener.onListItemClick(getAdapterPosition());
        }
    }
    public interface OnListItemClickListener {
        void onListItemClick(int clickedItemIndex);
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
