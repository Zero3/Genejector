package genejector.realm.gods;

import genejector.realm.Type;
import genejector.realm.genes.AbstractGene;
import genejector.realm.genes.AssignmentGene;
import genejector.realm.genes.InstructionGene;
import genejector.realm.genes.MethodGene;
import genejector.realm.genes.NewObjectGene;
import genejector.realm.genes.NewVariableGene;
import genejector.realm.genes.UseFieldGene;
import genejector.realm.genes.UseVariableGene;
import genejector.realm.genetraits.GeneTrait;
import genejector.realm.genetraits.ParentTrait;
import genejector.shared.BreedingClassSetup;
import genejector.shared.Settings;
import genejector.shared.exceptions.NonWhitelistedTypeException;
import genejector.shared.exceptions.ReflectionException;
import genejector.shared.exceptions.UnsupportedTypeException;
import genejector.shared.util.PaddingStringBuilder;
import genejector.shared.util.Tools;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

// "Yet the power of a god cannot be destroyed, and Kormir, making a choice that
// only a mortal could make, did take upon herself the mantle of the Goddess of
// Truth, with all its power and responsibility, all its dominion and duties."
// — Scriptures of Kormir, 1075 AE
public class Kormir
{
	private static final Type TYPE_VOID = Type.getType("void");

	private final Map<String, BreedingClassSetup> whitelistedClasses = new HashMap<String, BreedingClassSetup>();
	private final Map<Type, Set<GeneTrait>> starterGenePool = new TreeMap<Type, Set<GeneTrait>>();
	private final Set<String> skippedClasses = new TreeSet<String>();
	private final Set<String> unsupportedStuff = new TreeSet<String>();
	private static final Map<String, Class> rawTypesClassMap = new HashMap<String, Class>();

	// TODO: Support inheritance. Or perhaps casting instead? Tricky...
	// TODO: Ignore things marked as deprecated with annotations
	// TODO: Create class for gene pool. A manual map of sets become error prone to handle over time.

	static
	{
		for (Class clazz : new Class[] {boolean.class, byte.class, char.class, short.class, int.class, float.class, double.class, long.class})
		{
			rawTypesClassMap.put(clazz.getCanonicalName(), clazz);
		}
	}

	protected Kormir()
	{
		// Packpage protected constructor
	}

	public void reflectProblem()
	{
		// Prepare lookup map
		for (BreedingClassSetup classSetup : Settings.getSettings().getClasses())
		{
			whitelistedClasses.put(classSetup.className, classSetup);
		}

		// Add to the gene pool the 'problem' argument provided to the genetic individual's execute() method at runtime
		infuseUseFieldGene(starterGenePool, null, Type.getType(Settings.getSettings().getProblemClassName()), "problem", true);	// Treat the argument as a final class field

		// Set up starter gene pool
		doReflectionRun(starterGenePool);

		// Print starter gene pool to screen
		PaddingStringBuilder output = new PaddingStringBuilder();
		output.append("Kormir: Created initial gene pool:\n");
		PaddingStringBuilder.PaddingSection geneIdSection = new PaddingStringBuilder.PaddingSection(false);
		PaddingStringBuilder.PaddingSection geneNameSection = new PaddingStringBuilder.PaddingSection(true);

		for (Map.Entry<Type, Set<GeneTrait>> entry : starterGenePool.entrySet())
		{
			Set<GeneTrait> sortedEntry = new TreeSet<GeneTrait>(new AbstractGene.PrototypeIdComparator());
			sortedEntry.addAll(entry.getValue());

			output.append(" | " + entry.getKey() + ":\n");

			for (GeneTrait gene : sortedEntry)
			{
				output.append(" | | ").append(geneIdSection, gene + " ").append(geneNameSection, gene.getClass().getSimpleName()).append(" ").append(gene.prototypeTag()).append("\n");
			}
		}

		System.out.print(output);

		// Print skipped stuff to screen too
		if (!skippedClasses.isEmpty())
		{
			System.out.println("Kormir: Skipped all fields, methods and return values involving these non-approved classes:");
			Tools.prettyPrintStringList(skippedClasses);
		}

		// Print unsupported stuff to screen too
		if (!unsupportedStuff.isEmpty())
		{
			System.out.println("Kormir: Skipped these unsupported things:");
			Tools.prettyPrintStringList(unsupportedStuff);
		}
	}

