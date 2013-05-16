package gallery.lib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

public class ImageManager {
    private Context mContext;
    private AssetManager am;
    private File cacheDir;
    private Boolean cacheable=false;
    
    public ImageManager(Context mcontext) {
        mContext = mcontext;
        am = mContext.getAssets();
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
		{
            cacheDir=new File(Environment.getExternalStorageDirectory(),"minubeguides");
            cacheable=true;
            if(!cacheDir.exists())
                cacheDir.mkdirs();
		}
    }

    public Bitmap getImage(String url)
    {
    	String filename=String.valueOf(url.hashCode());
		File f = new File(cacheDir + "/" + filename);
		InputStream is;
		
		if (f.exists())
		{
			try {
				return decodeStream(new FileInputStream(f));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}else{
			try {
				is=new URL(url).openStream();
				if (cacheable)
		    	{
		    		OutputStream os;
		            os = new FileOutputStream(f);
		            CopyStream(is, os);
		            os.close();
		            return decodeStream(new FileInputStream(f));
		    	}
				return decodeStream(is);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    	return null;
    }
    
    private Bitmap decodeStream(InputStream is)
    {
    	try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            
            final int REQUIRED_SIZE=70;
            int width_tmp=o.outWidth, height_tmp=o.outHeight;
            int scale=1;
            while(true){
                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                    break;
                width_tmp/=2;
                height_tmp/=2;
                scale*=2;
            }
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;

           	return BitmapFactory.decodeStream(is, null, o2);
        } catch (Exception e) {}
    	return null;
    }
    
    private static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
              int count=is.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
              os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }
    
    public void fetchAsyncImageCallback(final String urlString, final ImageView imageView,final Handler call) 
    {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
            	Bitmap bm = (Bitmap) message.obj;
                imageView.setImageBitmap(bm);
                Message msg = new Message();
	        	call.sendMessage(msg);
            }
        };

        Thread thread = new Thread() {
            @Override
            public void run() {
                Bitmap bm = getImage(urlString);
                Message message = handler.obtainMessage(1, bm);
                handler.sendMessage(message);
            }
        };
        thread.start();
    }
    
    public void getAsyncImageCallback(final String urlString, final Handler call) 
    {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Bitmap bm = getImage(urlString);
                Message message = call.obtainMessage(1, bm);
                call.sendMessage(message);
            }
        };
        thread.start();
    }
    
    public void fetchAsyncImage(final String urlString, final ImageView imageView) 
    {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
            	Bitmap bm = (Bitmap) message.obj;
                imageView.setImageBitmap(bm);
            }
        };

        Thread thread = new Thread() {
            @Override
            public void run() {
                Bitmap bm = getImage(urlString);
                Message message = handler.obtainMessage(1, bm);
                handler.sendMessage(message);
            }
        };
        thread.start();
    }

    public Boolean checkIfAssetExists(String path)
    {
    	boolean bAssetOk = false;
        try {
            InputStream stream = am.open(path);
            stream.close();
            bAssetOk = true;
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        return bAssetOk;
    }
    
    public void getAsyncAssetCallback(final String path, final Handler call)
    {
    	Thread thread = new Thread() {
            @Override
            public void run() {
            	try {
                	InputStream is = am.open(path);
                    if (is != null)
                    {
                    	Bitmap bm = decodeStream(is);
        	            is.close();
        	            Message message = call.obtainMessage(1, bm);
                        call.sendMessage(message);
                    }
                } catch (MalformedURLException e) {
                } catch (IOException e) {
                }
            }
        };
        thread.start();
    }
}