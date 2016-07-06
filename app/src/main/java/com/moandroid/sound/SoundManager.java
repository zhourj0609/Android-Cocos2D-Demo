package com.moandroid.sound;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import android.util.Log;

public class SoundManager {
	public static final String  LOG_TAG = SoundManager.class.getSimpleName();
	
	private class Media implements MediaPlayer.OnErrorListener,MediaPlayer.OnCompletionListener
	{
		private int _nID = -1;
		public Context _context;
		public  MediaPlayer _mediaplayer;
		public  boolean _bLoop = false;;
		public  boolean _bMutex = false;
		public boolean _isPause = false;
		public Uri _path;
		
		public Media(Context context,int id, boolean loop,boolean mutex)
		{
			_nID = id;
			_bLoop = loop;
			_bMutex = mutex;
			_context = context;
			_mediaplayer = MediaPlayer.create(_context, _nID);
			_mediaplayer.setLooping(_bLoop);
			_mediaplayer.setOnCompletionListener(this);
	        _mediaplayer.setOnErrorListener(this);
		}
		
		public Media(Context context, String filePath, boolean loop, boolean mutex) throws IOException {
			if(filePath.charAt(0)== '/'){
				Uri uri = Uri.fromFile(new File(filePath));
				_mediaplayer = MediaPlayer.create(context, uri);
			}
			else{
				FileDescriptor fd;
				AssetFileDescriptor afd = context.getAssets().openFd(filePath);
				long offset = afd.getStartOffset();
				long len = afd.getLength();
				fd = afd.getFileDescriptor();
				_mediaplayer = new MediaPlayer();//.create(_context, _path);
				_mediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				_mediaplayer.setDataSource(fd, offset, len);			
			}
			_bLoop = loop;
			_bMutex = mutex;
			_context = context;
			_mediaplayer.setLooping(_bLoop);
			_mediaplayer.setOnCompletionListener(this);
	        _mediaplayer.setOnErrorListener(this);
			_mediaplayer.prepare();
			//_mediaplayer.seekTo(0);
		}

		public boolean Pause()
		{
			try
			{
				if(_mediaplayer!=null && _mediaplayer.isPlaying())
				{
					_mediaplayer.pause();
					_isPause = true;
				}
			}
			catch (IllegalStateException e)
			{
				e.printStackTrace();
			}
			return true;			
		}
		
		public boolean Resume()
		{
			try
			{
				if(_mediaplayer!=null && _isPause==true)
				{
					if(!_mediaplayer.isPlaying())
					{
						_mediaplayer.start();
					}
					_isPause = false;
				}
			}
			catch (IllegalStateException e)
			{
				e.printStackTrace();
			}
			return true;			
		}
		
		public boolean Prepare()
		{
			try
			{
				_mediaplayer.prepare();
				//_mediaplayer.seekTo(0);
			}
			catch (IllegalStateException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			return true;
		}
		
		//Called when the end of a media source is reached during playback.
		public void onCompletion(MediaPlayer _mediaplayer)
		{
			Log.v(LOG_TAG, "onCompletion()!");
			if(!_bLoop)
			{
				_mediaplayer.stop();
				Prepare();
				if(_nID != -1)
					_sharedSoundManager.onNotify(_nID,_bMutex);
				else if(_path != null){
					_sharedSoundManager.onNotify(_path.getPath(),_bMutex);
				}
			}
		}
	
		//Called to indicate an error.
		//True if the method handled the error, false if it didn't.
		public boolean  onError(MediaPlayer _mediaplayer, int what, int extra)
		{
			if(what == MediaPlayer.MEDIA_ERROR_UNKNOWN)
			   Log.e(LOG_TAG,"Unspecified media player error.");
			else
				Log.e(LOG_TAG,"Media server died.");
			_mediaplayer.reset();
			return false;
		}
	}
	
	private boolean onNotify(int _nID,boolean _bMutex)
	{
		if(_bMutex)
		{
			Iterator<Entry<Integer, Media>> it = sounds_ID.entrySet().iterator();
	    	while (it.hasNext()) {
	    		Entry<Integer, Media> entry = it.next();
	    		if(entry.getKey() == _nID){
	    			continue;
	    		}
	    		Media _m = entry.getValue();

	    		if(_m._bLoop && !_m._mediaplayer.isPlaying())
	    		{
	    			_m._mediaplayer.start();
	    		}
	    	}
		}
		return true;
	}

	private boolean onNotify(String filePath,boolean _bMutex)
	{
		if(_bMutex)
		{
			Iterator<Entry<String, Media>> it = sounds_URI.entrySet().iterator();
	    	while (it.hasNext()) {
	    		Entry<String, Media> entry = it.next();
	    		if(entry.getKey() == filePath)
	    		{
	    			continue;
	    		}
	    		Media _m = entry.getValue();

	    		if(_m._bLoop && !_m._mediaplayer.isPlaying())
	    		{
	    			_m._mediaplayer.start();
	    		}
	    	}
		}
		return true;
	}
	
