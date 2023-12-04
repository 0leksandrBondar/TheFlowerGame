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

    private int flowerPrice = 50;
    private float flowerX = 0;
    private float flowerY = 0;
    private Bitmap flowerBitmap;
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

    public int getFlowerPrice() {
        return flowerPrice;
    }

    private void setFlowerState(FlowerState state) {
        flowerState = state;

        if (flowerState == FlowerState.Flower) {
            // Use a handler to post a delayed runnable with a delay of 3 seconds
            final Handler handler = new Handler(Looper.getMainLooper());
            final Runnable coinUpdater = new Runnable() {
                @Override
                public void run() {
                    PlayerState.getInstance().increaseNumberCoins();
                    GameState.getInstance().updateCoinsLabel();
                    // Post the same runnable again after 3 seconds
                    handler.postDelayed(this, 3000);
                }
            };
            handler.postDelayed(coinUpdater, 3000);
        }

        invalidate();
    }
}
