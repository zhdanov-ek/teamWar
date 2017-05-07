package com.example.gek.teamwar.Utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Date;

import static android.content.Context.MODE_APPEND;

/**
 * Created by gek on 07.05.17.
 */

public class LogHelper {
    private static final String TAG = "LOG_HELPER";
    private final static String FILE_NAME = "teamWar.log";
    private String pathAbsolute;
    private Context ctx;

    public LogHelper(Context ctx) {
        this.ctx = ctx;
        pathAbsolute = ctx.getFilesDir().getAbsolutePath() + "/" + FILE_NAME;
    }


    public void writeLog(String mes, Date date){
        try {
            // отрываем поток для записи
            // BufferedWriter - класс, позволяющий записывать текст в указанный поток
            // Ему на вход подаем
            BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(ctx.openFileOutput(FILE_NAME, MODE_APPEND)));
            bw.write(formatDate(date) + " | " + mes + "\n");
            // закрываем поток
            bw.close();
            Log.d(TAG, "writeLog: write " + mes);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String readLog(){
        String result = "";
        try {
            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    ctx.openFileInput(FILE_NAME)));
            String str = "";
            // читаем содержимое
            while ((str = br.readLine()) != null) {
                result = result + str +"\n";
            }
            Log.d(TAG, "readLog: succes");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void clearLog(){
        File removeFile = new File(pathAbsolute);
        if (removeFile.exists()) {
            removeFile.delete();
            Log.d(TAG, "clearLog: removed file " + removeFile.getName());
        }
    }

    private String formatDate(Date date){
        return date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds();

    }
}
