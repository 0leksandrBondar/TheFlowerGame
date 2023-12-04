package com.flowers.world;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.flowers.R;
import com.flowers.mapEntity.Flower;
import com.flowers.mapEntity.Snake;

import java.util.ArrayList;

public class GameState {
    @SuppressLint("StaticFieldLeak")
    private static GameState _instance;

    // TODO: refactor accsesAddSnake
    private boolean accsesAddSnake = false;
    private FrameLayout _map;
    private AppCompatActivity _gameActivity;
    private final Handler handler = new Handler();

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
        handler.post(automaticallyAddSnake);
    }

    @SuppressLint("SetTextI18n")
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

    private final Runnable automaticallyAddSnake = new Runnable() {
        @Override
        public void run() {
            if (accsesAddSnake) {
                addSnake();
            }
            handler.postDelayed(this, 5000);
        }
    };

    private void addFlower(float posX, float posY) {
        Flower newFlower = new Flower(_gameActivity);
        newFlower.setPosition(posX, posY);
        accsesAddSnake = true;
        _map.addView(newFlower);
    }

    private void addSnake() {
        Snake snake = new Snake(_gameActivity);
        _map.addView(snake);
    }

    private boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float y = event.getY();
            float x = event.getX();
            removeSnakeNode(x, y);
            if (PlayerState.getInstance().buyFlower())
                addFlower(x, y);
            updateCoinsLabel();
            return true;
        }
        return false;
    }

    private void removeSnakeNode(float touchX, float touchY) {
        for (int i = 0; i < _map.getChildCount(); ++i) {
            View child = _map.getChildAt(i);
            if (child instanceof Snake) {
                Snake snake = (Snake) child;
                for (Snake.Node node : snake.getNodes()) {
                    if (Math.abs(node.getPosX() - touchX) < 160 && Math.abs(node.getPosY() - touchY) < 160) {
                        snake.removeNode();
                        break;
                    }
                }
            }
        }
    }
}
