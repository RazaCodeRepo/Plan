package com.example.kontrol.plan.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.kontrol.plan.R;
import com.example.kontrol.plan.model.Message;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kontrol on 5/30/2018.
 */

public class MessagesListService extends RemoteViewsService  {



    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext());
    }


    class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory, ValueEventListener{

        private FirebaseDatabase mFirebaseDatabase;
        private DatabaseReference mMessagesDatabaseReference;
        private ChildEventListener mChildEventListener;
        private Message message;
        List<Message> messageList;

        Context mContext;

        public ListRemoteViewsFactory(Context applicationContext) {
            mContext = applicationContext;
            messageList = new ArrayList<Message>();
            mFirebaseDatabase = FirebaseDatabase.getInstance();
//            String messagesNodeKey = PreferenceManager.getDefaultSharedPreferences(mContext).getString("MESSAGES_NODE_KEY", "No key");
//            mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("plan" ).child(messagesNodeKey).child("messages");

        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {

            String messagesNodeKey = PreferenceManager.getDefaultSharedPreferences(mContext).getString("MESSAGES_NODE_KEY", "No key");
            mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("plan" ).child(messagesNodeKey).child("messages");

            mMessagesDatabaseReference.addValueEventListener(this);

            synchronized (this) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        public void onDestroy() {
            messageList.clear();
        }

        @Override
        public int getCount() {
            return messageList.size();
        }

        @Override
        public RemoteViews getViewAt(int i) {
            RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.item_message);
            rv.setTextViewText(R.id.text_message_body, messageList.get(i).getText());
            rv.setTextViewText(R.id.text_message_name, messageList.get(i).getName());
            //rv.setTextViewText(R.id.text_message_time, messageList.get(i).getMsgTime());

            Bundle bundle = new Bundle();
            bundle.putString("WIDGET_MESSAGE_TEXT", messageList.get(i).getText());
            bundle.putString("WIDGET_MESSAGE_NAME", messageList.get(i).getName());
            bundle.putString("WIDGET_MESSAGE_TIME", messageList.get(i).getMsgTime());

            Intent fillInIntent = new Intent();
            fillInIntent.putExtra("EXTRA_WIDGET_BUNDLE", bundle);
            rv.setOnClickFillInIntent(R.id.message_list_item, fillInIntent);

            return rv;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }


        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
//            message = dataSnapshot.getValue(Message.class);
//            messageList.add(message);
            messageList.clear();
            for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                message = snapshot.getValue(Message.class);
                messageList.add(message);
            }
            synchronized (this) {
                this.notify();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }
}
