package genejector.realm.genes;

import genejector.realm.Type;
import genejector.realm.genetraits.GeneTrait;
import genejector.realm.genetraits.ParentTrait;
import genejector.realm.genetraits.ReferenceTrait;
import genejector.realm.mortal.SourceCompositionTask;
import genejector.shared.util.Tools;

public class AssignmentGene extends AbstractGene implements ParentTrait
{
	// Prototype immutable
	private final Type referenceType;
	private final Type childType;

	// Instance mutable
	private ReferenceTrait reference = null;
	private GeneTrait child = null;

	// Prototype constructor
	public AssignmentGene(Type referenceType, Type childType)
	{
		super();

		this.referenceType = referenceType;
		this.childType = childType;
	}

	@Override
	public boolean prototypeEquals(GeneTrait obj)
	{
		if (obj instanceof AssignmentGene)
		{
			AssignmentGene other = (AssignmentGene) obj;

			return Tools.pairEquals(
				referenceType,	other.referenceType,
				childType,		other.childType
			);
		}

		return false;
	}

	@Override
	public int prototypeHashCode()
	{
		return Tools.generateHashCode(this, referenceType, childType);
	}

	@Override
	// [ListingStart][L4]
	public String getCode(SourceCompositionTask task)
	{
		// Concise: "<variable / field name>=<code of child>"
		return reference.getCode(task) + "=" + child.getCode(task);
	}
	// [ListingEnd][L4]

	@Override
	public Type getChildType(int childIndex)
	{
		if (childIndex == 0)
		{
			return referenceType;
		}
		else if (childIndex == 1)
		{
			return childType;
		}
		else
		{
			throw new IllegalArgumentException("No such child index: " + childIndex);
		}
	}

	@Override
	public void setChild(int childIndex, GeneTrait newChild)
	{
		if (childIndex == 0)
		{
			this.reference = (ReferenceTrait) newChild;
		}
		else if (childIndex == 1)
		{
			this.child = newChild;
		}
		else
		{
			throw new IllegalArgumentException("No such child index: " + childIndex);
		}
	}

	@Override
	public GeneTrait getChild(int childIndex)
	{
		if (childIndex == 0)
		{
			return reference;
		}
		else if (childIndex == 1)
		{
			return child;
		}
		else
		{
			throw new IllegalArgumentException("No such child index: " + childIndex);
		}
	}

	@Override
	public int getChildCount()
	{
		return 2;
	}

	@Override
	public int indexOf(GeneTrait child)
	{
		if (child.equals(reference))
		{
			return 0;
		}
		else if (child.equals(this.child))
		{
			return 1;
		}
		else
		{
			throw new IllegalArgumentException("No such child: " + child);
		}
	}

	@Override
	public String prototypeTag()
	{
		return "[" + childType + "]";
	}
}