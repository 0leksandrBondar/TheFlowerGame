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

    private Handler handler;
    private float posY = 0, posX = 0;
    private Bitmap flowerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.grain);
    private Runnable coinUpdater;
    private Paint paint = new Paint();
    private FlowerState flowerState = FlowerState.Grain;

    public Flower(Context context) {
        super(context);
        new Handler(Looper.getMainLooper()).postDelayed(() -> setFlowerState(FlowerState.Flower), GameMode.getInstance().getFlowerStateChangeDelayMillis());
    }

    public boolean hasFlowerState() {
        return flowerState == FlowerState.Flower;
    }

    public void onDraw(@NonNull Canvas canvas) {
        canvas.drawBitmap(flowerBitmap, posX, posY, paint);
    }

    public void setPosition(float x, float y) {
        posX = x;
        posY = y;
        invalidate(); // display update
    }

    public float posX() {
        return posX;
    }

    public float posY() {
        return posY;
    }

    private void setFlowerState(FlowerState state) {
        flowerState = state;
        if (flowerState == FlowerState.Flower)
            flowerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.flower);

        if (hasFlowerState()) {
            handler = new Handler(Looper.getMainLooper());
            coinUpdater = new Runnable() {
                @Override
                public void run() {
                    PlayerState.getInstance().increaseNumberOfCoins();
                    GameState.getInstance().updateCoinsLabel();
                    handler.postDelayed(this, GameMode.getInstance().getCoinUpdateIntervalMillis());
                }
            };
            coinUpdater.run();
        }

        invalidate();
    }

    public void stopCoinUpdater() {
        if (handler != null && coinUpdater != null) {
            handler.removeCallbacks(coinUpdater);
        }
    }
}
