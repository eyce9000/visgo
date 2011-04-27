package srl.visgo.data;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class SoundFile
{
	private static final int	EXTERNAL_BUFFER_SIZE = 128000;
	private String pathToFile;

	public SoundFile(String path)
	{
		pathToFile = path;
	}
	
	class InternalSoundThread implements Runnable
	{
		Thread t;
		public void init()	
		{	
			t = new Thread(this);
			t.start();
		}

		@SuppressWarnings("deprecation")
		public void run()
		{
			File soundFile = new File(pathToFile);
			AudioInputStream audioInputStream = null;
			try
			{
				audioInputStream = AudioSystem.getAudioInputStream(soundFile);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return;
			}
			
			AudioFormat	audioFormat = audioInputStream.getFormat();
			SourceDataLine	line = null;
			DataLine.Info	info = new DataLine.Info(SourceDataLine.class, audioFormat);
			try
			{
				line = (SourceDataLine) AudioSystem.getLine(info);

				/*
				  The line is there, but it is not yet ready to
				  receive audio data. We have to open the line.
				*/
				line.open(audioFormat);
			}
			catch (LineUnavailableException e)
			{
				e.printStackTrace();
				return;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return;
			}
			
			line.start();
			int	nBytesRead = 0;
			byte[]	abData = new byte[EXTERNAL_BUFFER_SIZE];
			while (nBytesRead != -1)
			{
				try
				{
					nBytesRead = audioInputStream.read(abData, 0, abData.length);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				if (nBytesRead >= 0)
				{
					line.write(abData, 0, nBytesRead);
				}
			}
			
			line.drain();
			line.close();
			t.stop();
		}
	}
	
	public void play()
	{
		InternalSoundThread soundThread = new InternalSoundThread();
		soundThread.init();
	}
}