	private static SoundManager _sharedSoundManager;
	
	public static SoundManager sharedSoundManager()
	{
		if(_sharedSoundManager == null)
		{
			_sharedSoundManager = new SoundManager();
		}
		return _sharedSoundManager;
	}
	
	public static void purgeSharedManager(){
		if(_sharedSoundManager != null){
			_sharedSoundManager.unLoadAll();
			_sharedSoundManager = null;
		}
	}
	
	private static HashMap<Integer,Media> sounds_ID;
	private static HashMap<String,Media> sounds_URI;
	
	protected SoundManager(){
		sounds_ID = new HashMap<Integer,Media>(10);
		sounds_URI = new HashMap<String,Media>(10);
	}
	
	public boolean loadSound(Context context, int ID, boolean loop, boolean mutex)
	{
		if(!sounds_ID.containsKey(ID))
		{
			Media m;
			m = new Media( context, ID,
					loop,
					mutex);
			sounds_ID.put(ID, m);
			return true;
		}
		return true;		
	}
	
	public boolean loadSound(Context context, String filePath, boolean loop, boolean mutex)
	{
		if(!sounds_URI.containsKey(filePath))
		{
			Media m;
			try {
				m = new Media(context,filePath,
						loop,
						mutex);
				sounds_URI.put(filePath, m);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		}
		return true;		
	}
	
	public boolean playSound(int ID)
	{
		Media m = sounds_ID.get(ID);
		if(m != null)
		{
		  if(m._bMutex)
		  {
			   Collection<Media> values = sounds_ID.values();
			   for(Iterator<Media> it = values.iterator();it.hasNext();)
			   {
				  Media _m  = it.next();
				  if(_m._mediaplayer.isPlaying())
					    _m._mediaplayer.pause();
			  }
		  }
			try
			{
				m._mediaplayer.start();		
			}
			catch (IllegalStateException e)
			{
				e.printStackTrace();
			}
		}
		return m!=null;
	}
	
	public boolean playSound(String filePath)
	{
		Media m = sounds_URI.get(filePath);
		if(m != null)
		{
		  if(m._bMutex)
		  {
			   Collection<Media> values = sounds_ID.values();
			   for(Iterator<Media> it = values.iterator();it.hasNext();)
			   {
				  Media _m  = it.next();
				  if(_m._mediaplayer.isPlaying())
					    _m._mediaplayer.pause();
			  }
		  }
		  m._mediaplayer.start();			
		}
		return m!=null;
	}

	public void stopSound(String filePath)
	{
		Media m = sounds_URI.get(filePath);
		if(m != null)
		{
			m._mediaplayer.stop();
			m.Prepare();
			onNotify(filePath, m._bMutex);
		}		
	}
	
	public void stopSound(int ID)
	{
		Media m = sounds_ID.get(ID);
		if(m != null)
		{
			m._mediaplayer.stop();
			m.Prepare();
			onNotify(ID, m._bMutex);
		}		
	}
	
	public void unloadSound(int ID){
		Media m = sounds_ID.get(ID);
		if(m!=null){
			m._context = null;
			m._mediaplayer.release();
			sounds_ID.remove(ID);
		}
	}

	public void unloadSound(String filepath){
		Media m = sounds_URI.get(filepath);
		if(m!=null){
			m._context = null;
			m._mediaplayer.release();
			sounds_ID.remove(filepath);
		}
	}
	
	public void unLoadAll()
	{
	   Collection<Media> values = sounds_ID.values();
	   for(Iterator<Media> it = values.iterator(); it.hasNext();)
	   {
		  Media _m  = it.next();
		  _m._context=null;
		  _m._mediaplayer.release();
	   }
	   sounds_ID.clear();
	   
	   values = sounds_URI.values();
	   for(Iterator<Media> it = values.iterator(); it.hasNext();)
	   {
		  Media _m = it.next();
		  _m._context=null;
		  _m._mediaplayer.release();
	   }
	   sounds_URI.clear();
	}
	
	public void pauseAll()
	{
		Collection<Media> values = sounds_ID.values();
	   for(Iterator<Media> it = values.iterator();it.hasNext();)
	   {
		  Media _m  = it.next();
		  _m.Pause();
	  }		
	   
		values = sounds_URI.values();
	   for(Iterator<Media> it = values.iterator();it.hasNext();)
	   {
		  Media _m  = it.next();
		  _m.Pause();
	  }	
	}
	
	public void resumeAll()
	{
		Collection<Media> values = sounds_ID.values();
	   for(Iterator<Media> it = values.iterator();it.hasNext();)
	   {
		  Media _m  = it.next();
		  _m.Resume();
	  }	
	   values = sounds_URI.values();
	   for(Iterator<Media> it = values.iterator();it.hasNext();)
	   {
		  Media _m  = it.next();
		  _m.Resume();
	  }	      
	}
	
}
