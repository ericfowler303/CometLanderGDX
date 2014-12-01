package com.triunecomputing.CometLander;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.Random;
import java.util.Date;

public class CometLander extends ApplicationAdapter {
	Random rng = new Random();
	Date timeObj = new Date();
	long startAnimationTime = -1; // init with null time value
	World moonWorld;
	SpriteBatch batch;
	Sprite spaceship;
	ArrayList<Sprite> StarList = new ArrayList<Sprite>();
	Sprite star;
	Texture ship;
	Texture ship1;
	Texture ship2;
	Texture ship3;
	
	@Override
	public void create () {
		/*Box2D.init(); // Needed to start using physics
		// Acceleration due to gravity on the moon is 1.62 m/s^2, use that value for the world gravity setting
		// Create the World to reflect that gravity
		moonWorld = new World(new Vector2(0,-1.62f),true);

		// Create the body to represent the moon surface
		BodyDef moonBodyDef = new BodyDef();
		moonBodyDef.type = BodyDef.BodyType.StaticBody;
		moonBodyDef.position.set(new Vector2(0,10));
		Body moonBody = moonWorld.createBody(moonBodyDef);
		PolygonShape moonBox = new PolygonShape();
		moonBox.setAsBox(Gdx.graphics.getWidth()/2, 100f);
		moonBody.createFixture(moonBox, 0.0f);
		moonBox.dispose();*/
		Gdx.input.setInputProcessor(new InputAdapter() {
			public boolean touchDown (int x, int y, int pointer, int button) {
				// Save the first time that the user touches the screen
				if(startAnimationTime ==-1)
				{
					startAnimationTime = timeObj.getTime();

				}
				return true;
			}
			@Override
			public boolean touchDragged (int x, int y, int pointer) {
				startAnimationTime++;
				return true;
			}

			public boolean touchUp (int x, int y, int pointer, int button) {
				// Reset the time counter for the animation
				startAnimationTime =-1;
				return true;
			}
		});


		batch = new SpriteBatch();
		// Randomly generate a bunch of stars for the background
		Texture starTexture = new Texture(Gdx.files.internal("star.png"));

		for (int i = 0; i < 100; i++) {
			star = new Sprite(starTexture);
			star.setPosition(rng.nextInt(Gdx.graphics.getWidth()-5), rng.nextInt(Gdx.graphics.getHeight()-5));
			if(i % 5 == 0) {star.setScale(2.5f);}
			StarList.add(star);
		}
		// Load all textures
		ship = new Texture(Gdx.files.internal("spaceship.png"));
		ship1 = new Texture(Gdx.files.internal("spaceship1.png"));
		ship2 = new Texture(Gdx.files.internal("spaceship2.png"));
		ship3 = new Texture(Gdx.files.internal("spaceship3.png"));
		// Make the spaceship and place it at the top center
		spaceship = new Sprite(ship);
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
		// Animate the spaceship based on input
		ChangeSpaceshipTexture(startAnimationTime);

		batch.end();
		// update the physics at 60fps regardless of rendering speed
		//moonWorld.step(1/60f, 6, 2);
	}

	private void ChangeSpaceshipTexture(long startAnimationTime) {
		// Draw the frame based on how long the user has touched the screen
		if(startAnimationTime == -1) {
			// User isn't touching or just let up
			// so display the ship with no flames
			spaceship.setTexture(ship);
			spaceship.draw(batch);
		} else {
			// Get the time difference at this frame from the beginning
			long timeDiff = timeObj.getTime() - startAnimationTime;
			timeDiff = (timeDiff < 0 ? -timeDiff : timeDiff); // If difference is negative, make positive
			if(timeDiff > 1000) {
				spaceship.setTexture(ship3);
				spaceship.draw(batch);
			} else if (timeDiff >500) {
				spaceship.setTexture(ship2);
				spaceship.draw(batch);
			} else {
				spaceship.setTexture(ship1);
				spaceship.draw(batch);
			}
		}
	}
}
