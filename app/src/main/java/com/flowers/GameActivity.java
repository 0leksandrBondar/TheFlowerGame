package com.flowers;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;

import com.flowers.mapEntity.Flower;
import com.flowers.world.GameState;
import com.flowers.world.PlayerState;

public class GameActivity extends AppCompatActivity {

    private final Handler checkFlowersHandler = new Handler();
    private final Handler increaseCoinsHandler = new Handler();

    private boolean isIncreasingCoins = false;

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
            } else if (!hasFlower && PlayerState.getInstance().getNumberCoins() < 50 && !isIncreasingCoins) {
                increaseCoinsHandler.post(increaseCoins);
                isIncreasingCoins = true;
            }

            checkFlowersHandler.postDelayed(this, 1000);
        }
    };


    private final Runnable increaseCoins = new Runnable() {
        @Override
        public void run() {
            PlayerState.getInstance().increaseNumberCoins();
            GameState.getInstance().updateCoinsLabel();
            increaseCoinsHandler.postDelayed(this, 500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        GameState.getInstance().setActivity(this);
        GameState.getInstance().detectTouchAction();
        checkFlowersHandler.post(checkFlowers);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        checkFlowersHandler.removeCallbacks(checkFlowers);
        increaseCoinsHandler.removeCallbacks(increaseCoins);
    }
}
