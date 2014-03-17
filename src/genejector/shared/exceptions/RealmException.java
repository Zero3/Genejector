package genejector.shared.exceptions;

public class RealmException extends RuntimeException implements GenejectorException
{
	public RealmException(String message)
	{
		super(message);
	}
}