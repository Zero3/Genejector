package genejector.shared.util;

import java.util.Arrays;

public class DynamicByteClassLoader extends ClassLoader
{
	private final String clazz;
	private final byte[] source;

	public DynamicByteClassLoader(String clazz, byte[] source)
	{
		super();
		this.clazz = clazz;
		this.source = Arrays.copyOf(source, source.length);
	}

	@Override
	protected Class<?> findClass(String name)
	{
		if (!name.equals(clazz))
		{
			throw new RuntimeException("Cannot load class named " + name + ". Can only load instances of class " + clazz);
		}

		return defineClass(name, source, 0, source.length);
	}
}