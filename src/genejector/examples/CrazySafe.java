package genejector.examples;

public class CrazySafe
{
	private final Integer nuclearLaunchCode;

	public CrazySafe(Integer userid, String password)
	{
		if (userid.equals(42) && password.equals("TopSecret"))
		{
			nuclearLaunchCode = 542679985;
		}
		else
		{
			nuclearLaunchCode = null;
		}
	}

	public Integer getNuclearLaunchCode()
	{
		return nuclearLaunchCode;
	}
}