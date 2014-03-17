package genejector.shared.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class Tools
{
	public static void release(Object... releasables)
	{
		for (Object obj : releasables)
		{
			if (obj != null)
			{
				try
				{
					if (obj instanceof ServerSocket)
					{
						((ServerSocket)obj).close();
					}
					else if (obj instanceof Socket)
					{
						((Socket)obj).close();
					}
					else if (obj instanceof Process)
					{
						((Process)obj).destroy();
					}
					else if (obj instanceof Closeable)
					{
						((Closeable)obj).close();
					}
					else
					{
						throw new IllegalArgumentException("This method does not not how to release objects of type " + obj.getClass());
					}
				}
				catch (IOException ex)
				{
					// Ignore
				}
				catch (RuntimeException ex)
				{
					// Ignore
				}
			}
		}
	}

	public static String formatMilliSeconds(long milliseconds)
	{
		long seconds = milliseconds / 1000;

		// Split away minutes
		long minutes = seconds / 60;
		seconds -= minutes * 60;

		// Split away hours
		long hours = minutes / 60;
		minutes -= hours * 60;

		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}

	public static <M extends Comparable<M>, S> void prettyPrintSetMap(Map<M, Set<S>> setMap)
	{
		StringBuilder output = new StringBuilder();
		SortedMap<M, Set<S>> sortedSetMap = new TreeMap<M, Set<S>>(setMap);

		for (Map.Entry<M, Set<S>> set : sortedSetMap.entrySet())
		{
			output.append(" | ").append(set.getKey()).append(": ");

			boolean first = true;

			for (S entry : set.getValue())
			{
				if (first)
				{
					first = false;
				}
				else
				{
					output.append(", ");
				}

				output.append(entry);
			}

			output.append("\n");
		}

		System.out.print(output);
	}

	public static void prettyPrintStringList(Collection<String> strings)
	{
		StringBuilder output = new StringBuilder();

		for (String string : strings)
		{
			output.append(" | ").append(string).append("\n");
		}

		System.out.print(output.toString());
	}

	public static <T> void prettyPrintCounterMap(Map<T, Integer> counterMap)
	{
		PaddingStringBuilder output = new PaddingStringBuilder();

		Map<T, Integer> valueSortedMap = sortMapByValue(counterMap);
		PaddingStringBuilder.PaddingSection countSection = new PaddingStringBuilder.PaddingSection(false);

		for (Map.Entry<T, Integer> mapEntry : valueSortedMap.entrySet())
		{
			output.append(" | ").append(countSection, mapEntry.getValue() + "x ").append(mapEntry.getKey()).append("\n");
		}

		System.out.print(output.toString());
	}

	// Adapted from http://stackoverflow.com/a/2581754/1650137
	public static <K, V extends Comparable<? super V>> Map<K, V> sortMapByValue(Map<K, V> map)
	{
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());

		Collections.sort(list, new Comparator<Map.Entry<K, V>>()
		{
			@Override
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2)
			{
				return o2.getValue().compareTo(o1.getValue());
			}
		});

		Map<K, V> result = new LinkedHashMap<K, V>();

		for (Map.Entry<K, V> entry : list)
		{
			result.put(entry.getKey(), entry.getValue());
		}

		return result;
	}

	@SuppressWarnings("unchecked")	// We know that the output object will be of the same type as the input. It's a copy, after all. Compiler doesn't agree without this.
	public static <T> T deepCopy(T source)
	{
		ObjectOutputStream writePipe = null;
		ObjectInputStream readPipe = null;

		try
		{
			ByteArrayOutputStream storage = new ByteArrayOutputStream();

			writePipe = new ObjectOutputStream(storage);
			writePipe.writeObject(source);
			writePipe.flush();

			readPipe = new ObjectInputStream(new ByteArrayInputStream(storage.toByteArray()));
            return (T) readPipe.readObject();
		}
		catch (ClassNotFoundException ex)
		{
			throw new RuntimeException(ex);
		}
		catch (IOException ex)
		{
			throw new RuntimeException(ex);
		}
		finally
		{
			Tools.release(readPipe, writePipe);
		}
	}

	public static boolean pairEquals(Object... objects)
	{
		if (objects.length % 2 != 0)
		{
			throw new IllegalArgumentException("Method requies an even number of arguments to compare");
		}

		for (int i = 0; i < objects.length; i += 2)
		{
			if (objects[i] == null || objects[i+1] == null)
			{
				if ((objects[i] != null || objects[i+1] != null))
				{
					return false;
				}
			}
			else if (!objects[i].equals(objects[i+1]) || !objects[i+1].equals(objects[i]))
			{
				return false;
			}
		}

		return true;
	}

	public static int generateHashCode(Object containingObject, Object... objects)
	{
		int hash = containingObject.getClass().getCanonicalName().substring(0, 1).hashCode();
		int moreHash = containingObject.getClass().getCanonicalName().hashCode();

		for (Object object : objects)
		{
			hash += moreHash + (object != null ? object.hashCode() : 0);
		}

		return hash;
	}

	public static <M, S> long mapOfSetsSize(Map<M, Set<S>> mapOfSets)
	{
		long count = 0;

		for (Set set : mapOfSets.values())
		{
			count += set.size();
		}

		return count;
	}

	public static String implode(Iterable<?> list, String glue)
	{
		Iterator<?> iter = list.iterator();
		StringBuilder result = new StringBuilder();

		if (iter.hasNext())
		{
			result.append(iter.next().toString());

			while (iter.hasNext())
			{
				result.append(glue);
				result.append(iter.next().toString());
			}
		}

		return result.toString();
	}
}