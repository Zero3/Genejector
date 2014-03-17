package genejector.shared.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class StreamConsumerThread implements Runnable
{
	private final InputStream inputStream;
	private final PrintStream outputStream;
	private final boolean exitOnStreamEnd;
	
	public StreamConsumerThread(InputStream inputStream, PrintStream outputStream, boolean exitOnStreamEnd)
	{
		this.inputStream = inputStream;
		this.outputStream = outputStream;
		this.exitOnStreamEnd = exitOnStreamEnd;
	}
	
	public static void dispatch(StreamConsumerThread consumer)
	{
		Thread streamConsumer = new Thread(consumer);
		streamConsumer.setDaemon(true);
		streamConsumer.start();
	}
	
	@Override
	public void run()
	{
		BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream));
		
		try
		{
			for (String line = inputReader.readLine(); line != null; line = inputReader.readLine())
			{
				if (outputStream != null)
				{
					outputStream.println(line);
				}
			}
		}
		catch (IOException ex)
		{
			// Do nothing
		}
		
		Tools.release(inputReader);	// Ensure that all readers and the stream are closed
		
		if (exitOnStreamEnd)
		{
			System.exit(1);
		}
	}
}