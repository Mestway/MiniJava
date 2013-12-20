package parser;

import cs132.vapor.ast.VAssign;
import cs132.vapor.ast.VBranch;
import cs132.vapor.ast.VBuiltIn;
import cs132.vapor.ast.VCall;
import cs132.vapor.ast.VCodeLabel;
import cs132.vapor.ast.VFunction;
import cs132.vapor.ast.VGoto;
import cs132.vapor.ast.VMemRef.Global;
import cs132.vapor.ast.VOperand;
import cs132.vapor.ast.VInstr.Visitor;
import cs132.vapor.ast.VInstr.VisitorP;
import cs132.vapor.ast.VMemRead;
import cs132.vapor.ast.VMemWrite;
import cs132.vapor.ast.VReturn;
import cs132.vapor.ast.VVarRef.Local;

public class PrintVisitor extends VisitorP<Integer, Exception>{

	VFunction func;
	
	public void init_func(VFunction vfunc)
	{
		func = vfunc;
	}
	
	public void printLabels()
	{
		for(VCodeLabel i : func.labels)
		{
			System.out.println("label:  "+ i.ident + " " + i.sourcePos.line);
		}
	}
	
	public void print_params()
	{
		String params = "params:  ";
		for(Local i : func.params)
		{
			params += i.ident + ' ';
		}
		System.out.println(params);
	}
	
	@Override
	public void visit(Integer p, VAssign a) throws Exception {
		// TODO Auto-generated method stub
		System.out.println(a.sourcePos.line + ":  " + a.dest + "=" + a.source);
		
	}

	@Override
	public void visit(Integer p, VCall c) throws Exception {
		// TODO Auto-generated method stub
		String args = new String();
		
		for(VOperand i : c.args)
			args += " " + i.toString();
		
		//System.out.println(c.dest + "=" + c.op.name + ' ' + args);
		
		System.out.println(c.sourcePos.line + ":  " + c.dest + "=" + c.addr + " " + args);
	}

	@Override
	public void visit(Integer p, VBuiltIn c) throws Exception {
		// TODO Auto-generated method stub
		String args = new String();
		
		for(VOperand i : c.args)
			args += " " + i.toString();
		
		System.out.println(c.sourcePos.line + ":  " + c.dest + "=" + c.op.name + ' ' + args);
	}

	@Override
	public void visit(Integer p, VMemWrite w) throws Exception {
		// TODO Auto-generated method stub
		System.out.println(w.sourcePos.line + ":  "+ "[" + ((Global)w.dest).base+ "]" + "=" + w.source);
	}

	@Override
	public void visit(Integer p, VMemRead r) throws Exception {
		// TODO Auto-generated method stub
		System.out.println(r.sourcePos.line + ":  " + r.dest + "=" + "[" + ((Global)r.source).base + "]");
		
	}

	@Override
	public void visit(Integer p, VBranch b) throws Exception {
		// TODO Auto-generated method stub
		System.out.println(b.sourcePos.line + ":  branch  " + b.value + " " + b.target.getTarget().sourcePos.line+ " # " + b.target.getTarget().ident + " line:" + getNextInstructionLine(func.labels,b.target.getTarget().sourcePos.line));
		
	}

	@Override
	public void visit(Integer p, VGoto g) throws Exception {
		// TODO Auto-generated method stub
		
		VCodeLabel goto_dest = null;
		
		for(VCodeLabel i : func.labels)
		{
			if(g.target.toString().substring(1).equals(i.ident))
			{
				goto_dest = i;
			}
		}
		
		System.out.println(g.sourcePos.line + ":  goto  " + g.target + " line:" + getNextInstructionLine(func.labels,goto_dest.sourcePos.line));
	}

	@Override
	public void visit(Integer p, VReturn r) throws Exception {
		// TODO Auto-generated method stub
		System.out.println(r.sourcePos.line + ":  " + "ret"  + " " + r.value);
	}
	
    private int getNextInstructionLine(VCodeLabel[] labels, int line) {
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
