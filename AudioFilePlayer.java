import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.SourceDataLine;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.LineUnavailableException;

/**
 * @class AudioFilePlayer plays *.wav files.
 * No real reason to include it except for giggles.
 */
public class AudioFilePlayer {
	
	private final int SAMPLE_SIZE_BITS = 16;
	private final int MAX_BUFFER = 4096;

	public void play(String filePath) {
		File file = new File(filePath);

		try (AudioInputStream input = AudioSystem.getAudioInputStream(file)) {

			AudioFormat outputFormat = getOutFormat(input.getFormat());
			Info info = new Info(SourceDataLine.class, outputFormat);

			try (SourceDataLine line = (SourceDataLine)AudioSystem.getLine(info)) 
			{
				if (line != null) 
				{
					line.open(outputFormat);
					line.start();
					stream(AudioSystem.getAudioInputStream(outputFormat, input), line);
					line.drain();
					line.stop();
				}
				
				else {
					line.close();
				}
			}//try line
			
		} //try input
		catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) 
		{
			throw new IllegalStateException(e);
		}
	}
	
	private AudioFormat getOutFormat(AudioFormat inputFormat) 
	{
		int channel = inputFormat.getChannels();
	 	float rate = inputFormat.getSampleRate();
		return new AudioFormat(Encoding.PCM_SIGNED, rate, SAMPLE_SIZE_BITS, channel, channel * 2, rate, false);
	}

	private void stream(AudioInputStream input, SourceDataLine line) throws IOException 
	{
		byte[] buffer = new byte[MAX_BUFFER];
		for (int i = 0; i != -1; i = input.read(buffer, 0, buffer.length)) {
			line.write(buffer, 0, i);
		}
	}
	
}