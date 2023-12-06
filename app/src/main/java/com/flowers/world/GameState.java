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

    private final Runnable automaticallyAddSnake = new Runnable() {
        private long lastRunTime = System.currentTimeMillis();

        @Override
        public void run() {
            if (isPossibleAddSnake()) {
                long currentTime = System.currentTimeMillis();
                int tenSeconds = 10000;
                if (currentTime - lastRunTime >= tenSeconds) {
                    delayAddingSnakes *= 1.05;// increase delay on 5%
                    lastRunTime = currentTime;
                }
                addSnake();
            }
            handler.postDelayed(this, (long) delayAddingSnakes);
        }
    };

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

    public void updateSnakesSpeed() {
        FrameLayout map = getMap();

        for (int i = 0; i < map.getChildCount(); ++i) {
            View child = map.getChildAt(i);
            if (child instanceof Snake) {
                Snake snake = (Snake) child;
                float originalSpeed = snake.getSpeed();

                snake.setSpeed(originalSpeed * 2);
                new Handler().postDelayed(snake::resetSpeed, 10000);
            }
        }
    }

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
        int width = GameMode.getInstance().getBitmapWidth();
        int height = GameMode.getInstance().getBitmapHeight();

        for (int i = 0; i < _map.getChildCount(); ++i) {
            View child = _map.getChildAt(i);
            if (child instanceof Snake) {
                Snake snake = (Snake) child;
                for (Snake.Node node : snake.getNodes()) {
                    if (Math.abs(node.getPosX() - touchX) < width && Math.abs(node.getPosY() - touchY) < height) {
                        snake.removeNode();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isPossibleAddSnake() {
        if (GameMode.getInstance().wasFlowerAdded())
            return true;
        FrameLayout map = getMap();
        for (int i = 0; i < map.getChildCount(); i++) {
            View child = map.getChildAt(i);
            if (child instanceof Flower) {
                GameMode.getInstance().setWasFlowerAdded(true);
                break;
            }
        }
        return GameMode.getInstance().wasFlowerAdded();
    }

    public boolean isPossibleAddNode(Snake snake) {
        int snakeLength = snake.getNodes().size();
        return checkCountOfFlowersOnMap() && snakeLength < snake.getMaxSnakeLength() ||
                !checkCountOfFlowersOnMap() && snakeLength < snake.getMaxSnakeLength() / 2;
    }

    public boolean checkCountOfFlowersOnMap() {
        FrameLayout map = getMap();
        int flowerCount = 0;
        for (int i = 0; i < map.getChildCount(); ++i) {
            View child = map.getChildAt(i);
            if (child instanceof Flower) {
                ++flowerCount;
                if (flowerCount >= 2) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isPossibleToCreateNewFlower() {
        return PlayerState.getInstance().getNumberCoins() >= GameMode.getInstance().getFlowerPrice();
    }

}
