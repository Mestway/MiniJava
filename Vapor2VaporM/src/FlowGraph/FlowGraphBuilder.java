package FlowGraph;

import java.util.Map.Entry;

import cs132.vapor.ast.VAddr.Var;
import cs132.vapor.ast.VAssign;
import cs132.vapor.ast.VBranch;
import cs132.vapor.ast.VBuiltIn;
import cs132.vapor.ast.VCall;
import cs132.vapor.ast.VCodeLabel;
import cs132.vapor.ast.VFunction;
import cs132.vapor.ast.VGoto;
import cs132.vapor.ast.VInstr.VisitorP;
import cs132.vapor.ast.VMemRef.Global;
import cs132.vapor.ast.VMemRead;
import cs132.vapor.ast.VMemWrite;
import cs132.vapor.ast.VOperand;
import cs132.vapor.ast.VReturn;

public class FlowGraphBuilder extends VisitorP<Integer,Exception>{

	private FlowGraph flowGraph = new FlowGraph();
	VFunction func = null;
	boolean isLeaf = true;
	
	public void flowgraph_handle()
	{
		flowGraph.graph_handle_liveness();
		flowGraph.add_parameter(func);
	}
	
	public FlowGraphBuilder(VFunction vfunc)
	{
		func = vfunc;
		isLeaf = true;
		flowGraph.isLeaf = isLeaf;
	}

	public FlowGraph get_graph()
	{
		return flowGraph;
	}
	
	public void printGraph()
	{
		System.out.println("\n");
		for(Entry<Integer, GraphNode> i : flowGraph.flowGraph.entrySet())
		{
			System.out.println("instr"+ i.getValue().line + ":  ");
			
			String in = new String();
			for(Integer j : i.getValue().get_inList())
			{
				in += j.toString() + " ";
			}
			
			String out = new String();
			for(Integer j : i.getValue().get_outList())
			{
				out += j.toString() + " ";
			}
			
			String use = new String();
			for(String j : i.getValue().get_use())
			{
				use += j + " ";
			}
			String def = new String();
			for(String j : i.getValue().get_def())
			{
				def += j + " ";
			}
			
			System.out.println("      " + "in: " + in);
			System.out.println("      " + "out: " + out);
			System.out.println("      " + "use: " + use);
			System.out.println("      " + "def: " + def);
			
		}
	}
	
	
	@Override
	public void visit(Integer p, VAssign a) throws Exception {
		// TODO Auto-generated method stub
		
		int line = a.sourcePos.line;
		int nextInstr = getNextInstructionLine(func.labels,line);
		
		flowGraph.get_node(line).add_out(nextInstr);
		flowGraph.get_node(nextInstr).add_in(line);
		flowGraph.get_node(line).add_def(a.dest.toString());
		flowGraph.get_node(line).add_use(a.source.toString());
	}

	@Override
	public void visit(Integer p, VCall c) throws Exception {
		// TODO Auto-generated method stub
		
		isLeaf = false;
		flowGraph.isLeaf = isLeaf;
		
		int line = c.sourcePos.line;
		int nextInstr = getNextInstructionLine(func.labels,line);
		
		flowGraph.get_node(line).add_out(nextInstr);
		flowGraph.get_node(nextInstr).add_in(line);
		
		if(c.dest != null)
		{
			flowGraph.get_node(line).add_def(c.dest.toString());
		}
		
		for(VOperand i : c.args)
		{
			flowGraph.get_node(line).add_use(i.toString());
		}
		
		flowGraph.get_node(line).add_use(c.addr.toString());
		
		/* update parameter-out number in a function */
		flowGraph.update_param_out(c.args.length);
	}

