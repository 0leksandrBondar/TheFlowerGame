package com.flowers.world;

public class PlayerState {

    private int numberCoins = 150;

    private static PlayerState _instance;

    public boolean isPossibleCreateNewFlower() {
        if (numberCoins >= GameMode.getInstance().getFlowerPrice())
            return true;
        return false;
    }

    public void increaseNumberCoins() {
        ++numberCoins;
    }

    public static PlayerState getInstance() {
        if (_instance == null) {
            _instance = new PlayerState();
        }
        return _instance;
    }

    public int getNumberCoins() {
        return numberCoins;
    }

    public boolean buyFlower() {
        if (isPossibleCreateNewFlower()) {
            numberCoins -= GameMode.getInstance().getFlowerPrice();
            return true;
        }
        return false;
    }
}
