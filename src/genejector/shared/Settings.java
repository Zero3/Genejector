package genejector.shared;

import genejector.realm.breedingoperators.AbstractBreedingOperator;
import genejector.realm.breedingoperators.AddStatementBreedingOperator;
import genejector.realm.breedingoperators.MutationBreedingOperator;
import genejector.realm.breedingoperators.RemoveStatementBreedingOperator;
import genejector.shared.util.PaddingStringBuilder;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

public final class Settings implements Serializable
{
	// Hard-coded settings. Do not change unless you know what you are doing.
	public static final int VERSION = 1;									// Internal version number used for serialization of classes
	private static final long serialVersionUID = VERSION;					// For serialization of this class
	public static final int COMMUNICATIONS_PORT_PROJECT = 42000;			// Port used for TCP communication between project JVM and realm JVM
	public static final int COMMUNICATIONS_PORT = 42001;					// Port used for TCP communication between realm JVM and mortal JVM

	// Termination limits. When any of these are met, the system will return the best solution to the original project instance
	private Long scoreLimit = null;											// Stop when a mortal with at least this score has been bred. null = No limit.
	private Integer generationLimit = 100;									// Stop when this number of generations have been bred. null = No limit.
	// TODO: Settings for stopping after no improvement over x seconds/generations

	// Breeding setings
	private int populationSize = 100;										// Keep breeding mortals for a generation until this many has been bred
	private Integer executionTimeout = 10 * 1000;							// Milliseconds of execution time allowed per mortal. null = forever. Warning: Without this setting, mortals might get stuck in infinite loops and halt the evolution!	// TODO: Implment this
	private final List<BreedingOperatorSetup> breedingOperators = new LinkedList<BreedingOperatorSetup>();	// Defaults are added via addDefaultBreedingOperators() by Genejector if this list is left empty
	private String problemClassName = null;									// Canonical name of problem class (the class of the object being passed to Genejector.execute())
	private boolean addDefaultClasses = true;								// Whether to add default breeding classes after the project has specified its own
	private final Set<BreedingClassSetup> breedingClasses = new TreeSet<BreedingClassSetup>();				// Defaults are added via addDefaultBreedingClasses() (as well as the genejected class itself) by Genejector if this set is left empty

	// Log settings
	private Integer statusInterval = 5000;									// How often to report the status of the breeding process in milliseconds. null = never.
	private boolean printAllIndividuals = false;							// Print all generated individuals to screen?

	// Other settings
	private long randomSeed = new Random().nextLong();						// Seed for the random generator
	private String projectMain = System.getProperty("sun.java.command");	// Main method of original project. Mortal instances launched by Genejector will start here.

	// Singleton management
	private Settings(){};

	private static Settings settingsInstance = new Settings();

	public static Settings getSettings()
	{
		return settingsInstance;
	}

	public static void setSettings(Settings settings)
	{
		Settings.settingsInstance = settings;
	}

	// Set default locale. Doesn't really fit anywhere else than in this class
	static
	{
		Locale.setDefault(Locale.UK);
	}

	// Reporter
	public String toPrettyString()
	{
		PaddingStringBuilder output = new PaddingStringBuilder();

		// Settings section
		PaddingStringBuilder.PaddingSection settingsSection = new PaddingStringBuilder.PaddingSection(true);
		output
			.append(" | ").append(settingsSection, "Score limit: ").append(scoreLimit)
			.append("\n | ").append(settingsSection, "Population size: ").append(populationSize)
			.append("\n | ").append(settingsSection, "Generation limit: ").append(generationLimit)
			.append("\n | ").append(settingsSection, "Execution timeout: ").append(String.format("%,d", executionTimeout)).append(" (ms)")
			.append("\n | ").append(settingsSection, "Status interval: ").append(String.format("%,d", statusInterval)).append(" (ms)")
			.append("\n | ").append(settingsSection, "Random seed: ").append(randomSeed)
			.append("\n | ").append(settingsSection, "Project main class: ").append(projectMain)
			.append("\n | ").append(settingsSection, "Project problem class: ").append(problemClassName);

		// Breeding operators section
		DecimalFormat oneDotTwoFormat = new DecimalFormat("0.00");
		Double totalWeight = 0D;

		for (BreedingOperatorSetup opSetup : breedingOperators)
		{
			totalWeight += opSetup.weight;
		}

		output.append("\n | Breeding operators:");
		PaddingStringBuilder.PaddingSection breedOpWeights = new PaddingStringBuilder.PaddingSection(false);
		PaddingStringBuilder.PaddingSection breedOpPercent = new PaddingStringBuilder.PaddingSection(false);

		for (BreedingOperatorSetup opSetup : breedingOperators)
		{
			output.append("\n | | ").append(breedOpWeights, oneDotTwoFormat.format(opSetup.weight)).append(" -> ").append(breedOpPercent, Math.round(opSetup.weight / totalWeight * 100)).append("%: ").append(opSetup.breedingOperator);
		}

		// Breeding classes
		output.append("\n | Approved breeding classes:");
		StringBuilder classesWith = new StringBuilder();
		StringBuilder classesWithout  = new StringBuilder();

		for (BreedingClassSetup classSetup : breedingClasses)
		{
			if (classSetup.instantiable)
			{
				classesWith.append("\n | | | ").append(classSetup.className);
			}
			else
			{
				classesWithout.append("\n | | | ").append(classSetup.className);
			}
		}

		if (classesWith.length() > 0)
		{
			output.append("\n | | With instantiation:").append(classesWith);
		}

		if (classesWithout.length() > 0)
		{
			output.append("\n | | Without instantiation:").append(classesWithout);
		}

		return output.toString();
	}

