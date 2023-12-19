package com.flowers;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.EditText;

import com.flowers.world.PlayerState;

public class MainActivity extends AppCompatActivity {

    private Button _runButton;

    @Override
    protected void onDestroy() {
        super.onDestroy();

        int countPlayerCoins = PlayerState.getInstance().getNumberCoins();
        if (countPlayerCoins < 50) {
            int delay = (50 - countPlayerCoins) * 1000;
            scheduleNotification(delay);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, GameActivity.class);

        _runButton = findViewById(R.id.runButton);

        _runButton.setOnClickListener(v -> {
            startActivity(intent);
            EditText editText = findViewById(R.id.editText);
            PlayerState.getInstance().setName(editText.getText().toString());
        });
    }

    private void scheduleNotification(int delay) {
        Intent notificationIntent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }
}