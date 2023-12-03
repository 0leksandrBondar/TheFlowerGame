package com.flowers.mapEntity;

import static android.util.Half.EPSILON;
import static java.lang.Math.sqrt;

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

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Snake extends View {

    private Bitmap snakeHead = BitmapFactory.decodeResource(getResources(), R.drawable.snake_head);
    private float snakeY = 0;
    private float snakeX = 0;
    private float snakeTargetX = 0 ;
    private float snakeTargetY = 0;
    private final float speed = 15;

    public Snake(Context context) {
        super(context);
        randomizeDirection();
        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                updatePosition();
            }
        }, 0, 30);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        snakeHead = Bitmap.createScaledBitmap(snakeHead, 160, 160, false);
        canvas.drawBitmap(snakeHead, snakeX, snakeY, null);
    }

    private void updatePosition() {
        float directionX = snakeTargetX - snakeX;
        float directionY = snakeTargetY - snakeY;
        if (equals(directionX, 0.0f, (float) (speed * 1.2)) || equals(directionY, 0.0f, (float) (speed * 1.2))) {
            System.out.println("hello");

            randomizeDirection();
            directionX = snakeTargetX - snakeX;
            directionY = snakeTargetY - snakeY;
        }
        float vectorLen = (float) sqrt(directionX * directionX + directionY * directionY);
        directionX /= vectorLen;
        directionY /= vectorLen;

        snakeX += directionX * speed;
        snakeY += directionY * speed;

        //System.out.println("new pos: x = " + snakeX + " y = " + snakeY);
       // System.out.println("new DIRECTION: x = " + directionX + " y = " + directionY);

        detectCollisionWithBorderMap();

        postInvalidate();
    }

    private void randomizeDirection() {
        Random random = new Random(System.currentTimeMillis());
        snakeTargetX = random.nextInt(GameState.getInstance().mapWidth());
        snakeTargetY = random.nextInt(GameState.getInstance().mapHeight());
       System.out.println("m = " + System.currentTimeMillis());
    }

    public void updatePosition(float x, float y) {
        snakeX = x;
        snakeY = y;
        invalidate(); // display update
    }

    private void detectCollisionWithBorderMap() {
        if (snakeX < 0) {
            snakeX = 0;
            randomizeDirection();
        } else if (snakeX > GameState.getInstance().mapWidth() - snakeHead.getWidth()) {
            snakeX = GameState.getInstance().mapWidth() - snakeHead.getWidth();
            randomizeDirection();
        }
        if (snakeY < 0) {
            snakeY = 0;
            randomizeDirection();
        } else if (snakeY > GameState.getInstance().mapHeight() - snakeHead.getHeight()) {
            snakeY = GameState.getInstance().mapHeight() - snakeHead.getHeight();
            randomizeDirection();
        }
    }

    public static boolean equals(float a, float b, float Epsilon) {
        return a == b ? true : Math.abs(a - b) < Epsilon;
    }
}
