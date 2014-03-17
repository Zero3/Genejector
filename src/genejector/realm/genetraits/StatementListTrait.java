package genejector.realm.genetraits;

public interface StatementListTrait extends ParentTrait
{
	void addStatementSlot(int statementIndex);
	void removeStatementSlot(int statementIndex);
}