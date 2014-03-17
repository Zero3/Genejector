package genejector.realm.breedingoperators;

import genejector.realm.RealmInstanceManager;
import genejector.realm.genetraits.StatementListTrait;
import genejector.realm.gods.Abaddon;
import genejector.realm.mortal.Mortal;
import genejector.realm.mortal.MortalView;
import genejector.shared.Settings;
import genejector.shared.datastructures.WeightedRandomCollection;
import java.util.Map;

public class AddStatementBreedingOperator extends AbstractBreedingOperator
{
	@Override
	public Mortal breed(WeightedRandomCollection<MortalView> population, Abaddon abaddon)
	{
		MortalView oldMortal = population.getRandom();
		Mortal newMortal = oldMortal.copy();

		// Insert possible mutation points into a random collection weighted by the number of possible breeding points in each muxer
		WeightedRandomCollection<StatementListTrait> statementLists = new WeightedRandomCollection<StatementListTrait>(Settings.getSettings().getRandomSeed());
		for (Map.Entry<StatementListTrait, Integer> statementListEntry : newMortal.getStatementListCounts().entrySet())
		{
			statementLists.add(statementListEntry.getKey(), (statementListEntry.getValue() + 1));	// + 1 to weight as we can always add an instruction at the end too
		}

		// Now pick a point
		StatementListTrait parent = statementLists.getRandom();
		int childIndex = RealmInstanceManager.random.nextInt(parent.getChildCount() + 1);	// + 1 as above. Note that nextInt() returns an integer strictly below the argument

		// Breed!
		parent.addStatementSlot(childIndex);
		abaddon.growChild(newMortal, parent, childIndex);
		newMortal.repairGeneTree(abaddon);

		return newMortal;
	}
}