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

            // verification de l'état du service
            if (isMyServiceRunning(MainService.class))
                mainActivity.tvEtatService.setText("Etat du service : ON");
            else
                mainActivity.tvEtatService.setText("Etat du service : OFF");

            // rafrechissement de l'état de la lecture
            if(MainService.telechargementResult != null)
                mainActivity.tvEtatLecture.setText("Valeur lue : " + MainService.telechargementResult);
            else
                mainActivity.tvEtatLecture.setText("Valeur lue : null");

            // rafrechissement de l'image décodée
            if(MainService.telechargementResult != null) {
                for(int i = 0; i < mainActivity.vImage.length; i++){
                    for(int j = 0; j < mainActivity.vImage[i].length; j++){
                        int color = (int) (MainService.image[i][j] * 0xFF);
                        int colorCode = 0xFF000000 + color << 4 + color << 2 + color;
                        mainActivity.vImage[i][j].setBackgroundColor(colorCode);
                    }
                }
            } else {
                for(int i = 0; i < mainActivity.vImage.length; i++) {
                    for (int j = 0; j < mainActivity.vImage[i].length; j++) {
                        if(i - j == 0 || i + j == 4)
                            mainActivity.vImage[i][j].setBackgroundColor(0xFFFF0000); // rouge
                        else
                            mainActivity.vImage[i][j].setBackgroundColor(0xFFFFFFFF); // blanc
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