	private void doReflectionRun(Map<Type, Set<GeneTrait>> genePool)
	{
		long lastCount;
		long newCount = Tools.mapOfSetsSize(genePool);

		do
		{
			lastCount = newCount;

			for (BreedingClassSetup classSetup : Settings.getSettings().getClasses())
			{
				Class<?> clazz = rawTypesClassMap.get(classSetup.className);

				try
				{
					if (clazz == null)
					{
						clazz = Class.forName(classSetup.className);
					}

					reflectClassWrapper(genePool, classSetup.className, clazz.getTypeParameters().length, new ArrayList<String>(), 0);
				}
				catch (ClassNotFoundException ex)
				{
					throw new ReflectionException(ex);
				}
			}

			newCount = Tools.mapOfSetsSize(genePool);
		} while (newCount - lastCount > 0);
	}

	private void reflectClassWrapper(Map<Type, Set<GeneTrait>> genePool, String className, int parameterCount, List<String> parameterTypes, int parametersSet) throws ClassNotFoundException
	{
		// This wrapper takes care of running reflectClass() for all possible combinations of parameter types on classes that are parameterized (Java generics)
		if (parametersSet < parameterCount)
		{
			// Recurse!
			for (BreedingClassSetup classSetup : Settings.getSettings().getClasses())
			{
				if (!rawTypesClassMap.containsKey(classSetup.className))
				{
					// Raw types are not supported by Java generics, so skip those
					if (parameterTypes.size() >= parametersSet + 1)
					{
						parameterTypes.remove(parametersSet);
					}

					parameterTypes.add(parametersSet, classSetup.className);
					reflectClassWrapper(genePool, className, parameterCount, parameterTypes, parametersSet + 1);
				}
			}
		}
		else
		{
			Class<?> clazz = rawTypesClassMap.get(className);

			if (clazz == null)
			{
				clazz = Class.forName(className);
			}

			Type classType = Type.getType(className + ((parameterCount == 0) ? "" : ("<" + Tools.implode(parameterTypes, ",") + ">")));
			reflectClass(genePool, clazz, className, classType, parameterTypes);
		}
	}

	private void reflectClass(Map<Type, Set<GeneTrait>> genePool, Class<?> clazz, String rawClassName, Type finalClassType, List<String> classParameterTypes)
	{
		// Reflect fields
		fieldLoop:
		for (Field field : clazz.getDeclaredFields())
		{
			if (!Modifier.isPublic(field.getModifiers()))
			{
				continue;
			}

			// Skip if field is not static and we don't have any instances available of this class
			boolean isStatic = Modifier.isStatic(field.getModifiers());
			if (!isStatic && (genePool.get(finalClassType) == null || genePool.get(finalClassType).isEmpty()))
			{
				continue;
			}

			// Get field type
			Type fieldType;

			try
			{
				fieldType = resolveJavaType(clazz, classParameterTypes, field.getGenericType());
			}
			catch (NonWhitelistedTypeException ex)
			{
				skippedClasses.add(ex.getOffendingType());
				continue fieldLoop;
			}

			// All is good to go
			boolean isFinal = Modifier.isFinal(field.getModifiers());
			String fieldName = (isStatic ? (rawClassName + ".") : "") + field.getName();
			infuseUseFieldGene(genePool, (isStatic ? null : finalClassType), fieldType, fieldName, isFinal);
			infuseNewVariableGene(genePool, fieldType);

			if (!isFinal)
			{
				infuseAssignmentGene(genePool, fieldType);
			}
		}

		// Reflect methods
		methodLoop:
		for (Method method : clazz.getDeclaredMethods())
		{
			if (!Modifier.isPublic(method.getModifiers()))
			{
				continue;
			}

			// Skip resetState() method of problem class. There is no risk involved in genetic code calling it, but we might as well avoid the clutter
			if (finalClassType.toString().equals(Settings.getSettings().getProblemClassName()) && method.getName().equals("resetState"))
			{
				continue;
			}

			// Skip if method is not static and we don't have any instances available of this class
			boolean isStatic = Modifier.isStatic(method.getModifiers());
			if (!isStatic && (genePool.get(finalClassType) == null || genePool.get(finalClassType).isEmpty()))
			{
				continue;
			}

			// Get return type
			Type returnType;

			try
			{
				returnType = resolveJavaType(clazz, classParameterTypes, method.getGenericReturnType());
			}
			catch (NonWhitelistedTypeException ex)
			{
				// If (parts of) return type is not whitelisted, we just treat the method as a void returning one. In other words, we ignore the return value.
				skippedClasses.add(ex.getOffendingType());
				returnType = TYPE_VOID;
			}
			catch (UnsupportedTypeException ex)
			{
				unsupportedStuff.add(rawClassName + (isStatic ? "." : "#") + method.getName());
				continue methodLoop;
			}

			// Build argument list and check if they are all allowed and available
			List<Type> argClasses = new LinkedList<Type>();

			for (java.lang.reflect.Type javaArgType : method.getGenericParameterTypes())
			{
				try
				{
					Type argType = resolveJavaType(clazz, classParameterTypes, javaArgType);

					// Check for availability in the gene pool
					if (genePool.get(argType) == null || genePool.get(argType).isEmpty())
					{
						continue methodLoop;
					}

					argClasses.add(argType);
				}
				catch (NonWhitelistedTypeException ex)
				{
					skippedClasses.add(ex.getOffendingType());
					continue methodLoop;
				}
			}

			// All is good to go
			String methodName = (isStatic ? (rawClassName + ".") : "") + method.getName();
			infuseMethodGene(genePool, returnType, (isStatic ? null : finalClassType), methodName, argClasses);

			if (!returnType.equals(TYPE_VOID))
			{
				infuseNewVariableGene(genePool, returnType);
			}
		}

		// Reflect constructors
		// [ListingStart][L3]
		if (whitelistedClasses.get(rawClassName).instantiable)
		{
			constructorLoop:
			for (Constructor<?> constructor : clazz.getConstructors())
			{
				List<Type> argTypes = new LinkedList<Type>();

				for (java.lang.reflect.Type javaArgType : constructor.getGenericParameterTypes())
				{
					try
					{
						// Resolve generics and type parameters
						Type argType = resolveJavaType(clazz, classParameterTypes, javaArgType);

						// Ensure availability in the gene pool
						if (genePool.get(argType) == null || genePool.get(argType).isEmpty())
						{
							continue constructorLoop;
						}

						argTypes.add(argType);
					}
					catch (NonWhitelistedTypeException ex)
					{
						// No go. Log and skip this constructor.
						skippedClasses.add(ex.getOffendingType());
						continue constructorLoop;
					}
				}

				// All good. Infuse genes into gene pool.
				Type newType = Type.getType(whitelistedClasses.get(rawClassName).className);
				infuseNewObjectGene(genePool, newType, argTypes);
				infuseNewVariableGene(genePool, newType);
			}
		}
		// [ListingEnd][L3]
	}

