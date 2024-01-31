package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;


public class TypeRush extends Game implements InputProcessor {
    private static final List<String> possibleWords = new ArrayList<>();
    final Random random = new Random();
    SpriteBatch batch;
    private BitmapFont font;
    private List<Word> words;
    private String currentWord;
    private int lives;
    private long points;
    private LocalTime startTime;
    private ExitScreen exitScreen;

    private static ArrayList<String> loadWordsFromCSV() {
        ArrayList<String> words = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("assets/words.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(",");
                String word = StringUtils.trim(columns[0]).toLowerCase();
                words.add(word);

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return words;
    }

    @Override
    public void create() {
        //Startup
        batch = new SpriteBatch();
        font = new BitmapFont();
        words = new ArrayList<>();
        words = Collections.synchronizedList(words);
        possibleWords.addAll(loadWordsFromCSV());
        currentWord = "";
        lives = 3;
        points = 0;
        this.startTime = LocalTime.now();
        Gdx.input.setInputProcessor(this);

        font.getData().scaleX = 2;
        font.getData().scaleY = 2;

        //fill words on the screen
        for (int i = 0; i < 5; i++) {
            addWord(possibleWords.get(random.nextInt(possibleWords.size())), i);
        }
        for (Word word : words) {
            word.setText(word.getText().trim().toLowerCase());
        }
    }

    private void addWord(String text, int column) {
        words.add(new Word(text, column));
    }

    @Override
    public void render() {
        //background
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        batch.begin();
        //end screen
        if (lives < 1) {
            if (exitScreen == null) {
                exitScreen = new ExitScreen(points, Duration.between(startTime, LocalTime.now()));
            }
            setScreen(exitScreen);

            //Game screen
        } else {

            font.draw(batch, "Lives: " + lives, 1152, 742);
            font.draw(batch, "Points: " + points, 63, 742);

            for (int w = 0; w < words.size(); w++) {

                Word word = words.get(w);

                try {
                    this.updatePosition(word, 3 - word.getText().length() * 0.4f);
                } catch (ConcurrentModificationException e) {
                    w--;
                    continue;
                }

                font.setFixedWidthGlyphs(word.toString());
                float currentX = word.getPosition().x;

                for (int i = 0; i < word.getText().length(); i++) {
                    GlyphLayout c = new GlyphLayout();

                    //Set spacing between letters
                    currentX += font.getSpaceXadvance() * 4.5f;

                    //Highlight characters which have been typed
                    if (i < word.getHighlightedCharacters()) {
                        font.setColor(Color.WHITE);
                        c.setText(font, String.valueOf(word.getText().charAt(i)));
                        font.draw(batch, c, currentX, word.getPosition().y);
                        continue;
                    }

                    //Render rest characters as green
                    font.setColor(Color.GREEN);
                    c.setText(font, String.valueOf(word.getText().charAt(i)));
                    font.draw(batch, c, currentX, word.getPosition().y);
                }
            }
        }

        batch.end();
    }

    private void updatePosition(Word word, float deltaTime) {
        try {
            word.updatePosition(deltaTime);
        } catch (Exception e) {
            lives--;
            words.remove(word);
            addWord(possibleWords.get(random.nextInt(possibleWords.size())), word.getColumn());
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }

    @Override
    public boolean keyTyped(char character) {
        currentWord = currentWord.concat(String.valueOf(character));
        ArrayList<Word> wordsToRemove = new ArrayList<>();
        ArrayList<Word> wordsToAdd = new ArrayList<>();

        for (Word word : words) {
            if (currentWord.endsWith(word.toString())) {
                wordsToRemove.add(word);
                points += Math.floor(random.nextFloat(0.2f, 0.4f) * 100 * word.getText().length());

                wordsToAdd.add(new Word(possibleWords.get(random.nextInt(possibleWords.size())), word.getColumn()));
                currentWord = currentWord.substring(currentWord.length() - word.getText().length());
            } else {
                highlightConsecutive(word);
            }
        }
        words.removeAll(wordsToRemove);
        words.addAll(wordsToAdd);
        return true;
    }

    private void highlightConsecutive(Word word) {
        if (StringUtils.isNotBlank(currentWord)) {
            String text = word.getText();
            int matching = 0;

            int maxIndex = Math.min(text.length(), currentWord.length());

            for (int i = 1; i < maxIndex + 1; i++) {
                if (currentWord.endsWith(text.substring(0, i))) {
                    matching = i;
                }
            }
            word.setHighlightedCharacters(matching);
        }
    }

    @Override
    public void pause() {
    }


    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE) {
            Gdx.app.exit();
            return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }


}

