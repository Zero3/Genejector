package genejector.realm.genetraits;

import genejector.realm.Type;

public abstract interface ParentTrait extends GeneTrait
{
	int getChildCount();
	GeneTrait getChild(int childIndex);
	int indexOf(GeneTrait child);
	void setChild(int childIndex, GeneTrait newChild);
	Type getChildType(int childIndex);
}