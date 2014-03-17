package genejector.shared.exceptions;

public class GenejectorIllegalStateException extends IllegalStateException implements GenejectorException
{
	public GenejectorIllegalStateException(String s)
	{
		super(s);
	}
}