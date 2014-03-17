package genejector.shared.exceptions;

public class ImpossibleBreedingException extends BreedingException implements GenejectorException
{
	public ImpossibleBreedingException(String message)
	{
		super(message);
	}
}