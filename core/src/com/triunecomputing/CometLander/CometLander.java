package com.triunecomputing.CometLander;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.Random;
import java.util.Date;

public class CometLander extends ApplicationAdapter {
	private static final String TAG = "CometLander";
	private static final float DEGTORAD = 57.296f;
	Random rng = new Random();
	Date timeObj = new Date();
	BitmapFont statsFont;
	long startAnimationTime = -1; // init with null time value
	World moonWorld;
	Body moonSimpleSurfaceBody;
	SpriteBatch batch;
	// Tilt sensor info
	boolean accAvailable=false;
	float accelX = 0.0f;
	float accelY =0.0f;
	// The Star variables
	ArrayList<Sprite> StarList = new ArrayList<Sprite>();
	Sprite star;
	// The Spaceship variables
	Sprite spaceship;
	Body spaceShipBody;
	Texture ship;
	Texture ship1;
	Texture ship2;
	Texture ship3;
	
	@Override
	public void create () {
		// Font to use for the stats box
		statsFont = new BitmapFont();

		// See if the device has an accelerometer
		accAvailable = Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer);

		Box2D.init(); // Needed to start using physics
		// Acceleration due to gravity on the moon is 1.62 m/s^2, use that value for the world gravity setting
		// Create the World to reflect that gravity
		moonWorld = new World(new Vector2(0,-1.62f),true);

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
				// While the user is touching the screen apply a Force to the object to counteract gravity
				spaceShipBody.applyForceToCenter(0.0f,7.5f, true);
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


		// Create the body definition to hold the spaceship to interact with the world
		BodyDef spaceShipBodyDef = new BodyDef();
		spaceShipBodyDef.type = BodyDef.BodyType.DynamicBody;
		spaceShipBodyDef.position.set((Gdx.graphics.getWidth()/2)-(spaceship.getWidth()/2),Gdx.graphics.getHeight()-(spaceship.getHeight()*0.7f));
		spaceShipBody = moonWorld.createBody(spaceShipBodyDef);

		// Create the simple moon surface to test collisions
		BodyDef simpleMoonBodyDef = new BodyDef();
		simpleMoonBodyDef.position.set(20,20);
		moonSimpleSurfaceBody = moonWorld.createBody(simpleMoonBodyDef);
		PolygonShape moonSurfaceBox = new PolygonShape();
		moonSurfaceBox.setAsBox(Gdx.graphics.getWidth()*2, 40); // These args are half, so they need to be doubled first
		moonSimpleSurfaceBody.createFixture(moonSurfaceBox, 0.0f);
		moonSurfaceBox.dispose(); // Not needed anymore
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
		// Update ship rotation based on accelerometer
		if(accAvailable){UpdateSpaceshipRotation();}

		// Update spaceship position before checking which image it should have
		UpdateSpaceshipLocation();

		// Animate the spaceship image based on touch input
		ChangeSpaceshipTexture(startAnimationTime);

		// In the upper left corner display stats on the ship velocity
		statsFont.setColor(0.133f, 1.0f,0.227f,1.0f); // a bright green
		statsFont.setScale(2.0f);
		//Gdx.app.log(TAG, String.valueOf(spaceShipBody.getLinearVelocity().y));
		statsFont.draw(batch, "velocity: " + String.valueOf(spaceShipBody.getLinearVelocity().y)+
				"\n angle: "+String.valueOf((spaceShipBody.getAngle()*DEGTORAD)+
				"\n x-tilt: "+ Math.round(accelX)), 20,Gdx.graphics.getHeight()-20);

		batch.end();
		// update the physics at 60fps regardless of rendering speed
		moonWorld.step(1/60f, 6, 2);
	}

	private void UpdateSpaceshipRotation() {
		// Poll accelerometer for tilt info to change the rotation of the ship
		accelX = Gdx.input.getAccelerometerX();
		// Need some tilt to change rotation
		if(accelX < -1.0 || accelX > 1.0) {
			// left tilt
			if(accelX < 0) {
				float nextAngle = spaceShipBody.getAngle() - 0.02f;
				if (nextAngle*DEGTORAD < 90 && nextAngle*DEGTORAD > -90) {
					spaceShipBody.setTransform(spaceShipBody.getPosition(), nextAngle);
				}
			} else { // right tilt
				float nextAngle = spaceShipBody.getAngle() + 0.02f;
				if (nextAngle*DEGTORAD < 90 && nextAngle*DEGTORAD > -90) {
					spaceShipBody.setTransform(spaceShipBody.getPosition(), nextAngle);
				}
			}
		}
	}
	private void UpdateSpaceshipLocation() {
		// Update sprite position based on it's physics body
		spaceship.setPosition(spaceShipBody.getPosition().x, spaceShipBody.getPosition().y);
		// Update sprite rotation based on it's physics body
		spaceship.setRotation(MathUtils.radiansToDegrees * spaceShipBody.getAngle());
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
			//Gdx.app.log(TAG, "Time Diff: "+timeDiff);
			if(timeDiff > 48) {
				spaceship.setTexture(ship3);
				spaceship.draw(batch);
			} else if (timeDiff >30) {
				spaceship.setTexture(ship2);
				spaceship.draw(batch);
			} else {
				spaceship.setTexture(ship1);
				spaceship.draw(batch);
			}
		}
	}
}
