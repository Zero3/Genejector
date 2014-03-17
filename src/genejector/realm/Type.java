package genejector.realm;

import genejector.shared.Settings;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

// Types are simply identified by strings for now
// TODO: Add proper semantic type support
public final class Type implements Comparable<Type>, Serializable
{
	private static final long serialVersionUID = Settings.VERSION;
	private static final Map<String, Type> typeCache = new HashMap<String, Type>();
	private final String name;

	// This is private to encourage that we only instantiate one copy of each
	// type and look them up in the cache using getType(). Note that
	// serialization *do* cause indentical copies.
	private Type(String name)
	{
		this.name = name;
	}

	@Override
	public String toString()
	{
		return name;
	}

	public static Type getType(String typeID)
	{
		Type type = typeCache.get(typeID);

		if (type == null)
		{
			type = new Type(typeID);
			typeCache.put(typeID, type);
		}

		return type;
	}

	@Override
	public int compareTo(Type o)
	{
		return name.compareTo(o.name);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof Type)
		{
			Type otherType = (Type) obj;

			if (otherType.name.equals(name))
			{
				return true;
			}
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		int hash = 5;
		hash = 79 * hash + (this.name == null ? 0 : this.name.hashCode());
		return hash;
	}
}