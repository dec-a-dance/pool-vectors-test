package com.mygdx.game.aim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

public class Aim {
    private static ShapeRenderer debugRenderer = new ShapeRenderer();

    public static void DrawLine(Vector2 start, Vector2 end, Matrix4 projectionMatrix){
        Gdx.gl.glLineWidth(4);
        debugRenderer.setProjectionMatrix(projectionMatrix);
        debugRenderer.begin(ShapeRenderer.ShapeType.Line);
        debugRenderer.setColor(Color.BLACK);
        debugRenderer.line(start, end);
        debugRenderer.end();
        Gdx.gl.glLineWidth(2);
    }

}
