package Symbol;

import java.util.HashMap;
import java.util.Map.Entry;

public class ClassTable implements Table{
	
	private int lineNumber = 0;
	private String extendsClass = null;
	private HashMap<String,VarType> VarList = new HashMap<String,VarType>();
	private HashMap<String,MethodTable> MethodList = new HashMap<String,MethodTable>();
	
	public void put(String key, Object value)
	{
		if(value instanceof VarType)
		{
			VarList.put(key, (VarType)value);
		}
		else if(value instanceof MethodTable)
			MethodList.put(key, (MethodTable)value);
	}
	public boolean checkMain()
	{
		return this.MethodList.containsKey("main");
	}
	public void setEx(String extendsClassName)
	{
		extendsClass = extendsClassName;
	}
	public String getEx()
	{
		return extendsClass;
	}
	public void setLineNumber(int linenumber)
	{
		lineNumber = linenumber;
	}
	public int getLineNumber()
	{
		return lineNumber;
	}
	/*public void put(String key, MethodTable value)
	{
		MethodList.put(key, value);
	}*/
	public HashMap<String,VarType> getVarList()
	{
		return VarList;
	}
	public HashMap<String,MethodTable> getMethod()
	{
		return MethodList;
	}
	
	/* Return the index of a Method in a Class */
	public int getMethodIndex(String MethodName)
	{
		/* Start from Zero */
		int count = 0;
		for(Entry<String, MethodTable> i : this.getMethod().entrySet())
		{
			if(i.getKey().equals(MethodName))
				break;
			count++;
		}
		return count;
	}
	
	/* Return the index of a Variable in a Class */
	public int getVarIndex(String VarName) 
	{
		/* also Start from Zero */
		int count = 0;
		for(Entry<String,VarType> i : this.getVarList().entrySet())
		{
			if(i.getKey().equals(VarName))
				break;
			count ++;
		}
		return count;
	}
	
	/* return the number of variables in a class */
	public int getVariableNumber()
	{
		return this.getVarList().size();
	}
	
	/* get the addressToken for a variable in a class
	 * first look up in the Method, then in a class
	 */
	public String getVariableAddressToken(String MethodName, String VariableName)
	{
		if(this.getMethod().get(MethodName).getLocalList().get(VariableName) != null)
		{
			return this.getMethod().get(MethodName).getLocalList().get(VariableName).getAddressToken();
		}
		
		for(ParamType i : this.getMethod().get(MethodName).getParamList())
		{
			if(i.getName().equals(VariableName))
			{
				return i.getType().getAddressToken();
			}
		}
		
		return this.getVarList().get(VariableName).getAddressToken();
	}

}
