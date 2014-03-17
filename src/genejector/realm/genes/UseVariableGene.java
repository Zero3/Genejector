package genejector.realm.genes;

import genejector.realm.Type;
import genejector.realm.genetraits.GeneTrait;
import genejector.realm.genetraits.ParentTrait;
import genejector.realm.genetraits.ReferenceTrait;
import genejector.realm.mortal.SourceCompositionTask;
import genejector.shared.util.Tools;

public class UseVariableGene extends AbstractGene implements ReferenceTrait, ParentTrait
{
	// Prototype immutable
	private final Type referenceType;
	private final Type variableType;

	// Instance mutable
	private NewVariableGene reference = null;

	// Prototype constructor
	public UseVariableGene(Type variableType, Type referenceType)
	{
		super();

		this.referenceType = referenceType;
		this.variableType = variableType;
	}

	@Override
	public boolean prototypeEquals(GeneTrait obj)
	{
		if (obj instanceof UseVariableGene)
		{
			UseVariableGene other = (UseVariableGene) obj;

			// TODO: Make all equal variable checks work like this
			return Tools.pairEquals(
				referenceType,	other.referenceType,
				variableType,	other.variableType
			);
		}

		return false;
	}

	@Override
	public int prototypeHashCode()
	{
		// TODO: Generate all hashcodes like this
		return Tools.generateHashCode(this, referenceType, variableType);
	}

	@Override
	public String getCode(SourceCompositionTask task)
	{
		if (reference == null)
		{
			throw new IllegalStateException("Reference not set for " + this);
		}

		return task.getVariableName(reference);
	}

	public Type getVariableType()
	{
		return variableType;
	}

	@Override
	public int getChildCount()
	{
		return 1;
	}

	@Override
	public GeneTrait getChild(int childIndex)
	{
		if (childIndex != 0)
		{
			throw new IllegalArgumentException("No such child index: " + childIndex);
		}

		return reference;
	}

	@Override
	public int indexOf(GeneTrait child)
	{
		if (reference != child)
		{
			throw new IllegalArgumentException("No such child: " + child);
		}

		return 0;
	}

	@Override
	public void setChild(int childIndex, GeneTrait child)
	{
		if (childIndex != 0)
		{
			throw new IllegalArgumentException("No such child index: " + childIndex);
		}

		reference = (NewVariableGene) child;
	}

	@Override
	public Type getChildType(int childIndex)
	{
		if (childIndex != 0)
		{
			throw new IllegalArgumentException("No such child index: " + childIndex);
		}

		return referenceType;
	}

	@Override
	public boolean isBackReference(int childIndex)
	{
		if (childIndex != 0)
		{
			throw new IllegalArgumentException("No such child index: " + childIndex);
		}

		return true;
	}

	@Override
	public String prototypeTag()
	{
		return "[" + referenceType + "]";
	}
}