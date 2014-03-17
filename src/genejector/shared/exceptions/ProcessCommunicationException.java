package genejector.shared.exceptions;

public class ProcessCommunicationException extends RuntimeException implements GenejectorException
{
	public ProcessCommunicationException(Throwable cause)
	{
		super(cause);
	}

	public ProcessCommunicationException(String message)
	{
		super(message);
	}
}