	public void reflectMortal(Map<Type, Set<GeneTrait>> genePool, ParentTrait parent, Integer lastChild)
	{
		// TODO: Variables defined in a for/while loop? Consider that when implementing those statements...

		// Crawl up in tree, adding genes during the crawling
		for (int i = lastChild; i >= 0; i--)
		{
			GeneTrait child = parent.getChild(i);

			if (child instanceof InstructionGene)
			{
				child = ((InstructionGene) child).getChild(0);	// This is an instruction wrapper. Unwrap...
			}

			if (child instanceof NewVariableGene)
			{
				Type variableType = ((ParentTrait) child).getChildType(0);

				infuseVariableReference(genePool, variableType, (NewVariableGene) child);
				infuseUseVariableGene(genePool, variableType);
				infuseAssignmentGene(genePool, variableType);

				doReflectionRun(genePool);
			}
		}

		if (parent.getParent() != null)
		{
			reflectMortal(genePool, parent.getParent(), parent.getParent().indexOf(parent) - 1);
		}
	}

	private void addGeneToPool(Map<Type, Set<GeneTrait>> map, Type mapEntry, AbstractGene setEntry)
	{
		Set<GeneTrait> set = map.get(mapEntry);

		if (set == null)
		{
			set = new HashSet<GeneTrait>();
			map.put(mapEntry, set);
		}

		if (!set.contains(setEntry))
		{
			if (setEntry.getPrototypeId() == null)
			{
				setEntry.assignPrototypeId();	// Existing genes added to the pool (like NewVariableGene) already have an ID. Don't change that.
			}

			set.add(setEntry);
		}
	}

	private void infuseMethodGene(Map<Type, Set<GeneTrait>> genePool, Type returnType, Type objectType, String name, List<Type> argTypes)
	{
		AbstractGene newGene = new MethodGene(returnType, objectType, name, argTypes);

		if (returnType.equals(TYPE_VOID))
		{
			addGeneToPool(genePool, Type.getType("<Instruction>"), newGene);
			infuseInstructionCommandGene(genePool);
		}

		if (!returnType.equals(TYPE_VOID))
		{
			addGeneToPool(genePool, returnType, newGene);
		}
	}

	private void infuseInstructionCommandGene(Map<Type, Set<GeneTrait>> genePool)
	{
		addGeneToPool(genePool, Type.getType("<Statement>"), new InstructionGene());
	}

	private void infuseAssignmentGene(Map<Type, Set<GeneTrait>> genePool, Type type)
	{
		AssignmentGene varAssignment = new AssignmentGene(Type.getType("<WriteableVarOrField:" + type + ">"), type);
		addGeneToPool(genePool, Type.getType("<Instruction>"), varAssignment);
		infuseInstructionCommandGene(genePool);
	}