	// Getters and setters
	public Long getScoreLimit()
	{
		return scoreLimit;
	}

	public void setScoreLimit(Integer scoreLimit)
	{
		this.scoreLimit = scoreLimit.longValue();
	}

	public void setScoreLimit(Long scoreLimit)
	{
		this.scoreLimit = scoreLimit;
	}

	public Integer getGenerationLimit()
	{
		return generationLimit;
	}

	public void setGenerationLimit(Integer generationLimit)
	{
		this.generationLimit = generationLimit;
	}

	public int getPopulationSize()
	{
		return populationSize;
	}

	public void setPopulationSize(int populationSize)
	{
		this.populationSize = populationSize;
	}

	public Integer getExecutionTimeout()
	{
		return executionTimeout;
	}

	public void setExecutionTimeout(Integer executionTimeout)
	{
		this.executionTimeout = executionTimeout;
	}

	public List<BreedingOperatorSetup> getBreedingOperators()
	{
		return breedingOperators;
	}

	public void addBreedingOperator(AbstractBreedingOperator breedingOperator, double weight)
	{
		breedingOperators.add(new BreedingOperatorSetup(breedingOperator, weight));
	}

	public void setAddDefaultClasses(boolean addDefaultClasses)
	{
		this.addDefaultClasses = addDefaultClasses;
	}

	public boolean getAddDefaultClasses()
	{
		return addDefaultClasses;
	}

	public String getProblemClassName()
	{
		return problemClassName;
	}

	public void setProblemClassName(String problemClassName)
	{
		this.problemClassName = problemClassName;
	}

	public Set<BreedingClassSetup> getClasses()
	{
		return breedingClasses;
	}

	public void addClass(Class<?> breedingClass, boolean instantiable)
	{
		breedingClasses.add(new BreedingClassSetup(breedingClass.getCanonicalName(), instantiable));
	}

	public void addClass(String canonicalBreedingClassName, boolean instantiable)
	{
		breedingClasses.add(new BreedingClassSetup(canonicalBreedingClassName, instantiable));
	}

	public Integer getStatusInterval()
	{
		return statusInterval;
	}

	public void setStatusInterval(Integer statusInterval)
	{
		this.statusInterval = statusInterval;
	}

	public boolean getPrintAllIndividuals()
	{
		return printAllIndividuals;
	}

	public void setPrintAllIndividuals(boolean printAllIndividuals)
	{
		this.printAllIndividuals = printAllIndividuals;
	}

	public long getRandomSeed()
	{
		return randomSeed;
	}

	public void setRandomSeed(long randomSeed)
	{
		this.randomSeed = randomSeed;
	}

	public String getProjectMain()
	{
		return projectMain;
	}

	public void setProjectMain(String projectMain)
	{
		this.projectMain = projectMain;
	}

	public void addDefaults()
	{
		if (breedingOperators.isEmpty())
		{
			// TODO: Add crossover (tournament selection?)
			breedingOperators.add(new BreedingOperatorSetup(new MutationBreedingOperator(), 16));
			breedingOperators.add(new BreedingOperatorSetup(new AddStatementBreedingOperator(), 4));
			breedingOperators.add(new BreedingOperatorSetup(new RemoveStatementBreedingOperator(), 1));
		}

		if (addDefaultClasses)
		{
			// TODO: Add more!
			breedingClasses.add(new BreedingClassSetup(Integer.class.getCanonicalName(), false));
			breedingClasses.add(new BreedingClassSetup(String.class.getCanonicalName(), false));
		}
	}
}