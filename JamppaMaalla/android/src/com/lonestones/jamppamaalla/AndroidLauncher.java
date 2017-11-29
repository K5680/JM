package com.lonestones.jamppamaalla;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.lonestones.jamppamaalla.JamppaClass;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

		// kiihtyvyysanturi ja kompassi pois virran säästämiseksi
		config.useAccelerometer = false;
		config.useCompass = false;

		initialize(new JamppaMaalla(), config);
	}
}
