package com.example.kontrol.plan.activities;

import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.kontrol.plan.model.Message;
import com.example.kontrol.plan.adapters.MessageAdapter;
import com.example.kontrol.plan.R;
import com.example.kontrol.plan.widget.PlanAppWidgetProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.kontrol.plan.activities.NewPlanActivity.PLAN_NODE_KEY;


public class MessagesActivity extends AppCompatActivity {

    public static final String GROUP_NAME_INTENT_KEY = "groupName";
    public static final String GROUP_EXP_DATE_INTENT_KEY = "groupDate";
    public static final String GROUP_EXP_TIME_INTENT_KEY = "groupTime";
    public static final String ANONYMOUS = "anonymous";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;

    private ListView mMessageListView;
    private MessageAdapter mMessageAdapter;
    private String mUsername;

    @BindView(R.id.et_messageEditText) EditText mMessageEditText;
    @BindView(R.id.btn_sendButton) Button mSendButton;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;

    private ChildEventListener mChildEventListener;

    DataSnapshot messagesNode;
    String planNodeKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        String groupName = intent.getStringExtra(GROUP_NAME_INTENT_KEY);
        planNodeKey = intent.getStringExtra(PLAN_NODE_KEY);

        setTitle(groupName);

        mUsername = ANONYMOUS;

        mFirebaseDatabase = FirebaseDatabase.getInstance();


        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("plan" ).child(planNodeKey).child("messages");

        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("MESSAGES_NODE_KEY", planNodeKey).apply();
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("WIDGET_PLAN_NAME", groupName).apply();
        PlanAppWidgetProvider.sendRefreshBroadcast(getApplicationContext());

        mMessageListView = (ListView) findViewById(R.id.messageListView);

        List<Message> friendlyMessages = new ArrayList<>();
        mMessageAdapter = new MessageAdapter(this, R.layout.item_message_2, friendlyMessages);
        mMessageListView.setAdapter(mMessageAdapter);

        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message mMessage = dataSnapshot.getValue(Message.class);
                mMessageAdapter.add(mMessage);
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
        mMessagesDatabaseReference.addChildEventListener(mChildEventListener);

        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});

        // Send button sends a message and clears the EditText
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mUsername = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("SAVED_USER_NAME", "No User Name");
                String stringMessage = mMessageEditText.getText().toString();

                String time = getTime();

                Message message = new Message(stringMessage, mUsername, null, time);

                Map<String, Object> messageValue = message.toMap();
                mMessagesDatabaseReference.push().setValue(messageValue);

                mMessageEditText.setText("");
                PlanAppWidgetProvider.sendRefreshBroadcast(getApplicationContext());
            }
        });


    }

    private String getTime(){
        Date date = new Date();

        SimpleDateFormat sdf = new SimpleDateFormat("EEE,HH:mm:ss");
        final String time = sdf.format(date);
        return time;
    }

}
