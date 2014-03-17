package genejector.shared.exceptions;

public class ReflectionException extends RuntimeException implements GenejectorException
{
	public ReflectionException(Throwable cause)
	{
		super(cause);
	}
}