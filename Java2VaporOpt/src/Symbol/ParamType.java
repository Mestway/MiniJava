package Symbol;

public class ParamType{
	private String ParamName;
	private VarType varType;
	ParamType(String pName,VarType vType)
	{
		ParamName = pName;
		varType = vType;
	}
	public String getName()
	{
		return ParamName;
	}
	public VarType getType()
	{
		return varType;
	}

}

