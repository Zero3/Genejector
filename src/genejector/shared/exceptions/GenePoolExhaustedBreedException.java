package genejector.shared.exceptions;

import genejector.realm.Type;

public class GenePoolExhaustedBreedException extends BreedingException implements GenejectorException
{
	public final Type growType;

	public GenePoolExhaustedBreedException(Type growType)
	{
		super("Gene pool exhausted for genes of type " + growType);
		this.growType = growType;
	}
}