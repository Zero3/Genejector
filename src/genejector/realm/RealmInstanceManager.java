package genejector.realm;

import genejector.realm.gods.Dwayna;
import genejector.shared.Genejector;
import genejector.shared.Settings;
import genejector.shared.messages.NoSolutionFoundMessage;
import genejector.shared.messages.ReadyMessage;
import genejector.shared.messages.SettingsMessage;
import genejector.shared.messages.SolutionMessage;
import genejector.shared.util.Messenger;
import genejector.shared.util.StreamConsumerThread;
import genejector.shared.util.Tools;
import java.util.Random;

public class RealmInstanceManager
{
	public static Random random = new Random(Settings.getSettings().getRandomSeed());

	public static void main(String[] args)
	{
		Messenger messenger = null;

		// Set instance role
		Genejector.setInstanceRole(Genejector.InstanceRole.REALM);

		// Keep an eye on parent process and suicide if it disappears
		StreamConsumerThread.dispatch(new StreamConsumerThread(System.in, null, true));

		try
		{
			// Connect to project instance and receive settings object
			messenger = new Messenger(Messenger.LOCALHOST, Settings.COMMUNICATIONS_PORT_PROJECT);
			messenger.writeMessage(new ReadyMessage());										// Say hello
			Settings.setSettings(((SettingsMessage)messenger.readMessage()).getSettings());	// Receive settings

			// Launch Dwayna!
			Solution solution = new Dwayna().rise();

			// Handle result
			if (solution == null)
			{
				messenger.writeMessage(new NoSolutionFoundMessage());
			}
			else
			{
				messenger.writeMessage(new SolutionMessage(solution.getClassBytes(), solution.getSourceCode()));
			}
		}
		finally
		{
			Tools.release(messenger);
		}
	}
}