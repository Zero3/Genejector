package genejector.shared.exceptions;

public class NonWhitelistedTypeException extends RuntimeException implements GenejectorException
{
	private final String offendingType;

	public NonWhitelistedTypeException(String offendingType)
	{
		super();
		
		this.offendingType = offendingType;
	}

	public String getOffendingType()
	{
		return offendingType;
	}
}