package genejector.realm.genes;

import genejector.realm.Type;
import genejector.realm.genetraits.GeneTrait;
import genejector.realm.genetraits.ParentTrait;
import genejector.realm.mortal.SourceCompositionTask;
import genejector.shared.util.Tools;

public class NewVariableGene extends AbstractGene implements ParentTrait
{
	// Prototype immutable
	private final Type variableType;

	// Instance mutable
	private GeneTrait child = null;

	// Prototype constructor
	public NewVariableGene(Type variableType)
	{
		super();

		this.variableType = variableType;
	}

	@Override
	public boolean prototypeEquals(GeneTrait obj)
	{
		if ((obj instanceof NewVariableGene))
		{
			NewVariableGene other = (NewVariableGene) obj;

			return Tools.pairEquals(
				variableType,	other.variableType
			);
		}

		return false;
	}

	@Override
	public int prototypeHashCode()
	{
		return Tools.generateHashCode(this, variableType);
	}

	@Override
	public String getCode(SourceCompositionTask task)
	{
		return (variableType + " " + task.getVariableName(this) + "=" + child.getCode(task));
	}

	@Override
	public Type getChildType(int childIndex)
	{
		if (childIndex != 0)
		{
			throw new IllegalArgumentException("No such child index: " + childIndex);
		}

		return variableType;
	}

	@Override
	public void setChild(int childIndex, GeneTrait child)
	{
		if (childIndex != 0)
		{
			throw new IllegalArgumentException("No such child index: " + childIndex);
		}

		this.child = child;
	}

	@Override
	public GeneTrait getChild(int childIndex)
	{
		if (childIndex != 0)
		{
			throw new IllegalArgumentException("No such child index: " + childIndex);
		}

		return child;
	}

	@Override
	public int getChildCount()
	{
		return 1;
	}

	@Override
	public int indexOf(GeneTrait child)
	{
		if (child.equals(this.child))
		{
			return 0;
		}

		throw new IllegalArgumentException("No such child: " + child);
	}

	public Type getVariableType()
	{
		return variableType;
	}

	@Override
	public String prototypeTag()
	{
		return "[" + variableType + "]";
	}
}