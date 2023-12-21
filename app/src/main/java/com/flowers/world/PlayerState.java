package com.flowers.world;

public class PlayerState {

    private int _numberCoins = 150;
    private String _name;

    private static PlayerState _instance;

    public void increaseNumberOfCoins() {
        ++_numberCoins;
    }

    public void addCoins(int coins) {
        _numberCoins += coins;
    }

    public static PlayerState getInstance() {
        if (_instance == null) {
            _instance = new PlayerState();
        }
        return _instance;
    }

    public int getNumberCoins() {
        return _numberCoins;
    }

    public void setNumberCoins(int coins) {
        _numberCoins = coins;
    }

    public boolean buyFlower() {
        if (GameState.getInstance().isPossibleToCreateNewFlower()) {
            _numberCoins -= GameMode.getInstance().getFlowerPrice();
            return true;
        }
        return false;
    }

    public String getName() {
        return _name;
    }

    public void setName(String _name) {
        this._name = _name;
    }
}
