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

public class GameState {
    @SuppressLint("StaticFieldLeak")
    private static GameState _instance;

    private double delayAddingSnakes = 5000;
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

    public void updateSnakeSpeed() {
        FrameLayout map = GameState.getInstance().getMap();

        for (int i = 0; i < map.getChildCount(); ++i) {
            View child = map.getChildAt(i);
            if (child instanceof Snake) {
                Snake snake = (Snake) child;
                float originalSpeed = snake.getSpeed();

                snake.setSpeed(originalSpeed * 2);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        snake.resetSpeed();
                    }
                }, 10000);
            }
        }
    }

    private final Runnable automaticallyAddSnake = new Runnable() {
        private long lastRunTime = System.currentTimeMillis();
        private int tenSeconds = 10000;

        @Override
        public void run() {
            if (GameMode.getInstance().isPossibleAddSnake()) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastRunTime >= tenSeconds) {
                    delayAddingSnakes *= 1.05;// increase delay on 5%
                    lastRunTime = currentTime;
                }
                addSnake();
            }
            handler.postDelayed(this, (long) delayAddingSnakes);
        }
    };

    private void addFlower(float posX, float posY) {
        Flower newFlower = new Flower(_gameActivity);
        newFlower.setPosition(posX, posY);
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
            if (tryRemoveSnakeNode(x, y))
                return true;
            else if (PlayerState.getInstance().buyFlower())
                addFlower(x, y);
            updateCoinsLabel();
            return true;
        }
        return false;
    }

    private boolean tryRemoveSnakeNode(float touchX, float touchY) {
        for (int i = 0; i < _map.getChildCount(); ++i) {
            View child = _map.getChildAt(i);
            if (child instanceof Snake) {
                Snake snake = (Snake) child;
                for (Snake.Node node : snake.getNodes()) {
                    if (Math.abs(node.getPosX() - touchX) < 160 && Math.abs(node.getPosY() - touchY) < 160) {
                        snake.removeNode();
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
