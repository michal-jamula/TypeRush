package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.time.Duration;

public class ExitScreen implements Screen {
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final long score;
    private final Duration duration;

    public ExitScreen(long points, Duration duration) {
        this.duration = duration;
        this.score = points;
        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
    }


    @Override
    public void show() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        font.getData().scaleX = 2;
        font.getData().scaleY = 2;
        font.setColor(Color.GREEN);

        batch.begin();

        // Display score and duration
        font.draw(batch, "Game Over", Gdx.graphics.getWidth() / 2f - 100, Gdx.graphics.getHeight() - 100);
        font.draw(batch, "Score: " + score, Gdx.graphics.getWidth() / 2f - 100, Gdx.graphics.getHeight() - 250);
        font.draw(batch, String.format("Your game took: %s seconds", duration.getSeconds()), Gdx.graphics.getWidth() / 2f - 100, Gdx.graphics.getHeight() - 350);

        batch.end();
    }

    @Override
    public void render(float delta) {
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

}
