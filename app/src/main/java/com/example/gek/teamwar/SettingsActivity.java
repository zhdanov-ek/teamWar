package com.example.gek.teamwar;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.gek.teamwar.Data.Const;
import com.example.gek.teamwar.Utils.Connection;
import com.example.gek.teamwar.Utils.LogHelper;

import java.util.Date;

public class SettingsActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    public static final String TAG = "SETTINGS";
    private SeekBar sbRate;
    private TextView tvStateRate;
    private SharedPreferences mSharedPreferences;
    private SwitchCompat switchOldWariors;
    private SwitchCompat switchShowCircle;
    private LogHelper logHelper;
    private RadioButton rbNetwork, rbGps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setSupportActionBar((Toolbar) findViewById(R.id.toolBar));
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        logHelper = new LogHelper(getBaseContext());

        rbGps = (RadioButton) findViewById(R.id.rbGps);
        rbGps.setOnClickListener(this);
        rbNetwork = (RadioButton) findViewById(R.id.rbNetwork);
        rbNetwork.setOnClickListener(this);
        if (Connection.getInstance().getProvider() == Const.PROVIDER_GPS){
            rbGps.setChecked(true);
        } else {
            rbNetwork.setChecked(true);
        }

        tvStateRate = (TextView) findViewById(R.id.tvStateRate);
        sbRate = (SeekBar) findViewById(R.id.sbRate);
        switchOldWariors = (SwitchCompat) findViewById(R.id.switchOldWariors);
        switchOldWariors.setOnCheckedChangeListener((buttonView, isChecked) -> updateShowOldWariors(isChecked));

        switchShowCircle = (SwitchCompat) findViewById(R.id.switchShowCircle);
        switchShowCircle.setOnCheckedChangeListener((buttonView, isChecked) -> updateShowCircle(isChecked));

        sbRate.setOnSeekBarChangeListener(this);
        updateFrequancy(Connection.getInstance().getFrequancyLocationUpdate());
        sbRate.setProgress(Connection.getInstance().getFrequancyLocationUpdate()/Const.BASE_STEP_FREQUENCY - 1);
        switchOldWariors.setChecked(Connection.getInstance().getShowOldWariors());
        switchShowCircle.setChecked(Connection.getInstance().getShowCircle());
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int frequancy = seekBar.getProgress() * Const.BASE_STEP_FREQUENCY + Const.BASE_STEP_FREQUENCY;
        updateFrequancy(frequancy);
        Log.d(TAG, "onProgressChanged: ");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Log.d(TAG, "onStartTrackingTouch: ");
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.d(TAG, "onStopTrackingTouch: ");
        int frequancy = seekBar.getProgress() * Const.BASE_STEP_FREQUENCY + Const.BASE_STEP_FREQUENCY;
        Connection.getInstance().setFrequancyLocationUpdate(frequancy);
        logHelper.writeLog("Set delay to " + frequancy, new Date());
    }


    private void updateFrequancy(int frequancy){
        tvStateRate.setText(String.format(getResources().
                getString(R.string.rate_location), Integer.toString(frequancy)));
    }

    private void updateShowOldWariors(Boolean b){
        Connection.getInstance().setShowOldWariors(b);
    }

    private void updateShowCircle(Boolean b){
        Connection.getInstance().setShowCircle(b);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rbGps:
                Connection.getInstance().setProvider(Const.PROVIDER_GPS);
                break;
            case R.id.rbNetwork:
                Connection.getInstance().setProvider(Const.PROVIDER_NETWORK);
                break;
        }
    }
}
