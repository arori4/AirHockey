package com.arori4.airhockey;

import android.os.Bundle;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.arori4.airhockey.AirHockeyGame;

public class AndroidLauncher extends AndroidApplication{
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

		//use some optimizations
		config.useAccelerometer = false;
		config.useCompass = false;

		initialize(new AirHockeyGame(), config);
	}

}
