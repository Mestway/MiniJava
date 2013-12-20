package FlowGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import cs132.vapor.ast.VFunction;
import cs132.vapor.ast.VVarRef.Local;

public class SecondPassInput {
	
	private FlowGraph flowGraph = new FlowGraph();
	public VFunction vfunc = null;
	public HashMap<String,Boolean> usedreg = new HashMap<String,Boolean>();
	private int callersave = 0;
	public String retsave = new String();
		
	/* for code emit*/
	static int emit_blank = 0;
	
	public String get_reg(String var)
	{
		if(isLabelReference(var) || isNumeric(var))
		{
			return var;
		}
		
		if(var.startsWith("\""))
		{
			return var;
		}
		
		String reg = flowGraph.var_2_register.get(var);
		
		if(!reg.startsWith("$"))
		{
			return "local[" + new Integer(callersave + Integer.parseInt(reg)) + "]";
		}
		else return reg;
	}
	
	public SecondPassInput(FlowGraph flowgraph, VFunction vfunction)
	{
		set_graph(flowgraph);
		set_VFunction(vfunction);
		ArrayList<String> arr = new ArrayList<String>();
		
		for(Entry<String,String> str : flowGraph.var_2_register.entrySet())
		{
			if(str.getValue().startsWith("$s") && !usedreg.containsKey(str.getValue()))
				usedreg.put(str.getValue(), new Boolean(true));
		}

		callersave = usedreg.size();
	}
	public void set_graph(FlowGraph fg)
	{
		flowGraph = fg;
	}
	public void set_VFunction(VFunction vf)
	{
		vfunc = vf;
	}
	
	public void EmitFunctionHead()
	{
		String params = "[";
		String in = "in " + new Integer(flowGraph.param_in).toString();
		
		int outnum = 0;
		
		if(flowGraph.param_out > 4)
			outnum = flowGraph.param_out - 4;
		String out = "out " + new Integer(outnum).toString();
		String local = "local " + new Integer(flowGraph.local_num + callersave).toString();
		
		params = "[" + in + "," + out + "," + local + "]";
		
		emit("func " + vfunc.ident + params);
		emit_blank ++;

		int localsave = 0;
		for(Entry<String,Boolean> i : usedreg.entrySet())
		{
			emit("local[" + new Integer(localsave).toString() + "]" + " = " + i.getKey());
			retsave += " " + i.getKey() + " = " + "local[" + new Integer(localsave).toString() + "]\n";
			localsave ++;
		}
		emit_blank--;
		
		int t = 0;
		for(Local i : vfunc.params)
		{
			String right = new String();
			if(t < 4)
				right = "$a" + new Integer(t);
			else
				right = "in[" + new Integer(t-4) + "]";
				
			emit_blank++;
			
			try{
				emit(get_reg(i.ident) + " = " + right);
			}
			catch(Exception e)
			{
				
			}
			//params += i.ident + ' ';
			emit_blank--;
			t++;
		}
	}
	
	public static void emit(String str)
	{		
		String blank = "";
		for(int i = 0; i < emit_blank; i++)
		{
			blank += " ";
		}
			
		System.out.println(blank + str);
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
