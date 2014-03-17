package genejector.realm.gods;

import genejector.shared.Settings;
import genejector.shared.exceptions.GenejectedExecutionException;
import genejector.shared.exceptions.ProcessCommunicationException;
import genejector.shared.exceptions.ProjectExecutionException;
import genejector.shared.messages.MortalMessage;
import genejector.shared.messages.ScoreMessage;
import genejector.shared.messages.SettingsMessage;
import genejector.shared.util.Messenger;
import genejector.shared.util.StreamConsumerThread;
import genejector.shared.util.Tools;
import java.io.Closeable;
import java.io.IOException;

// "I am Melandru, the Mother of earth and nature. Henceforth I bind ye to these
// lands. When they suffer, so shall ye suffer."
// — Scriptures of Melandru, 48 BE
public class Melandru implements Closeable
{
	private Process proc = null;
	private Messenger messenger = null;

	protected Melandru()
	{
		// Packpage protected constructor
	}

	// TODO: Return after dispatching and wait for scoring in another thread if scoring becomes a bottleneck
	public long bindMortal(MortalMessage mortal)
	{
		if (proc != null)
		{
			try
			{
				messenger.readReadyMessage();
			}
			catch (ProcessCommunicationException ex)
			{
				// Fail. Project probably just doesn't support being reused for scoring. TODO: Statistics on how often this happens.
				close();
			}
		}

		if (proc == null)
		{
			// Prepare messenger
			messenger = new Messenger(Settings.COMMUNICATIONS_PORT);

			// Spawn process to execute mortal
			// TODO: Pass on original command line arguments
			ProcessBuilder pb = new ProcessBuilder(
				"java",
				"-cp",
				System.getProperty("java.class.path"),
				"genejector.risen.RisenInstanceManager"
			);

			try
			{
				proc = pb.start();
			}
			catch (IOException ex)
			{
				throw new RuntimeException(ex);	// This shouldn't ever happen. Wrap in RuntimeException to handle.
			}

			// Throw away output. Note that getInputStream() is actually the console output for some reason
			StreamConsumerThread.dispatch(new StreamConsumerThread(proc.getInputStream(), System.out, false));	// TODO: Send to null?
			StreamConsumerThread.dispatch(new StreamConsumerThread(proc.getErrorStream(), System.err, false));	// TODO: Send to null?

			// Initial communication
			messenger.readReadyMessage();											// Receive hello (client needs to initiate conversation with something)
			messenger.writeMessage(new SettingsMessage(Settings.getSettings()));	// Send settings
			messenger.readReadyMessage();											// Wait for process to become ready for first mortal
		}

		// Send mortal and receive score
		// TODO: Move this to its own thread as readObject could block forever. We can interrupt the thread from the outside after a timeout instead
		try
		{
			messenger.writeMessage(mortal);										// Send mortal
			long score = ((ScoreMessage) messenger.readMessage()).getScore();	// Receive and return score

			if (score == -1)
			{
				throw new GenejectedExecutionException("Score -1 received from risen JVM");
			}

			return score;
		}
		catch (ProcessCommunicationException ex)
		{
			// TODO: Report this case in a meaningful way for stats and stuff. Split up reasons of death?
			close();
			throw new ProjectExecutionException("Risen realm died while scoring mortal");
		}
	}

	@Override
	public void close()
	{
		Tools.release(messenger, proc);
		messenger = null;
		proc = null;
	}
}