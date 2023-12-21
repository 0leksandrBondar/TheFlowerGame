package com.flowers;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.flowers.mapEntity.Flower;
import com.flowers.world.GameMode;
import com.flowers.world.GameState;
import com.flowers.world.PlayerState;

public class GameActivity extends AppCompatActivity {
    private SharedPreferences prefs;
    private boolean isIncreasingCoins = false;
    private final Handler checkFlowersHandler = new Handler();
    private final Handler increaseCoinsHandler = new Handler();
    private final int minuteInMillis = 60000;

    private final Runnable checkFlowers = new Runnable() {
        @Override
        public void run() {
            FrameLayout map = GameState.getInstance().getMap();

            boolean hasFlower = false;
            for (int i = 0; i < map.getChildCount(); ++i) {
                View child = map.getChildAt(i);
                if (child instanceof Flower) {
                    hasFlower = true;
                    break;
                }
            }

            if (hasFlower) {
                increaseCoinsHandler.removeCallbacks(increaseCoins);
                isIncreasingCoins = false;
            } else if (PlayerState.getInstance().getNumberCoins() < GameMode.getInstance().getFlowerPrice() && !isIncreasingCoins) {
                increaseCoinsHandler.post(increaseCoins);
                isIncreasingCoins = true;
            }

            checkFlowersHandler.postDelayed(this, GameMode.getInstance().getFlowerCheckDelayMillis());
        }
    };

    private final Runnable increaseCoins = new Runnable() {
        @Override
        public void run() {
            PlayerState.getInstance().increaseNumberOfCoins();
            GameState.getInstance().updateCoinsLabel();
            increaseCoinsHandler.postDelayed(this, GameMode.getInstance().getCoinIncreaseDelayMillis());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        TextView textName = findViewById(R.id.name);
        textName.setText(PlayerState.getInstance().getName());

        GameState.getInstance().setActivity(this);
        GameState.getInstance().detectTouchAction();
        GameState.getInstance().updateCoinsLabel();

        checkFlowersHandler.post(checkFlowers);
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateGameData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveGameData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        checkFlowersHandler.removeCallbacks(checkFlowers);
        increaseCoinsHandler.removeCallbacks(increaseCoins);
    }

    private void scheduleNotification(int delay) {
        Intent notificationIntent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    private void updateGameData() {
        prefs = getSharedPreferences("GamePrefs", MODE_PRIVATE);
        int savedCoins = prefs.getInt("numberCoins", 0);

        if (savedCoins < GameMode.getInstance().getFlowerPrice()) {
            long lastCloseTime = prefs.getLong("lastCloseTime", 0);
            long currentTime = System.currentTimeMillis();

            if (lastCloseTime != 0) {
                long differenceInMinutes = (currentTime - lastCloseTime) / minuteInMillis;
                int coinsToAdd = (int) differenceInMinutes;
                PlayerState.getInstance().setNumberCoins(savedCoins);
                PlayerState.getInstance().addCoins(coinsToAdd);
                GameState.getInstance().updateCoinsLabel();
            }
        }
    }

    private void saveGameData() {
        final int flowerPrice = GameMode.getInstance().getFlowerPrice();
        final int countPlayerCoins = PlayerState.getInstance().getNumberCoins();

        if (countPlayerCoins < flowerPrice) {
            prefs = getSharedPreferences("GamePrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong("lastCloseTime", System.currentTimeMillis());
            editor.putInt("numberCoins", countPlayerCoins);
            editor.apply();

            final int delay = (flowerPrice - countPlayerCoins) * (1000 * 60);
            scheduleNotification(delay);
        }
    }
}
