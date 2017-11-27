package com.lonestones.vesada;

import android.os.Bundle;

// libGDX -kirjastot
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.lonestones.vesada.JamppaMaalla;

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
