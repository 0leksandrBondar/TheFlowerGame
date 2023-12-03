package com.flowers.mapEntity;

import static java.lang.Math.sqrt;
import static java.lang.Math.tan;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.View;

import com.flowers.R;
import com.flowers.world.GameState;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Snake extends View {

    enum NodeType {
        Body,
        Head
    }

    public class Node extends View {
        private Node prevNode;
        private Bitmap bitmap;
        private float posX, posY;
        private NodeType nodeType;
        private float targetX, targetY;

        public void setPos(float newPosX, float newPosY) {
            posX = newPosX;
            posY = newPosY;
        }

        public void setBitMap(int id) {
            bitmap = BitmapFactory.decodeResource(getResources(), id);
            bitmap = Bitmap.createScaledBitmap(bitmap, 160, 160, false);
        }

        public Node(Context context, NodeType type) {
            super(context);

            nodeType = type;
            setBitMap(type == NodeType.Head ? R.drawable.snake_head : R.drawable.body);
            tryRandomizeDirection();
        }

        public void draw(Canvas canvas) {
            super.draw(canvas);
            canvas.drawBitmap(bitmap, posX, posY, null);
        }

        public float vectorLen(float x, float y) {
            return (float) sqrt(x * x + y * y);
        }

        private void updatePosition() {
            if (prevNode != null && vectorLen(posX - prevNode.posX, posY - prevNode.posY) < 110.f) {
                return;
            }

            float tX = (prevNode != null) ? prevNode.posX : targetX;
            float tY = (prevNode != null) ? prevNode.posY : targetY;

            float directionX = tX - posX;
            float directionY = tY - posY;
            float len = vectorLen(directionX, directionY);

            if (equals(len, 0.0f, speed * 1.2f)) {
                tryRandomizeDirection();
                directionX = tX - posX;
                directionY = tY - posY;
                len = vectorLen(directionX, directionY);
            }

            directionX /= len;
            directionY /= len;

            posX += directionX * speed;
            posY += directionY * speed;

            detectCollisionWithBorderMap();
            postInvalidate();
        }

        public boolean equals(float a, float b, float Epsilon) {
            return a == b ? true : Math.abs(a - b) < Epsilon;
        }

        private void detectCollisionWithBorderMap() {
            if (nodeType == NodeType.Body) {
                return;
            }

            final float mapWidth = GameState.getInstance().mapWidth() - bitmap.getWidth();
            final float mapHeight = GameState.getInstance().mapHeight() - bitmap.getHeight();

            if (posX < 0 || posY < 0 || posX > mapWidth || posY > mapHeight) {
                if (posX > mapWidth) {
                    posX = mapWidth;
                }
                if (posY > mapHeight) {
                    posY = mapHeight;
                }
                tryRandomizeDirection();
            }
        }

        private void tryRandomizeDirection() {
            if (nodeType == NodeType.Head) {
                Random random = new Random(System.currentTimeMillis());
                targetX = random.nextInt(GameState.getInstance().mapWidth());
                targetY = random.nextInt(GameState.getInstance().mapHeight());
            }
        }
    }

    private final float speed = 5;
    private ArrayList<Node> nodes = new ArrayList<>();

    public Snake(Context context) {
        super(context);

        addNode(NodeType.Head);
        addNode(NodeType.Body);

        Timer timerUpdate = new Timer();

        increaseSnakeNode();
        timerUpdate.schedule(new TimerTask() {
            @Override
            public void run() {
                synchronized (nodes) {
                    for (Node node : nodes) {
                        node.updatePosition();
                    }
                }
                postInvalidate();
            }
        }, 0, 10);
    }

    private void increaseSnakeNode() {
        Timer timerAdd = new Timer();

        timerAdd.schedule(new TimerTask() {
            @Override
            public void run() {
                synchronized (nodes) {
                    addNode(NodeType.Body);
                }
            }
        }, 0, 3000);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        for (Node node : nodes)
            node.draw(canvas);
    }

    public void addNode(NodeType type) {
        Node node = new Node(GameState.getInstance().getGameActivity(), type);

        if (!nodes.isEmpty())
            node.prevNode = nodes.get(nodes.size() - 1);
        if (type == NodeType.Head) {
            Random rand = new Random();
            node.setPos(rand.nextInt(GameState.getInstance().mapWidth()), rand.nextInt(GameState.getInstance().mapHeight()));
        } else {
            node.setPos(node.prevNode.posX, node.prevNode.posY);
        }
        nodes.add(node);
    }
}
