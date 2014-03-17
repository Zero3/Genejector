package genejector.realm.genes;

import genejector.realm.genetraits.GeneTrait;
import genejector.realm.genetraits.ParentTrait;
import genejector.realm.genetraits.ReferenceTrait;
import genejector.shared.Settings;
import java.io.Serializable;
import java.util.Comparator;

public abstract class AbstractGene implements GeneTrait, Serializable
{
	private static final long serialVersionUID = Settings.VERSION;
	private static int prototypeIdCounter = 0;							// Global ID counter. Ensures that each prototype gene gets an unique ID
	private static long instanceIdCounter = 0;							// Global ID counter. Ensures that each instantiated gene gets an unique ID

	private Integer prototypeId = null;
	private Long instanceId = null;
	private ParentTrait parentGene = null;

	private static synchronized int generatePrototypeId()
	{
		prototypeIdCounter++;
		return prototypeIdCounter;
	}

	public void assignPrototypeId()
	{
		prototypeId = generatePrototypeId();
	}

	@Override
	public final Integer getPrototypeId()
	{
		return prototypeId;
	}

	private static synchronized long generateInstanceId()
	{
		instanceIdCounter++;
		return instanceIdCounter;
	}

	@Override
	public final void instantiate()
	{
		instanceId = generateInstanceId();

		if (this instanceof ParentTrait)
		{
			ParentTrait parentThis = (ParentTrait) this;

			for (int i = 0; i < parentThis.getChildCount(); i++)
			{
				if (!(parentThis instanceof ReferenceTrait) || !((ReferenceTrait) parentThis).isBackReference(i))
				{
					GeneTrait childGene = parentThis.getChild(i);

					if (childGene != null)
					{
						childGene.instantiate();	// Recurse!
					}
				}
			}
		}
	}

	@Override
	public final String toString()
	{
		return "[G" + prototypeId + (instanceId != null ? ("|I" + instanceId) : "") + "]";
	}

	@Override
	public final ParentTrait getParent()
	{
		return parentGene;
	}

	@Override
	public final void setParent(ParentTrait newParent)
	{
		parentGene = newParent;
	}

	@Override
	public final boolean equals(Object obj)
	{
		if (!(obj instanceof AbstractGene))
		{
			return false;
		}

		AbstractGene other = (AbstractGene) obj;

		// Instance comparison
		if (instanceId != null)
		{
			return instanceId.equals(other.instanceId);
		}
		else if (other.instanceId != null)
		{
			return false;	// We have a null id and they don't. Not a match.
		}

		// Prototype comparison
		return prototypeEquals(other);
	}

	@Override
	public int hashCode()
	{
		// It's important to only include prototype fields in the hashcode generation.
		// Of not, all our HashMaps will go crazy when genes get new childs and their
		// hashcodes changes while in the maps (which leads to undefined behaviour)
		return prototypeHashCode();
	}

	public static class PrototypeIdComparator implements Comparator<GeneTrait>
	{
		@Override
		public int compare(GeneTrait o1, GeneTrait o2)
		{
			return o1.getPrototypeId().compareTo(o2.getPrototypeId());
		}
	}
}