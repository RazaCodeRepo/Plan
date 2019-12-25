package com.example.kontrol.plan.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.preference.PreferenceManager;

import com.example.kontrol.plan.adapters.ContactsCursorAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.evernote.android.job.JobManager;
import com.example.kontrol.plan.service.PlanJobCreator;
import com.example.kontrol.plan.service.PlanRemoveJob;
import com.example.kontrol.plan.R;
import com.example.kontrol.plan.model.Plan;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewPlanActivity extends AppCompatActivity {

    @BindView(R.id.et_newGroup_name)
    EditText et_newGroupName;

    @BindView(R.id.fab_newGroup_addMembers)
    FloatingActionButton fab_addMembers;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mGroupDatabaseReference;

    private static final int RESULT_PICK_CONTACT = 85500;

    private ArrayList<String> addedContacts;

    public static final String PLAN_NODE_KEY = "planNodeKey";

    private String hours;
    private String minutes;

    public static int duration;
    private String durationString;


    public static String groupKeyCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JobManager.create(this).addJobCreator(new PlanJobCreator());
        setContentView(R.layout.activity_new_plan);
        setTitle(R.string.new_group_activity_title);
        ButterKnife.bind(this);

        addedContacts = new ArrayList<String> ();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mGroupDatabaseReference = mFirebaseDatabase.getReference().child("plan");

    }


    @OnClick(R.id.fab_newGroup_addMembers)
    public void addMembers(View view){
        Intent startAddMembersActivityIntent = new Intent(this, AddMembersActivity.class);
        startActivityForResult(startAddMembersActivityIntent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 0 && resultCode == RESULT_OK) {
            addedContacts = data.getStringArrayListExtra(AddMembersActivity.ADDED_MEMBERS_KEY);
        }
    }

    private void createGroup(){
        final String groupName = et_newGroupName.getText().toString();

        String planAdmin = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("SAVED_USER_NAME", "no_user_name");


        String time = getTime();

        Plan mPlan = new Plan(planAdmin, groupName, durationString, addedContacts,time);

        mGroupDatabaseReference.push().setValue(mPlan, new DatabaseReference.CompletionListener(){
            @Override
            public void onComplete(DatabaseError databaseError,
                                   DatabaseReference databaseReference) {
                String uniqueKey = databaseReference.getKey();
                groupKeyCheck = uniqueKey;
                Intent startMessageActivityIntent = new Intent(NewPlanActivity.this, MessagesActivity.class);
                startMessageActivityIntent.putExtra(MessagesActivity.GROUP_NAME_INTENT_KEY, groupName);
                startMessageActivityIntent.putExtra(PLAN_NODE_KEY, uniqueKey);
                int d = duration;
                PlanRemoveJob.scheduleExactjob();
                startActivity(startMessageActivityIntent);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_plan_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_save:
                createGroup();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton)view).isChecked();

        switch (view.getId()){
            case R.id.rb_sameDay:
                if (checked){
                    Toast.makeText(this, getResources().getString(R.string.tag_same_day), Toast.LENGTH_SHORT).show();
                    durationString = "same_day";
                    inputDialog();
                }
                break;

            case R.id.rb_oneDay:
                if(checked) {
                    setDuration("24", "0");
                    durationString = "one_day";
                    Toast.makeText(this, getResources().getString(R.string.tag_one_day), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.rb_twoDay:
                if(checked) {
                    setDuration("48", "0");
                    durationString = "two_day";
                    Toast.makeText(this, getResources().getString(R.string.tag_two_day), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.rb_threeDay:
                if(checked) {
                    setDuration("72", "0");
                    durationString = "three_day";
                    Toast.makeText(this, getResources().getString(R.string.tag_three_day), Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void inputDialog(){
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View mView = layoutInflaterAndroid.inflate(R.layout.input_dialog, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this);
        alertDialogBuilderUserInput.setView(mView);

        final EditText et_hours = (EditText) mView.findViewById(R.id.db_hours);
        final EditText et_minutes = (EditText) mView.findViewById(R.id.db_minutes);
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.tag_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                        hours = et_hours.getText().toString();
                        minutes = et_minutes.getText().toString();
                        if(hours.equals("")){
                            hours = "0";
                        }
                        if(minutes.equals("")){
                            minutes = "0";
                        }
                        setDuration(hours, minutes);
                    }
                })

                .setNegativeButton(getResources().getString(R.string.tag_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
    }

    private void setDuration(String hrs, String min) {
        int hoursInt = Integer.parseInt(hrs);
        int minInt = Integer.parseInt(min);
        if(hoursInt == 0 && minInt != 0) {
            duration = minInt;
        } else if (minInt == 0 && hoursInt != 0) {
            duration = hoursInt * 60;
        } else {
            duration = (hoursInt * 60) + minInt;
        }
    }

    private String getTime(){
        Date date = new Date();

        SimpleDateFormat sdf = new SimpleDateFormat("EEE,HH:mm:ss");
        final String time = sdf.format(date);
        return time;
    }


}
