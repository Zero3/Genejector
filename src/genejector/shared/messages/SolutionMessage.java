package genejector.shared.messages;

import genejector.shared.Settings;
import java.io.Serializable;
import java.util.Arrays;

public class SolutionMessage implements Serializable
{
	private static final long serialVersionUID = Settings.VERSION;
	private final byte[] mortal;
	private final String sourceCode;

	public SolutionMessage(byte[] mortal, String sourceCode)
	{
		this.mortal = Arrays.copyOf(mortal, mortal.length);
		this.sourceCode = sourceCode;
	}
	
	public byte[] getMortal()
	{
		return Arrays.copyOf(mortal, mortal.length);
	}

	public String getSourceCode()
	{
		return sourceCode;
	}
}