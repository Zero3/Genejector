package genejector.realm.genes;

import genejector.realm.Type;
import genejector.realm.genetraits.GeneTrait;
import genejector.realm.genetraits.ParentTrait;
import genejector.realm.mortal.SourceCompositionTask;
import genejector.shared.util.Tools;

public class InstructionGene extends AbstractGene implements ParentTrait
{
	// Instance mutable
	private GeneTrait instruction = null;

	@Override
	public boolean prototypeEquals(GeneTrait obj)
	{
		return (obj instanceof InstructionGene);
	}

	@Override
	public int prototypeHashCode()
	{
		return Tools.generateHashCode(this);
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

		return instruction;
	}

	@Override
	public int indexOf(GeneTrait child)
	{
		if (child != this.instruction)
		{
			throw new IllegalArgumentException("No such child: " + child);
		}

		return 0;
	}

	@Override
	public void setChild(int childIndex, GeneTrait newChild)
	{
		if (childIndex != 0)
		{
			throw new IllegalArgumentException("No such child index: " + childIndex);
		}

		instruction = newChild;
	}

	@Override
	public Type getChildType(int childIndex)
	{
		if (childIndex != 0)
		{
			throw new IllegalArgumentException("No such child index: " + childIndex);
		}

		return Type.getType("<Instruction>");
	}

	@Override
	public String getCode(SourceCompositionTask task)
	{
		return instruction.getCode(task) + ";";
	}

	@Override
	public String prototypeTag()
	{
		return "[]";	// Nothing interesting to return, really
	}
}