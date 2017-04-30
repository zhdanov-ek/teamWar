package com.example.gek.teamwar;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setSupportActionBar((Toolbar) findViewById(R.id.toolBar));
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        tvStateRate = (TextView) findViewById(R.id.tvStateRate);
        sbRate = (SeekBar) findViewById(R.id.sbRate);

        sbRate.setOnSeekBarChangeListener(this);
        updateFrequancy(Connection.getInstance(this).getFrequancyLocationUpdate());
        sbRate.setProgress(Connection.getInstance(this).getFrequancyLocationUpdate()/20 - 1);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int frequancy = seekBar.getProgress()*20 + 20;
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
        int frequancy = seekBar.getProgress()*20 + 20;
        Connection.getInstance(this).setFrequancyLocationUpdate(frequancy);
    }


    private void updateFrequancy(int frequancy){
        tvStateRate.setText(String.format(getResources().
                getString(R.string.rate_location), Integer.toString(frequancy)));
    }
}
