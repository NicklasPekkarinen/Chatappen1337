package com.autorave.chatapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.autorave.chatapp.Activitys.ChatPage;
import com.autorave.chatapp.R;
import com.autorave.chatapp.SQLite.NameChangeDBHelper;
import com.autorave.chatapp.Templates.User;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ContactsListAdapter extends RecyclerView.Adapter<ContactsListAdapter.ViewHolder> {

    NameChangeDBHelper nameChangeDBHelper;
    private Context mContext;
    private List<User> mContacts;
    private List<User> copyContacts;

    public ContactsListAdapter(List<User> mContacts, Context mContext) {

        this.mContext = mContext;
        this.mContacts = mContacts;
        copyContacts = new ArrayList<>();
        copyContacts.addAll(mContacts);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.contacts_item, parent, false);
        return new ContactsListAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        nameChangeDBHelper = new NameChangeDBHelper(mContext);

        final User user = mContacts.get(position);

        ArrayList<String> SQLData = (ArrayList)nameChangeDBHelper.getDataSQL();

        for (int i = 0; i < SQLData.size(); i++) {
            if (SQLData != null && SQLData.get(i).equals(user.getId())) {
                holder.mUserName.setText(SQLData.get(i-1));
            }
        }

        if (holder.mUserName.getText().length() <= 0) {
            holder.mUserName.setText(user.getUsername());
        }

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
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mUserImage;
        private ImageView mStatus;
        private TextView mUserName;

        public ViewHolder(View itemView) {

            super(itemView);
            mUserImage = itemView.findViewById(R.id.contacts_image);
            mStatus = itemView.findViewById(R.id.status_icon_contacts);
            mUserName = itemView.findViewById(R.id.contacts_username);

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
