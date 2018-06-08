package me.yanxinpie.androcam;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainService extends Service {
    private static final String URL = "";

    static String telechargementResult = null;
    static double[][] image = new double[5][5];

    private static int[][] pixelValMax = new int[][]{
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0}
    };
    private static int[][] pixelValMin = new int[][]{
            {1024, 1024, 1024, 1024, 1024},
            {1024, 1024, 1024, 1024, 1024},
            {1024, 1024, 1024, 1024, 1024},
            {1024, 1024, 1024, 1024, 1024},
            {1024, 1024, 1024, 1024, 1024}
    };

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
            if(telechargementResult != null){
                String[] split = telechargementResult.split("-");

                // test le String recu
                if(split.length != 25){
                    Log.e("MainService", "Le String recu n'est pas compatible (pas le bon format)");
                    telechargementResult = null;
                    continue;
                }
                // String[25] -> int[5][5]
                int[][] imageint = new int[5][5];
                try{
                    for(int i = 0; i < split.length; i++)
                        imageint[i/5][i%5] = Integer.parseInt(split[i]);
                } catch (NumberFormatException e){
                    Log.e("MainService", "Le String recu n'est pas compatible (pas des entiers)");
                    telechargementResult = null;
                    break;
                }

                // contraste
                for(int i = 0; i < imageint.length; i++){
                    for (int j = 0; j < imageint[i].length; j++){
                        if(pixelValMax[i][j] < imageint[i][j])
                            pixelValMax[i][j] = imageint[i][j];
                        if(pixelValMin[i][j] > imageint[i][j])
                            pixelValMin[i][j] = imageint[i][j];
                    }
                }

                //int[5][5] -> image (double[5][5])
                for(int i = 0; i < imageint.length; i++)
                    for (int j = 0; j < imageint[i].length; j++)
                        image[i][j] = (imageint[i][j] - pixelValMin[i][j]) / (pixelValMax[i][j] - pixelValMin[i][j]);
            }
        }
    }
}

class AsyncTelechargement extends AsyncTask<String, Void, String> {
    protected String doInBackground(String... urls) {
        String result = "";
        try {
            URL url = new URL(urls[0]);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(200);
            urlConnection.setReadTimeout(500);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new BufferedInputStream(urlConnection.getInputStream())));
            String line = "";
            result = "";
            while ((line = bufferedReader.readLine()) != null)
                result += line + "\n";
            if(result.length() > 0)
                result = result.substring(0, result.length() - 1);

            bufferedReader.close();
            urlConnection.disconnect();
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