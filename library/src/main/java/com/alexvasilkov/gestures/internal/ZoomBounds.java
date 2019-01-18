package com.alexvasilkov.gestures.internal;

import android.graphics.Matrix;
import android.graphics.RectF;

import com.alexvasilkov.gestures.Settings;
import com.alexvasilkov.gestures.State;
import com.alexvasilkov.gestures.utils.MathUtils;

public class ZoomBounds {

    private static final Matrix tmpMatrix = new Matrix();
    private static final RectF tmpRectF = new RectF();

    private final Settings settings;

    private float minZoom;
    private float maxZoom;
    private float fitZoom;

    public ZoomBounds(Settings settings) {
        this.settings = settings;
    }

    public ZoomBounds set(State state) {
        float imageWidth = settings.getImageW();
        float imageHeight = settings.getImageH();

        float areaWidth = settings.getViewportW();
        float areaHeight = settings.getViewportH();

        if (imageWidth == 0f || imageHeight == 0f || areaWidth == 0f || areaHeight == 0f) {
            minZoom = maxZoom = fitZoom = 1f;
            return this;
        }

        minZoom = settings.getMinZoom();
        maxZoom = settings.getMaxZoom();

        final float rotation = state.getRotation();

        if (!State.equals(rotation, 0f)) {
            tmpMatrix.setRotate(rotation);
            tmpRectF.set(0, 0, imageWidth, imageHeight);
            tmpMatrix.mapRect(tmpRectF);
            imageWidth = tmpRectF.width();
            imageHeight = tmpRectF.height();
        }

        fitZoom = Math.min(areaWidth / imageWidth, areaHeight / imageHeight);

        if (minZoom <= 0f) {
            minZoom = fitZoom;
        }
        if (maxZoom <= 0f) {
            maxZoom = fitZoom;
        }

        if (fitZoom > maxZoom) {
            maxZoom = fitZoom;
        }

        if (minZoom > maxZoom) {
            minZoom = maxZoom;
        }

        if (fitZoom < minZoom) {
            minZoom = fitZoom;
        }

        return this;
    }


    public float getMinZoom() {
        return minZoom;
    }

    public float getMaxZoom() {
        return maxZoom;
    }

    public float getFitZoom() {
        return fitZoom;
    }

    public float restrict(float zoom, float extraZoom) {
        return MathUtils.restrict(zoom, minZoom / extraZoom, maxZoom * extraZoom);
    }
}
