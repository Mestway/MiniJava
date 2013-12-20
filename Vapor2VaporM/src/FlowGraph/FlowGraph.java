package FlowGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import cs132.vapor.ast.VFunction;
import cs132.vapor.ast.VVarRef.Local;

public class FlowGraph {
	
	/* for print remove info */
	boolean IF_DEBUG = false;
	
	int instr_num;
	HashMap<Integer,GraphNode> flowGraph = new HashMap<Integer,GraphNode>();
	HashMap<String, LiveInterval> variableList = new HashMap<String, LiveInterval>();
	ArrayList<String> parameterList = new ArrayList<String>();
	
	/* if the function is leaf node */
	boolean isLeaf;
	
	/* Use for sort of the nodes*/
	int sort_num = 0;
	HashMap<Integer,Boolean> visited = new HashMap<Integer,Boolean>();
	Integer[] sorted = null;
	
	/* temp store for live-in and live-out */
	HashMap<Integer,ArrayList<String>> temp_livein = new HashMap<Integer,ArrayList<String>>();
	HashMap<Integer,ArrayList<String>> temp_liveout = new HashMap<Integer,ArrayList<String>>();
	
	/* Parameter Handle */
	int parameterNum = 0;
	int param_in = 0;
	int param_out = 0; //not subtracted by 4
	
	/* Register allocation part*/
	ArrayList<LiveInterval> interval_list = new ArrayList<LiveInterval>();
	ArrayList<String> reg_free = new ArrayList<String>();
	ArrayList<String> reg_alloc = new ArrayList<String>();
	ArrayList<LiveInterval> interval_active = new ArrayList<LiveInterval>();
	int local_num = 0;
	
    //private static String[] nonleafRegisters = {"$s0", "$s1"};
    //private static String[] leafRegisters = {"$t0", "$t1"};
    private static String[] leafRegisters = {"$t0", "$t1", "$t2", "$t3", "$t4", "$t5", "$t6", "$t7", "$t8"};
    private static String[] nonleafRegisters = {"$s0", "$s1", "$s2", "$s3", "$s4", "$s5", "$s6", "$s7"};
	private String[] register_assign;
	HashMap<String,String> var_2_register = new HashMap<String,String>();
    
    
	public void graph_handle_liveness()
	{
		sort_node();
		//print_sorted();
		liveness_calc();
		//print_liveness();
		count_live_interval();
		//print_live_interval();
		
		linear_scan_register_allocation();
		
		//print_reg_assign();
	}
	
	
	public void print_reg_assign()
	{
		System.out.println("result:");
		for(Entry<String,String> i : var_2_register.entrySet())
		{
			System.out.println(i.getKey() + " --> " + i.getValue());
		}
	}
	
	public void linear_scan_register_allocation()
	{
		if(isLeaf)
		{
			register_assign = leafRegisters;
		}
		else
		{
			register_assign = nonleafRegisters;
		}
		
		for(int i = 0; i < register_assign.length; i++)
		{
			reg_free.add(register_assign[i]);
		}
		
		for(Entry<String,LiveInterval> i : variableList.entrySet())
		{
			interval_list.add(i.getValue());
			var_2_register.put(i.getKey(), null);
		}
		
		Collections.sort(interval_list, LiveInterval.RangeComparator);
	
		for(LiveInterval i :  interval_list)
		{
			expire_old_intervals(i);
			
			if(interval_active.size() == register_assign.length)
			{
				spill_at_interval(i);
			}
			else
			{
				var_2_register.remove(i.variableName);
				var_2_register.put(i.variableName, reg_free.get(0));
				reg_free.remove(0);
				interval_active.add(new LiveInterval(i));
				Collections.sort(interval_active,LiveInterval.ActiveComparator);
				/*System.out.println("+++++++++++++++++++++++");
				for(LiveInterval t : interval_active)
				{
					System.out.println("**->" + ' ' + t.variableName + "["+t.get_left()+","+t.get_right()+"]");
				}*/
			}
		}
	}
	private void expire_old_intervals(LiveInterval i)
	{
		ArrayList<LiveInterval> removeList = new ArrayList<LiveInterval>();
		
		for(int j = 0; j < interval_active.size(); j++)
		{	
			if(interval_active.get(j).get_right() >= i.get_left())
			{
				break;
			}
			removeList.add(interval_active.get(j));
			reg_free.add(0,var_2_register.get(interval_active.get(j).variableName));
		}
		
		
		/* DEBUG INFO */
		if(IF_DEBUG)
		{	
			System.out.println("Before:");
			for(LiveInterval t : interval_active)
			{
				t.print();
			}
		}
		for(LiveInterval j : removeList)
		{
			if(IF_DEBUG){
				System.out.print("This remove:");
				j.print();
			}
			interval_active.remove(j);
		}
		
		if(IF_DEBUG)
		{
			System.out.println("After:");
			for(LiveInterval t : interval_active)
			{
				t.print();
			}
		}
	}
	
	private void spill_at_interval(LiveInterval i)
	{
		LiveInterval spill = interval_active.get(interval_active.size()-1);
		
		if(spill.get_right() > i.get_right())
		{
			var_2_register.remove(i.variableName);
			var_2_register.put(i.variableName, var_2_register.get(spill.variableName));
			
			var_2_register.remove(spill.variableName);
			var_2_register.put(spill.variableName, new Integer(local_num).toString());
			local_num ++;
			
			interval_active.remove(spill);
			interval_active.add(new LiveInterval(i));
			Collections.sort(interval_active,LiveInterval.ActiveComparator);
		}
		else
		{
			var_2_register.put(i.variableName, new Integer(local_num).toString());
			local_num++;
		}
	}
	
