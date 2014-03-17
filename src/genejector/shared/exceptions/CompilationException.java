package genejector.shared.exceptions;

public class CompilationException extends RuntimeException implements GenejectorException
{
	public final String source;
	public final String compilationOutput;
	
	public CompilationException(String message, String source, String compilationoutput)
	{
		super(message + ": " + compilationoutput);
		
		this.source = source;
		this.compilationOutput = compilationoutput;
	}
}