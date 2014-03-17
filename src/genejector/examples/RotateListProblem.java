package genejector.examples;

import genejector.project.GenejectorProblem;
import java.util.ArrayList;
import java.util.List;

public class RotateListProblem implements GenejectorProblem
{
	public static final int REMOVE_AT = 0;	// Genejector has no built-in int constants (yet), so we need to make a zero available to the breeding process

	private final List<Integer> originalList;
	public ArrayList<Integer> list = null;

	public RotateListProblem(List<Integer> originalList)
	{
		this.originalList = originalList;
	}

	@Override
	public void resetState()
	{
		list = new ArrayList<Integer>(originalList);
	}
}