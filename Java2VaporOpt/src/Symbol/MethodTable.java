package Symbol;

import java.util.ArrayList;
import java.util.HashMap;

public class MethodTable implements Table{
	private String retType;
	private int lineNumber;
	//private HashMap<String,VarType> ParamList = new HashMap<String,VarType>();
	private ArrayList<ParamType> ParamList = new ArrayList<ParamType>();
	private HashMap<String,VarType> LocalList = new HashMap<String,VarType>();
	
	public boolean isLocal(String VarName)
	{
		for(ParamType i : ParamList)
		{
			if(i.getName().equals(VarName))
				return true;
		}
		
		if(LocalList.containsKey(VarName))
		{
			return true;
		}
		
		return false;
	}
	public void put(String key, Object value)
	{
		if(((VarType)value).getInfo().equals("Parameter"))
			ParamList.add(new ParamType(key, (VarType)value));
		else if(((VarType)value).getInfo().equals("Variable"))
			LocalList.put(key, (VarType)value);
	}
	public void set(String returnType, int LineNumber)
	{
		retType = returnType;
		lineNumber = LineNumber;
	}
	public String getRetType()
	{
		return retType;
	}
	public int getLineNumber()
	{
		return lineNumber;
	}
	public ArrayList<ParamType> getParamList()
	{
		return ParamList;
	}
	public HashMap<String,VarType> getLocalList()
	{
		return LocalList;
	}
	public boolean setAddr(String VariableName, String AddrToken)
	{
		if(this.LocalList.get(VariableName) != null)
		{
			this.LocalList.get(VariableName).setAddress(AddrToken);
			return true;
		}
		
		for(ParamType i : this.ParamList)
		{
			if(i.getName().equals(VariableName))
			{
				i.getType().setAddress(AddrToken);
				return true;
			}
		}
		return false;
	}
}

