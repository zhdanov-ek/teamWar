package com.example.gek.teamwar;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.gek.teamwar.Data.Const;
import com.example.gek.teamwar.Data.Mark;
import com.example.gek.teamwar.Utils.Connection;
import com.example.gek.teamwar.Utils.FbHelper;
import com.example.gek.teamwar.Utils.Utils;
import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public class MarkActivity extends AppCompatActivity {
    private LatLng mMyLocation;
    private EditText etMarkName;
    private Button btnAddMark;
    private RadioGroup radioGroup;
    private int mMarkType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark);
        etMarkName = (EditText) findViewById(R.id.etMarkName);
        etMarkName.addTextChangedListener(textWatcher);
        btnAddMark = (Button) findViewById(R.id.btnAddMark);
        btnAddMark.setOnClickListener(v -> addMark());
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);


        Intent intent = getIntent();
        if ((intent.hasExtra(Const.EXTRA_MODE) &&
                (intent.getIntExtra(Const.EXTRA_MODE, 0) == Const.MODE_MARK_NEW))){
            Double latitude = intent.getDoubleExtra(Const.EXTRA_LATITUDE, 0);
            Double longitude = intent.getDoubleExtra(Const.EXTRA_LONGITUDE, 0);
            mMyLocation = new LatLng(latitude, longitude);
        }

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId){
                case -1:
                    mMarkType = 0;
                    break;
                case R.id.rbMark:
                    mMarkType = Const.TYPE_MARK_OWN;
                    break;
                case R.id.rbMarkEnemy:
                    mMarkType = Const.TYPE_MARK_ENEMY;
                    break;
                case R.id.rbMarkNeutral:
                    mMarkType = Const.TYPE_MARK_NEUTRAL;
                    break;
                default:
                    mMarkType = 0;
            }
            if ((mMarkType != 0) && (etMarkName.getText().length() > 0)){
                btnAddMark.setEnabled(true);
            } else {
                btnAddMark.setEnabled(false);
            }
        });
    }


    private void addMark(){
        if (mMyLocation == null){
            Toast.makeText(this, "Location is null", Toast.LENGTH_SHORT).show();
        } else {
            String name = etMarkName.getText().toString();
            Mark mark = new Mark();
            mark.setName(name);
            mark.setOwnerName(Connection.getInstance(this).getUserName());
            mark.setOwnerEmail(Connection.getInstance(this).getUserEmail());
            mark.setLatitude(Connection.getInstance(this).getLastLocation().latitude);
            mark.setLongitude(Connection.getInstance(this).getLastLocation().longitude);
            mark.setType(mMarkType);
            mark.setDate(new Date());
            String key = Utils.removeCriticalSymbols(mark.getOwnerEmail() + name +
                    (mark.getLatitude() + mark.getLongitude()));
            mark.setKey(Utils.removeCriticalSymbols(key));
            mark.setKey(Utils.removeCriticalSymbols(key));
            FbHelper.updateMark(mark, this);
            etMarkName.setText("");
            radioGroup.clearCheck();
            finish();
        }
    }


    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if ((mMarkType != 0) && (etMarkName.getText().length() > 0)){
                btnAddMark.setEnabled(true);
            } else {
                btnAddMark.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
