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
        public float targetX;
        public float targetY;
        public float posX;
        public float posY;
        public Node prevNode;

        public void setPosX(float newPos) {
            posX = newPos;
        }

        public void setPosY(float newPos) {
            posY = newPos;
        }

        private Bitmap bitmap;

        private NodeType nodeType;

        public void setBitMap(int id) {
            bitmap = BitmapFactory.decodeResource(getResources(), id);
            bitmap = Bitmap.createScaledBitmap(bitmap, 160, 160, false);
        }

        public Node(Context context, NodeType type) {
            super(context);

            nodeType = type;

            if (type == NodeType.Head) {
                setBitMap(R.drawable.snake_head);
                tryRandomizeDirection();
            } else {
                setBitMap(R.drawable.body);
            }
        }

        public NodeType nodeType() {
            return nodeType;
        }

        public void draw(Canvas canvas) {
            super.draw(canvas);
            canvas.drawBitmap(bitmap, posX, posY, null);
        }

        public float vectorLen(float x, float y)
        {
            return (float)sqrt(x * x + y * y);
        }

        private void updatePosition() {
            if (prevNode != null && vectorLen(posX - prevNode.posX, posY - prevNode.posY) < 110.f)
            {

            }
            else
            {
                float tX = targetX;
                float tY = targetY;
                if (prevNode != null)
                {
                    tX = prevNode.posX;
                    tY = prevNode.posY;
                }

                float directionX = tX - posX;
                float directionY = tY - posY;
                if (equals(directionX, 0.0f, (float) (speed * 1.2)) && equals(directionY, 0.0f, (float) (speed * 1.2))) {
                    tryRandomizeDirection();
                    directionX = tX - posX;
                    directionY = tY - posY;
                }
                directionX /= vectorLen(directionX, directionY);
                directionY /= vectorLen(directionX, directionY);

                posX += directionX * speed;
                posY += directionY * speed;

                detectCollisionWithBorderMap();
            }

            postInvalidate();
        }

        public boolean equals(float a, float b, float Epsilon) {
            return a == b ? true : Math.abs(a - b) < Epsilon;
        }

        private void detectCollisionWithBorderMap() {

            if (nodeType == NodeType.Body) {
                return;
            }

            if (posX < 0) {
                posX = 0;
                tryRandomizeDirection();
            } else if (posX > GameState.getInstance().mapWidth() - bitmap.getWidth()) {
                posX = GameState.getInstance().mapWidth() - bitmap.getWidth();
                tryRandomizeDirection();
            }
            if (posY < 0) {
                posY = 0;
                tryRandomizeDirection();
            } else if (posY > GameState.getInstance().mapHeight() - bitmap.getHeight()) {
                posY = GameState.getInstance().mapHeight() - bitmap.getHeight();
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

    private ArrayList<Node> nodes = new ArrayList<>();
    private float posX, posY;
    private final float speed = 5;

    public Snake(Context context) {
        super(context);

        addNode(NodeType.Head, null);
        addNode(NodeType.Body,nodes.get(0));
        addNode(NodeType.Body,nodes.get(1));
        addNode(NodeType.Body,nodes.get(2));
        addNode(NodeType.Body,nodes.get(3));
        addNode(NodeType.Body,nodes.get(4));

        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                for (Node node : nodes) {
                    node.updatePosition();
                    postInvalidate();
                }
            }
        }, 0, 10);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        for (Node node : nodes) {
            node.draw(canvas);
        }
    }

    public void addNode(NodeType type, Node prev) {
        Node node = new Node(GameState.getInstance().getGameActivity(), type);

        if (prev != null) {
            node.prevNode = prev;
        }
        if (type == NodeType.Head)
        {
            setRandomPos();
        }
        node.setPosX(posX);
        node.setPosY(posY);

        nodes.add(node);
    }

    private void setRandomPos()
    {
        Random rand = new Random();
        posX = rand.nextInt(GameState.getInstance().mapWidth());
        posY = rand.nextInt(GameState.getInstance().mapHeight());
    }
}
