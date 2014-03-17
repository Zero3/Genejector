package genejector.realm;

import java.util.Arrays;

public class Solution
{
	private final String sourceCode;
	private final byte[] classBytes;
	private final long score;
	private final String id;
	
	public Solution(String sourceCode, byte[] classBytes, long score, String id)
	{
		this.sourceCode = sourceCode;
		this.classBytes = Arrays.copyOf(classBytes, classBytes.length);
		this.score = score;
		this.id = id;
	}

	public String getSourceCode()
	{
		return sourceCode;
	}

	public byte[] getClassBytes()
	{
		return Arrays.copyOf(classBytes, classBytes.length);
	}

	public long getScore()
	{
		return score;
	}

	public String getId()
	{
		return id;
	}
}