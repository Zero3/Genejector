package genejector.shared.exceptions;

import java.lang.reflect.Type;

public class UnsupportedTypeException extends RuntimeException implements GenejectorException
{
	private final Type javaType;

	public UnsupportedTypeException(Type javaType)
	{
		super();
		
		this.javaType = javaType;
	}

	public Type getJavaType()
	{
		return javaType;
	}
}