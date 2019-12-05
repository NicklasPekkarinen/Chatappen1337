package com.autorave.chatapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.autorave.chatapp.Activitys.ChatPage;
import com.autorave.chatapp.SQLite.NameChangeDBHelper;
import com.autorave.chatapp.Templates.User;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class GroupContactsAdapter extends RecyclerView.Adapter<GroupContactsAdapter.ViewHolder> {

    NameChangeDBHelper nameChangeDBHelper;
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
    public void onBindViewHolder(@NonNull final GroupContactsAdapter.ViewHolder holder, final int position) {

        holder.bind(position);

    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    // --------------------- ViewHolder ------------------- //
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

        void bind(int position) {
            nameChangeDBHelper = new NameChangeDBHelper(mContext);

            ArrayList<String> SQLData = (ArrayList)nameChangeDBHelper.getDataSQL();

            final User user = mContacts.get(position);

            for (int i = 0; i < SQLData.size(); i++) {
                if (SQLData != null && SQLData.get(i).equals(user.getId())) {
                    user.setUsername(SQLData.get(i-1));
                }
            }

            mUserName.setText(user.getUsername());

            if (user.getImageURL().equals("default")) {
                mUserImage.setImageResource(R.mipmap.ic_launcher);
            } else {
                Glide.with(mContext).load(user.getImageURL()).into(mUserImage);
            }

            if (user.getStatus().equals("online")) {
                mStatus.setVisibility(View.VISIBLE);
                mStatus.setImageResource(R.color.statusOnline);
            } else if (user.getStatus().equals("offline")) {
                mStatus.setVisibility(View.GONE);
            }

            mCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCheckBox.isChecked()) {
                        onItemCheckListener.onItemCheck(user);
                    } else {
                        onItemCheckListener.onItemUncheck(user);
                    }
                }
            });
        }
    }
}
