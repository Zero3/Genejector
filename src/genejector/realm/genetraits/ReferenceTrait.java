package genejector.realm.genetraits;

public interface ReferenceTrait extends GeneTrait
{
	boolean isBackReference(int childIndex);	// Specifies whether this child is a back reference to somewhere else in the tree. TODO: Separate references from childs completely?
}
