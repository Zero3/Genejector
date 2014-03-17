package genejector.shared.exceptions;

public class BreedingException extends RuntimeException implements GenejectorException
{
	public BreedingException(String message)
	{
		super(message);
	}
}