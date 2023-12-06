package com.flowers.mapEntity;

import static java.lang.Math.sqrt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.flowers.R;
import com.flowers.world.GameMode;
import com.flowers.world.GameState;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

public class Snake extends View {

    enum NodeType {
        Body,
        Head
    }

    private final float _defaultSpeed = 5;
    private float _speed = _defaultSpeed;
    private final float _maxSpeed = _speed * 2;
    private final int _maxSnakeLength = 10;

    private final CopyOnWriteArrayList<Node> nodes = new CopyOnWriteArrayList<>();

    public class Node extends View {
        private Node _prevNode;
        private Bitmap _bitmap;
        private float _posX, _posY;
        private final NodeType _nodeType;
        private float _targetX, _targetY;

        public float getPosX() {
            return _posX;
        }

        public float getPosY() {
            return _posY;
        }

        public void setPos(float newPosX, float newPosY) {
            _posX = newPosX;
            _posY = newPosY;
        }

        public void setBitMap(int id) {
            _bitmap = BitmapFactory.decodeResource(getResources(), id);
            _bitmap = Bitmap.createScaledBitmap(_bitmap, GameMode.getInstance().getBitmapWidth(), GameMode.getInstance().getBitmapHeight(), false);
        }

        public Node(Context context, NodeType type) {
            super(context);

            _nodeType = type;
            setBitMap(type == NodeType.Head ? R.drawable.snake_head : R.drawable.body);
            tryRandomizeDirection();
        }

        public void draw(Canvas canvas) {
            super.draw(canvas);
            canvas.drawBitmap(_bitmap, _posX, _posY, null);
        }

        public float vectorLen(float x, float y) {
            return (float) sqrt(x * x + y * y);
        }

        private void updatePosition() {
            float dist = GameMode.getInstance().getDistBetweenNodes();
            if (_prevNode != null && vectorLen(_posX - _prevNode._posX, _posY - _prevNode._posY) < dist) {
                return;
            }

            float tX = (_prevNode != null) ? _prevNode._posX : _targetX;
            float tY = (_prevNode != null) ? _prevNode._posY : _targetY;

            float directionX = tX - _posX;
            float directionY = tY - _posY;
            float len = vectorLen(directionX, directionY);

            if (equals(len, 0.0f, _speed * 1.2f)) {
                tryRandomizeDirection();
                directionX = tX - _posX;
                directionY = tY - _posY;
                len = vectorLen(directionX, directionY);
            }

            directionX /= len;
            directionY /= len;

            _posX += directionX * _speed;
            _posY += directionY * _speed;

            detectCollisionWithFlower();
            detectCollisionWithBorderMap();
            postInvalidate();
        }

        public boolean equals(float a, float b, float Epsilon) {
            return a == b ? true : Math.abs(a - b) < Epsilon;
        }


        private void detectCollisionWithBorderMap() {
            if (_nodeType == NodeType.Body)
                return;

            final float mapWidth = GameState.getInstance().mapWidth() - _bitmap.getWidth();
            final float mapHeight = GameState.getInstance().mapHeight() - _bitmap.getHeight();

            if (_posX < 0 || _posY < 0 || _posX > mapWidth || _posY > mapHeight) {
                if (_posX > mapWidth) {
                    _posX = mapWidth;
                }
                if (_posY > mapHeight) {
                    _posY = mapHeight;
                }
                tryRandomizeDirection();
            }
        }

        private void detectCollisionWithFlower() {
            if (_nodeType == NodeType.Body)
                return;

            FrameLayout map = GameState.getInstance().getMap();
            Node head = nodes.get(0);

            for (int i = 0; i < map.getChildCount(); ++i) {
                View flowerChild = map.getChildAt(i);
                if (flowerChild instanceof Flower) {
                    Flower flower = (Flower) flowerChild;
                    if (flower.hasFlowerState() && Math.abs(head.getPosX() - flower.posX()) < 160 && Math.abs(head.getPosY() - flower.posY()) < 160) {
                        flower.stopCoinUpdater();
                        GameState.getInstance().getGameActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                GameState.getInstance().updateSnakesSpeed();
                                map.removeView(flower);
                            }
                        });
                        break;
                    }
                }
            }
        }

        private void tryRandomizeDirection() {
            if (_nodeType == NodeType.Head) {
                Random random = new Random(System.currentTimeMillis());
                _targetX = random.nextInt(GameState.getInstance().mapWidth());
                _targetY = random.nextInt(GameState.getInstance().mapHeight());
            }
        }
    }

    public Snake(Context context) {
        super(context);

        addNode(NodeType.Head);
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

    public float getSpeed() {
        return _speed;
    }

    public void setSpeed(float newSpeed) {
        if (_speed < _maxSpeed)
            _speed = newSpeed;
    }

    public void resetSpeed() {
        _speed = _defaultSpeed;
    }

    public int getMaxSnakeLength() {
        return _maxSnakeLength;
    }

    public void removeNode() {
        if (!nodes.isEmpty()) {
            nodes.remove(nodes.size() - 1);
            postInvalidate();
        }
    }

    public CopyOnWriteArrayList<Node> getNodes() {
        return nodes;
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
        }, 0, GameMode.getInstance().getSnakeNodeIncreaseIntervalMillis());
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        for (Node node : nodes)
            node.draw(canvas);
    }

    public void addNode(NodeType type) {
        if (!GameState.getInstance().isPossibleAddNode(this))
            return;

        Node node = new Node(GameState.getInstance().getGameActivity(), type);

        if (!nodes.isEmpty()) {
            node._prevNode = nodes.get(nodes.size() - 1);
            node.setPos(node._prevNode._posX, node._prevNode._posY);
        }
        if (type == NodeType.Head) {
            Random rand = new Random();
            node.setPos(rand.nextInt(GameState.getInstance().mapWidth()), rand.nextInt(GameState.getInstance().mapHeight()));
        }
        nodes.add(node);
    }
}
