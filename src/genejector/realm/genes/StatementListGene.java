package genejector.realm.genes;

import genejector.realm.Type;
import genejector.realm.genetraits.GeneTrait;
import genejector.realm.genetraits.StatementListTrait;
import genejector.realm.mortal.SourceCompositionTask;
import genejector.shared.util.Tools;
import java.util.LinkedList;
import java.util.List;

public class StatementListGene extends AbstractGene implements StatementListTrait
{
	// Instance mutable
	private final List<GeneTrait> childs = new LinkedList<GeneTrait>();

	@Override
	public boolean prototypeEquals(GeneTrait obj)
	{
		return (obj instanceof StatementListGene);
	}

	@Override
	public int prototypeHashCode()
	{
		return Tools.generateHashCode(this);
	}

	@Override
	public String getCode(SourceCompositionTask task)
	{
		StringBuilder output = new StringBuilder();

		for (GeneTrait child : childs)
		{
			output.append(child.getCode(task));
		}

		return output.toString();
	}

	@Override
	public void setChild(int childIndex, GeneTrait child)
	{
		if (childIndex < 0 || childIndex > childs.size() - 1)
		{
			throw new IllegalArgumentException("No such child index: " + childIndex);
		}

		childs.set(childIndex, child);
	}

	@Override
	public void addStatementSlot(int position)
	{
		if (position < 0 || position > childs.size())	// Not -1, as we allow insertion at the end too
		{
			throw new IllegalArgumentException("Invalid position: " + position);
		}

		childs.add(position, null);
	}

	@Override
	public GeneTrait getChild(int childIndex)
	{
		if (childIndex < 0 || childIndex > childs.size() - 1)
		{
			throw new IllegalArgumentException("No such child index: " + childIndex);
		}

		return childs.get(childIndex);
	}

	@Override
	public void removeStatementSlot(int childIndex)
	{
		if (childIndex < 0 || childIndex > childs.size() - 1)
		{
			throw new IllegalArgumentException("No such statement slot: " + childIndex);
		}

		if (childs.get(childIndex) != null)
		{
			throw new IllegalArgumentException("Cannot remove a statement slot with a gene in it");
		}

		childs.remove(childIndex);
	}

	@Override
	public int getChildCount()
	{
		return childs.size();
	}

	@Override
	public int indexOf(GeneTrait child)
	{
		for (int i = 0; i < childs.size(); i++)
		{
			if (child.equals(childs.get(i)))
			{
				return i;
			}
		}

		throw new IllegalArgumentException("No such child: " + child);
	}

	@Override
	public Type getChildType(int childIndex)
	{
		if (childIndex < 0 || childIndex > childs.size() - 1)
		{
			throw new IllegalArgumentException("No such child index: " + childIndex);
		}

		return Type.getType("<Statement>");
	}

	@Override
	public String prototypeTag()
	{
		return "[]";
	}
}