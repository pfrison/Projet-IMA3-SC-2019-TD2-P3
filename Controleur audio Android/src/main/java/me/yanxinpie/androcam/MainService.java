package me.yanxinpie.androcam;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

public class MainService extends Service {
    static String URL = "";

    static String derniereErreur = null;

    static String telechargementResult = null;
    static double[][] image = new double[5][5];

    private int[][] pixelValMax = new int[][]{
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0}
    };
    private int[][] pixelValMin = new int[][]{
            {1024, 1024, 1024, 1024, 1024},
            {1024, 1024, 1024, 1024, 1024},
            {1024, 1024, 1024, 1024, 1024},
            {1024, 1024, 1024, 1024, 1024},
            {1024, 1024, 1024, 1024, 1024}
    };

    private long centreTime = -1;
    private long droiteGaucheTime = -1;
    private long gaucheDroiteTime = -1;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // fait en sorte que le service ne se fasse pas detruire
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (Thread.interrupted()) break;
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }

                    // telechargement http de l'image
                    new AsyncTelechargement().execute(URL);

                    // String -> image (double[5][5])
                    if(MainService.telechargementResult != null && !MainService.telechargementResult.equals("")) {
                        String[] split = telechargementResult.split("-");

                        // test le String recu
                        if (split.length != 25) {
                            derniereErreur = "Le String recu n'est pas compatible (pas le bon format)";
                            Log.e("MainService", derniereErreur);
                            telechargementResult = null;
                            continue;
                        }
                        // String[25] -> int[5][5]
                        int[][] imageint = new int[5][5];
                        try {
                            for (int i = 0; i < split.length; i++)
                                imageint[i / 5][i % 5] = Integer.parseInt(split[i]);
                        } catch (NumberFormatException e) {
                            derniereErreur = "Le String recu n'est pas compatible (pas des entiers)";
                            Log.e("MainService", derniereErreur);
                            telechargementResult = null;
                            break;
                        }

                        // contraste
                        for (int i = 0; i < imageint.length; i++) {
                            for (int j = 0; j < imageint[i].length; j++) {
                                if (pixelValMax[i][j] < imageint[i][j])
                                    pixelValMax[i][j] = imageint[i][j];
                                if (pixelValMin[i][j] > imageint[i][j])
                                    pixelValMin[i][j] = imageint[i][j];
                            }
                        }

                        //int[5][5] -> image (double[5][5])
                        for (int i = 0; i < imageint.length; i++){
                            for (int j = 0; j < imageint[i].length; j++) {
                                if (pixelValMax[i][j] - pixelValMin[i][j] != 0)
                                    image[i][j] = (imageint[i][j] - pixelValMin[i][j]) / (pixelValMax[i][j] - pixelValMin[i][j]);
                                else
                                    image[i][j] = 0d;
                            }
                        }
                    }

                    // Interpretation
                    if(MainService.telechargementResult != null && !MainService.telechargementResult.equals("")) {
                        // centre = play/pause
                        boolean centre =   image[1][1] < 0.5 && image[1][2] < 0.5 && image[1][3] < 0.5;
                        centre = centre && image[2][1] < 0.5 && image[2][2] < 0.5 && image[2][3] < 0.5;
                        centre = centre && image[3][1] < 0.5 && image[3][2] < 0.5 && image[3][3] < 0.5;
                        if(centre){
                            centreTime = SystemClock.currentThreadTimeMillis();
                        }else if(centreTime != -1 && SystemClock.currentThreadTimeMillis() - centreTime < 1000){
                            //                       ^^^ l'action doit etre faite en moins de 1 seconde
                            // oommande play/pause
                            MainActivity.envoisCommandeMusicPlayer(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, getBaseContext());
                        }

                        boolean gauche =   image[0][0] < 0.5;
                        gauche = gauche && image[0][1] < 0.5;
                        gauche = gauche && image[0][2] < 0.5;
                        gauche = gauche && image[0][3] < 0.5;
                        gauche = gauche && image[0][4] < 0.5;

                        boolean droite =   image[4][0] < 0.5;
                        droite = droite && image[4][1] < 0.5;
                        droite = droite && image[4][2] < 0.5;
                        droite = droite && image[4][3] < 0.5;
                        droite = droite && image[4][4] < 0.5;

                        // droite vers gauche = precedent
                        if(droite && !gauche){
                            droiteGaucheTime = SystemClock.currentThreadTimeMillis();
                        }else if(gauche && !droite
                                && droiteGaucheTime != 1 && SystemClock.currentThreadTimeMillis() - droiteGaucheTime < 1000){
                            //                              ^^^ l'action doit etre faite en moins de 1 seconde
                            // oommande prev
                            MainActivity.envoisCommandeMusicPlayer(KeyEvent.KEYCODE_MEDIA_PREVIOUS, getBaseContext());
                        }

                        // gauche vers droite = suivant
                        if(gauche && !droite){
                            gaucheDroiteTime = SystemClock.currentThreadTimeMillis();
                        }else if(droite && !gauche
                                && gaucheDroiteTime != 1 && SystemClock.currentThreadTimeMillis() - gaucheDroiteTime < 1000){
                            //                              ^^^ l'action doit etre faite en moins de 1 seconde
                            // oommande prev
                            MainActivity.envoisCommandeMusicPlayer(KeyEvent.KEYCODE_MEDIA_NEXT, getBaseContext());
                        }
                    }
                }
            }
        }).start();
    }
}

class AsyncTelechargement extends AsyncTask<String, Void, String> {
    protected String doInBackground(String... urls) {
        if(urls[0] == null || urls[0].equals(""))
            return null;

        String result = "";
        try {
            URL url = new URL(urls[0]);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(800);
            urlConnection.setReadTimeout(1000);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new BufferedInputStream(urlConnection.getInputStream())));
            String line;
            result = "";
            while ((line = bufferedReader.readLine()) != null)
                result += line + "\n";
            if (result.length() > 0)
                result = result.substring(0, result.length() - 1);

            bufferedReader.close();
            urlConnection.disconnect();
        } catch (SocketTimeoutException | FileNotFoundException | MalformedURLException ignored) {
            MainService.derniereErreur = "Erreur de connection";
            Log.e("MainService", MainService.derniereErreur);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    protected void onProgressUpdate(Void... progress) {}

    protected void onPostExecute(String result) {
        MainService.telechargementResult = result;
    }
}