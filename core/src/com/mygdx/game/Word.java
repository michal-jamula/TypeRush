package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;

import java.util.Random;

public class Word {
    private final Vector2 position;
    private final int column;
    private String text;
    private int highlightedCharacters = 0;


    public Word(String text, int column) {
        this.text = text;
        this.column = column;
        this.position = new Vector2(getSpaceWithinColumn(column), 768);
    }

    private float getSpaceWithinColumn(int column) {
        Random random = new Random();
        return random.nextFloat(1, 245) + column * 248;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void updatePosition(float deltaTime) throws Exception {
        position.set(position.x, position.y - deltaTime);

        if (position.y < 0) {
            throw new Exception("Word reached the bottom");
        }
    }


    public int getHighlightedCharacters() {
        return highlightedCharacters;
    }

    public void setHighlightedCharacters(int i) {
        this.highlightedCharacters = i;
    }

    @Override
    public String toString() {
        return text;
    }

    public int getColumn() {
        return column;
    }
}