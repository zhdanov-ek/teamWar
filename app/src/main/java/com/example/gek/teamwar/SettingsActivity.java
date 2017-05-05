package com.example.gek.teamwar;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.gek.teamwar.Data.Const;
import com.example.gek.teamwar.Utils.Connection;

public class SettingsActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    public static final String TAG = "SETTINGS";
    private SeekBar sbRate;
    private TextView tvStateRate;
    private SharedPreferences mSharedPreferences;
    private SwitchCompat switchOldWariors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setSupportActionBar((Toolbar) findViewById(R.id.toolBar));
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        tvStateRate = (TextView) findViewById(R.id.tvStateRate);
        sbRate = (SeekBar) findViewById(R.id.sbRate);
        switchOldWariors = (SwitchCompat) findViewById(R.id.switchOldWariors);
        switchOldWariors.setOnCheckedChangeListener((buttonView, isChecked) -> updateShowOldWariors(isChecked));

        sbRate.setOnSeekBarChangeListener(this);
        updateFrequancy(Connection.getInstance().getFrequancyLocationUpdate());
        sbRate.setProgress(Connection.getInstance().getFrequancyLocationUpdate()/Const.BASE_STEP_FREQUENCY - 1);
        switchOldWariors.setChecked(Connection.getInstance().getShowOldWariors());
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
    }


    private void updateFrequancy(int frequancy){
        tvStateRate.setText(String.format(getResources().
                getString(R.string.rate_location), Integer.toString(frequancy)));
    }

    private void updateShowOldWariors(Boolean b){
        Connection.getInstance().setShowOldWariors(b);
    }
}
