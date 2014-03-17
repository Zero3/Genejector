package genejector.realm.gods;

import genejector.shared.Settings;

// "True beauty is measured not by appearance but by actions and deeds. Many
// have eyes, but few have seen. Of all here, you saw the beauty behind the
// illusion. And you alone shall be blessed with My gifts."
// — Scriptures of Lyssa, 45 BE
public class Lyssa implements Runnable
{
	private final Dwayna dwayna;

	protected Lyssa(Dwayna dwayna)
	{
		this.dwayna = dwayna;
	}

	public static void dispatch(Lyssa reporter)
	{
		Thread reporterThread = new Thread(reporter);
		reporterThread.setDaemon(true);
		reporterThread.start();
	}

	@Override
	public void run()
	{
		while (!dwayna.isFinished())
		{
			Integer sleepTime = Settings.getSettings().getStatusInterval();

			if (sleepTime == null)
			{
				break;
			}

			try
			{
				Thread.sleep(sleepTime.longValue());
			}
			catch (InterruptedException ex)
			{
				throw new RuntimeException(ex); // Should never happen
			}

			long runMilliSeconds = System.currentTimeMillis() - dwayna.getStartTime();
			StringBuilder output = new StringBuilder();

			output.append("Lyssa: Current generation: ").append(dwayna.getGeneration()).append(".");

			if (dwayna.getBestMortal() != null)
			{
				output.append(" Best score: ").append(dwayna.getBestMortal().getScore()).append(".");
			}

			if (dwayna.getLastAverageScore() != null)
			{
				output.append(" Average score last generation: ").append(dwayna.getLastAverageScore()).append(".");
			}

			int mortalsPerSecond = (int) (dwayna.getMortalCounter() / ((float) runMilliSeconds / 1000));
			output.append(" Mortals evaluted: ").append(dwayna.getMortalCounter()).append(" (").append(mortalsPerSecond).append(" per second).");

			output.append(" Breeding failure rate: ").append((int)(((dwayna.getBreedCounter() - dwayna.getMortalCounter()) / ((float) dwayna.getBreedCounter())) * 100)).append("%.");

			System.out.println(output);
		}
	}
}