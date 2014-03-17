package genejector.shared;

import java.io.Serializable;

public class BreedingClassSetup implements Serializable, Comparable<BreedingClassSetup>
{
	private static final long serialVersionUID = Settings.VERSION;
	public final String className;
	public final boolean instantiable;

	public BreedingClassSetup(String className, boolean instantiable)
	{
		this.className = className;
		this.instantiable = instantiable;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof BreedingClassSetup)
		{
			BreedingClassSetup other = (BreedingClassSetup) obj;

			if (className.equals(other.className))
			{
				return true;
			}
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		int hash = 7;
		hash = 31 * hash + (this.className != null ? this.className.hashCode() : 0);
		return hash;
	}

	@Override
	public int compareTo(BreedingClassSetup o)
	{
		return className.compareTo(o.className);
	}
}