	private void infuseNewVariableGene(Map<Type, Set<GeneTrait>> genePool, Type variableType)
	{
		if (whitelistedClasses.containsKey(variableType.toString()))
		{
			NewVariableGene referenceGene = new NewVariableGene(variableType);
			addGeneToPool(genePool, Type.getType("<Instruction>"), referenceGene);
			infuseInstructionCommandGene(genePool);
		}
	}

	private void infuseUseFieldGene(Map<Type, Set<GeneTrait>> genePool, Type objectType, Type fieldType, String name, boolean isFinal)
	{
		AbstractGene newGene = new UseFieldGene(objectType, fieldType, name);
		addGeneToPool(genePool, fieldType, newGene);

		if (!isFinal)
		{
			addGeneToPool(genePool, Type.getType("<WriteableVarOrField:" + fieldType + ">"), newGene);
		}
	}

	private void infuseUseVariableGene(Map<Type, Set<GeneTrait>> genePool, Type variableType)
	{
		AbstractGene newGene = new UseVariableGene(variableType, Type.getType("<Variable:" + variableType + ">"));
		addGeneToPool(genePool, variableType, newGene);
		addGeneToPool(genePool, Type.getType("<WriteableVarOrField:" + variableType + ">"), newGene);
	}

	private void infuseVariableReference(Map<Type, Set<GeneTrait>> genePool, Type variableType, NewVariableGene newVariableGene)
	{
		addGeneToPool(genePool, Type.getType("<Variable:" + variableType + ">"), newVariableGene);
	}

	private void infuseNewObjectGene(Map<Type, Set<GeneTrait>> genePool, Type variableType, List<Type> childTypes)
	{
		AbstractGene newGene = new NewObjectGene(variableType, childTypes);
		addGeneToPool(genePool, variableType, newGene);
	}

	public Map<Type, Set<GeneTrait>> copyGenePool()
	{
		// The Type key can be re-used, as can (and should!) the actual genes,
		// but the set needs to be duplicated so we can add new stuff to it
		// without affecting the original set.

		Map<Type, Set<GeneTrait>> newPool = new HashMap<Type, Set<GeneTrait>>();

		for (Entry<Type, Set<GeneTrait>> poolEntry : starterGenePool.entrySet())
		{
			newPool.put(poolEntry.getKey(), new HashSet<GeneTrait>(poolEntry.getValue()));
		}

		return newPool;
	}

	private Type resolveJavaType(Class<?> clazz, List<String> classParameterTypes, java.lang.reflect.Type javaType)
	{
		if (javaType instanceof Class)
		{
			// The base case. A plain normal class type.
			String rawClass = ((Class) javaType).getCanonicalName();
			Type type = Type.getType(rawClass);

			if (!type.equals(TYPE_VOID) && !whitelistedClasses.containsKey(rawClass))	// Void is always OK (since it's basically a placeholder for "nothing")
			{
				throw new NonWhitelistedTypeException(rawClass);
			}

			return Type.getType(rawClass);
		}
		// [ListingStart][L9]
		else if (javaType instanceof ParameterizedType)
		{
			// This type has at least one named parameter, like ArrayList<Integer>.
			ParameterizedType paraTypes = (ParameterizedType) javaType;
			String rawClass = ((Class) paraTypes.getRawType()).getCanonicalName();

			// Ensure that the parameterized class is whitelisted
			if (!whitelistedClasses.containsKey(rawClass))
			{
				throw new NonWhitelistedTypeException(rawClass);
			}

			List<String> paraStrings = new LinkedList<String>();

			for (java.lang.reflect.Type paraType : paraTypes.getActualTypeArguments())
			{
				// We do not (yet) support nesting, so this will work for ArrayList<Integer>
				// but not for ArrayList<ArrayList<Integer>> for example
				String paraTypeName = ((Class) paraType).getCanonicalName();

				// Ensure that the parameter class is whitelisted
				if (!whitelistedClasses.containsKey(paraTypeName))
				{
					throw new NonWhitelistedTypeException(paraTypeName);
				}

				paraStrings.add(paraTypeName);
			}

			// Compile final type string
			return Type.getType(rawClass + "<" + Tools.implode(paraStrings, ",") + ">");
		}
		// [ListingEnd][L9]
		else if (javaType instanceof TypeVariable)
		{
			// This is a type variable, like in "boolean add(E e)". Look it up in our lookup list. Should already be whitelisted by now
			return Type.getType(classParameterTypes.get(Arrays.asList(clazz.getTypeParameters()).indexOf(javaType)));
		}
		else
		{
			throw new UnsupportedTypeException(javaType);
		}
	}
}