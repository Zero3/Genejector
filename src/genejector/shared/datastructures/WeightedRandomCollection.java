package genejector.shared.datastructures;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

// Data structure capable of randomly selecting weighted entries.
// Adapted from: http://stackoverflow.com/a/6409791/1650137
public class WeightedRandomCollection<E>
{
	private final NavigableMap<Double, E> map = new TreeMap<Double, E>();
	private Random random;
	private double totalWeight = 0;

	public WeightedRandomCollection(long randomSeed)
	{
		random = new Random(randomSeed);
	}

	public final void add(E element, double weight)
	{
		if (weight <= 0)
		{
			throw new IllegalArgumentException("Weight must be >= 0");
		}

		totalWeight += weight;
		map.put(totalWeight, element);
	}

	public E getRandom()
	{
		double value = random.nextDouble() * totalWeight;
		return map.ceilingEntry(value).getValue();
	}

	public int size()
	{
		return map.size();
	}

	public void setRandomSeed(long randomSeed)
	{
		this.random = new Random(randomSeed);
	}

	public String toPrettyString()
	{
		StringBuilder output = new StringBuilder();
		Double lastWeight = 0D;
		DecimalFormat oneDotTwoFormat = new DecimalFormat("0.00");
		DecimalFormat twoFormat = new DecimalFormat("00");

		// Print out the good stuff
		for (Map.Entry<Double, E> mapEntry : map.entrySet())
		{
			if (output.length() > 0)
			{
				output.append("\n");
			}

			output.append(" | ").append(oneDotTwoFormat.format(mapEntry.getKey() - lastWeight)).append(" -> ").append(twoFormat.format(Math.round((mapEntry.getKey() - lastWeight) / totalWeight * 100))).append("%: ").append(mapEntry.getValue());
			lastWeight = mapEntry.getKey();
		}

		return output.toString();
	}
}