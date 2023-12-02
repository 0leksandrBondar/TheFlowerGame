package com.flowers.world;

public class PlayerState {

    private int numberCoins = 150;
    private int flowerPrice = 50;
    private static PlayerState _instance;

    public boolean isPossibleCreateNewFlower() {
        if (numberCoins >= flowerPrice)
            return true;
        return false;
    }

    public void increaseNumberCoins()
    {
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
            numberCoins -= flowerPrice;
            return true;
        }
        return false;
    }
}
