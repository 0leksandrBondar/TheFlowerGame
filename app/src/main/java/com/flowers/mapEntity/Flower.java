package com.flowers.mapEntity;

import android.annotation.SuppressLint;
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
import com.flowers.world.GameState;
import com.flowers.world.PlayerState;

public class Flower extends View {
    public enum FlowerState {
        Grain, Flower
    }

    private Handler handler;
    private float flowerY = 0, flowerX = 0;
    private Bitmap flowerBitmap;
    private Runnable coinUpdater;
    private Paint paint = new Paint();
    private FlowerState flowerState = FlowerState.Grain;

    public Flower(Context context) {
        super(context);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                setFlowerState(FlowerState.Flower);
            }
        }, 3000);
    }

    public boolean hasFlowerState() {
        return flowerState == FlowerState.Flower;
    }

    @SuppressLint("DrawAllocation")
    public void onDraw(@NonNull Canvas canvas) {
        int idFlowerImages = (flowerState == FlowerState.Grain) ? R.drawable.grain : R.drawable.flower;
        flowerBitmap = BitmapFactory.decodeResource(getResources(), idFlowerImages);

        canvas.drawBitmap(flowerBitmap, flowerX, flowerY, paint);
    }

    public void setPosition(float x, float y) {
        flowerX = x;
        flowerY = y;
        invalidate(); // display update
    }

    public float posX() {
        return flowerX;
    }

    public float posY() {
        return flowerY;
    }

    private void setFlowerState(FlowerState state) {
        flowerState = state;

        if (flowerState == FlowerState.Flower) {
            handler = new Handler(Looper.getMainLooper());
            coinUpdater = new Runnable() {
                @Override
                public void run() {
                    PlayerState.getInstance().increaseNumberCoins();
                    GameState.getInstance().updateCoinsLabel();
                    handler.postDelayed(this, 3000);
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
