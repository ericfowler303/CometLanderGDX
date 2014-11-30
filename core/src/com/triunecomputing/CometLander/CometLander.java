package com.triunecomputing.CometLander;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.Random;

public class CometLander extends ApplicationAdapter {
	Random rng = new Random();
	SpriteBatch batch;
	Sprite spaceship;
	ArrayList<Sprite> StarList = new ArrayList<>();
	Sprite star;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		// Randomly generate a bunch of stars for the background
		Texture starTexture = new Texture(Gdx.files.internal("star.png"));

		for (int i = 0; i < 100; i++) {
			star = new Sprite(starTexture);
			star.setPosition(rng.nextInt(Gdx.graphics.getWidth()-5), rng.nextInt(Gdx.graphics.getHeight()-5));
			StarList.add(star);
		}

		// Make the spaceship and place it at the top center
		Texture spaceshipTexture = new Texture(Gdx.files.internal("spaceship.png"));
		spaceship = new Sprite(spaceshipTexture);
		spaceship.setScale(0.3f);
		spaceship.setPosition((Gdx.graphics.getWidth()/2)-(spaceship.getWidth()/2),Gdx.graphics.getHeight()-(spaceship.getHeight()*0.7f));


	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0,0,0,0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		// render all of the stars
		for(Sprite starSprite : StarList) {
			starSprite.draw(batch);
		}
		spaceship.draw(batch);

		batch.end();
	}
}
