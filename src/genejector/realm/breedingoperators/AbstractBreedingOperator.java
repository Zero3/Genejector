package genejector.realm.breedingoperators;

import genejector.realm.gods.Abaddon;
import genejector.realm.mortal.Mortal;
import genejector.realm.mortal.MortalView;
import genejector.shared.Settings;
import genejector.shared.datastructures.WeightedRandomCollection;
import java.io.Serializable;

public abstract class AbstractBreedingOperator implements Serializable
{
	private static final long serialVersionUID = Settings.VERSION;

	public abstract Mortal breed(WeightedRandomCollection<MortalView> population, Abaddon abaddon);

	protected void log(String message)
	{
		System.out.println(toString() + ": " + message);
	}

	@Override
	public String toString()
	{
		return this.getClass().getSimpleName();
	}
}