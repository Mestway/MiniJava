package Java2Vapor;

public class VarNType {
	public String varName;
	public String varType;
	VarNType(){};
	VarNType(String Name, String Type)
	{
		set(Name,Type);
	}
	void set(String Name, String Type)
	{
		varName = Name;
		varType = Type;
	}
}
