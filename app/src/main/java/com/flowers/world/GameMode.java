package com.flowers.world;

import android.view.View;
import android.widget.FrameLayout;

import com.flowers.mapEntity.Flower;
import com.flowers.mapEntity.Snake;

public class GameMode {
    private static GameMode _instance;

    public boolean isPossibleAddSnake() {
        FrameLayout map = GameState.getInstance().getMap();
        for (int i = 0; i < map.getChildCount(); i++) {
            View child = map.getChildAt(i);
            if (child instanceof Flower) {
                return true;
            }
        }
        return false;
    }

    public boolean isPossibleAddNode(Snake snake) {
        int snakeLength = snake.getNodes().size();
        if (hasMoreThanTwoFlowers() && snakeLength < snake.getMaxSnakeLength() ||
                !hasMoreThanTwoFlowers() && snakeLength < snake.getMaxSnakeLength() / 2)
            return true;
        return false;
    }

    public boolean hasMoreThanTwoFlowers() {
        FrameLayout map = GameState.getInstance().getMap();
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


    public static GameMode getInstance() {
        if (_instance == null) {
            _instance = new GameMode();
        }
        return _instance;
    }
}
