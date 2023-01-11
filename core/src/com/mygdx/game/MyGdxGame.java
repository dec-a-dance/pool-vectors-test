package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.aim.Aim;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;

public class MyGdxGame extends ApplicationAdapter {
    SpriteBatch batch;
    private OrthographicCamera camera;
    int affectedBalls = 0;
    Texture test;
    Texture whiteSprite;
    Texture redSprite;
    Texture blueSprite;
    Texture emptySprite;
    Circle white;
    Circle blue;
    Circle red;
    Boolean blueAffected = false;
    Boolean redAffected = false;

    @Override
    public void create() {
        batch = new SpriteBatch();
        whiteSprite = new Texture("white.png");
        redSprite = new Texture("red.png");
        blueSprite = new Texture("blue.png");
        emptySprite = new Texture("empty.png");
        test = new Texture("badlogic.jpg");
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 640, 960);
        // change this params
        // white ball
        white = new Circle();
        white.y = 150;
        white.x = 640 / 2 - 64 / 2;
        white.radius = 64;
        // blue ball
        blue = new Circle();
        blue.y = 300;
        blue.x = 640 - 640 / 2 + 64 / 2;
        blue.radius = 64;
        // red ball
        red = new Circle();
        red.y = 500;
        red.x = 640 - 640 / 3 + 64 / 2;
        red.radius = 64;
    }


    @Override
    public void render() {
        ScreenUtils.clear(0, 0.5F, 0, 1);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(blueSprite, blue.x, blue.y, 64, 64);
        batch.draw(redSprite, red.x, red.y, 64, 64);
        batch.draw(whiteSprite, white.x, white.y, 64, 64);
        batch.draw(test, 0, 0, 0, 0);
        // mouse driver
        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            // finding the coeffitient (x/y) to find the vector of ball's movement
            float xDiff = abs(touchPos.x - white.x - 32);
            float yDiff = abs(touchPos.y - white.y - 32);
            float coef = xDiff / yDiff;
            drawLine(touchPos, coef, white);
        }
        batch.end();
    }

    @Override
    public void dispose() {
        whiteSprite.dispose();
        blueSprite.dispose();
        redSprite.dispose();
        batch.dispose();
    }

    // function that draws lines for vectors of movement
    public void drawLine(Vector3 touchPos, float coef, Circle ball) {
        System.out.println("coef= " + coef);
        float ln = 1;
        Vector2 center = new Vector2(ball.x + 32 + (ln * coef), ball.y + 32 + ln);
        Vector2 found = null;
        // cycle of increasing the vector length until we find ball or border
        // some magic with +- to fix the coordinates
        while (found == null) {
            if (touchPos.x - ball.x - 32 < 0) {
                if (touchPos.y - ball.y - 32 < 0) {
                    center = new Vector2(ball.x + 32 + (ln * coef), ball.y + 32 + ln);
                } else {
                    center = new Vector2(ball.x + 32 + (ln * coef), ball.y + 32 - ln);
                }
            } else {
                if (touchPos.y - ball.y - 32 < 0) {
                    center = new Vector2(ball.x + 32 - (ln * coef), ball.y + 32 + ln);
                } else {
                    center = new Vector2(ball.x + 32 - (ln * coef), ball.y + 32 - ln);
                }
            }
            ln += 1;
            found = checkCollision(center);
        }
        // drawing the line itself
        Aim.DrawLine(new Vector2(ball.x + 32, ball.y + 32), new Vector2(center.x + 32, center.y + 32), camera.combined);
        redAffected = false;
        blueAffected = false;
    }

    // function checks if the ball with center in c.x and c.y collide with others
    public Vector2 checkCollision(Vector2 c) {
        List<Vector2> bill = new ArrayList<>();
        // creating something close to a circle for future position of a ball to use it while checking the collision
        for (float i = 0; i <= PI * 2; i += 0.01) {
            float x = (float) sqrt(1024 / (1 + pow(tan(i), 2)));
            if (i < PI / 2) {
                bill.add(new Vector2(c.x + x, (float) (c.y + x * tan(i))));
            } else if (i <= PI) {
                x = -x;
                bill.add(new Vector2(c.x + x, (float) (c.y + x * tan(i))));
            } else if (i <= 3 * PI / 2) {
                x = -x;
                bill.add(new Vector2(c.x + x, (float) (c.y + x * tan(i))));
            } else {
                bill.add(new Vector2(c.x + x, (float) (c.y + x * tan(i))));
            }
        }
        float xB = blue.x;
        float yB = blue.y;
        float xR = red.x;
        float yR = red.y;
        // checking collision and out of screen
        for (Vector2 b : bill) {
            if (pow(b.x - xB, 2) + pow(b.y - yB, 2) <= 1024 && !blueAffected) {
                System.out.println("blue");
                float coef = abs(blue.x - c.x) / abs(blue.y - c.y);
                blueAffected = true;
                System.out.println(affectedBalls);
                // recursion for movement of affected balls
                drawLine(new Vector3(c.x, c.y, 0), coef, blue);
                return b;
            } else if (pow(b.x - xR, 2) + pow(b.y - yR, 2) <= 1024 && !redAffected) {
                System.out.println("red");
                float coef = abs(red.x - c.x) / abs(red.y - c.y);
                redAffected = true;
                // recursion for movement of affected balls
                drawLine(new Vector3(c.x, c.y, 0), coef, red);
                return b;
            } else if (c.x > 640 || c.y > 960 || c.x < 0 || c.y < 0) {
                // out of borders case
                System.out.println(b);
                return b;
            }
        }
        // returning null in case if no collide found
        return null;
    }
}
