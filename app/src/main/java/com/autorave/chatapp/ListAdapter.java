package com.autorave.chatapp;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class ListAdapter extends RecyclerView.Adapter {

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


    }


    @Override
    public int getItemCount() {
        return 0;
    }

    private class ListViewHolder extends RecyclerView.ViewHolder implements ViewGroup.OnClickListener {

        private ImageView mUserImage;
        private TextView mUserName;
        private TextView mLastMsg;

        public ListViewHolder(View itemView) {

            super(itemView);
            mUserImage = itemView.findViewById(R.id.user_chats_image);
            mUserName = itemView.findViewById(R.id.chats_username);
            mLastMsg = itemView.findViewById(R.id.chats_last_message);

        }



        public void bindData(int position) {

            //

        }

        public void onClick(View view) {
            
        }

    }

}
