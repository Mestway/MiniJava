package FlowGraph;

import java.util.HashMap;
import java.util.Map.Entry;

import cs132.vapor.ast.VAssign;
import cs132.vapor.ast.VBranch;
import cs132.vapor.ast.VBuiltIn;
import cs132.vapor.ast.VCall;
import cs132.vapor.ast.VCodeLabel;
import cs132.vapor.ast.VFunction;
import cs132.vapor.ast.VGoto;
import cs132.vapor.ast.VOperand;
import cs132.vapor.ast.VInstr.Visitor;
import cs132.vapor.ast.VMemRef.Global;
import cs132.vapor.ast.VMemRead;
import cs132.vapor.ast.VMemWrite;
import cs132.vapor.ast.VReturn;

public class SecondPass extends Visitor<Exception>{

	static int emit_blank = 0;
	public SecondPassInput input;
	HashMap<String,Integer> label2instr = new HashMap<String,Integer>();
	
	public SecondPass(FlowGraph flowgraph, VFunction vfunction)
	{
		input = new SecondPassInput(flowgraph,vfunction);
	
		for(VCodeLabel i : input.vfunc.labels)
		{
			label2instr.put(i.ident,getNextInstructionLine(input.vfunc.labels,i.sourcePos.line));
		}
	}
	
	public void printLabel(int line)
	{
		for(Entry<String, Integer> i : label2instr.entrySet())
		{
			if(i.getValue().equals(line))
				emit(i.getKey() + ":");
		}
	}
	
	@Override
	public void visit(VAssign a) throws Exception {
		// TODO Auto-generated method stub
		
		printLabel(a.sourcePos.line);
		emit_blank ++;
		
		try{
			emit(input.get_reg(a.dest.toString()) + " = " + input.get_reg(a.source.toString()));
		}
		catch(Exception e)
		{
			
		}
		emit_blank --;
	}

	@Override
	public void visit(VCall c) throws Exception {
		// TODO Auto-generated method stub
		printLabel(c.sourcePos.line);
		int t = 0;
		for(VOperand i : c.args)
		{	
			String left = new String();
			if(t < 4)
				left = "$a" + new Integer(t).toString();
			else
				left = "out[" + new Integer(t-4).toString() + "]";
				
			t++;
			
			//args += " " + i.toString();
			emit_blank ++;
			emit(left + " = " + input.get_reg(i.toString()));
			emit_blank --;
		}
		
		emit_blank++;
		String emit_str = new String();
		
		emit("call " + input.get_reg(c.addr.toString()));
		
		//System.out.println(">>> " + c.dest);
		
		try{
			if(c.dest != null)
				emit(input.get_reg(c.dest.ident) + " = " + "$v0");
		}
		catch(Exception e)
		{

		}
		emit_blank--;
	}

	@Override
	public void visit(VBuiltIn c) throws Exception {
		// TODO Auto-generated method stub
		
		printLabel(c.sourcePos.line);
		
		String args = new String();
		int localnum = 0;
		
		emit_blank ++;
		
		boolean first = true;
		for(VOperand i : c.args)
		{	
			if(first)
			{
				first = false;
			}
			else{
				args += " ";
			}
			String temparg = input.get_reg(i.toString());
			if(temparg.startsWith("local"))
			{
				emit("$v" + Integer.toString(localnum) + " = " + temparg);
				temparg = "$v" + Integer.toString(localnum);
				localnum ++;
			}
			args += temparg;
		}
		
		String left = new String();
		if(c.dest != null)
			left = input.get_reg(c.dest.toString()) + " = ";
		

		emit( left  + c.op.name + "(" + args + ")");
		emit_blank --;
	}

	@Override
	public void visit(VMemWrite w) throws Exception {
		// TODO Auto-generated method stub
		printLabel(w.sourcePos.line);
		
		String offset = new String();
		if(((Global)w.dest).byteOffset >=0)
		{
			offset = " + "  + new Integer(((Global)w.dest).byteOffset).toString();
		}
		
		String base = input.get_reg(((Global)w.dest).base.toString());
		if(base.startsWith("local"))
		{
			emit("$v0 =" + base);
			base = "$v0";
		}
		
		emit_blank ++;
		emit("[" + base + offset + "]" + "=" + input.get_reg(w.source.toString()));
		emit_blank --;
	}

	@Override
	public void visit(VMemRead r) throws Exception {
		// TODO Auto-generated method stub
		printLabel(r.sourcePos.line);
		
		emit_blank ++;
		
		String offset = new String();
		if(((Global)r.source).byteOffset >=0)
		{
			offset = " + "  + new Integer(((Global)r.source).byteOffset).toString();
		}
		
		String base = input.get_reg(((Global)r.source).base.toString());
		if(base.startsWith("local"))
		{
			emit("$v0 =" + base);
			base = "$v0";
		}
		
		emit(input.get_reg(r.dest.toString()) + "=" + "[" + base + offset +"]");
		emit_blank--;
	}

	@Override
	public void visit(VBranch b) throws Exception {
		// TODO Auto-generated method stub
		printLabel(b.sourcePos.line);
		
		String head = "if";
		if(!b.positive)
			head = "if0";
		
		String getval = input.get_reg(b.value.toString());
		
		emit_blank ++;
		if(getval.startsWith("local"))
		{
			emit("$v1 = " + getval);
			getval = "$v1";
		}
		
		
		emit(head + " " + getval + " goto " + ":" + b.target.getTarget().ident);
		emit_blank--;
	}

	@Override
	public void visit(VGoto g) throws Exception {
		// TODO Auto-generated method stub
		printLabel(g.sourcePos.line);
		
		emit_blank ++;
		emit("goto " + g.target);
		emit_blank --;
	}

	@Override
	public void visit(VReturn r) throws Exception {
		// TODO Auto-generated method stub
		printLabel(r.sourcePos.line);
		
		String retval = new String();
		
		if(r.value != null)
		{	
			retval = r.value.toString();
			retval = input.get_reg(retval);
			emit("$v0 = " + retval);
		}
		emit_blank ++;
		emit(input.retsave);
		emit("ret");
		emit_blank --;
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
    
	public static void emit(String str)
	{		
		String blank = "";
		for(int i = 0; i < emit_blank; i++)
		{
			blank += " ";
		}
			
		System.out.println(blank + str);
	}
}
