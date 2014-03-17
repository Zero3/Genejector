package genejector.project;

import genejector.shared.Settings;
import genejector.shared.exceptions.NoSolutionFoundException;
import genejector.shared.exceptions.ProcessCommunicationException;
import genejector.shared.exceptions.RealmException;
import genejector.shared.messages.NoSolutionFoundMessage;
import genejector.shared.messages.SettingsMessage;
import genejector.shared.messages.SolutionMessage;
import genejector.shared.util.Messenger;
import genejector.shared.util.StreamConsumerThread;
import genejector.shared.util.Tools;
import java.io.IOException;

public class ProjectInstanceManager
{
	private static String solutionSourceCode = null;

	public static byte[] getSolution()
	{
		System.out.println("Genjector: Request to bind to project received. Starting...");

		// Prepare messenger
		Messenger messenger = new Messenger(Settings.COMMUNICATIONS_PORT_PROJECT);

		try
		{
			// Awake Dwayna and wait for her to return a solution
			// TODO: Pass on original command line arguments
			ProcessBuilder pb = new ProcessBuilder(
				"java",
				"-cp",
				System.getProperty("java.class.path"),
				"genejector.realm.RealmInstanceManager"
			);

			// Let's go
			Process proc = null;

			try
			{
				proc = pb.start();
			}
			catch (IOException ex)
			{
				throw new RuntimeException(ex);	// This should never happen. Just wrap it in a RuntimeException.
			}

			// Forward output of launched instance to output of this instance (getInputStream() is actually the console output for some confusing reason)
			StreamConsumerThread.dispatch(new StreamConsumerThread(proc.getInputStream(), System.out, false));
			StreamConsumerThread.dispatch(new StreamConsumerThread(proc.getErrorStream(), System.err, false));

			// Send settings
			messenger.readReadyMessage();											// Wait for realm to say hello
			messenger.writeMessage(new SettingsMessage(Settings.getSettings()));	// Send settings

			// Receive solution
			ProcessCommunicationException messengerFailure = null;
			Object message = null;
			try
			{
				message = messenger.readMessage();
			}
			catch (ProcessCommunicationException ex)
			{
				messengerFailure = ex;	// Rethrow below
			}

			// Wait for process to end
			try
			{
				proc.waitFor();
			}
			catch (InterruptedException ex)
			{
				throw new RuntimeException(ex);	// This should never happen. Just wrap it in a RuntimeException.
			}

			// Check if realm terminated unexpectedly (due to an exception or something). If so, throw an exception for project code to handle
			if (proc.exitValue() != 0)
			{
				throw new RealmException("Genjector realm instance terminated unexpectedly with exit code " + proc.exitValue());
			}
			else if (messengerFailure != null)
			{
				throw messengerFailure;
			}

			if (message instanceof NoSolutionFoundMessage)
			{
				throw new NoSolutionFoundException();
			}

			// Unpack solution
			SolutionMessage solutionMessage = (SolutionMessage) message;
			solutionSourceCode = solutionMessage.getSourceCode();

			return solutionMessage.getMortal();
		}
		finally
		{
			Tools.release(messenger);
		}
	}

	public static String getSolutionSourceCode()
	{
		return solutionSourceCode;
	}
}