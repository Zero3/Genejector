package genejector.realm.gods;

import genejector.realm.Solution;
import genejector.realm.breedingoperators.AbstractBreedingOperator;
import genejector.realm.mortal.Mortal;
import genejector.realm.mortal.MortalView;
import genejector.shared.BreedingOperatorSetup;
import genejector.shared.Settings;
import genejector.shared.datastructures.WeightedRandomCollection;
import genejector.shared.exceptions.BreedingException;
import genejector.shared.exceptions.GenePoolExhaustedBreedException;
import genejector.shared.exceptions.GenejectedExecutionException;
import genejector.shared.exceptions.RealmException;
import genejector.shared.messages.MortalMessage;
import genejector.shared.util.Tools;

// "For I am your goddess, and I will give blessings to all who follow these
// teachings."
// — Scriptures of Dwayna, 115 BE
public class Dwayna
{
	private final Melandru melandru = new Melandru();
	private final Kormir kormir = new Kormir();
	private final Abaddon abaddon = new Abaddon(kormir);
	private final Balthazar balthazar = new Balthazar();

	private Long startTime = null;
	private boolean finished = false;
	private Integer generation = null;
	private Long lastAverageScore = null;
	private Solution bestMortal = null;
	private int breedCounter = 0;
	private int mortalCounter = 0;

	public synchronized Solution rise()
	{
		if (startTime != null)
		{
			throw new RealmException("This object is single use only");
		}

		// Print settings
		System.out.println("Dwayna: Starting run with the following settings: ");
		System.out.println(Settings.getSettings().toPrettyString());

		startTime = System.currentTimeMillis();

		try
		{
			// Setup breeding operators
			WeightedRandomCollection<AbstractBreedingOperator> breedingOperators = new WeightedRandomCollection<AbstractBreedingOperator>(Settings.getSettings().getRandomSeed());

			for (BreedingOperatorSetup opSetup : Settings.getSettings().getBreedingOperators())
			{
				breedingOperators.add(opSetup.breedingOperator, opSetup.weight);
			}

			// Setup the initial gene pool
			kormir.reflectProblem();

			// Setup population
			WeightedRandomCollection<MortalView> mortals = new WeightedRandomCollection<MortalView>(Settings.getSettings().getRandomSeed());
			MortalView initialMortal = new Mortal(); // Create base mortal as starting point of run
			mortals.add(initialMortal, Double.MIN_VALUE);

			System.out.println("Dwayna: Created initial population of mortal " + initialMortal + " with root gene " + initialMortal.getRootGene() + " (" + initialMortal.getRootGene().getClass().getSimpleName() + ").");
			System.out.println("Dwayna: Evolutionary breeding commenced!");

			if (Settings.getSettings().getStatusInterval() != null)
			{
				Lyssa.dispatch(new Lyssa(this));
			}

			// Go! Go! Go!
			// TODO: Implement stop condition(s). Suggestion: When score doesn't improve for x generations OR it reaches 0.
			for (generation = 1; (Settings.getSettings().getGenerationLimit() == null || generation <= Settings.getSettings().getGenerationLimit()); generation++)
			{
				WeightedRandomCollection<MortalView> newMortals = new WeightedRandomCollection<MortalView>(Settings.getSettings().getRandomSeed());
				long scoreSum = 0;

				//System.out.println("Dwayna: Breeding generation " + generation);

				while (newMortals.size() < Settings.getSettings().getPopulationSize())
				{
					// Add another mortal to the new population
					AbstractBreedingOperator oper = breedingOperators.getRandom();
					StringBuilder output = new StringBuilder();

					try
					{
						breedCounter++;
						Mortal newMortal = oper.breed(mortals, abaddon);

						// TODO: Streamline this. Might as well score mortals while breeding the rest of the generation?
						String mortalSource = newMortal.getCode();

						if (Settings.getSettings().getPrintAllIndividuals())
						{
							output.append("Dwayna: ").append(newMortal).append(": ").append(mortalSource);
						}

						// TODO: Use a compiler pool with each their own thread. This is currently one of the main bottlenecks
						MortalMessage compiledMortal = balthazar.compile(mortalSource);
						mortalCounter++;

						long score = melandru.bindMortal(compiledMortal);

						if (Settings.getSettings().getPrintAllIndividuals())
						{
							output.append(" [Score: ").append(score).append("]");
							System.out.println(output);
						}

						// Check if this mortal is better than all we've seen previously
						if (score > 0 && (bestMortal == null || score > bestMortal.getScore()))
						{
							bestMortal = new Solution(mortalSource, compiledMortal.getMortal(), score, newMortal.toString());

							// Check if score limit has been reached
							if (Settings.getSettings().getScoreLimit() != null && bestMortal.getScore() >= Settings.getSettings().getScoreLimit())
							{
								System.out.println("Dwayna: Solution " + bestMortal.getId() + " with a score of " + bestMortal.getScore() + " found. Bred " + mortalCounter + " mortals in "+ Tools.formatMilliSeconds(System.currentTimeMillis() - startTime) + ". Termination reason: Score limit reached.");
								return bestMortal;
							}
						}

						if (score > 0)
						{
							scoreSum += score;
							newMortals.add(newMortal, score);
						}
						else
						{
							// We keep a mortal even if it is useless at the moment.
							// If there are no usable mortals in the generation at all, something broken is better than nothing at all
							// If there are usable mortals in the generation this one will be killed by selection pressure soon enough
							newMortals.add(newMortal, 0.1);
						}
					}
					catch (GenePoolExhaustedBreedException ex)
					{
						// Ignore. Will be consolidated by Abaddon and reported at end of run.
					}
					catch (BreedingException ex)
					{
						// Ignore. Mostly caused by not being able to mutate on empty individuals during the first couple of generations. TODO: Find a way to avoid these failures
						//System.out.println("Dwayna: " + oper + " failed to breed with a " + ex.getClass().getSimpleName() + ". Skipping.");
					}
					catch (GenejectedExecutionException ex)
					{
						// TODO: Logging/stats of how often this happens
						if (Settings.getSettings().getPrintAllIndividuals())
						{
							System.out.println(output + " [Execution failure]");
						}
					}
				}

				// TODO: Stop if > x% mortality rate. Something must be wrong then.

				lastAverageScore = scoreSum / Settings.getSettings().getPopulationSize();

				// Make new population current
				mortals = newMortals;
			}

			if (bestMortal == null)
			{
				System.out.print("Dwayna: No solution found.");
			}
			else
			{
				System.out.print("Dwayna: Solution " + bestMortal.getId() + " with a score of " + bestMortal.getScore() + " found.");
			}

			System.out.println(" Bred " + mortalCounter + " mortals in " + Tools.formatMilliSeconds(System.currentTimeMillis() - startTime) + ". Termination reason: Generation limit reached.");

			return bestMortal;
		}
		finally
		{
			finished = true;
			//abaddon.reportPoolExhaustions();	// Enable this to report gene pool exhaustions after the run
		}
	}

	public int getGeneration()
	{
		return generation;
	}

	public boolean isFinished()
	{
		return finished;
	}

	public Solution getBestMortal()
	{
		return bestMortal;
	}

	public Long getLastAverageScore()
	{
		return lastAverageScore;
	}

	public int getBreedCounter()
	{
		return breedCounter;
	}

	public int getMortalCounter()
	{
		return mortalCounter;
	}

	public Long getStartTime()
	{
		return startTime;
	}
}