package genejector.realm.breedingoperators;

import genejector.realm.genetraits.GeneTrait;
import genejector.realm.genetraits.ParentTrait;
import genejector.realm.gods.Abaddon;
import genejector.realm.mortal.Mortal;
import genejector.realm.mortal.MortalView;
import genejector.shared.datastructures.WeightedRandomCollection;
import genejector.shared.exceptions.ImpossibleBreedingException;

public class MutationBreedingOperator extends AbstractBreedingOperator
{
	@Override
	public Mortal breed(WeightedRandomCollection<MortalView> population, Abaddon abaddon)
	{
		// In order to give all breeding points (read: childs) approximately same
		// chance of being picked, we pick a any random gene and crawl up in tree
		// to its parent and mutate on the corresponding child index. If we
		// instead only picked among parents, we would have to weight the picking
		// ccording to the number of childs in each parent, complicating things
		// because of the needed bookkeeping inside the Mortal

		MortalView oldMortal = population.getRandom();

		if (oldMortal.geneCount() == 0)
		{
			throw new ImpossibleBreedingException("Cannot mutate on an empty mortal");
		}

		Mortal newMortal = oldMortal.copy();

		// Pick a gene and figure out where we are in tree
		GeneTrait bpChild = newMortal.getRandomGene();
		ParentTrait bpParent = bpChild.getParent();

		int bpIndex = bpParent.indexOf(bpChild);

		// Mutate!
		abaddon.growChild(newMortal, bpParent, bpIndex);
		newMortal.repairGeneTree(abaddon);

		return newMortal;
	}
}