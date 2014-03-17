package genejector.examples;

import genejector.shared.Genejector;

// Goal: Perform 2 unlocking steps in CrazyKeyring, create a CrazySafe, unlock
// safe with username and password from keyring and set nuclear launch code in
// CrazyProblem with code obtained from unlocked safe.
public class CrazyExample
{
	public static void main(String[] args)
	{
		CrazyProblem problem = new CrazyProblem();

		// 1) Change default settings as needed
		Genejector.getSettings().setScoreLimit(2);
		Genejector.getSettings().setPopulationSize(500);
		// [ListingStart][L1]
		Genejector.getSettings().addClass("genejector.examples.CrazyKeyring", false);
		Genejector.getSettings().addClass("genejector.examples.CrazySafe", true);
		// [ListingEnd][L1]

		while (!Genejector.isSolutionFound())
		{
			// 2) Request a new candidate solution
			Genejector.geneject(problem.getClass());

			// 3) Test candidate solution
			long score = 0;
			Genejector.execute(problem);
			score += ((problem.getKeyring() != null && problem.getKeyring().getPassword() != null && problem.getKeyring().getPassword().equals("TopSecret")) ? 1 : 0);
			score += ((problem.getNuclearLaunchCode() != null && problem.getNuclearLaunchCode().equals(542679985)) ? 1 : 0);

			// 4) Send back score
			Genejector.submitScore(score);
		}

		System.out.println("Solution: " + Genejector.getSolutionSourceCode());
	}
}
