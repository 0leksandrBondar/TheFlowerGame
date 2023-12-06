package com.flowers;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.flowers.mapEntity.Flower;
import com.flowers.world.GameMode;
import com.flowers.world.GameState;
import com.flowers.world.PlayerState;

public class GameActivity extends AppCompatActivity {
    private boolean isIncreasingCoins = false;
    private final Handler checkFlowersHandler = new Handler();
    private final Handler increaseCoinsHandler = new Handler();

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
    protected void onDestroy() {
        super.onDestroy();
        checkFlowersHandler.removeCallbacks(checkFlowers);
        increaseCoinsHandler.removeCallbacks(increaseCoins);
    }
}
