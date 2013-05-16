package gallery.lib;

import java.util.ArrayList;

import com.keygenteam.animals.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

public class ZoomGallery 
{
	private ArrayList<String> Urls;
	private static LayoutInflater inflater=null;
	private ImageManager imgLoader;
	private Context mContext;
	ViewSwitcher viewSwitcher;
	Boolean busy=true;
	int total_views=0;
	public OnScreenClickListener pvtOnScreenClickListener;
	
	public ZoomGallery(Context context,ArrayList<String> urls) {
		Urls = urls;
		mContext=context;
		init();
	}
	
	private void init()
	{
		inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imgLoader = new ImageManager(mContext);
		viewSwitcher = new ViewSwitcher(mContext.getApplicationContext());
		total_views = viewSwitcher.getChildCount();

		viewSwitcher.setOnScreenSwitchListener(new ViewSwitcher.OnScreenSwitchListener() 
		{
			public void onScreenSwitched(int screen) {
				loadImageAtPosition(screen,false);
			}
		});
		viewSwitcher.setOnScreenClickListener(new ViewSwitcher.OnScreenClickListener() 
		{
			public void onScreenClicked(int screen) {
				pvtOnScreenClickListener.onScreenClicked(screen);
			}
		});

		for (int i=0; i<Urls.size();i++)
			createChild(i);
	}
	
	public ViewSwitcher getLayout()
	{
		/**
		 * Returns the screen layout
		 * 
		 */
		return viewSwitcher;
	}
	
	public void goToPic(int pos)
	{
		/**
		 * Jump to selected picture
		 * 
		 * @param pos The desired position
		 */
		viewSwitcher.setCurrentScreen(pos);
		loadImageAtPosition(pos,false);
	}
	
	public static interface OnScreenClickListener {
		/**
		 * Notifies listeners about click in the screen. Runs after the animation completed.
		 * 
		 * @param screen clicked
		 */
		void onScreenClicked(int screen);

	}
	
	public void setOnClickListener(OnScreenClickListener onScreenClickListener) {
		pvtOnScreenClickListener = onScreenClickListener;
	}
	
	private void loadImageAtPosition(final int pos,final Boolean itSelf)
	{
		busy=true;
	
		if (!itSelf)
			saveMemory(pos);
		
		try{
			View child = viewSwitcher.getChildAt(pos);
			 final ProgressBar p = (ProgressBar)child.findViewById(R.id.progress);
			 final ImageViewTouch photo = (ImageViewTouch)child.findViewById(R.id.photo);
			final String filename = Urls.get(pos);
			if (photo.getDrawable()==null)
				p.setVisibility(View.VISIBLE);
			
			imgLoader.getAsyncAssetCallback(filename,new Handler()
			{
			    @SuppressLint("HandlerLeak")
				public void handleMessage(Message msg) 
			    {
			    	Bitmap bitmap = (Bitmap) msg.obj;
			    	photo.setImageBitmapReset( bitmap, 0, true );
			    	photo.setClickable(true);
			    	p.setVisibility(View.GONE);
			    	busy=false;
			    	if (!itSelf)
			    	{
			    		loadImageAtPosition(pos-1,true);
			    		loadImageAtPosition(pos+1,true);
			    	}
			    }
			});
		}catch(Exception e){}
	}
	
	private void saveMemory(int pos)
	{
		int ppos;
		
		if (pos-1 >= 0)
		{
			ppos=pos - 1;
			cleanBitmap((ImageViewTouch)viewSwitcher.getChildAt(ppos).findViewById(R.id.photo),ppos);
		}
		
		if (pos-2 >= 0)
		{
			ppos=pos - 2;
			cleanBitmap((ImageViewTouch)viewSwitcher.getChildAt(ppos).findViewById(R.id.photo),ppos);
		}
		
		if (pos+1 < total_views -1)
		{
			ppos=pos+1;
			cleanBitmap((ImageViewTouch)viewSwitcher.getChildAt(ppos).findViewById(R.id.photo),ppos);
		}
		
		if (pos+2 < total_views -1)
		{
			ppos=pos+2;
			cleanBitmap((ImageViewTouch)viewSwitcher.getChildAt(ppos).findViewById(R.id.photo),ppos);
		}
		System.gc();
	}
	
	private void cleanBitmap(ImageViewTouch img,int pos)
	{
		try{
			Drawable drawable = img.getDrawable();
			if (drawable instanceof BitmapDrawable) {
			    BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
			    Bitmap bitmap = bitmapDrawable.getBitmap();
			    bitmap.recycle();
			}
			viewSwitcher.removeViewAt(pos);
			createChild(pos);
		}catch(Exception e){
		}
	}
	
	private void createChild(int pos)
	{
		View vi = inflater.inflate(R.layout.activity_galeria_aves, null);
		viewSwitcher.addView(vi,pos);
	}
}
