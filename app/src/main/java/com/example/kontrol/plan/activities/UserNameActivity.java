package com.example.kontrol.plan.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.kontrol.plan.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserNameActivity extends AppCompatActivity {

    @BindView(R.id.username)
    EditText mUserNameEditText;

    @BindView(R.id.btn_save_user_name)
    Button mSaveButton;

    @BindView(R.id.iv_app_name)
    ImageView iv_app_name;

    public static final String KEY_USER_NAME = "userName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_name);
        ButterKnife.bind(this);

        new DisplayImageTask().execute(R.drawable.plan_image);

        mUserNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSaveButton.setEnabled(true);
                } else {
                    mSaveButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = mUserNameEditText.getText().toString();
                Intent returnIntent = new Intent();
                returnIntent.putExtra(KEY_USER_NAME, userName);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

    }

    private class DisplayImageTask extends AsyncTask<Integer, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(Integer... integers) {
            int imageId = integers[0];
            Resources res = getResources();
            Bitmap planImage = BitmapFactory.decodeResource(res, imageId);
            return planImage;
        }

        protected  void onPostExecute(Bitmap bitmap) {
            iv_app_name.setImageBitmap(bitmap);
        }
    }


}
