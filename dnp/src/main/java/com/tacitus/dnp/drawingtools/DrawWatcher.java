package com.tacitus.dnp.drawingtools;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import com.tacitus.dnp.scenario.Step;
import com.tacitus.dnp.widget.DrawingView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public abstract class DrawWatcher {

    public enum Line {
        CURVE, STRAIGHT, REVERSED_CURVE
    }

    public abstract void draw(Canvas canvas, ArrayList<DrawPath> drawPaths, Step step);

    protected void connectWithNearest(Canvas canvas, ArrayList<DrawPath> drawPaths, Step step) {
        Path path = new Path();
        Paint paint = DrawingView.createPaint();
        Paint paintHollow = null;
        float size = 0;
        for (int j = 0; j < drawPaths.size(); j++) {
            size += drawPaths.get(j).getSize();

            float moveX = 0;
            float moveY = 0;
            float lineToX = 0;
            float lineToY = 0;


            float lastX1 = drawPaths.get(j).getLastX();
            float lastY1 = drawPaths.get(j).getLastY();
            float firstX1 = drawPaths.get(j).getFirstX();
            float firstY1 = drawPaths.get(j).getFirstY();

            double minimalDistance = 0;

            for (int i = 0; i < drawPaths.size(); i++) {
                if ( i == j ) {
                    continue;
                }
                float lastX2 = drawPaths.get(i).getLastX();
                float lastY2 = drawPaths.get(i).getLastY();
                float firstX2 = drawPaths.get(i).getFirstX();
                float firstY2 = drawPaths.get(i).getFirstY();

                double distanceFirst1First2 = Math.sqrt((firstX1 - firstX2) * (firstX1 - firstX2) + (firstY1 - firstY2) * (firstY1 - firstY2));
                double distanceFirst1Last2 = Math.sqrt((firstX1 - lastX2) * (firstX1 - lastX2) + (firstY1 - lastY2) * (firstY1 - lastY2));
                double distanceFirst2Last1 = Math.sqrt((firstX2 - lastX1) * (firstX2 - lastX1) + (firstY2 - lastY1) * (firstY2 - lastY1));
                double distanceLast1Last2 = Math.sqrt((lastX1 - lastX2) * (lastX1 - lastX2) + (lastY1 - lastY2) * (lastY1 - lastY2));

                if (distanceFirst1First2 < minimalDistance || minimalDistance == 0) {
                    moveX = firstX1;
                    moveY = firstY1;
                    lineToX = firstX2;
                    lineToY = firstY2;
                    minimalDistance = distanceFirst1First2;
                }
                if (distanceFirst1Last2 < minimalDistance) {
                    moveX = firstX1;
                    moveY = firstY1;
                    lineToX = lastX2;
                    lineToY = lastY2;
                    minimalDistance = distanceFirst1Last2;
                }
                if (distanceFirst2Last1 < minimalDistance) {
                    moveX = firstX2;
                    moveY = firstY2;
                    lineToX = lastX1;
                    lineToY = lastY1;
                    minimalDistance = distanceFirst2Last1;
                }
                if (distanceLast1Last2 < minimalDistance) {
                    moveX = lastX1;
                    moveY = lastY1;
                    lineToX = lastX2;
                    lineToY = lastY2;
                    minimalDistance = distanceLast1Last2;
                }
            }
            // For each path trace a line between nearest extremities found
            path.moveTo(moveX, moveY);
            path.lineTo(lineToX, lineToY);
        }

        size = size / drawPaths.size();

        paint.setColor(step.getLine().getColor());
        if (step.getLine().isHollow()) {
            paintHollow = DrawingView.createPaint();
            paintHollow.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            paintHollow.setStrokeWidth(size - (size * DrawingView.HOLLOW_LINE_THICKNESS_RATIO / 100));

        }
        paint.setStrokeWidth(size);
        if (step.getLine().isUnderline()) {
            paint.setShadowLayer(1f, size / 2, size / 2, Color.BLACK);
        }

        canvas.drawPath(path, paint);
        if (paintHollow != null) {
            canvas.drawPath(path, paintHollow);
        }
    }

    protected void connectWithFollowing(Canvas canvas, ArrayList<DrawPath> drawPaths, Step step, Line line ) {
        Path path = new Path();
        Paint paint = DrawingView.createPaint();
        Paint paintHollow = null;
        float size = 0;
        for (int j = 0; j < drawPaths.size() - 1; j++) {
            size += drawPaths.get(j).getSize();

            float lastX1 = drawPaths.get(j).getLastX();
            float lastY1 = drawPaths.get(j).getLastY();
            float firstX1 = drawPaths.get(j).getFirstX();
            float firstY1 = drawPaths.get(j).getFirstY();

            double minimalDistance;
            int i = j + 1;

            float lastX2 = drawPaths.get(i).getLastX();
            float lastY2 = drawPaths.get(i).getLastY();
            float firstX2 = drawPaths.get(i).getFirstX();
            float firstY2 = drawPaths.get(i).getFirstY();

            double distanceFirst1First2 = Math.sqrt((firstX1 - firstX2) * (firstX1 - firstX2) + (firstY1 - firstY2) * (firstY1 - firstY2));
            double distanceFirst1Last2 = Math.sqrt((firstX1 - lastX2) * (firstX1 - lastX2) + (firstY1 - lastY2) * (firstY1 - lastY2));
            double distanceFirst2Last1 = Math.sqrt((firstX2 - lastX1) * (firstX2 - lastX1) + (firstY2 - lastY1) * (firstY2 - lastY1));
            double distanceLast1Last2 = Math.sqrt((lastX1 - lastX2) * (lastX1 - lastX2) + (lastY1 - lastY2) * (lastY1 - lastY2));

            float moveX = firstX1;
            float moveY = firstY1;
            float lineToX = firstX2;
            float lineToY = firstY2;
            minimalDistance = distanceFirst1First2;

            if (distanceFirst1Last2 < minimalDistance) {
                moveX = firstX1;
                moveY = firstY1;
                lineToX = lastX2;
                lineToY = lastY2;
                minimalDistance = distanceFirst1Last2;
            }
            if (distanceFirst2Last1 < minimalDistance) {
                moveX = firstX2;
                moveY = firstY2;
                lineToX = lastX1;
                lineToY = lastY1;
                minimalDistance = distanceFirst2Last1;
            }
            if (distanceLast1Last2 < minimalDistance) {
                moveX = lastX1;
                moveY = lastY1;
                lineToX = lastX2;
                lineToY = lastY2;
            }

            // For each path trace a line between nearest extremities found
            path.moveTo(moveX, moveY);
            switch (line){
                case STRAIGHT:
                    path.lineTo(lineToX, lineToY);
                    break;
                case CURVE:
                    path.quadTo(0, canvas.getHeight() / 2, lineToX, lineToY);
                    break;
                case REVERSED_CURVE:
                    path.quadTo(canvas.getWidth(), canvas.getHeight() / 2, lineToX, lineToY);
                    break;
            }
        }

        size = size / drawPaths.size();

        paint.setColor(step.getLine().getColor());
        if (step.getLine().isHollow()) {
            paintHollow = DrawingView.createPaint();
            paintHollow.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            paintHollow.setStrokeWidth(size - (size * DrawingView.HOLLOW_LINE_THICKNESS_RATIO / 100));

        }
        paint.setStrokeWidth(size);
        if (step.getLine().isUnderline()) {
            paint.setShadowLayer(1f, size / 2, size / 2, Color.BLACK);
        }

        canvas.drawPath(path, paint);
        if (paintHollow != null) {
            canvas.drawPath(path, paintHollow);
        }
    }



    protected void sortTopToBottom(ArrayList<DrawPath> drawPaths){
        HashMap<Float, DrawPath> positions = new HashMap<Float, DrawPath>();
        for (int i = 0; i < drawPaths.size(); i++) {
            DrawPath drawPath = drawPaths.get(i);
            positions.put((drawPath.getFirstY() + drawPath.getLastY()), drawPath);
        }
        ArrayList<Float> positionsList = new ArrayList<Float>();
        positionsList.addAll(positions.keySet());
        Collections.sort(positionsList);

        drawPaths.clear();
        for (int i = 0; i < positionsList.size(); i++) {
            drawPaths.add(0, positions.get(positionsList.get(i)));
        }

    }


}
