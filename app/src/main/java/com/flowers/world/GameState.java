package com.flowers.world;

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
    public int mapWidth()
    {
        return _map.getWidth();
    }
    public int mapHeight()
    {
        return _map.getHeight();
    }
    public void setActivity(AppCompatActivity activity) {
        _gameActivity = activity;
        _map = _gameActivity.findViewById(R.id.map_layout);
    }

    public void detectTouchAction() {
        _gameActivity.findViewById(R.id.map_layout).setOnTouchListener(this::onTouch);
    }

    private boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && PlayerState.getInstance().buyFlower()) {
            float x = event.getX();
            float y = event.getY();
            // Flower newFlower = new Flower(_gameActivity);
            Snake snake = new Snake(_gameActivity);
            snake.updatePosition(x, y);
            //newFlower.updatePosition(x+20, y+20);
            updateCoinsLabel();
            //_map.addView(newFlower);
            _map.addView(snake);
            return true;
        }
        return false;
    }
    public void updateCoinsLabel()
    {
        TextView tv = _gameActivity.findViewById(R.id.coinsCount_text);
        tv.setText("Coins: " + PlayerState.getInstance().getNumberCoins());
    }
    public static GameState getInstance() {
        if (_instance == null) {
            _instance = new GameState();
        }
        return _instance;
    }
}
