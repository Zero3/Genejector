package genejector.shared.datastructures;

import genejector.shared.Settings;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

// Data structire capable of randomy selecting non-weighted entries.
// This structure offers the very compelling running times.
// Adapted from: http://stackoverflow.com/a/5669034
// TODO: We could use a weighted version of this
public class RandomSet<E> implements Serializable
{
	private static final long serialVersionUID = Settings.VERSION;
	private final List<E> data = new ArrayList<E>();
	private final Map<E, Integer> index = new HashMap<E, Integer>();

	public RandomSet()
	{
		// Empty default constructor
	}

	public RandomSet(Collection<E> items)
	{
		addAll(items);
	}

	public final void addAll(Collection<E> items)
	{
		for (E item : items)
		{
			add(item);
		}
	}

	public final boolean add(E item)
	{
		if (index.containsKey(item))
		{
			return false;
		}

		index.put(item, data.size());
		data.add(item);

		return true;
	}

	public boolean contains(E item)
	{
		return index.containsKey(item);
	}

	// The trick: To prevent ArrayList from collapsing we move the last element into the hole just created
	public E removeAt(int id)
	{
		if (id >= data.size())
		{
			return null;
		}

		E res = data.get(id);
		index.remove(res);
		E last = data.remove(data.size() - 1);

		if (id < data.size())
		{
			// Skip filling the hole if last is removed
			index.put(last, id);
			data.set(id, last);
		}

		return res;
	}

	public boolean remove(E item)
	{
		Integer id = index.get(item);

		if (id == null)
		{
			return false;
		}

		removeAt(id);

		return true;
	}

	public E get(int i)
	{
		return data.get(i);
	}

	public E getRandom(Random rnd)
	{
		if (data.isEmpty())
		{
			throw new IllegalStateException("Cannot pick a random entry from an empty set");
		}

		return data.get(rnd.nextInt(data.size()));
	}

	public int size()
	{
		return data.size();
	}

	@Override
	public String toString()
	{
		boolean first = true;

		StringBuilder sb = new StringBuilder();
		sb.append("{");

		for (E element : data)
		{
			if (first)
			{
				first = false;
			}
			else
			{
				sb.append(", ");
			}

			sb.append(element);
		}

		sb.append("}");

		return sb.toString();
	}
}