package com.keygenteam.animals;

import android.os.Bundle;
import android.webkit.WebView;
import android.app.Activity;

public class Aves extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_aves);
		
		WebView web = (WebView)findViewById(R.id.web_Aves);
		web.loadUrl("file:///android_asset/aves.html");
		
	}
}