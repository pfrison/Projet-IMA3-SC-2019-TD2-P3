package me.yanxinpie.androcam;

import android.app.ActivityManager;
import android.content.Context;

public class DebogueurThread extends Thread implements Runnable {
    private boolean stop = false;
    private MainActivity mainActivity;

    DebogueurThread(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    void stopSafe() {
        stop = true;
    }

    @Override
    public void run() {
        while (!stop) {
            if (Thread.interrupted()) break;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }

            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // verification de l'état du service
                    if (isMyServiceRunning(MainService.class))
                        mainActivity.tvEtatService.setText("Etat du service : ON");
                    else
                        mainActivity.tvEtatService.setText("Etat du service : OFF");

                    // rafrechissement de l'état de la lecture
                    if(MainService.telechargementResult == null || MainService.telechargementResult.equals(""))
                        mainActivity.tvEtatLecture.setText("Valeur lue : null");
                    else
                        mainActivity.tvEtatLecture.setText("Valeur lue : \"" + MainService.telechargementResult + "\"");

                    //rafrechissement de le derniere erreur
                    if(MainService.derniereErreur != null && MainService.derniereErreur != mainActivity.tvErreur.getText())
                        mainActivity.tvErreur.setText(MainService.derniereErreur);
                }
            });

            // rafrechissement de l'image décodée
            if(MainService.telechargementResult == null || MainService.telechargementResult.equals("")) {
                for(int i = 0; i < mainActivity.vImage.length; i++) {
                    for (int j = 0; j < mainActivity.vImage[i].length; j++) {
                        if(i - j == 0 || i + j == 4)
                            mainActivity.vImage[i][j].setBackgroundColor(0xFFFF0000); // rouge
                        else
                            mainActivity.vImage[i][j].setBackgroundColor(0xFFFFFFFF); // blanc
                    }
                }
            } else {
                for(int i = 0; i < mainActivity.vImage.length; i++){
                    for(int j = 0; j < mainActivity.vImage[i].length; j++){
                        int color = (int) (MainService.image[i][j] * 255d);
                        int colorCode = 0xFF000000 | color << 16 | color << 8 | color;
                        mainActivity.vImage[i][j].setBackgroundColor(colorCode);
                    }
                }
            }
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) mainActivity.getSystemService(Context.ACTIVITY_SERVICE);
        assert manager != null;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
