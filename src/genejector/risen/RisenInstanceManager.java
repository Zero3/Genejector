package genejector.risen;

import genejector.shared.Genejector;
import genejector.shared.Settings;
import genejector.shared.exceptions.ProjectExecutionException;
import genejector.shared.exceptions.ReflectionException;
import genejector.shared.messages.MortalMessage;
import genejector.shared.messages.ReadyMessage;
import genejector.shared.messages.ScoreMessage;
import genejector.shared.messages.SettingsMessage;
import genejector.shared.util.Messenger;
import genejector.shared.util.StreamConsumerThread;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RisenInstanceManager
{
	private static Messenger messenger = new Messenger(Messenger.LOCALHOST, Settings.COMMUNICATIONS_PORT);

	public static void main(String[] args)
	{
		Genejector.setInstanceRole(Genejector.InstanceRole.RISEN);

		// Keep an eye on parent process and suicide if it disappears
		StreamConsumerThread.dispatch(new StreamConsumerThread(System.in, null, true));

		// Initial communication
		messenger.writeMessage(new ReadyMessage());											// Say hello to server
		Settings.setSettings(((SettingsMessage) messenger.readMessage()).getSettings());	// Receive settings

		// Call original main method
		try
		{
			Class<?> clazz = Class.forName(Settings.getSettings().getProjectMain());
			Method mainMethod = clazz.getMethod("main", String[].class);
			String[] params = null;	// TODO: Needs to be transferred from original instance
			mainMethod.invoke(null, (Object) params);
		}
		catch (ClassNotFoundException ex)
		{
			throw new ReflectionException(ex);
		}
		catch (NoSuchMethodException ex)
		{
			throw new ReflectionException(ex);
		}
		catch (IllegalAccessException ex)
		{
			throw new ReflectionException(ex);
		}
		catch (InvocationTargetException ex)
		{
			throw new ProjectExecutionException(ex.getCause());	// Rewrap
		}
	}

	public static byte[] getMortal()
	{
		messenger.writeMessage(new ReadyMessage());
		return ((MortalMessage) messenger.readMessage()).getMortal();
	}

	public static void submitScore(long score)
	{
		messenger.writeMessage(new ScoreMessage(score));
	}
}