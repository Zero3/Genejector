package genejector.realm.gods;

import genejector.realm.RealmInstanceManager;
import genejector.realm.Type;
import genejector.realm.genes.MethodGene;
import genejector.realm.genes.UseFieldGene;
import genejector.realm.genetraits.GeneTrait;
import genejector.realm.genetraits.ParentTrait;
import genejector.realm.genetraits.ReferenceTrait;
import genejector.realm.mortal.Mortal;
import genejector.shared.datastructures.WeightedRandomCollection;
import genejector.shared.exceptions.GenePoolExhaustedBreedException;
import genejector.shared.util.Tools;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

// "Abaddon! Lord of the Everlasting Depths, Keeper of Secrets, open mine eyes
// and bestow upon me the knowledge of the Abyss that I might smite mine enemies
// and send them to the watery depths!"
// — Scriptures of Abaddon, 1 BE
public class Abaddon
{
	private static final int GENE_REUSE_MAX_TIMES = 3;				// TODO: Consider making this a setting. Is it relevant as a setting when we fix the excessive method chaining?
	private static final double NON_METHOD_WEIGHT_FACTOR = 0.25;	// TODO: Consider making this a setting. Used to fix excessive method chaining by weighted all other genes this much relative to the number of available genes of the needed type

	private final Kormir kormir;
	private final Map<Type, Integer> poolExhaustionMap = new TreeMap<Type, Integer>();
	private int poolExhaustionCount = 0;

	protected Abaddon(Kormir kormir)
	{
		this.kormir = kormir;
	}

	public GeneTrait growChild(Mortal mortal, ParentTrait parent, int childIndex) throws GenePoolExhaustedBreedException
	{
		Map<Type, Set<GeneTrait>> genePool = kormir.copyGenePool();

		// Add all objects created by instructions prior to the instruction this gene belongs to/is
		kormir.reflectMortal(genePool, parent, (childIndex - 1));

		GeneTrait newGene = growChild(mortal, parent, childIndex, genePool, new HashMap<Type, Map<GeneTrait, Integer>>());

		return newGene;
	}

	private GeneTrait growChild(Mortal mortal, ParentTrait parent, int childIndex, Map<Type, Set<GeneTrait>> genePool, Map<Type, Map<GeneTrait, Integer>> usedPool) throws GenePoolExhaustedBreedException
	{
		Type growType = parent.getChildType(childIndex);

		Set<GeneTrait> geneSet = genePool.get(growType);

		if (geneSet == null || geneSet.isEmpty())
		{
			Integer currentCount = poolExhaustionMap.get(growType);
			currentCount = (currentCount != null ? currentCount : 0);
			poolExhaustionMap.put(growType, currentCount + 1);
			poolExhaustionCount++;

			throw new GenePoolExhaustedBreedException(growType);
		}

		// Now pick a random one
		WeightedRandomCollection<GeneTrait> randomizedGeneSet = new WeightedRandomCollection<GeneTrait>(RealmInstanceManager.random.nextLong());

		for (GeneTrait gene : geneSet)
		{
			// TODO: Support for custom weighting of genes for use when picking. NewVariableGene drowns in all the MethodGenes without the workaround below.
			// TODO: Turn this into a setting of some kind. Atm. we just weigh everything but methods way higher to avoid insanely long chains
			randomizedGeneSet.add(gene, ((gene instanceof MethodGene) ? 1 : (geneSet.size() * NON_METHOD_WEIGHT_FACTOR)));
		}

		GeneTrait pickedGene = randomizedGeneSet.getRandom();

		// Link or copy old child
		GeneTrait newChild;

		if (parent instanceof ReferenceTrait && ((ReferenceTrait) parent).isBackReference(childIndex))
		{
			// Don't copy, just link
			newChild = pickedGene;
		}
		else
		{
			// Create a copy
			newChild = Tools.deepCopy(pickedGene);
			newChild.instantiate();
		}

		// Bind new gene
		mortal.bindGene(parent, childIndex, newChild);

		// To avoid infinite recursion we only allow each gene to be selected a specific number of times per growing session
		Map<GeneTrait, Integer> countMap = usedPool.get(growType);

		if (countMap == null)
		{
			countMap = new HashMap<GeneTrait, Integer>();
			usedPool.put(growType, countMap);
		}

		Integer count = countMap.get(pickedGene);

		if (count == null)
		{
			count = 0;
			countMap.put(pickedGene, count);
		}

		if (count.equals(GENE_REUSE_MAX_TIMES))
		{
			genePool.get(growType).remove(pickedGene);
		}
		else if (!(pickedGene instanceof UseFieldGene) || ((UseFieldGene) pickedGene).getChildCount() > 0 || !((UseFieldGene) pickedGene).getName().equals("problem"))
		{
			// If this is not the problem object
			count++;
			countMap.put(pickedGene, count);
		}

		// TODO: How will this work when we implement while loops and other fancy stuff?
		if (!(parent instanceof ReferenceTrait) || !(((ReferenceTrait) parent).isBackReference(childIndex)))
		{
			if (newChild instanceof ParentTrait)
			{
				// Grow childs too
				ParentTrait pNewGene = (ParentTrait) newChild;

				for (int i = 0; i < pNewGene.getChildCount(); i++)
				{
					growChild(mortal, pNewGene, i, genePool, usedPool);
				}
			}
		}

		return newChild;
	}

	public void reportPoolExhaustions()
	{
		if (!poolExhaustionMap.isEmpty())
		{
			System.out.println("Abaddon: Note: Breeding failed and result was thrown away because of gene pool exhaustion as follows:");
			Tools.prettyPrintCounterMap(poolExhaustionMap);
		}
	}

	public int getPoolExhaustionCount()
	{
		return poolExhaustionCount;
	}
}