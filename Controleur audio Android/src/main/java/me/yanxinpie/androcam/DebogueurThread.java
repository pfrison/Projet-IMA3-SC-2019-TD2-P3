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

            // rafreshissement de l'état de la lecture
            // TODO this
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
