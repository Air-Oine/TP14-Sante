package com.example.admin.tp_sante;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity {

    public static final String PROGRESS = "PROGRESS";
    public static final int PROGRESSION = 1;

    private ProgressBar progressBar;
    private ProgressBar progressBar2;

    private AtomicBoolean isRunning = new AtomicBoolean(false);
    private AtomicBoolean isPausing = new AtomicBoolean(false);

    //Affiche la progression
    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if(msg.what == PROGRESSION) {
                progressBar.incrementProgressBy(1);
            }
        }
    };

    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                Message message;

                for (int i = 0; i<101 && isRunning.get(); i++){
                    while (isPausing.get() && isRunning.get()) {
                        Thread.sleep(1000);
                    }
                    //On simule l'execution d'un code
                    Thread.sleep(100);

                    message = handler.obtainMessage();
                    message.what = PROGRESSION;

                    handler.sendMessage(message);
                }

                isRunning.set(false);
                isPausing.set(true);

            } catch (Throwable t) {

            }
        }
    });

    class Traitement extends AsyncTask<Void, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Toast.makeText(getApplicationContext(), "Démarrage...", Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            //Téléchargement terminé
            Toast.makeText(getApplicationContext(), "Fini !", Toast.LENGTH_LONG).show();

            Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(1000);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            progressBar2.setProgress(values[0].intValue());
        }

        @Override
        protected String doInBackground(Void... voids) {

            for (int i = 0; i<101; i++){
                if(isCancelled()) {
                    break;
                }

                //On simule l'execution d'un code
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                publishProgress(i);
            }

            return "";
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar2 = (ProgressBar) findViewById(R.id.progressBar2);

        progressBar.setProgress(0);
        progressBar.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
        progressBar2.setProgress(0);

        isRunning.set(true);
        isPausing.set(false);
        thread.start();

        Traitement traitement = new Traitement();
        traitement.execute();
    }

    @Override
    protected void onPause() {
        super.onPause();

        isPausing.set(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        isPausing.set(false);
    }
}
