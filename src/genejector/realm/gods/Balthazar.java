package genejector.realm.gods;

import genejector.risen.GenejectedMortal;
import genejector.shared.Settings;
import genejector.shared.exceptions.CompilationException;
import genejector.shared.messages.MortalMessage;
import genejector.shared.util.MemoryJavaFileManager;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

// "Lift up thy weapons. For you are my soldiers, and must you be steadfast,
// strong, and brave of heart. They who neither hesitate nor stumble shall be
// rewarded. Then shall you have glory. Then shall your deeds be remembered
// for eternity."
// — Scriptures of Balthazar, 48 BE
public class Balthazar
{
	private static final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();	// Prepare system compiler. Requires JDK installed
	private final MemoryJavaFileManager fileManager = new MemoryJavaFileManager(compiler.getStandardFileManager(null, null, null));	// Setup our custom memory file manager which the compiler will use to save the resulting class
	private final List<JavaFileObject> jfiles = new ArrayList<JavaFileObject>();

	protected Balthazar()
	{
		// Package protected constructor
	}

	static
	{
		if (compiler == null)
		{
			throw new UnsupportedOperationException("Could not find any Java compiler. Is the JDK installed? Correctly?");
		}
	}

	public MortalMessage compile(String src)
	{
		// TODO: Apply source formatting through some lib?
		StringBuilder fullSrc = new StringBuilder();
		fullSrc.append("import ").append(GenejectedMortal.class.getCanonicalName()).append(";\n");
		fullSrc.append("\n");
		fullSrc.append("public class GenejectorMortal implements ").append(GenejectedMortal.class.getCanonicalName()).append("\n");
		fullSrc.append("{\n");
		fullSrc.append("	public static void execute(").append(Settings.getSettings().getProblemClassName()).append(" problem) throws Exception\n");
		fullSrc.append("	{\n");
		fullSrc.append(src).append("\n");
		fullSrc.append("	}\n");
		fullSrc.append("}");

		// Setup source as a virtual file
		jfiles.clear();
		jfiles.add(MemoryJavaFileManager.makeStringSource("GenejectorMortal.java", fullSrc.toString()));

		// Let's go! Compile the thing and send back the resulting raw bytes
		StringWriter compilationOutput = new StringWriter();
		boolean result = compiler.getTask(compilationOutput, fileManager, null, null, null, jfiles).call();

		if (!result)
		{
			throw new CompilationException("Compilation of mortal failed unexpectedly", fullSrc.toString(), compilationOutput.toString());
		}

		// TODO: Simplify this call by cleaning up MemoryJaveFileManager.java
		// TODO: Don't send null in case of failure. Send back some kind of exception instead.
		return new MortalMessage(fileManager.getClassBytes().get("GenejectorMortal"));
	}
}