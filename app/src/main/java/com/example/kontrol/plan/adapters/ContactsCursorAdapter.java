package com.example.kontrol.plan.adapters;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kontrol.plan.R;
import com.example.kontrol.plan.activities.AddMembersActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by kontrol on 2/1/2018.
 */

public class ContactsCursorAdapter extends CursorAdapter {

    private ArrayList<String> addedContacts;

    private final ListItemClickListener mOnClickListener;

    public interface ListItemClickListener{
        void onListItemClickListener(ArrayList<String> selectedMembers);
    }

//    public interface ListItemClickListener{
//        void onListItemClickListener(String selectedMember);
//    }

    public ContactsCursorAdapter(Context context, Cursor c, int flags, ListItemClickListener listener){
        super(context, c, flags);
        mOnClickListener = listener;
        addedContacts = new ArrayList<String> ();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.contacts_list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        final TextView contactsNameTextView = (TextView)view.findViewById(R.id.item_contact_name);
        final TextView contactsNumberTextView = (TextView)view.findViewById(R.id.item_contact_number);
        final CheckBox mCheckbox = (CheckBox)view.findViewById(R.id.cb_listview_checkbox);

        int  phoneIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        int  nameIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

        final String phoneNo = cursor.getString(phoneIndex);
        final String name = cursor.getString(nameIndex);

        contactsNameTextView.setText(name);
        contactsNumberTextView.setText(phoneNo);

        if(addedContacts.contains(phoneNo)){
            mCheckbox.setChecked(true);
        } else {
            mCheckbox.setChecked(false);
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCheckbox.isChecked()){
                    mCheckbox.setChecked(false);
                    addedContacts.remove(phoneNo);
                } else {
                    mCheckbox.setChecked(true);
                    addedContacts.add(phoneNo);
                }
            }
        });

//        if(addedContacts.contains(phoneNo)) {
//            tempCheckBox.setChecked(true);
//        }
//        else {
//            tempCheckBox.setChecked(false);
//        }
//
//        final Context c = context;
//
//        linearLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                tempCheckBox.performClick();
//
//                if(tempCheckBox.isChecked()){
//                    mOnClickListener.onListItemClickListener(phoneNo);
//                } else if(!tempCheckBox.isChecked()) {
//                    int removalIndex = addedContacts.indexOf(phoneNo);
//                    addedContacts.remove(removalIndex);
//                }
//
//            }
//        });
        mOnClickListener.onListItemClickListener(addedContacts);
    }

}
