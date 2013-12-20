package FlowGraph;

import java.util.ArrayList;

public class GraphNode {
	int line = 0;
	
	/* graph representing by predecessor and successor */
	private ArrayList<Integer> inList = new ArrayList<Integer>();
	private ArrayList<Integer> outList = new ArrayList<Integer>();
	
	
	/* information about use and def of variables */
	private ArrayList<String> use = new ArrayList<String>();
	private ArrayList<String> def = new ArrayList<String>();
	
	/* for live-analysis */
	public ArrayList<String> live_in = new ArrayList<String>();
	public ArrayList<String> live_out = new ArrayList<String>();
	
	
	void add_use(String variable)
	{
		if(!isNumeric(variable) && !isLabelReference(variable))
			use.add(variable);
	}
	void add_def(String variable)
	{
		if(!isNumeric(variable) && !isLabelReference(variable))
			def.add(variable);
	}
	ArrayList<String> get_use()
	{	
		return use;
	}
	ArrayList<String> get_def()
	{
		return def;
	}
	ArrayList<Integer> get_inList()
	{
		return inList;
	}
	ArrayList<Integer> get_outList()
	{
		return outList;
	}
	GraphNode(int lineNo)
	{
		line = lineNo;
	}
	void add_in(int inNo)
	{
		inList.add(inNo);
	}
	void add_out(int outNo)
	{
		outList.add(outNo);
	}
	boolean check_inList(int num)
	{
		if(inList.contains(num))
			return true;
		else return false;
	}
	boolean check_outList(int num)
	{
		if(outList.contains(num))
			return true;
	
		else return false;
	}
    private boolean isNumeric(String str)
    {
        try
        {
        	int i = Integer.parseInt(str);
        } 
        catch(NumberFormatException nfe)
        {
                return false;
        }
        
        return true;
    }
    private boolean isLabelReference(String str)
    {
    	if(str.startsWith(":"))
    	{
    		return true;
    	}
    	else return false;
    }
}
