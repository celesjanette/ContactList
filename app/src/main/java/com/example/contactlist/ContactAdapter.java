package com.example.contactlist;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.View;
import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class ContactAdapter extends RecyclerView.Adapter {
    private ArrayList<String> contactData;
    private View.OnClickListener mOnItemClickListener;
    public class ContactViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewContact;
        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewContact = itemView.findViewById(R.id.textViewName);
        itemView.setTag(this);
        itemView.setOnClickListener(mOnItemClickListener);
        }

        public TextView getContactTextView() {
            return textViewContact;
        }
    }
    public ContactAdapter(ArrayList<String> arrayList){
        contactData = arrayList;
    }
    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_item_view, parent, false);
        return new ContactViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ContactViewHolder cvh = (ContactViewHolder) holder;
        cvh.getContactTextView().setText(contactData.get(position));
    }

    @Override
    public int getItemCount() {
        return contactData.size();
    }

}
