package genejector.shared.messages;

import genejector.shared.Settings;
import java.io.Serializable;

public class SettingsMessage implements Serializable
{
	private static final long serialVersionUID = Settings.VERSION;
	private final Settings settings;

	public SettingsMessage(Settings settings)
	{
		this.settings = settings;
	}

	public Settings getSettings()
	{
		return settings;
	}
}