	public void add_node(GraphNode gn)
	{
		flowGraph.put(gn.line, gn);
	}
	GraphNode get_node(int lineNo)
	{
		if(flowGraph.containsKey(lineNo))
			return flowGraph.get(lineNo);
		else
		{
			flowGraph.put(lineNo, new GraphNode(lineNo));
			return flowGraph.get(lineNo);
		}
	}
	
	/* sort node for liveness calculation by DFS*/
	public void sort_node()
	{
		instr_num = flowGraph.size();
		sort_num = instr_num;
		
		sorted = new Integer[instr_num + 1];
		Integer startNode = Integer.MAX_VALUE;
		
		for(Entry<Integer, GraphNode> i : flowGraph.entrySet())
		{
			visited.put(i.getKey(), false);
			if(i.getKey() < startNode)
			{
				startNode = i.getKey();
			}
		}
		
		DFS(startNode);
	}
	
	private void DFS(Integer node)
	{
		if(visited.get(node).equals(false))
		{
			visited.remove(node);
			visited.put(node, true);
			
			for(Integer i : flowGraph.get(node).get_outList())
			{
				
				DFS(i);
			}

			sorted[sort_num] = node;
			sort_num --;
		}
	}
	
	public void print_sorted()
	{
		System.out.println("Sorted Ans:");
		for(Integer i = 1; i <= instr_num; i++)
		{
			System.out.println(i.toString() + " : " + sorted[i]);
		}
	}
	public void print_liveness()
	{
		System.out.println("\n");
		for(Entry<Integer, GraphNode> i : flowGraph.entrySet())
		{
			System.out.println("instr"+ i.getValue().line + ":  ");
			
			String livein = new String();
			for(String j : i.getValue().live_in)
			{
				livein += j.toString() + " ";
			}
			
			String liveout = new String();
			for(String j : i.getValue().live_out)
			{
				liveout += j.toString() + " ";
			}

			System.out.println("      " + "livein: " + livein);
			System.out.println("      " + "liveout: " + liveout);
		}
	}
	
	/* calculate liveness by iteration */
	public void liveness_calc()
	{
		boolean converge = false;
		for(int i = instr_num; i >= 1; i--)
		{
			temp_livein.put(new Integer(sorted[i]), new ArrayList<String>());
			temp_liveout.put(new Integer(sorted[i]), new ArrayList<String>());
		}
		
		while(!converge)
		{
			for(int i = instr_num; i >= 1; i--)
			{
				GraphNode temp = flowGraph.get(sorted[i]);
				
				ArrayListCopy(temp_livein.get(sorted[i]), temp.live_in);
				ArrayListCopy(temp_liveout.get(sorted[i]), temp.live_out);
				
				for(String j : temp.get_use())
				{
					if(!temp.live_in.contains(j))
						temp.live_in.add(j);
				}
				for(String j : temp.live_out)
				{
					if(!temp.get_def().contains(j) && !temp.live_in.contains(j))
					{
						temp.live_in.add(j);
					}
				}
				
				for(Integer k : temp.get_outList())
				{
					GraphNode tempNode = flowGraph.get(k);
					for(String t : tempNode.live_in)
					{
						if(! temp.live_out.contains(t))
						{
							temp.live_out.add(t);
						}
					}
				}
			}
			
			converge = check_converge();
		}
	}
	
	private void ArrayListCopy(ArrayList<String> dest,ArrayList<String> src)
	{
		dest.clear();
		for(String i : src)
		{
			dest.add(i);
		}
	}
	
	private boolean check_converge()
	{
		for(int i = instr_num; i >= 1; i--)
		{
			if(flowGraph.get(sorted[i]).live_in.size() != temp_livein.get(sorted[i]).size())
			{
				return false;
			}
			for(String j : flowGraph.get(sorted[i]).live_in)
			{
				if(!temp_livein.get(sorted[i]).contains(j))
				{
					return false;
				}
			}
			if(flowGraph.get(sorted[i]).live_out.size() != temp_liveout.get(sorted[i]).size())
			{
				return false;
			}
			for(String j : flowGraph.get(sorted[i]).live_out)
			{
				if(!temp_liveout.get(sorted[i]).contains(j))
				{
					return false;
				}
			}
		}
		return true;
	}
	
	/* count live intervals */
	public void count_live_interval()
	{
		for(int i = 1; i <= instr_num; i++)
		{
			for(String str : flowGraph.get(sorted[i]).live_in)
			{
				update_interval(str,i,i);
			}
			for(String str : flowGraph.get(sorted[i]).live_out)
			{
				update_interval(str,i,i);
			}

		}
	}
	private void update_interval(String varName, int begin, int end)
	{
		if(!variableList.containsKey(varName))
		{
			variableList.put(varName, new LiveInterval(varName,begin,end));
			return;
		}
		else
		{
			if(variableList.get(varName).get_right() < end)
			{
				variableList.get(varName).set_right(end);
			}
		}
	}
	public void print_live_interval()
	{
		for(Entry<String,LiveInterval> i : variableList.entrySet())
		{
			System.out.println(i.getKey() + ": " + "["+ i.getValue().get_left() + "," + i.getValue().get_right() + "]");
		}
	}
	
	/* count parameter number in this graph , for in[] */
	public void add_parameter(VFunction func)
	{
		for(Local i : func.params)
		{
			parameterList.add(i.ident);
		}
		parameterNum = parameterList.size();
		
		if(parameterNum > 4)
			param_in = parameterNum - 4;
	}
	/* count parameter-out number */
	public void update_param_out(int number)
	{
		if(number > param_out)
			param_out = number;
	}
}
