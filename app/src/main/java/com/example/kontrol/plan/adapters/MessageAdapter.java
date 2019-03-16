package com.example.kontrol.plan.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.kontrol.plan.model.Message;
import com.example.kontrol.plan.R;

import java.util.List;

/**
 * Created by kontrol on 1/21/2018.
 */

public class MessageAdapter extends ArrayAdapter<Message> {

    public MessageAdapter(Context context, int resource, List<Message> objects) {
        super(context, resource, objects);
        }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_message_2, parent, false);
        }


        TextView messageTextView = (TextView) convertView.findViewById(R.id.text_message_body);
        TextView authorTextView = (TextView) convertView.findViewById(R.id.text_message_name);
        TextView timeTextView = (TextView)convertView.findViewById(R.id.text_message_time);


        Message message = getItem(position);


        messageTextView.setText(message.getText());
        authorTextView.setText(message.getName());
        timeTextView.setText(message.getMsgTime());


        return convertView;
    }
}
