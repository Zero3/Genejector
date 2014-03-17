package genejector.shared;

import genejector.project.GenejectorProblem;
import genejector.project.ProjectInstanceManager;
import genejector.risen.GenejectedMortal;
import genejector.risen.RisenInstanceManager;
import genejector.shared.exceptions.GenejectedExecutionException;
import genejector.shared.exceptions.GenejectorIllegalStateException;
import genejector.shared.exceptions.ReflectionException;
import genejector.shared.util.DynamicByteClassLoader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Genejector
{
	// Global variables
	private static GenejectedMortal genejectedMortal = null;
	private static Method executeMethod = null;
	private static boolean scoreSubmitted = false;
	private static boolean executeIsRunning = false;
	private static boolean geneticExceptionThrown = false;

	// Instance role management
	private static InstanceRole instanceRole = InstanceRole.PROJECT;	// Default. Will be overriden by setInstanceRole() if this is a JVM launched by Genejector

	public enum InstanceRole
	{
		PROJECT, REALM, RISEN
	}

	public static InstanceRole getInstanceRole()
	{
		return instanceRole;
	}

	public static void setInstanceRole(InstanceRole instanceRole)
	{
		Genejector.instanceRole = instanceRole;
	}

	// Methods
	public static synchronized void geneject(Class<? extends GenejectorProblem> problem)
	{
		byte[] mortalBytes;

		if (instanceRole == InstanceRole.PROJECT)
		{
			// Finalize settings
			Settings.getSettings().setProblemClassName(problem.getCanonicalName());
			Settings.getSettings().addClass(problem.getCanonicalName(), false);
			Settings.getSettings().addDefaults();

			// Go! Go! Go!
			mortalBytes = ProjectInstanceManager.getSolution();
		}
		else if (instanceRole == InstanceRole.RISEN)
		{
			mortalBytes = RisenInstanceManager.getMortal();
		}
		else
		{
			throw new GenejectorIllegalStateException("Method called from illegal instance of type " + instanceRole);
		}

		// Bind a mortal to this instance
		try
		{
			// [ListingStart][L6]
			DynamicByteClassLoader mortalClassLoader = new DynamicByteClassLoader("GenejectorMortal", mortalBytes);
			Class<?> genejectedClass = Class.forName("GenejectorMortal", true, mortalClassLoader);
			genejectedMortal = (GenejectedMortal) genejectedClass.newInstance();
			executeMethod = genejectedClass.getMethod("execute", problem);
			// [ListingEnd][L6]
			scoreSubmitted = false;
			executeIsRunning = false;
			geneticExceptionThrown = false;
		}
		catch (ClassNotFoundException ex)
		{
			throw new ReflectionException(ex);
		}
		catch (InstantiationException ex)
		{
			throw new ReflectionException(ex);
		}
		catch (IllegalAccessException ex)
		{
			throw new ReflectionException(ex);
		}
		catch (NoSuchMethodException ex)
		{
			throw new ReflectionException(ex);
		}
	}

	public static void execute(GenejectorProblem problem)
	{
		if (instanceRole == InstanceRole.PROJECT || instanceRole == InstanceRole.RISEN)
		{
			if (executeIsRunning)
			{
				return;	// TODO: Consider throwing an exception instead once we are able to propagate them properly to the realm instance
			}

			try
			{
				executeIsRunning = true;
				problem.resetState();
				executeMethod.invoke(genejectedMortal, problem);
				executeIsRunning = false;
			}
			catch (IllegalAccessException ex)
			{
				throw new ReflectionException(ex);
			}
			catch (InvocationTargetException ex)
			{
				if (instanceRole == InstanceRole.RISEN)
				{
					// We don't like exceptions when evolving genetic code. Swallow it and override returned score with -1
					geneticExceptionThrown = true;
				}
				else
				{
					throw new GenejectedExecutionException(ex.getCause());
				}
			}
		}
		else
		{
			throw new GenejectorIllegalStateException("Method called from illegal instance of type " + instanceRole);
		}
	}

	public static synchronized void submitScore(long score)
	{
		if (instanceRole == InstanceRole.RISEN)
		{
			if (genejectedMortal == null)
			{
				throw new GenejectorIllegalStateException("A score cannot be submitted before a mortal has been genejected");
			}

			if (scoreSubmitted)
			{
				throw new GenejectorIllegalStateException("A score has already been submitted for this mortal");
			}

			scoreSubmitted = true;
			RisenInstanceManager.submitScore((geneticExceptionThrown ? -1 : score));
		}
		else if (instanceRole == InstanceRole.PROJECT)
		{
			// Ignore when called from the original project
		}
		else
		{
			throw new GenejectorIllegalStateException("Method called from illegal instance of type " + instanceRole);
		}
	}

	public static boolean isSolutionFound()
	{
		if (instanceRole == InstanceRole.PROJECT)
		{
			return (ProjectInstanceManager.getSolutionSourceCode() != null);
		}
		else if (instanceRole == InstanceRole.RISEN)
		{
			return false;	// Don't let it think so
		}
		else
		{
			throw new GenejectorIllegalStateException("Method called from illegal instance of type " + instanceRole);
		}
	}

	public static String getSolutionSourceCode()
	{
		if (instanceRole != InstanceRole.PROJECT)
		{
			throw new GenejectorIllegalStateException("Method called from illegal instance of type " + instanceRole);
		}

		return ProjectInstanceManager.getSolutionSourceCode();
	}

	public static Settings getSettings()
	{
		if (instanceRole == InstanceRole.PROJECT || instanceRole == InstanceRole.RISEN)
		{
			// We just ignore the fact that risen instances modify their local copy of the settings. It won't affect anything
			return Settings.getSettings();
		}
		else
		{
			throw new GenejectorIllegalStateException("Method called from illegal instance of type " + instanceRole);
		}
	}
}