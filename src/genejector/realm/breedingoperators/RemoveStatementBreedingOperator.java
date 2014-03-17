package genejector.realm.breedingoperators;

import genejector.realm.RealmInstanceManager;
import genejector.realm.genetraits.StatementListTrait;
import genejector.realm.gods.Abaddon;
import genejector.realm.mortal.Mortal;
import genejector.realm.mortal.MortalView;
import genejector.shared.Settings;
import genejector.shared.datastructures.WeightedRandomCollection;
import genejector.shared.exceptions.ImpossibleBreedingException;
import java.util.Map;

public class RemoveStatementBreedingOperator extends AbstractBreedingOperator
{
	@Override
	public Mortal breed(WeightedRandomCollection<MortalView> population, Abaddon abaddon)
	{
		MortalView oldMortal = population.getRandom();

		if (oldMortal.geneCount() == 0)
		{
			throw new ImpossibleBreedingException("Cannot remove a statement when mortal has none");
		}

		Mortal newMortal = oldMortal.copy();

		// Insert possible mutation points into a random collection weighted by the number of possible breeding points in each muxer
		WeightedRandomCollection<StatementListTrait> stateMentLists = new WeightedRandomCollection<StatementListTrait>(Settings.getSettings().getRandomSeed());
		for (Map.Entry<StatementListTrait, Integer> statementListEntry : newMortal.getStatementListCounts().entrySet())
		{
			if (statementListEntry.getValue() > 0)
			{
				stateMentLists.add(statementListEntry.getKey(), statementListEntry.getValue());
			}
		}

		// Now pick a point
		StatementListTrait parent = stateMentLists.getRandom();
		int childIndex = RealmInstanceManager.random.nextInt(parent.getChildCount());	// Note that nextInt() returns an integer strictly below the argument

		// Breed!
		newMortal.removeGene(parent, childIndex);
		newMortal.repairGeneTree(abaddon);

		return newMortal;
	}
}