	@Override
	public void visit(Integer p, VBuiltIn c) throws Exception {
		// TODO Auto-generated method stub
		
		int line = c.sourcePos.line;
		int nextInstr = getNextInstructionLine(func.labels,line);
        
        switch (c.op.name)
        {
	        case "Add":
	        case "Sub":
	        case "MulS":
	        case "Eq":
	        case "Lt":
	        case "LtS":
	                flowGraph.get_node(line).add_use(c.args[0].toString());
	                flowGraph.get_node(line).add_use(c.args[1].toString());
	                flowGraph.get_node(line).add_def(c.dest.toString());
	                break;
	        case "PrintIntS":
	                flowGraph.get_node(line).add_use(c.args[0].toString());
	                break;
	        case "HeapAllocZ":
	                flowGraph.get_node(line).add_use(c.args[0].toString());
	                flowGraph.get_node(line).add_def(c.dest.toString());
	                break;
	        case "Error":
	                break;
	        default:
	                throw(new Exception("bad op name at line " + c.sourcePos.line + " col " + c.sourcePos.column));
        }
		
        flowGraph.get_node(line).add_out(nextInstr);
        flowGraph.get_node(nextInstr).add_in(line);
	}

	@Override
	public void visit(Integer p, VMemWrite w) throws Exception {
		// TODO Auto-generated method stub
		int line = w.sourcePos.line;
		int nextInstr = getNextInstructionLine(func.labels,line);
		
		flowGraph.get_node(line).add_use(w.source.toString());
		flowGraph.get_node(line).add_use(((Global)w.dest).base.toString());
		
		flowGraph.get_node(line).add_out(nextInstr);
		flowGraph.get_node(nextInstr).add_in(line);
	}

	@Override
	public void visit(Integer p, VMemRead r) throws Exception {
		// TODO Auto-generated method stub
		int line = r.sourcePos.line;
		int nextInstr = getNextInstructionLine(func.labels,line);
		
		flowGraph.get_node(line).add_use(((Global)r.source).base.toString());
		flowGraph.get_node(line).add_def(r.dest.toString());
		
		flowGraph.get_node(line).add_out(nextInstr);
		flowGraph.get_node(nextInstr).add_in(line);
	}

	@Override
	public void visit(Integer p, VBranch b) throws Exception {
		// TODO Auto-generated method stub
		int line = b.sourcePos.line;
		int nextInstr = getNextInstructionLine(func.labels,line);
		
		flowGraph.get_node(line).add_use(b.value.toString());
		flowGraph.get_node(line).add_out(nextInstr);
		
		
		int nextBranch = getNextInstructionLine(func.labels,b.target.getTarget().sourcePos.line);
		
		flowGraph.get_node(line).add_out(nextBranch);
		flowGraph.get_node(nextInstr).add_in(line);
		flowGraph.get_node(nextBranch).add_in(line);
		
	}

	@Override
	public void visit(Integer p, VGoto g) throws Exception {
		// TODO Auto-generated method stub
		int line = g.sourcePos.line;
		
		VCodeLabel goto_dest = null;
		
		for(VCodeLabel i : func.labels)
		{
			if(g.target.toString().substring(1).equals(i.ident))
			{
				goto_dest = i;
			}
		}
		
		int gotodest = getNextInstructionLine(func.labels,goto_dest.sourcePos.line);
		
		flowGraph.get_node(line).add_out(gotodest);
		flowGraph.get_node(gotodest).add_in(line);
		
	}

	@Override
	public void visit(Integer p, VReturn r) throws Exception {
		// TODO Auto-generated method stub
		
		int line = r.sourcePos.line;
		
		if(r.value != null)
			flowGraph.get_node(line).add_use(r.value.toString());
	}

    private int getNextInstructionLine(VCodeLabel[] labels, int line) 
    {
        int nextLine = line+1;
        int labelIndex = 0;
        while (labelIndex < labels.length && labels[labelIndex].sourcePos.line < nextLine)
        {
                labelIndex++;
        }
        while (labelIndex < labels.length && labels[labelIndex].sourcePos.line == nextLine)
        {
                nextLine++;
                labelIndex++;
        }
        return nextLine;
    }
	
}
