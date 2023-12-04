package com.flowers.world;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.flowers.R;
import com.flowers.mapEntity.Flower;
import com.flowers.mapEntity.Snake;

public class GameState {

    private FrameLayout _map;
    private AppCompatActivity _gameActivity;
    private static GameState _instance;

    public FrameLayout getMap() {
        return _map;
    }

    public int mapWidth() {
        return _map.getWidth();
    }

    public int mapHeight() {
        return _map.getHeight();
    }

    public void setActivity(AppCompatActivity activity) {
        _gameActivity = activity;
        _map = _gameActivity.findViewById(R.id.map_layout);
    }

    public AppCompatActivity getGameActivity() {
        return _gameActivity;
    }

    public void detectTouchAction() {
        _gameActivity.findViewById(R.id.map_layout).setOnTouchListener(this::onTouch);
        startAddingSnakes();
    }

    private Handler handler = new Handler();
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            if (GameMode.getInstance().isPossibleAddSnake()) {
                addSnake();
            }
            handler.postDelayed(this, 5000);
        }
    };

    public void addFlower(float posX, float posY) {
        Flower newFlower = new Flower(_gameActivity);
        newFlower.setPosition(posX, posY);
        _map.addView(newFlower);
    }

    public void addSnake() {
        Snake snake = new Snake(_gameActivity);
        _map.addView(snake);
    }

    private boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && PlayerState.getInstance().buyFlower()) {
            float x = event.getX();
            float y = event.getY();
            addFlower(x, y);
            updateCoinsLabel();
            return true;
        }
        return false;
    }

    public void updateCoinsLabel() {
        TextView tv = _gameActivity.findViewById(R.id.coinsCount_text);
        tv.setText("Coins: " + PlayerState.getInstance().getNumberCoins());
    }

    public static GameState getInstance() {
        if (_instance == null) {
            _instance = new GameState();
        }
        return _instance;
    }

    public void startAddingSnakes() {
        handler.post(runnableCode);
    }

    public void stopAddingSnakes() {
        handler.removeCallbacks(runnableCode);
    }
}
