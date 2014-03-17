package genejector.examples;

import genejector.project.GenejectorProblem;

public class CrazyProblem implements GenejectorProblem
{
	private Integer nuclearLaunchCode = null;
	private CrazyKeyring keyring = new CrazyKeyring();

	@Override
	public void resetState()
	{
		nuclearLaunchCode = null;
		keyring = new CrazyKeyring();
	}

	public CrazyKeyring getKeyring()
	{
		return keyring;
	}

	public Integer getNuclearLaunchCode()
	{
		return nuclearLaunchCode;
	}

	public void setNuclearLaunchCode(Integer nuclearLaunchCode)
	{
		this.nuclearLaunchCode = nuclearLaunchCode;
	}
}