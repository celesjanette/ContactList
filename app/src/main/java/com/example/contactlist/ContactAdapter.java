package com.example.contactlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private ArrayList<Contact> contactData;
    private View.OnClickListener mOnItemClickListener;
    private boolean isDeleting;
    private Context parentContext;

    public class ContactViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewContact;
        public TextView textHome;
        public TextView textMail;
        public Button deleteButton;
        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewContact = itemView.findViewById(R.id.textContactName);
            textHome = itemView.findViewById(R.id.textHomeNumber);
            textMail = itemView.findViewById(R.id.textMail);
            deleteButton = itemView.findViewById(R.id.buttonDeleteContact);
            itemView.setTag(this);
            itemView.setOnClickListener(mOnItemClickListener);
        }

        public TextView getContactTextView() {
            return textViewContact;
        }
        public TextView getHomeTextView() {
            return textHome;
        }
        public TextView getTextMail() { return textMail;}
        public Button getDeleteButton() {
            return deleteButton;
        }
    }

    public ContactAdapter(ArrayList<Contact> arrayList, Context context) {
        contactData = arrayList;
        parentContext = context;
    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }

    @NonNull // required method for a RecyclerView.Adapter. It overrides the superclasses method. This method is called for each item in the data set to be displayed. Its job is to create the visual display for each item using the layout file we created. For each item, a ViewHolder is created using the inflated XML and returned to the RecylcerView to be displayed in the activity.
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ContactViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, final int position) {
        holder.getContactTextView().setText(contactData.get(position).getContactName());
        holder.getHomeTextView().setText(contactData.get(position).getHomeNumber());
        holder.getTextMail().setText(contactData.get(position).geteMail());
        if (isDeleting) {
            holder.getDeleteButton().setVisibility(View.VISIBLE);

            holder.getDeleteButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int adapterPosition = holder.getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        deleteItem(adapterPosition);
                    }
                }
            });
        }
        else {
            holder.getDeleteButton().setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return contactData.size();
    }

    private void deleteItem(int position) {
        Contact contact = contactData.get(position);
        ContactDataSource ds = new ContactDataSource(parentContext);
        try {
            ds.open();
            boolean didDelete = ds.deleteContact(contact.getContactID());
            ds.close();
            if (didDelete) {
                contactData.remove(position);
                notifyDataSetChanged();
            }
            else {
                Toast.makeText(parentContext, "Delete Failed!", Toast.LENGTH_LONG).show();
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDelete(boolean b) {
        isDeleting = b;
    }
}
// KEY NOTES
// adapter provides access to the underlying data source for the list and implements special list functionality, such as deleting list items.
// ViewHolder is an object that belongs to the adapter
// LayoutManager. A LayoutManager is responsible for measuring and positioning items (views) in the list, as well as determining when a view needs to be recycled (or reused because another item in the list needs to be displayed).