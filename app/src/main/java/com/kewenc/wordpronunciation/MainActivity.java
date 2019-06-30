/*
 * Copyright (c) 2019/6 - Present, KewenC.
 */

package com.kewenc.wordpronunciation;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity{

    private String sdCardPath = Environment.getExternalStorageDirectory() + "/";
    private String cachePath = sdCardPath + "PictureCache/";
    private static final String ADDR = "http://dict.youdao.com/dictvoice?type=1&audio=";
    private File file;
    private MediaPlayer mediaPlayer;
    private EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et = findViewById(R.id.et);
        mediaPlayer = new MediaPlayer();
    }

    private class DownloadFileTask extends AsyncTask<String,Integer,Integer>{

        @Override
        protected Integer doInBackground(String... strings) {
            String urlString = strings[0];
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = connection.getInputStream();
//                String fileName = urlString.substring(urlString.lastIndexOf("/")+1);
                File dir = new File(cachePath);
                if (!dir.exists()){
                    dir.mkdir();
                }
                file = new File(cachePath + et.getText().toString()+".mp3");
                if(!file.exists()){
                    file.createNewFile();
                }
                OutputStream outputStream = new FileOutputStream(file);
                byte[] buffer = new byte[1024*4];
                int byteRead = 0;
                while ((byteRead = inputStream.read(buffer)) != -1){
                    outputStream.write(buffer,0,byteRead);
                }
                outputStream.flush();
                outputStream.close();
                inputStream.close();
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            if (integer >0){
                Toast.makeText(MainActivity.this,"下载成功！",Toast.LENGTH_SHORT).show();
                try {
                    if (mediaPlayer.isPlaying()){
                        mediaPlayer.stop();
                    }
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(file.getAbsolutePath());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(MainActivity.this,"下载失败！",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onClick(View view){
        if (view.getId() == R.id.btnLoad){
            new DownloadFileTask().execute(ADDR+et.getText().toString());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
