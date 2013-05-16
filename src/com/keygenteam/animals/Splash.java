package com.keygenteam.animals;

import com.actionbarsherlock.app.SherlockActivity;

import android.os.Bundle;
import android.os.Handler;
import android.content.Intent;

public class Splash extends SherlockActivity {

	// Usado para que al usar el boton volver en la clase main, no volvamos al
	// splash, si no, salgamos de la app
	private boolean mIsBackButtonPressed;
	// Tiempo de ejecucion del Splash
	private static final int SPLASH_DURATION = 3500; // 3.5 seg

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		Handler handler = new Handler();

		// pasado el SPLASH_DURATION, corre este thread
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				finish();

				if (!mIsBackButtonPressed) {
					Intent intent = new Intent(Splash.this, Main.class);
					Splash.this.startActivity(intent);
					// overridePendingTransition(R.anim.accelerate_decelerate_interpolator,
					// R.anim.accelerate_interpolator);
				}

			}

		}, SPLASH_DURATION);

	}

	@Override
	public void onBackPressed() {
		// set the flag to true so the next activity won't start up
		mIsBackButtonPressed = true;
		super.onBackPressed();

	}
}