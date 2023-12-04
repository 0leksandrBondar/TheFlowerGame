package com.flowers.world;

import android.view.View;
import android.widget.FrameLayout;

import com.flowers.mapEntity.Flower;

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

    public static GameMode getInstance() {
        if (_instance == null) {
            _instance = new GameMode();
        }
        return _instance;
    }
}
