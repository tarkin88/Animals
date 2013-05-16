package com.keygenteam.animals;

import gallery.lib.ZoomGallery;

import java.io.IOException;
import java.util.ArrayList;

import android.os.Bundle;
import android.widget.Toast;
import android.app.Activity;
import android.content.res.AssetManager;

public class GaleriaAves extends Activity {

	// se declara la ruta de las imagenes
	ArrayList<String> pics_path = new ArrayList<String>();
	ZoomGallery gallery;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_galeria_aves);
		
		AssetManager assetManager = getAssets();
	    String[] files = null;
	    try {
	        files = assetManager.list("");
	    } catch (IOException e) {
	    }

	    for(String filename : files) 
	    {
	    	if (filename.contains("jpg"))
	    		pics_path.add(filename);
	    }
	    gallery = new ZoomGallery(this,pics_path);
	    setContentView(gallery.getLayout());
	    gallery.goToPic(0);

	    gallery.setOnClickListener(new ZoomGallery.OnScreenClickListener() 
		{
			@Override
			public void onScreenClicked(int screen) {
				//Log.d("click","click on screen " + screen);
				String Texto = "Test de galeria";
				Toast.makeText(getApplicationContext(), Texto, Toast.LENGTH_SHORT).show();
				
				
			}
		
	    });
	}
}
