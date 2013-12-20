package Symbol;

import java.util.HashMap;


public class GoalTable implements Table{
	private HashMap<String,ClassTable> ClassList = new HashMap<String,ClassTable>();
	
	public HashMap<String,ClassTable> getClassList()
	{
		return ClassList;
	}
	public void put(String name, Object table)
	{
		ClassList.put(name, (ClassTable)table);
	}
	public ClassTable get(String name)
	{
		return ClassList.get(name);
	}
	public MethodTable getMethodTable(String ClassName, String MethodName)
	{
		return this.ClassList.get(ClassName).getMethod().get(MethodName);
	}
}
