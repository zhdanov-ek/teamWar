package com.example.gek.teamwar;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.gek.teamwar.Utils.LogHelper;

public class LogActivity extends AppCompatActivity {
    private TextView tvLog;
    private FloatingActionButton fabClear;
    private LogHelper logHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        logHelper = new LogHelper(getBaseContext());

        tvLog = (TextView) findViewById(R.id.tvLog);
        tvLog.setText(logHelper.readLog());

        fabClear = (FloatingActionButton) findViewById(R.id.fabClear);
        fabClear.setOnClickListener(v -> clickFab());
    }

    private void clickFab(){
        if (tvLog.getText().length() > 0){
            logHelper.clearLog();
            tvLog.setText("");
            fabClear.setImageResource(R.drawable.ic_refresh);
        } else {
            String log = logHelper.readLog();
            if ((log != null) && (log.length() > 0)){
                tvLog.setText(log);
                fabClear.setImageResource(R.drawable.ic_delete);
            }
        }

    }

}
