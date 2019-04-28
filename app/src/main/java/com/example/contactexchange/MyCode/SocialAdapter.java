package com.example.contactexchange.MyCode;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.contactexchange.R;

import java.util.ArrayList;

public class SocialAdapter extends RecyclerView.Adapter<SocialAdapter.ViewHolder> {

    private ArrayList<Social> mList;
    final private OnListItemClickListener mOnListItemClickListener;

    private Boolean btnDeleteVisible = true;

    private Context context;

    public SocialAdapter(ArrayList<Social> mList, OnListItemClickListener mOnListItemClickListener, Context context) {
        this.mList = mList;
        this.mOnListItemClickListener = mOnListItemClickListener;
        this.context = context;
    }

    @NonNull
    @Override
    public SocialAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.social_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SocialAdapter.ViewHolder viewHolder, int i) {
        viewHolder.tvUsername.setText(mList.get(i).getUsername());
        switch (mList.get(i).getSocialId()) {
            case 0:
                viewHolder.ivLogo.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_twitter));
                break;
            case 1:
                viewHolder.ivLogo.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_instagram));
                break;
            case 2:
                viewHolder.ivLogo.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_facebook));
                break;
        }
        if(!btnDeleteVisible) {
            viewHolder.btnDelete.setVisibility(ImageButton.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivLogo;
        TextView tvUsername;
        ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivLogo = itemView.findViewById(R.id.ivLogo);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            btnDelete = itemView.findViewById(R.id.btnDelete);

            itemView.setOnClickListener(this);

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnListItemClickListener.onDeleteClick(getAdapterPosition());
                }
            });
        }

        @Override
        public void onClick(View v) {
            mOnListItemClickListener.onListItemClick(getAdapterPosition());
        }
    }

    public void setDeleteBtnVisibility(Boolean bool){
        btnDeleteVisible = bool;
    }

    public interface OnListItemClickListener {
        void onListItemClick(int clickedItemIndex);

        void onDeleteClick(int clickerItemIndex);
    }
}
