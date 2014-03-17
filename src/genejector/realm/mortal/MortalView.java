package genejector.realm.mortal;

import genejector.realm.genes.StatementListGene;
import genejector.realm.genetraits.GeneTrait;
import genejector.realm.genetraits.StatementListTrait;
import java.util.Map;

// TODO: Create GeneView too
public interface MortalView
{
	StatementListGene getRootGene();
	int geneCount();
	GeneTrait getRandomGene();
	Map<StatementListTrait, Integer> getStatementListCounts();
	String getCode();
	Mortal copy();
}