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
    private RadioGroup radioGroupTypeMark;
    private RadioGroup radioGroupMode;
    private EditText etLat, etLng;
    private int mMarkType = 0;
    private int mMarkMode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark);
        etLat = (EditText) findViewById(R.id.etLat);
        etLng = (EditText) findViewById(R.id.etLng);
        etMarkName = (EditText) findViewById(R.id.etMarkName);

        etLat.addTextChangedListener(textWatcher);
        etLng.addTextChangedListener(textWatcher);
        etMarkName.addTextChangedListener(textWatcher);
        btnAddMark = (Button) findViewById(R.id.btnAddMark);
        btnAddMark.setOnClickListener(v -> addMark());
        radioGroupTypeMark = (RadioGroup) findViewById(R.id.radioGroupTypeMark);
        radioGroupMode = (RadioGroup) findViewById(R.id.radioGroupMode);

        Intent intent = getIntent();
        if ((intent.hasExtra(Const.EXTRA_MODE) &&
                (intent.getIntExtra(Const.EXTRA_MODE, 0) == Const.MODE_MARK_NEW))){
            Double latitude = intent.getDoubleExtra(Const.EXTRA_LATITUDE, 0);
            Double longitude = intent.getDoubleExtra(Const.EXTRA_LONGITUDE, 0);
            mMyLocation = new LatLng(latitude, longitude);
        }

        radioGroupMode.setOnCheckedChangeListener((group, checkedId) -> changeMarkMode(checkedId));
        radioGroupTypeMark.setOnCheckedChangeListener((group, checkedId) -> changeMarkType(checkedId));
    }


    private void addMark(){
        Boolean isHaveLatLong = false;
        Mark mark = new Mark();

        switch (mMarkMode) {
            case Const.TYPE_MARK_MODE_CURRENT:
                if (mMyLocation == null) {
                    Toast.makeText(this, "Location is null", Toast.LENGTH_SHORT).show();
                } else {
                    mark.setLatitude(Connection.getInstance().getLastLocation().latitude);
                    mark.setLongitude(Connection.getInstance().getLastLocation().longitude);
                    isHaveLatLong = true;
                }
                break;
            case Const.TYPE_MARK_MODE_MANUAL:
                double lat = Double.parseDouble(etLat.getText().toString());
                double lng = Double.parseDouble(etLng.getText().toString());
                if (Utils.validateLatLong(lat, lng)) {
                    mark.setLatitude(lat);
                    mark.setLongitude(lng);
                    isHaveLatLong = true;
                } else {
                    Toast.makeText(this, "Not correct coordinates", Toast.LENGTH_SHORT).show();
                }
                break;
        }

        if (isHaveLatLong){
            String name = etMarkName.getText().toString();
            mark.setName(name);
            mark.setOwnerName(Connection.getInstance().getUserName());
            mark.setOwnerEmail(Connection.getInstance().getUserEmail());
            mark.setType(mMarkType);
            mark.setDate(new Date());
            String key = Utils.removeCriticalSymbols(mark.getOwnerEmail() + name +
                    (mark.getLatitude() + mark.getLongitude()));
            mark.setKey(Utils.removeCriticalSymbols(key));
            FbHelper.updateMark(mark);
            etMarkName.setText("");
            etLat.setText("");
            etLng.setText("");
            radioGroupTypeMark.clearCheck();
            Toast.makeText(this, "New mark added on map", Toast.LENGTH_SHORT).show();
        }
    }


    private void changeMarkMode(@IdRes int checkedId){
        switch (checkedId){
            case R.id.rbCurrentLocation:
                mMarkMode = Const.TYPE_MARK_MODE_CURRENT;
                break;
            case R.id.rbManualLocation:
                mMarkMode = Const.TYPE_MARK_MODE_MANUAL;
                break;
        }
        if (mMarkMode == Const.TYPE_MARK_MODE_CURRENT){
            etLat.setEnabled(false);
            etLng.setEnabled(false);
        } else {
            etLat.setEnabled(true);
            etLng.setEnabled(true);
        }
    }


    private void changeMarkType(@IdRes int checkedId){
        switch (checkedId) {
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
        if ((mMarkType != 0) && (etMarkName.getText().length() > 0)) {
            btnAddMark.setEnabled(true);
        } else {
            btnAddMark.setEnabled(false);
        }
    }


    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            checkInputData();
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    /** Enable Button ADD after check input data */
    private void checkInputData(){
        Boolean isOk = true;
        if ((mMarkType == 0) || (etMarkName.getText().length() == 0)){
            isOk = false;
        }
        if (mMarkMode == Const.TYPE_MARK_MODE_MANUAL){
            if ((etLat.getText().toString().length() == 0) ||
                    (etLng.getText().toString().length() == 0)){
                isOk = false;
            } else {
                double lat = Double.parseDouble(etLat.getText().toString());
                double lng = Double.parseDouble(etLng.getText().toString());
                if (! Utils.validateLatLong(lat, lng)) {
                    isOk = false;
                }
            }
        }
        if (isOk){
            btnAddMark.setEnabled(true);
        } else {
            btnAddMark.setEnabled(false);
        }
    }
}
