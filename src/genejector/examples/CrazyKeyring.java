package genejector.examples;

public class CrazyKeyring
{
	private static final Integer USER_ID = 42;
	private String password;

	public Integer getUserId()
	{
		return USER_ID;
	}

	public void unlockPasswordStep1()
	{
		password = "Top";
	}

	public void unlockPasswordStep2()
	{
		password += "Secret";
	}

	public String getPassword()
	{
		return password;
	}
}
