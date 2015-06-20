package com.tacitus.dnp.drawingtools;


import android.graphics.Canvas;

import com.tacitus.dnp.scenario.Step;

import java.util.ArrayList;

public class DrawWatcherD extends DrawWatcher{
    @Override
    public void draw(Canvas canvas, ArrayList<DrawPath> drawPaths, Step step) {

        int mid = canvas.getWidth() / 2;

        ArrayList<DrawPath> rightDrawPaths = new ArrayList<DrawPath>();
        ArrayList<DrawPath> leftDrawPaths = new ArrayList<DrawPath>();

        for (int j = 0; j < drawPaths.size(); j++) {
            if (drawPaths.get(j).getFirstX() < mid && drawPaths.get(j).getLastX() < mid ){
                leftDrawPaths.add(drawPaths.get(j));
            } else {
                if (drawPaths.get(j).getFirstX() > mid && drawPaths.get(j).getLastX() > mid) {
                    rightDrawPaths.add(drawPaths.get(j));
                }
            }
        }


        sortTopToBottom(rightDrawPaths);
        sortTopToBottom(leftDrawPaths);
        connectWithFollowing(canvas, rightDrawPaths, step, Line.REVERSED_CURVE);
        connectWithFollowing(canvas, leftDrawPaths, step, Line.CURVE);
    }
}
