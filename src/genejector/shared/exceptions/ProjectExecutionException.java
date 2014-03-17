package genejector.shared.exceptions;

public class ProjectExecutionException extends RuntimeException implements GenejectorException
{
	public ProjectExecutionException(Throwable cause)
	{
		super("Exception thrown in project code", cause);
	}

	public ProjectExecutionException(String message)
	{
		super(message);
	}
}