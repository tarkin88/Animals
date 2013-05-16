package com.keygenteam.animals;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class Main extends SherlockFragmentActivity {
	ViewPager vp;
	private vpAdapter miAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		vp = (ViewPager) findViewById(R.android.viewpager);
		miAdapter = new vpAdapter();
		vp.setAdapter(miAdapter);

	}

	private class vpAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return 4;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			// va a regresar los view o los layouts
			return view == ((RelativeLayout) object);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// destruye el layout anterior
			((ViewPager) container).removeView((RelativeLayout) object);

		}

		@Override
		public void finishUpdate(ViewGroup container) {
			// TODO Auto-generated method stub

		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// aqui sucede la magia xDD
			LayoutInflater inflater = (LayoutInflater) container.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v = null;
			switch (position) {
			// Todo esto para lo referente a la clase e informacion aves
			case 0:
				v = inflater.inflate(R.layout.aves, null);
				// Boton que nos lleva a Informacion (HTML+CSS)
				ImageButton b_aves = (ImageButton) v
						.findViewById(R.id.ibtn_infoAves);
				b_aves.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent avesClass = new Intent();
						avesClass.setClass(Main.this, Aves.class);
						startActivity(avesClass);
					}
				});
				// Boton que nos lleva a Imagenes
				ImageButton galeria_aves = (ImageButton) v
						.findViewById(R.id.ibtn_picsAves);
				galeria_aves.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent avesPics = new Intent();
						avesPics.setClass(Main.this, GaleriaAves.class);
						startActivity(avesPics);
					}
				});

				// Boton que nos lleva a videos
				ImageButton video_aves = (ImageButton) v
						.findViewById(R.id.ibtn_vidAves);
				video_aves.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Intent avesVideo = new Intent();
						avesVideo.setClass(Main.this, Videos_Aves.class);
						startActivity(avesVideo);
					}
				});

				// fin aves

				break;
			case 1:// inicia mamiferos
				v = inflater.inflate(R.layout.mamiferos, null);
				// declaramos el Imagen button de que nos llevara a info
				// maiferos
				ImageButton ibtn_mam_info = (ImageButton) v
						.findViewById(R.id.ibtn_infoMam);
				ibtn_mam_info.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Intent aInfoMam = new Intent();
						aInfoMam.setClass(Main.this, InfoMamiferos.class);
						startActivity(aInfoMam);
					}
				});//fin info mamiferos
				//nos lleva a la galeria de los mamiferos
				ImageButton ibtn_mam_gal = (ImageButton) v
						.findViewById(R.id.ibtn_picsMam);
				ibtn_mam_gal.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Intent aGaleriaMam = new Intent();
						aGaleriaMam.setClass(Main.this, GaleriaMamiferos.class);
						startActivity(aGaleriaMam);

					}
				});

				break;
			case 2:
				v = inflater.inflate(R.layout.reptiles, null);
				break;
			case 3:

				v = inflater.inflate(R.layout.anfibios, null);
				break;

			}
			((ViewPager) container).addView(v, 0);
			return v;

		}

		@Override
		public Parcelable saveState() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void startUpdate(ViewGroup container) {
			// TODO Auto-generated method stub

		}
	}

}
