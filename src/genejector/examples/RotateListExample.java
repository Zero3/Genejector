package genejector.examples;

import genejector.shared.Genejector;
import java.util.Arrays;

// Goal: Move the first integer in a list to the back of the list
public class RotateListExample
{
	public static void main(String[] args)
	{
		RotateListProblem problem1 = new RotateListProblem(Arrays.asList(1, 2, 4, 8, 16, 32));
		RotateListProblem problem2 = new RotateListProblem(Arrays.asList(0, 1, 1, 2, 3, 5, 8, 13, 21, 34));

		// 1) Change default settings as needed
		Genejector.getSettings().setScoreLimit(4);
		Genejector.getSettings().setPrintAllIndividuals(true);
		Genejector.getSettings().addClass("int", false);
		Genejector.getSettings().addClass("java.util.ArrayList", false);

		while (!Genejector.isSolutionFound())
		{
			// 2) Request a new candidate solution
			Genejector.geneject(RotateListProblem.class);

			// 3) Test candidate solution
			long score = 0;

			// Problem 1
			Genejector.execute(problem1);
			if (problem1.list != null && problem1.list.equals(Arrays.asList(2, 4, 8, 16, 32, 1)))
			{
				score += 2;		// Solution found. 2 points
			}
			else if (problem1.list != null && (problem1.list.equals(Arrays.asList(2, 4, 8, 16, 32)) || problem1.list.equals(Arrays.asList(1, 2, 4, 8, 16, 32, 1))))
			{
				score += 1;		// First element removed or last element inserted. 1 point
			}

			// Problem 2
			Genejector.execute(problem2);
			if (problem2.list != null && problem2.list.equals(Arrays.asList(1, 1, 2, 3, 5, 8, 13, 21, 34, 0)))
			{
				score += 2;		// Solution found. 2 points
			}
			else if (problem2.list != null && (problem2.list.equals(Arrays.asList(1, 1, 2, 3, 5, 8, 13, 21, 34)) || problem2.list.equals(Arrays.asList(0, 1, 1, 2, 3, 5, 8, 13, 21, 34, 0))))
			{
				score += 1;		// First element removed or last element inserted. 1 point
			}

			// 4) Send back score
			Genejector.submitScore(score);
		}

		System.out.println("Solution: " + Genejector.getSolutionSourceCode());
	}
}