package com.triunecomputing.CometLander;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class CometLander extends ApplicationAdapter {
	SpriteBatch batch;
	Sprite spaceship;
	Sprite star;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		// Randomly generate a bunch of stars for the background

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
		spaceship.draw(batch);

		batch.end();
	}
}
