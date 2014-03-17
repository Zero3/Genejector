package genejector.examples;

import genejector.shared.Genejector;

// Goal: Copy the value of one variable into another
// [ListingStart][L7]
public class TripleCopyExample
{
	public static void main(String[] args)
	{
		TripleCopyProblem problem = new TripleCopyProblem();

		// 1) Change default settings as needed
		Genejector.getSettings().setScoreLimit(6);
		Genejector.getSettings().setPrintAllIndividuals(true);
		Genejector.getSettings().setAddDefaultClasses(false);
		Genejector.getSettings().addClass("java.lang.Integer", false);

		while (!Genejector.isSolutionFound())
		{
			// 2) Request a new candidate solution
			Genejector.geneject(problem.getClass());

			// 3) Test candidate solution
			long score = 0;
			problem.setInputs(4, 5, 6);
			Genejector.execute(problem);
			score += ((problem.getOutput1() != null && problem.getOutput1().equals(4)) ? 1 : 0);
			score += ((problem.getOutput2() != null && problem.getOutput2().equals(5)) ? 1 : 0);
			score += ((problem.getOutput3() != null && problem.getOutput3().equals(6)) ? 1 : 0);

			problem.setInputs(-1337, 42, 27);
			Genejector.execute(problem);
			score += ((problem.getOutput1() != null && problem.getOutput1().equals(-1337)) ? 1 : 0);
			score += ((problem.getOutput2() != null && problem.getOutput2().equals(42)) ? 1 : 0);
			score += ((problem.getOutput3() != null && problem.getOutput3().equals(27)) ? 1 : 0);

			// 4) Send back score
			Genejector.submitScore(score);
		}

		System.out.println("Solution: " + Genejector.getSolutionSourceCode());
	}
}
// [ListingEnd][L7]