package genejector.realm.genes;

import genejector.realm.Type;
import genejector.realm.genetraits.GeneTrait;
import genejector.realm.genetraits.ParentTrait;
import genejector.realm.genetraits.ReferenceTrait;
import genejector.realm.mortal.SourceCompositionTask;
import genejector.shared.util.Tools;

public class UseFieldGene extends AbstractGene implements ParentTrait, ReferenceTrait
{
	// Prototype immutable
	private final Type objectType;		// May be null if only name is to be used
	private final Type fieldType;
	private final String fieldName;		// Static fields will contain the classname inside this too

	// Instance mutable
	private GeneTrait object = null;

	// Prototype constructor
	public UseFieldGene(Type objectType, Type fieldType, String fieldName)
	{
		super();

		this.objectType = objectType;
		this.fieldType = fieldType;
		this.fieldName = fieldName;
	}

	@Override
	public boolean prototypeEquals(GeneTrait obj)
	{
		if (obj instanceof UseFieldGene)
		{
			UseFieldGene other = (UseFieldGene) obj;

			return Tools.pairEquals(
				objectType,	other.objectType,
				fieldType,	other.fieldType,
				fieldName,	other.fieldName
			);
		}

		return false;
	}

	@Override
	public int prototypeHashCode()
	{
		return Tools.generateHashCode(this, objectType, fieldType, fieldName);
	}

	@Override
	public String getCode(SourceCompositionTask task)
	{
		if (objectType != null && object == null)
		{
			throw new IllegalStateException("Object not set for " + this);
		}

		return (objectType != null ? (object.getCode(task) + ".") : "") + fieldName;
	}

	public Type getFieldType()
	{
		return fieldType;
	}

	public String getName()
	{
		return fieldName;
	}

	@Override
	public int getChildCount()
	{
		return (objectType != null ? 1 : 0);
	}

	@Override
	public GeneTrait getChild(int childIndex)
	{
		if (childIndex != 0 || objectType == null)
		{
			throw new IllegalArgumentException("No such child index: " + childIndex);
		}

		return object;
	}

	@Override
	public int indexOf(GeneTrait child)
	{
		if (object != child)
		{
			throw new IllegalArgumentException("No such child: " + child);
		}

		return 0;
	}

	@Override
	public void setChild(int childIndex, GeneTrait child)
	{
		if (childIndex != 0 || objectType == null)
		{
			throw new IllegalArgumentException("No such child index: " + childIndex);
		}

		object = child;
	}

	@Override
	public Type getChildType(int childIndex)
	{
		if (childIndex != 0 || objectType == null)
		{
			throw new IllegalArgumentException("No such child index: " + childIndex);
		}

		return objectType;
	}

	@Override
	public boolean isBackReference(int childIndex)
	{
		if (childIndex != 0 || objectType == null)
		{
			throw new IllegalArgumentException("No such child index: " + childIndex);
		}

		return false;
	}

	@Override
	public String prototypeTag()
	{
		if (getChildCount() == 0)
		{
			return "[" + fieldName + "]";
		}
		else
		{
			return "[" + objectType + "#" + fieldName + "]";
		}
	}
}