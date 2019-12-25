package com.example.kontrol.plan.activities;

import android.Manifest;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import com.example.kontrol.plan.R;
import com.example.kontrol.plan.adapters.ContactsCursorAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class AddMembersActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, ContactsCursorAdapter.ListItemClickListener {

    private ContactsCursorAdapter contactsCursorAdapter;

    private ListView listView;

    private static final int CONTACTS_LOADER = 1356;

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 5231;

    private ArrayList<String> addedContacts;

    public static final String ADDED_MEMBERS_KEY = "added_members";

    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;

    ArrayList<String> uidlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_members);
        setTitle(R.string.add_members_activity_title);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("user");

        addedContacts = new ArrayList<String>();
        uidlist = new ArrayList<>();

        listView = (ListView)findViewById(R.id.lv_contacts_list);

        contactsCursorAdapter = new ContactsCursorAdapter(AddMembersActivity.this, null, 1, this);
        listView.setAdapter(contactsCursorAdapter);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }

        } else {
            getLoaderManager().initLoader(CONTACTS_LOADER, null, this);
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };


        return new android.content.CursorLoader(this,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" ASC");

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        contactsCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        contactsCursorAdapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_members_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_members_done:
                Intent intent = new Intent();
                rotInHell(intent);
//                intent.putStringArrayListExtra(ADDED_MEMBERS_KEY, addedContacts);
//                intent.putStringArrayListExtra(ADDED_MEMBERS_KEY, uidlist);
//                setResult(RESULT_OK, intent );
//                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    getLoaderManager().initLoader(CONTACTS_LOADER, null, this);

                } else {

                }
                return;
            }

        }
    }

//    @Override
//    public void onListItemClickListener(ArrayList<String> selectedMembers) {
////        final String mem = formatNumber(selectedMember);
//
//
//        //addedContacts = selectedMembers;
//        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Map<String, Object> temp = (Map<String, Object>) dataSnapshot.getValue();
//                getUids(temp, selectedMembers.get(0) );
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

    private void getUids(Map<String, Object> users, String num) {
        num.replaceAll("\\s+", "");
        if(!users.isEmpty() && users != null){
            for (Map.Entry<String, Object> entry : users.entrySet()) {
                Map singleUser = (Map) entry.getValue();
                String num2 = (String) singleUser.get("number");
                String uid = (String) singleUser.get("uid");

                if(num2.equals(num)){
                    uidlist.add(uid);
                    String test = "test";
                }
            }
        }
      

        ArrayList<String> temp = addedContacts;
    }

    private String formatNumber(String number) {
        String removeZero = number.substring(1, number.length());
        String realNumber = "+92" + removeZero;
        return realNumber;
    }


    @Override
    public void onListItemClickListener(ArrayList<String> selectedMembers) {
        addedContacts = selectedMembers;

    }

    private void rotInHell(Intent intent){
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> temp = (Map<String, Object>) dataSnapshot.getValue();
                getUids(temp, addedContacts.get(0) );

                intent.putStringArrayListExtra(ADDED_MEMBERS_KEY, uidlist);
                setResult(RESULT_OK, intent );
                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
