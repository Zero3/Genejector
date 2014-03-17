package genejector.shared.messages;

import genejector.shared.Settings;
import java.io.Serializable;
import java.util.Arrays;

public class MortalMessage implements Serializable
{
	private static final long serialVersionUID = Settings.VERSION;
	private final byte[] mortal;

	public MortalMessage(byte[] mortal)
	{
		this.mortal = Arrays.copyOf(mortal, mortal.length);
	}
	
	public byte[] getMortal()
	{
		return Arrays.copyOf(mortal, mortal.length);
	}
}