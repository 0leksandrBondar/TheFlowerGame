package com.flowers.mapEntity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.NonNull;

import com.flowers.R;
import com.flowers.world.GameMode;
import com.flowers.world.GameState;
import com.flowers.world.PlayerState;

public class Flower extends View {
    public enum FlowerState {
        Grain, Flower
    }

    private Handler _handler;
    private float _posY = 0, _posX = 0;
    private Bitmap _flowerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.grain);
    private Runnable _coinUpdater;
    private Paint _paint = new Paint();
    private FlowerState _flowerState = FlowerState.Grain;

    public Flower(Context context) {
        super(context);
        new Handler(Looper.getMainLooper()).postDelayed(() -> setFlowerState(FlowerState.Flower), GameMode.getInstance().getFlowerStateChangeDelayMillis());
    }

    public boolean hasFlowerState() {
        return _flowerState == FlowerState.Flower;
    }

    public void onDraw(@NonNull Canvas canvas) {
        canvas.drawBitmap(_flowerBitmap, _posX, _posY, _paint);
    }

    public void setPosition(float x, float y) {
        _posX = x;
        _posY = y;
        invalidate(); // display update
    }

    public float posX() {
        return _posX;
    }

    public float posY() {
        return _posY;
    }

    private void setFlowerState(FlowerState state) {
        _flowerState = state;
        if (_flowerState == FlowerState.Flower)
            _flowerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.flower);

        if (hasFlowerState()) {
            _handler = new Handler(Looper.getMainLooper());
            _coinUpdater = new Runnable() {
                @Override
                public void run() {
                    PlayerState.getInstance().increaseNumberOfCoins();
                    GameState.getInstance().updateCoinsLabel();
                    _handler.postDelayed(this, GameMode.getInstance().getCoinUpdateIntervalMillis());
                }
            };
            _coinUpdater.run();
        }

        invalidate();
    }

    public void stopCoinUpdater() {
        if (_handler != null && _coinUpdater != null) {
            _handler.removeCallbacks(_coinUpdater);
        }
    }
}
