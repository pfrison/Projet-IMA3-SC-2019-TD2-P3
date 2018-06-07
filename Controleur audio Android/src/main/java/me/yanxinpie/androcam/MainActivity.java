package me.yanxinpie.androcam;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener {
    TextView tvEtatService;
    TextView tvEtatLecture;
    ImageButton btPrev;
    ImageButton btPlay;
    ImageButton btNext;
    private DebogueurThread dbThread = new DebogueurThread(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // enregistre les TextView et les ImageButton
        tvEtatService = findViewById(R.id.etatService);
        tvEtatLecture = findViewById(R.id.etatlecture);
        btPrev = findViewById(R.id.prev);
        btPlay = findViewById(R.id.play);
        btNext = findViewById(R.id.next);

        // ajout des listeners sur le ImageButton
        btPrev.setOnClickListener(this);
        btPlay.setOnClickListener(this);
        btNext.setOnClickListener(this);

        // démarre le thread de rafrechisseemnt des informations sur l'écran de deboguage
        dbThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbThread.stopSafe();
    }

    @Override
    public void onClick(View v) {
        if (v == btPrev || v == btPlay || v == btNext) {
            int commande = -1;
            if (v == btPrev)
                commande = KeyEvent.KEYCODE_MEDIA_PREVIOUS;
            else if (v == btPlay)
                commande = KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE;
            else //btNext
                commande = KeyEvent.KEYCODE_MEDIA_NEXT;
            envoisCommandeMusicPlayer(commande);
        }
    }

    private void envoisCommandeMusicPlayer(int commande) {
        long temps = SystemClock.uptimeMillis();

        Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
        KeyEvent downEvent = new KeyEvent(temps, temps, KeyEvent.ACTION_DOWN, commande, 0);
        downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent);
        sendOrderedBroadcast(downIntent, null);

        Intent upIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
        KeyEvent upEvent = new KeyEvent(temps, temps, KeyEvent.ACTION_UP, commande, 0);
        upIntent.putExtra(Intent.EXTRA_KEY_EVENT, upEvent);
        sendOrderedBroadcast(upIntent, null);
    }
}
