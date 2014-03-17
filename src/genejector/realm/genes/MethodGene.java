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

public class MethodGene extends AbstractGene implements ParentTrait
{
	// Prototype immutable
	private final Type returnType;
	private final Type objectType;
	private final String methodName;
	private final List<Type> childTypes;

	// Instance mutable
	private GeneTrait object = null;
	private final Map<Integer, GeneTrait> childGenes = new HashMap<Integer, GeneTrait>(0);

	// Prototype constructor
	public MethodGene(Type returnType, Type objectType, String methodName, List<Type> childTypes)
	{
		super();

		this.returnType = returnType;
		this.objectType = objectType;
		this.methodName = methodName;
		this.childTypes = Collections.unmodifiableList(new ArrayList<Type>(childTypes));
	}

	@Override
	public boolean prototypeEquals(GeneTrait obj)
	{
		if (obj instanceof MethodGene)
		{
			MethodGene other = (MethodGene) obj;

			return Tools.pairEquals(
				returnType,		other.returnType,
				objectType,		other.objectType,
				methodName,		other.methodName,
				childTypes,		other.childTypes
			);
		}

		return false;
	}

	@Override
	public int prototypeHashCode()
	{
		return Tools.generateHashCode(this, returnType, objectType, methodName, childTypes);
	}

	@Override
	public Type getChildType(int childIndex)
	{
		if (objectType == null)
		{
			if (childIndex < childTypes.size())
			{
				return childTypes.get(childIndex);
			}
		}
		else
		{
			if (childIndex == 0)
			{
				return objectType;
			}
			else if (childIndex - 1 < childTypes.size())
			{
				return childTypes.get(childIndex - 1);
			}
		}

		throw new IllegalArgumentException("No such child index: " + childIndex);
	}

	@Override
	public void setChild(int childIndex, GeneTrait child)
	{
		if (objectType == null)
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
		else
		{
			if (childIndex == 0)
			{
				object = child;
			}
			else if (childIndex - 1 < childTypes.size())
			{
				childGenes.put(childIndex - 1, child);
			}
			else
			{
				throw new IllegalArgumentException("No such child index: " + childIndex);
			}
		}
	}

	@Override
	public String getCode(SourceCompositionTask task)
	{
		StringBuilder string = new StringBuilder();

		if (objectType != null)
		{
			string.append(object.getCode(task)).append(".");
		}

		string.append(methodName).append("(");

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

	@Override
	public GeneTrait getChild(int childIndex)
	{
		if (objectType == null)
		{
			if (childIndex < childTypes.size())
			{
				return childGenes.get(childIndex);
			}

			throw new IllegalArgumentException("No such child index: " + childIndex);
		}
		else
		{
			if (childIndex == 0)
			{
				return object;
			}
			else if (childIndex - 1 < childTypes.size())
			{
				return childGenes.get(childIndex - 1);
			}

			throw new IllegalArgumentException("No such child index: " + childIndex);
		}
	}

	@Override
	public int getChildCount()
	{
		return (objectType == null ? 0 : 1) + childTypes.size();
	}

	@Override
	public int indexOf(GeneTrait child)
	{
		if (objectType != null && child.equals(object))
		{
			return 0;
		}

		for (int i = 0; i < childGenes.size(); i++)
		{
			if (child.equals(childGenes.get(i)))
			{
				return i + (objectType != null ? 1 : 0);
			}
		}

		throw new IllegalArgumentException("No such child: " + child);
	}

	public String getMethodName()
	{
		return methodName;
	}

	@Override
	public String prototypeTag()
	{
		StringBuilder output = new StringBuilder();

		output.append("[").append(objectType != null ? objectType + "#": "").append(methodName).append("(");

		int startIndex = (objectType != null ? 1 : 0);

		for (int i = startIndex; i < getChildCount(); i++)
		{
			if (i > startIndex)
			{
				output.append(", ");
			}

			output.append(getChildType(i));
		}

		output.append(")]");

		return output.toString();
	}
}