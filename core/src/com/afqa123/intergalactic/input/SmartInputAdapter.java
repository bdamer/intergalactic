package com.afqa123.intergalactic.input;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.TimeUtils;

public abstract class SmartInputAdapter implements InputProcessor {

    private static final long DEFAULT_DOUBLE_TAP_THRESHOLD = 200l;
    private static final long DEFAULT_LONG_TAP_THRESHOLD = 400l;
    private static final int DEFAULT_DRAG_THRESHOLD = 8;
    private long doubleTapThreshold = DEFAULT_DOUBLE_TAP_THRESHOLD;
    private long longTapThreshold = DEFAULT_LONG_TAP_THRESHOLD;
    private int dragThreshold = DEFAULT_DRAG_THRESHOLD;
    private long lastTouchDown;
    private long lastTouchUp;
    private boolean dragging;
    private int touchX;
    private int touchY;
    private int lastX;
    private int lastY;
    
    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {
        lastTouchDown = TimeUtils.millis();
        lastX = touchX = x;
        lastY = touchY = y;
        dragging = false;        
        return true;
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {
        if (!dragging) {
            if (TimeUtils.timeSinceMillis(lastTouchUp) < doubleTapThreshold) {
                onDoubleClick(x, y);
            } else if (TimeUtils.timeSinceMillis(lastTouchDown) < longTapThreshold) {
                onClick(x, y);
            } else {
                onLongClick(x, y);
            }
        }
        lastTouchUp = TimeUtils.millis();
        return true;
    }

    @Override
    public boolean touchDragged(int x, int y, int pointer) {
        // Check if we've moved enough to start dragging
        if (!dragging) {
            int dx = x - touchX;
            int dy = y - touchY;
            dragging = (dx * dx + dy * dy) >= dragThreshold;
        }
        if (dragging) {
            onDrag(x - lastX, y - lastY);
            lastX = x;
            lastY = y;
        }
        return true;
    }
    
    @Override
    public boolean keyUp(int i) {
        return false;
    }

    @Override
    public boolean keyTyped(char c) {
        return false;
    }

    @Override
    public boolean mouseMoved(int i, int i1) {
        return false;
    }

    @Override
    public boolean scrolled(int i) {
        return false;
    }
    
    public abstract void onDrag(int dx, int dy);
    
    public abstract void onClick(int x, int y);
    
    public abstract void onLongClick(int x, int y);
    
    public abstract void onDoubleClick(int x, int y);

    public long getDoubleTapThreshold() {
        return doubleTapThreshold;
    }

    public void setDoubleTapThreshold(long doubleTapThreshold) {
        this.doubleTapThreshold = doubleTapThreshold;
    }

    public long getLongTapThreshold() {
        return longTapThreshold;
    }

    public void setLongTapThreshold(long longTapThreshold) {
        this.longTapThreshold = longTapThreshold;
    }

    public int getDragThreshold() {
        return dragThreshold;
    }

    public void setDragThreshold(int dragThreshold) {
        this.dragThreshold = dragThreshold;
    }
}