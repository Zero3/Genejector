package genejector.realm.mortal;

import genejector.realm.RealmInstanceManager;
import genejector.realm.genes.InstructionGene;
import genejector.realm.genes.StatementListGene;
import genejector.realm.genetraits.GeneTrait;
import genejector.realm.genetraits.ParentTrait;
import genejector.realm.genetraits.ReferenceTrait;
import genejector.realm.genetraits.StatementListTrait;
import genejector.realm.gods.Abaddon;
import genejector.shared.Settings;
import genejector.shared.datastructures.RandomSet;
import genejector.shared.util.Tools;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Mortal implements MortalView, Serializable
{
	private static final long serialVersionUID = Settings.VERSION;
	private static int idCounter = 0;		// Global ID counter. Ensures that each mortal gets an unique ID

	private String id = generateID();
	private final StatementListGene rootGene;
	private final RandomSet<GeneTrait> genes = new RandomSet<GeneTrait>();
	private final Map<StatementListTrait, Integer> statementListCounts = new HashMap<StatementListTrait, Integer>();
	private final List<GeneTrait> uncheckedGenes = new LinkedList<GeneTrait>();	// Added to by methods that can potentially invalidate the tree (like separating variable declaration gene from variable usage gene). Removed from by the fixup method. TODO: Can we safely make this a set?

	public Mortal()
	{
		this.rootGene = new StatementListGene();
		this.rootGene.assignPrototypeId();
		this.statementListCounts.put(this.rootGene, 0);
	}

	@Override
	public Mortal copy()
	{
		Mortal mortalCopy = Tools.deepCopy(this);
		mortalCopy.instantiate();
		return mortalCopy;
	}

	private void instantiate()
	{
		id = generateID();
		rootGene.instantiate();	// Recursive throughout the tree
	}

	@Override
	public int geneCount()
	{
		return genes.size();
	}

	@Override
	public final StatementListGene getRootGene()
	{
		return rootGene;
	}

	private void updateMetadata(GeneTrait gene, boolean add)
	{
		// Add/remove if the gene is not the root (which is not treated as a "real" gene metadata-wise)
		if (!(gene == getRootGene()))
		{
			if (add)
			{
				int countBefore = genes.size();

				genes.add(gene);
				uncheckedGenes.add(gene);

				if (genes.size() != countBefore + 1)
				{
					throw new IllegalStateException("Metadata update internal failure. Expected " + (countBefore + 1) + " genes but found " + genes.size());
				}
			}
			else
			{
				int countBefore = genes.size();

				uncheckedGenes.remove(gene);
				genes.remove(gene);

				if (genes.size() != countBefore - 1)
				{
					throw new IllegalStateException("Metadata update internal failure. Expected " + (countBefore - 1) + " genes but found " + genes.size());
				}
			}
		}

		// Update muxCounts
		if (gene instanceof StatementListTrait)
		{
			if (add)
			{
				statementListCounts.put((StatementListTrait) gene, 0);
			}
			else
			{
				statementListCounts.remove((StatementListTrait) gene);
			}
		}

		if (gene.getParent() instanceof StatementListTrait)
		{
			StatementListTrait muxParent = (StatementListTrait) gene.getParent();

			Integer currentCount = statementListCounts.get(muxParent);

			if (add)
			{
				statementListCounts.put(muxParent, currentCount + 1);
			}
			else if (currentCount != null)
			{
				statementListCounts.put(muxParent, currentCount - 1);
			}
		}

		// Recurse
		if (gene instanceof ParentTrait)
		{
			ParentTrait parent = (ParentTrait) gene;

			for (int i = 0; i < parent.getChildCount(); i++)
			{
				if (!(parent instanceof ReferenceTrait) || !((ReferenceTrait) parent).isBackReference(i))	// Don't recurse back in tree (!)
				{
					GeneTrait child = parent.getChild(i);

					if (child != null)
					{
						updateMetadata(child, add);
					}
				}
			}
		}
	}

	@Override
	public GeneTrait getRandomGene()
	{
		if (genes.size() == 0)
		{
			throw new RuntimeException("Cannot get a random gene from an empty gene list");
		}

		return genes.getRandom(RealmInstanceManager.random);
	}

	@Override
	public Map<StatementListTrait, Integer> getStatementListCounts()
	{
		return Collections.unmodifiableMap(statementListCounts);
	}

	public static synchronized String generateID()
	{
		idCounter++;
		return "M" + idCounter;
	}

	@Override
	public String toString()
	{
		return "[" + id + "]";
	}

	@Override
	public String getCode()
	{
		if (!uncheckedGenes.isEmpty())
		{
			throw new IllegalStateException("Breeding operator forgot to repair mortal after modification. Unchecked internal tree structure might be invalid. Aborting. DEBUG: " + uncheckedGenes);
		}

		return getRootGene().getCode(new SourceCompositionTask());
	}

	// It is important that all gene parent/child relationships are handled by these functions to properly update the mortal metadata
	public void bindGene(ParentTrait parent, int childIndex, GeneTrait child)
	{
		unbindGene(parent, childIndex);

		parent.setChild(childIndex, child);

		if (!(parent instanceof ReferenceTrait) || !((ReferenceTrait) parent).isBackReference(childIndex))
		{
			// This is not a backreference. Change child's parent and update metadata
			child.setParent(parent);
			updateMetadata(child, true);
		}
	}

	public GeneTrait unbindGene(ParentTrait parent, int childIndex)
	{
		GeneTrait child = parent.getChild(childIndex);

		if (child != null)
		{
			// Uncheck things forward in the tree that might be affected by this subtree removal
			ParentTrait uncheckParent = parent;
			GeneTrait uncheckChild = child;
			if (uncheckParent instanceof InstructionGene)
			{
				// Unwrap
				uncheckChild = uncheckParent;
				uncheckParent = uncheckParent.getParent();
			}

			recursiveUncheck(uncheckParent, uncheckParent.indexOf(uncheckChild) + 1);

			// Update metadata for removed subtree
			if (!(parent instanceof ReferenceTrait) || !((ReferenceTrait) parent).isBackReference(childIndex))	// Don't recurse back in tree (!)
			{
				updateMetadata(child, false);
			}

			// Actually decouple parent and child
			parent.setChild(childIndex, null);
			child.setParent(null);
		}

		return child;
	}

	private void recursiveUncheck(ParentTrait parent, int childIndex)
	{
		for (int i = childIndex; i < parent.getChildCount(); i++)
		{
			if (!(parent instanceof ReferenceTrait) || !((ReferenceTrait) parent).isBackReference(i))
			{
				GeneTrait child = parent.getChild(i);

				if (child != null)
				{
					uncheckedGenes.add(child);

					if (child instanceof ParentTrait)
					{
						recursiveUncheck((ParentTrait) child, 0);
					}
				}
			}
		}
	}

	public GeneTrait removeGene(StatementListTrait parent, int childIndex)
	{
		GeneTrait removedGene = unbindGene(parent, childIndex);
		parent.removeStatementSlot(childIndex);
		return removedGene;
	}

	// TODO: Map old references to new, so if 2 instruction referencing a now-dead gene they will both point to the same new gene after this method has run
	public void repairGeneTree(Abaddon abaddon)
	{
		while (!uncheckedGenes.isEmpty())	// This is a while loop over isEmpty() as the list might be added to in the code below
		{
			GeneTrait currentGene = uncheckedGenes.remove(0);

			if (currentGene instanceof ParentTrait)
			{
				ParentTrait currentParent = (ParentTrait) currentGene;

				for (int i = 0; i < currentParent.getChildCount(); i++)
				{
					GeneTrait currentChild = currentParent.getChild(i);

					if (!genes.contains(currentChild))
					{
						abaddon.growChild(this, currentParent, i);
					}
				}
			}
		}
	}
}