package parser;

import java.util.HashMap;
import java.util.Map.Entry;

import cs132.vapor.ast.VAssign;
import cs132.vapor.ast.VBranch;
import cs132.vapor.ast.VBuiltIn;
import cs132.vapor.ast.VCall;
import cs132.vapor.ast.VCodeLabel;
import cs132.vapor.ast.VFunction;
import cs132.vapor.ast.VGoto;
import cs132.vapor.ast.VInstr.Visitor;
import cs132.vapor.ast.VMemRead;
import cs132.vapor.ast.VMemRef.Global;
import cs132.vapor.ast.VMemRef.Stack;
import cs132.vapor.ast.VMemWrite;
import cs132.vapor.ast.VReturn;

public class instrVisitor extends Visitor<Exception>{

	private VFunction vfunc;
	HashMap<String,Integer> label2instr = new HashMap<String,Integer>();
	
	static int emit_blank = 0;
	private static void emit(String str)
	{
		String blank = new String();
		for(int i = 0; i < emit_blank; i++)
		{
			blank += " ";
		}
		System.out.println(blank + str);
	}
	
	instrVisitor(VFunction vfunction)
	{
		vfunc = vfunction;
		for(VCodeLabel i : vfunc.labels)
		{
			label2instr.put(i.ident,getNextInstructionLine(vfunc.labels,i.sourcePos.line));
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
	
	private void emit_function_tail()
	{
		emit_blank ++;
		emit("lw $ra -4($fp)");
		emit("lw $fp -8($fp)");
		int stack_alloc_space = vfunc.stack.local + vfunc.stack.out;
		emit("addu $sp $sp " + Integer.toString(stack_alloc_space *4 + 8));
		emit("jr $ra");
		emit_blank --;
	}
	
	@Override
	public void visit(VAssign a) throws Exception {
		// TODO Auto-generated method stub
		printLabel(a.sourcePos.line);
		
		emit_blank ++;
		if(a.source.toString().startsWith("$"))
		{
			emit("move " + a.dest.toString() + " " + a.source.toString());
		}
		else if(a.source.toString().startsWith(":"))
		{
			emit("la " + a.dest.toString() + " " + a.source.toString().substring(1));
		}
		else if(isNumeric(a.source.toString()))
		{
			emit("li " + a.dest.toString() + " " + a.source.toString());
		}
		emit_blank --;
	}

	@Override
	public void visit(VCall c) throws Exception {
		// TODO Auto-generated method stub
		printLabel(c.sourcePos.line);
		
		emit_blank ++;
		if(c.addr.toString().startsWith(":"))
		{
			emit("jal " + c.addr.toString().substring(1));
		}
		else
		{
			emit("jalr " + c.addr.toString());
		}
		emit_blank --;
	}

	@Override
	public void visit(VBuiltIn c) throws Exception {
		// TODO Auto-generated method stub
		printLabel(c.sourcePos.line);
		emit_blank ++;
		
        switch (c.op.name)
        {
	        case "Add":
	        	if(c.args[0].toString().startsWith("$"))
	        	{
	        		String first = c.args[0].toString();
	        		if(c.args[1].toString().startsWith("$"))
	        		{
	        			emit("addu " + c.dest + " " + first + " " +c.args[1].toString());
	        		}
	        		else{
	        			emit("li " + "$t9 " + c.args[1].toString());
	        			emit("addu " + c.dest + " " + first + " " + "$t9");
	        		}
	        	}
	        	else{
	        		if(c.args[1].toString().startsWith("$"))
	        		{
	        			emit("li " + "$t9 " + c.args[0].toString());
	        			emit("addu " + c.dest + " " + "$t9" + " " + c.args[1].toString());
	        		}
	        		else{
	        			int temp_sum = Integer.parseInt(c.args[0].toString()) + Integer.parseInt(c.args[1].toString());
	        			emit("li " + c.dest + " "  + Integer.toString(temp_sum));
	        		}
	        	}
	        	break;
	        case "Sub":
	        	if(c.args[0].toString().startsWith("$"))
	        	{
	        		String first = c.args[0].toString();
	        		if(c.args[1].toString().startsWith("$"))
	        		{
	        			emit("subu " + c.dest + " " + first + " " +c.args[1].toString());
	        		}
	        		else{
	        			emit("li " + "$t9 " + c.args[1].toString());
	        			emit("subu " + c.dest + " " + first + " " + "$t9");
	        		}
	        	}
	        	else{
	        		if(c.args[1].toString().startsWith("$"))
	        		{
	        			emit("li " + "$t9 " + c.args[0].toString());
	        			emit("subu " + c.dest + " " + "$t9" + " " + c.args[1].toString());
	        		}
	        		else{
	        			int temp_sub = Integer.parseInt(c.args[0].toString()) - Integer.parseInt(c.args[1].toString());
	        			emit("li " + c.dest + " "  + Integer.toString(temp_sub));
	        		}
	        	}
	        	break;
	        case "MulS":
	        	if(c.args[0].toString().startsWith("$"))
	        	{
	        		String first = c.args[0].toString();
	        		if(c.args[1].toString().startsWith("$"))
	        		{
	        			emit("mul " + c.dest + " " + first + " " +c.args[1].toString());
	        		}
	        		else{
	        			emit("li " + "$t9 " + c.args[1].toString());
	        			emit("mul " + c.dest + " " + first + " " + "$t9");
	        		}
	        	}
	        	else{
	        		if(c.args[1].toString().startsWith("$"))
	        		{
	        			emit("li " + "$t9 " + c.args[0].toString());
	        			emit("mul " + c.dest + " " + "$t9" + " " + c.args[1].toString());
	        		}
	        		else{
	        			int temp_product = Integer.parseInt(c.args[0].toString()) * Integer.parseInt(c.args[1].toString());
	        			emit("li " + c.dest + " "  + Integer.toString(temp_product));
	        		}
	        	}
	        	break;
	        case "Eq":
	        	if(c.args[0].toString().startsWith("$"))
	        	{
	        		String first = c.args[0].toString();
	        		if(c.args[1].toString().startsWith("$"))
	        		{
	        			emit("xor " + c.dest + " " + first + " " +c.args[1].toString());
	        			emit("slti " + c.dest + " " + c.dest + " " + "1");
	        		}
	        		else{
	        			emit("li " + "$t9 " + c.args[1].toString());
	        			emit("xor " + c.dest + " " + first + " " + "$t9");
	        			emit("slti " + c.dest + " " + c.dest + " " + "1");
	        		}
	        	}
	        	else{
	        		if(c.args[1].toString().startsWith("$"))
	        		{
	        			emit("li " + "$t9 " + c.args[0].toString());
	        			emit("xor " + c.dest + " " + "$t9" + " " + c.args[1].toString());
	        			emit("slti " + c.dest + " " + c.dest + " " + "1");
	        		}
	        		else{
	        			int temp_eq = 0;
	        			if(c.args[0].toString().equals(c.args[1].toString()))
	        			{
	        				temp_eq = 1;
	        			}
	        			emit("li " + c.dest + " "  + Integer.toString(temp_eq));
	        		}
	        	}
	        	break;
	        case "Lt":
	        case "LtS":
	        	if(c.args[0].toString().startsWith("$"))
	        	{
	        		String first = c.args[0].toString();
	        		if(c.args[1].toString().startsWith("$"))
	        		{
	        			emit("slt " + c.dest.toString() + " " + first + " " + c.args[1].toString());
	        		}
	        		else{
	        			emit("slti " + c.dest + " " + c.args[0].toString() + " " + c.args[1].toString());
	        		}
	        	}
	        	else{
	        		if(c.args[1].toString().startsWith("$"))
	        		{
	        			emit("li " + "$t9 " + c.args[0].toString());
	        			emit("slt " + c.dest + " " + "$t9" + " " + c.args[1].toString());
	        		}
	        		else{
	        			int temp_lts = 0;
	        			if(new Integer(c.args[0].toString()) < new Integer(c.args[1].toString()))
	        			{
	        				temp_lts = 1;
	        			}
	        			emit("li " + c.dest + " "  + Integer.toString(temp_lts));
	        		}
	        	}
	        	break;
	        case "PrintIntS":
	        	if(c.args[0].toString().startsWith("$"))
	        	{
	        		emit("move " + "$a0" + " " + c.args[0].toString());
	        	}
	        	else
	        	{
	        		emit("li " + "$a0 " + c.args[0].toString());
	        	}
	        	emit("jal _print");
	        	break;
	        case "HeapAllocZ":
	        	if(c.args[0].toString().startsWith("$"))
	        	{
	        		emit("move " + "$a0" + " " + c.args[0].toString());
	        	}
	        	else
	        	{
	        		emit("li " + "$a0 " + c.args[0].toString());
	        	}
	        	emit("jal _heapAlloc");
	        	emit("move " + c.dest.toString() + " " + "$v0");
	        	break;
	        case "Error":
	        	if(c.args[0].toString().startsWith("$"))
	        	{
	        		emit("lw " + "$a0" + " " + "_str0");
	        	}
	        	emit("jal _error");
	        	break;
	        default:
	                throw(new Exception("bad op name at line " + c.sourcePos.line + " col " + c.sourcePos.column));
        }
        emit_blank --;
	}

	@Override
	public void visit(VMemWrite w) throws Exception {
		// TODO Auto-generated method stub
		printLabel(w.sourcePos.line);
		emit_blank ++;
		
		String source = new String();
		
		if(w.source.toString().startsWith("$"))
		{
			source = w.source.toString();
		}
		else if(w.source.toString().startsWith(":"))
		{
			emit("la $t9 " + w.source.toString().substring(1));
			source = "$t9";
		}
		else if(isNumeric(w.source.toString()))
		{
			emit("li $t9 " + w.source.toString());
			source = "$t9";
		}
		
		if(Stack.class.isInstance(w.dest))
		{
			Stack wref = (Stack)w.dest;
			switch(wref.region)
			{
				case Out:
					String out_index = Integer.toString(wref.index * 4);
					emit("sw " + source + " " + out_index + "($sp)");
					break;
				case Local:
					String local_index = Integer.toString(4 * (wref.index + vfunc.stack.out));
					emit("sw " + source + " " + local_index + "($sp)");
					break;
				default:
					break;
			}
		}
		else
		{
			Global wref = (Global)w.dest;
			String offset_s = Integer.toString(wref.byteOffset);
			emit("sw " + source + " " + offset_s + "(" + wref.base.toString() + ")");
		
		}
		emit_blank --;
	}

	@Override
	public void visit(VMemRead r) throws Exception {
		// TODO Auto-generated method stub
		printLabel(r.sourcePos.line);
		emit_blank ++;
		
		if(Stack.class.isInstance(r.source))
		{
			Stack rref = (Stack)r.source;
			switch(rref.region)
			{
				case In:
					String in_index = Integer.toString(rref.index * 4);
					emit("lw " + r.dest.toString() + " " + in_index + "($fp)");
					break;
				case Local:
					String local_index = Integer.toString((rref.index + vfunc.stack.out) * 4);
					emit("lw " + r.dest.toString() + " " + local_index + "($sp)");
					break;
				default:
					System.out.println("????????????");
					break; 
			}
		}
		else
		{
			Global rref = (Global)r.source;
			String offset_r = Integer.toString(rref.byteOffset);
			emit("lw " + r.dest.toString() + " " + offset_r + "(" + rref.base + ")");
		}
		
		emit_blank --;
	}

	@Override
	public void visit(VBranch b) throws Exception {
		// TODO Auto-generated method stub
		printLabel(b.sourcePos.line);
		emit_blank ++;
		String branch_instr = new String();
		if(b.positive){
			branch_instr = "bnez";
		}
		else{
			branch_instr = "beqz";
		}
		emit(branch_instr + " " + b.value + " " + b.target.ident);
		emit_blank --;
	}

	@Override
	public void visit(VGoto g) throws Exception {
		// TODO Auto-generated method stub
		printLabel(g.sourcePos.line);
		emit_blank ++;
		emit("j " + g.target.toString().substring(1));
		emit_blank --;
	}

	@Override
	public void visit(VReturn r) throws Exception {
		// TODO Auto-generated method stub
		printLabel(r.sourcePos.line);
		emit_function_tail();
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
}
