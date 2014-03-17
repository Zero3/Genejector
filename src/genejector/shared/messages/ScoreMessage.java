package genejector.shared.messages;

import genejector.shared.Settings;
import java.io.Serializable;

public class ScoreMessage implements Serializable
{
	private static final long serialVersionUID = Settings.VERSION;
	private final long score;

	public ScoreMessage(long score)
	{
		this.score = score;
	}

	public long getScore()
	{
		return score;
	}
}