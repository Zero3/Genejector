package genejector.shared.exceptions;

public class GenejectedExecutionException extends RuntimeException implements GenejectorException
{
	public GenejectedExecutionException(Throwable cause)
	{
		super("Exception thrown in genejected code", cause);
	}

	public GenejectedExecutionException(String string)
	{
		super(string);
	}
}