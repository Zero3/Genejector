package genejector.realm.genetraits;

import genejector.realm.mortal.SourceCompositionTask;

public interface GeneTrait
{
	ParentTrait getParent();
	void setParent(ParentTrait newParent);
	String getCode(SourceCompositionTask task);
	void instantiate();
	boolean prototypeEquals(GeneTrait other);
	int prototypeHashCode();
	Integer getPrototypeId();
	String prototypeTag();
}