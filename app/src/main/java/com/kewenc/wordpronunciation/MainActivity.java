package com.kewenc.wordpronunciation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private URL url;
    private String sdCardPath = Environment.getExternalStorageDirectory() + "/";
    private String cachePath = sdCardPath + "pictureCache/";
    private static final String ADDR = "http://dict.youdao.com/dictvoice?type=1&audio=";
    private File file;
    private AppCompatImageView img;
    private MediaPlayer mediaPlayer;
    private EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = findViewById(R.id.img);
        et = findViewById(R.id.et);
        mediaPlayer = new MediaPlayer();
//        new DownloadFileTask().execute("https://image.baidu.com/search/down?tn=download&ipn=dwnl&word=download&ie=utf8&fr=result&url=http%3A%2F%2Fimg4.duitang.com%2Fuploads%2Fitem%2F201405%2F17%2F20140517213641_TQBMu.thumb.600_0.jpeg&thumburl=https%3A%2F%2Fss0.bdstatic.com%2F70cFuHSh_Q1YnxGkpoWK1HF6hhy%2Fit%2Fu%3D1397409047%2C955032347%26fm%3D26%26gp%3D0.jpg");
//        new DownloadFileTask().execute("http://dict.youdao.com/dictvoice?type=1&audio=abandon");
    }

    private class DownloadFileTask extends AsyncTask<String,Integer,Integer>{

        @Override
        protected Integer doInBackground(String... strings) {
            String urlString = strings[0];
            try {
                url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = connection.getInputStream();
//                String fileName = urlString.substring(urlString.lastIndexOf("/")+1);
//                String fileName = urlString.substring(urlString.lastIndexOf("/")+1);
                File dir = new File(cachePath);
                if (!dir.exists()){
                    dir.mkdir();
                }
                file = new File(cachePath + et.getText().toString()+".mp3");
                file.createNewFile();
                OutputStream outputStream = new FileOutputStream(file);
                byte[] buffer = new byte[1024*4];
                while (inputStream.read(buffer) != -1){
                    outputStream.write(buffer);
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
                Log.e("TAGF","onPostExecute");
//                Drawable d = Drawable.createFromPath(file.getPath());
//                img.setImageDrawable(d);
                try {
                    mediaPlayer.setDataSource(file.getAbsolutePath());
                    mediaPlayer.prepareAsync();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void onClick(View view){
        new DownloadFileTask().execute(ADDR+et.getText().toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }
}
