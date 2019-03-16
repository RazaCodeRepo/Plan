package com.example.kontrol.plan.activities;

import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.kontrol.plan.R;
import com.example.kontrol.plan.adapters.GroupAdapter;
import com.example.kontrol.plan.model.Plan;
import com.example.kontrol.plan.model.User;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.kontrol.plan.activities.MessagesActivity.ANONYMOUS;
import static com.example.kontrol.plan.activities.NewPlanActivity.PLAN_NODE_KEY;
import static com.example.kontrol.plan.activities.UserNameActivity.KEY_USER_NAME;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.lv_main_groupList)
    ListView mainGroupList;

    @BindView(R.id.fab_main_addGroup)
    FloatingActionButton addNewGroup;

    private GroupAdapter groupAdapter;
    private ListView groupListView;

    private String mUsername;

    private ChildEventListener mChildEventListener;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mPlanDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private DatabaseReference mUserDatabaseReference;

    public static int RC_SIGN_IN = 1;

    public static ArrayList<String> childNodesKeys;

    public static int RC_USER_NAME_ACTIVITY = 5;

    String phoneNumber;
    String uid;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        setTitle(R.string.main_activity_title);

        groupListView = (ListView) findViewById(R.id.lv_main_groupList);

        childNodesKeys = new ArrayList<String>();

        mFirebaseDatabase = FirebaseDatabase.getInstance();

        mFirebaseAuth = FirebaseAuth.getInstance();

        mPlanDatabaseReference = mFirebaseDatabase.getReference().child("plan");
        mUserDatabaseReference = mFirebaseDatabase.getReference().child("user");

        List<Plan> groupsCreated = new ArrayList<>();
        groupAdapter = new GroupAdapter(this, R.layout.plan_list_item, groupsCreated);
        groupListView.setAdapter(groupAdapter);

        groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                final Plan mPlan = (Plan) adapterView.getItemAtPosition(position);

                ArrayList<String> temp = childNodesKeys;

                String selectedPlanKey = childNodesKeys.get(position);

                Intent startMessageActivityIntent = new Intent(MainActivity.this, MessagesActivity.class);
                        startMessageActivityIntent.putExtra(MessagesActivity.GROUP_NAME_INTENT_KEY, mPlan.getName());
                        startMessageActivityIntent.putExtra(PLAN_NODE_KEY, selectedPlanKey);
                        startActivity(startMessageActivityIntent);

            }
        });


        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
               FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null){

                    onSignedInInitialize(user.getDisplayName());

                } else {


                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.PhoneBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };


                Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);

        if (isFirstRun) {
            //show start activity

            startActivityForResult(new Intent(MainActivity.this, UserNameActivity.class), RC_USER_NAME_ACTIVITY);
            Toast.makeText(MainActivity.this, getResources().getString(R.string.tag_first_run), Toast.LENGTH_LONG)
                    .show();
        }

        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isFirstRun", false).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            if(resultCode == RESULT_OK){
                Toast.makeText(this, getResources().getString(R.string.signed_in_tag), Toast.LENGTH_SHORT).show();

                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                phoneNumber = mFirebaseUser.getPhoneNumber();
                uid = mFirebaseUser.getUid();


            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, getResources().getString(R.string.cancel_sign_in_tag), Toast.LENGTH_SHORT).show();
                finish();

            }

        } else if(requestCode == RC_USER_NAME_ACTIVITY) {

            String userName = data.getStringExtra(KEY_USER_NAME);
            User user = new User(phoneNumber, uid, userName);
            Map<String, Object> userValue = user.toMap();
            mUserDatabaseReference.push().setValue(userValue);
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                    .edit().putString("SAVED_USER_NAME", userName).apply();
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                    .edit().putString("SAVED_USER_PHONE_NUMBER", phoneNumber).apply();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mAuthStateListener != null){
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        detachDatabaseReadListener();
        groupAdapter.clear();
    }

    private void onSignedInInitialize(String userName) {
        mUsername = userName;

        attachDatabaseReadListener();
    }

    private void onSignOutCleanup(){
        mUsername = ANONYMOUS;
        groupAdapter.clear();
    }

    private void attachDatabaseReadListener(){
        if(mChildEventListener ==  null){
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Plan group = dataSnapshot.getValue(Plan.class);
                    groupAdapter.add(group);
                    String key = dataSnapshot.getKey();
                    if(!childNodesKeys.contains(key)){
                        childNodesKeys.add(key);
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {


                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mPlanDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    private void detachDatabaseReadListener(){
        if (mChildEventListener != null){
            mPlanDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    @OnClick(R.id.fab_main_addGroup)
    public void createNewGroup(View view){
        Intent startCreateNewGroupActivityIntent = new Intent(this, NewPlanActivity.class);
        startActivity(startCreateNewGroupActivityIntent);
    }


}
