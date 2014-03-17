package genejector.realm.mortal;

import genejector.realm.genes.NewVariableGene;
import java.util.HashMap;
import java.util.Map;

public class SourceCompositionTask
{
	private final Map<NewVariableGene, String> variableMap = new HashMap<NewVariableGene, String>();
	private int variableNameCounter = 0;	// ID counter. Ensures that each variable gets a unique name

	public String getVariableName(NewVariableGene gene)
	{
		String variableName = variableMap.get(gene);

		if (variableName == null)
		{
			variableNameCounter++;
			variableName = "var" + variableNameCounter;
			variableMap.put(gene, variableName);
		}

		return variableName;
	}
}
