package genejector.shared.util;

import java.util.LinkedList;
import java.util.List;

public class PaddingStringBuilder
{
	private final List<String> strings = new LinkedList<String>();
	private final List<PaddingSection> sections = new LinkedList<PaddingSection>();

	public PaddingStringBuilder append(int padding, Object obj)
	{
		if (obj != null)
		{
			String objString = obj.toString();

			strings.add(objString);
			sections.add(new PaddingSection(padding));
		}
		
		return this;
	}

	public PaddingStringBuilder append(int padding, Object obj, boolean rightPad)
	{
		if (obj != null)
		{
			String objString = obj.toString();

			strings.add(objString);
			sections.add(new PaddingSection(padding, rightPad));
		}

		return this;
	}

	public PaddingStringBuilder append(PaddingSection section, Object obj)
	{
		if (obj != null)
		{
			String objString = obj.toString();
			strings.add(objString);

			section.length = Math.max(section.length, objString.length());
			sections.add(section);
		}

		return this;
	}

	public PaddingStringBuilder append(Object obj)
	{
		if (obj != null)
		{
			strings.add(obj.toString());
			sections.add(new PaddingSection(obj.toString().length()));
		}

		return this;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		while (!strings.isEmpty())
		{
			String string = strings.remove(0);
			PaddingSection section = sections.remove(0);

			if (section.rightPad)
			{
				sb.append(string);
			}

			for (int i = 0; i < section.length - string.length(); i++)
			{
				sb.append(" ");
			}

			if (!section.rightPad)
			{
				sb.append(string);
			}
		}

		return sb.toString();
	}

	public static class PaddingSection
	{
		public int length = 0;
		public boolean rightPad = true;

		public PaddingSection(int length)
		{
			this.length = length;
		}

		public PaddingSection(boolean rightPad)
		{
			this.rightPad = rightPad;
		}

		public PaddingSection(int length, boolean rightPad)
		{
			this.length = length;
			this.rightPad = rightPad;
		}
	}
}
