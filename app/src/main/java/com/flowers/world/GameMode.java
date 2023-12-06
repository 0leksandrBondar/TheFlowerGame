package com.flowers.world;

public class GameMode {
    private static GameMode _instance;
    private final int _flowerPrice = 50;
    private final int _flowerCheckDelayMillis = 1000;
    private final int _flowerStateChangeDelayMillis = 3000;
    private final int _coinIncreaseDelayMillis = 60000;
    private final int _coinUpdateIntervalMillis = 3000;
    private final int _snakeNodeIncreaseIntervalMillis = 3000;
    private final int _bitmapHeight = 160;
    private final int _bitmapWidth = 160;
    private final float _distBetweenNodes = 110;
    private float _delayAddingSnakes = 5000;


    private boolean _wasFlowerAdded = false;

    public int getFlowerPrice() {
        return _flowerPrice;
    }

    public int getFlowerCheckDelayMillis() {
        return _flowerCheckDelayMillis;
    }

    public int getCoinIncreaseDelayMillis() {
        return _coinIncreaseDelayMillis;
    }

    public int getSnakeNodeIncreaseIntervalMillis() {
        return _snakeNodeIncreaseIntervalMillis;
    }

    public int getCoinUpdateIntervalMillis() {
        return _coinUpdateIntervalMillis;
    }

    public static GameMode getInstance() {
        if (_instance == null) {
            _instance = new GameMode();
        }
        return _instance;
    }

    public int getBitmapWidth() {
        return _bitmapWidth;
    }

    public int getBitmapHeight() {
        return _bitmapHeight;
    }

    public int getFlowerStateChangeDelayMillis() {
        return _flowerStateChangeDelayMillis;
    }

    public float getDistBetweenNodes() {
        return _distBetweenNodes;
    }

    public void setWasFlowerAdded(boolean isAdded) {
        _wasFlowerAdded = isAdded;
    }

    public boolean wasFlowerAdded() {
        return _wasFlowerAdded;
    }

    public float getDelayAddingSnakes() {
        return _delayAddingSnakes;
    }
}
