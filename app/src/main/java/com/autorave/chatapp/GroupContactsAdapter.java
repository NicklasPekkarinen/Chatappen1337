package com.autorave.chatapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class GroupContactsAdapter extends RecyclerView.Adapter<GroupContactsAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mContacts;
    private List<User> copyContacts;
    @NonNull
    private OnItemCheckListener onItemCheckListener;

    interface OnItemCheckListener {
        void onItemCheck(User user);
        void onItemUncheck(User user);
    }

    public GroupContactsAdapter(List<User> mContacts, Context mContext, @NonNull OnItemCheckListener onItemCheckListener) {

        this.mContext = mContext;
        this.mContacts = mContacts;
        this.onItemCheckListener = onItemCheckListener;
        copyContacts = new ArrayList<>();
        copyContacts.addAll(mContacts);
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.group_contacts_item, parent, false);
        return new GroupContactsAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final GroupContactsAdapter.ViewHolder holder, int position) {

        final User user = mContacts.get(position);
        holder.mUserName.setText(user.getUsername());

        if (user.getImageURL().equals("default")) {
            holder.mUserImage.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(mContext).load(user.getImageURL()).into(holder.mUserImage);
        }

        if (user.getStatus().equals("online")) {
            holder.mStatus.setVisibility(View.VISIBLE);
            holder.mStatus.setImageResource(R.color.statusOnline);
        } else if (user.getStatus().equals("offline")) {
            holder.mStatus.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ChatPage.class);
                intent.putExtra("userId", user.getId());
                mContext.startActivity(intent);
            }
        });

        holder.mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.mCheckBox.isChecked()) {
                    onItemCheckListener.onItemCheck(user);
                } else {
                    onItemCheckListener.onItemUncheck(user);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mUserImage;
        private ImageView mStatus;
        private TextView mUserName;
        private CheckBox mCheckBox;

        public ViewHolder(View itemView) {

            super(itemView);
            mUserImage = itemView.findViewById(R.id.group_contacts_image);
            mStatus = itemView.findViewById(R.id.group_status_icon_contacts);
            mUserName = itemView.findViewById(R.id.group_contacts_username);
            mCheckBox = itemView.findViewById(R.id.group_checkbox);

        }
    }

    public void filter(String text) {

        mContacts.clear();

        if (text.isEmpty()) {

            mContacts.addAll(copyContacts);

        } else {

            text = text.toLowerCase();

            for (User user: copyContacts) {

                if (user.getUsername().toLowerCase().contains(text)) {
                    mContacts.add(user);
                }
            }

        }
        notifyDataSetChanged();
    }
}
