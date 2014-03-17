package genejector.realm.genes;

import genejector.realm.Type;
import genejector.realm.genetraits.GeneTrait;
import genejector.realm.genetraits.ParentTrait;
import genejector.realm.mortal.SourceCompositionTask;
import genejector.shared.util.Tools;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewObjectGene extends AbstractGene implements GeneTrait, ParentTrait
{
	// Prototype immutable
	private final Type variableType;
	private final List<Type> childTypes;

	// Instance mutable
	private final Map<Integer, GeneTrait> childGenes = new HashMap<Integer, GeneTrait>(0);

	// Prototype constructor
	public NewObjectGene(Type variableType, List<Type> childTypes)
	{
		super();

		this.variableType = variableType;
		this.childTypes = Collections.unmodifiableList(new ArrayList<Type>(childTypes));
	}

	@Override
	public boolean prototypeEquals(GeneTrait obj)
	{
		if ((obj instanceof NewObjectGene))
		{
			NewObjectGene other = (NewObjectGene) obj;

			return Tools.pairEquals(
				variableType,	other.variableType,
				childTypes,		other.childTypes
			);
		}

		return false;
	}

	@Override
	public int prototypeHashCode()
	{
		return Tools.generateHashCode(this, variableType, childTypes);
	}

	@Override
	public String prototypeTag()
	{
		StringBuilder output = new StringBuilder();

		output.append("[").append(variableType).append("(");

		for (int i = 0; i < getChildCount(); i++)
		{
			if (i > 0)
			{
				output.append(", ");
			}

			output.append(getChildType(i));
		}

		output.append(")]");

		return output.toString();
	}

	@Override
	// [ListingStart][L5]
	public String getCode(SourceCompositionTask task)
	{
		// Concise: "new <variable type>(<code of child1>,<code of child2>,...)"
		StringBuilder string = new StringBuilder();

		string.append("new ").append(variableType).append("(");

		for (int i = 0; i < childGenes.size(); i++)
		{
			if (i > 0)
			{
				string.append(",");
			}

			string.append(childGenes.get(i).getCode(task));
		}

		string.append(")");

		return string.toString();
	}
	// [ListingEnd][L5]

	@Override
	public Type getChildType(int childIndex)
	{
		if (childIndex < childTypes.size())
		{
			return childTypes.get(childIndex);
		}

		throw new IllegalArgumentException("No such child index: " + childIndex);
	}

	@Override
	public void setChild(int childIndex, GeneTrait child)
	{
		if (childIndex < childTypes.size())
		{
			childGenes.put(childIndex, child);
		}
		else
		{
			throw new IllegalArgumentException("No such child index: " + childIndex);
		}
	}

	@Override
	public GeneTrait getChild(int childIndex)
	{
		if (childIndex < childTypes.size())
		{
			return childGenes.get(childIndex);
		}

		throw new IllegalArgumentException("No such child index: " + childIndex);
	}

	@Override
	public int getChildCount()
	{
		return childTypes.size();
	}

	@Override
	public int indexOf(GeneTrait child)
	{
		for (int i = 0; i < childGenes.size(); i++)
		{
			if (child.equals(childGenes.get(i)))
			{
				return i;
			}
		}

		throw new IllegalArgumentException("No such child: " + child);
	}
}