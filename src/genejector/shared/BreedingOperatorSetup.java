package genejector.shared;

import genejector.realm.breedingoperators.AbstractBreedingOperator;
import java.io.Serializable;

public class BreedingOperatorSetup implements Serializable
{
	private static final long serialVersionUID = Settings.VERSION;
	public final AbstractBreedingOperator breedingOperator;
	public final double weight;

	public BreedingOperatorSetup(AbstractBreedingOperator breedingOperator, double weight)
	{
		this.breedingOperator = breedingOperator;
		this.weight = weight;
	}

	@Override
	public String toString()
	{
		return breedingOperator.getClass().getSimpleName() + "(" + weight + ")";
	}
}