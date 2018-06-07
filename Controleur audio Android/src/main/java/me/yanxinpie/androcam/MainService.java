package me.yanxinpie.androcam;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MainService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        while (true) {
            if (Thread.interrupted()) break;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
