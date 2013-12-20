package Symbol;

public class VarType {
	private String typeName; /* what is the type for this one, like "int" or "boolean"*/
	private String typeInfo; /* it is a local one or not */
	private int lineNumber;
	private String AddressToken = null; /* the address token for a class in ANYWHERE */
	public void set(String name,String info,int LineNumber)
	{
		typeName = name;
		typeInfo = info;
		lineNumber = LineNumber;
	}
	
	/*get the type name*/
	public String get(){return typeName;}
	/*Don't use this!!*/
	public String getInfo(){return typeInfo;}
	public int getLineNumber(){return lineNumber;}
	public boolean typeEqual(VarType varType)
	{
		return this.typeName.equals(varType.get());
	}
	public void setAddress(String token)
	{
		AddressToken = token;
	}
	public String getAddressToken()
	{
		return AddressToken;
	}
}

