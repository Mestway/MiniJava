package parser;

import java.io.*;
import java.util.ArrayList;

import FlowGraph.FlowGraphBuilder;
import FlowGraph.SecondPass;
import FlowGraph.SecondPassInput;
import cs132.util.ProblemException;
import cs132.vapor.ast.VBuiltIn.Op;
import cs132.vapor.ast.VCodeLabel;
import cs132.vapor.ast.VDataSegment;
import cs132.vapor.ast.VFunction;
import cs132.vapor.ast.VInstr;
import cs132.vapor.ast.VOperand;
import cs132.vapor.ast.VVarRef;
import cs132.vapor.ast.VaporProgram;
import cs132.vapor.parser.VaporParser;


public class V2VM {
	static boolean IF_PRINT_VISITOR = false;
	static int emit_blank = 0;
	
	public static void emit(String str)
	{		
		String blank = "";
		for(int i = 0; i < emit_blank; i++)
		{
			blank += " ";
		}
			
		System.out.println(blank + str);
	}
	
	public static VaporProgram parseVapor(InputStream in, PrintStream err) throws Exception
	{
		Op[] ops = {Op.Add, Op.Sub, Op.MulS, Op.Eq, Op.Lt, Op.LtS,
		Op.PrintIntS, Op.HeapAllocZ, Op.Error};
		boolean allowLocals = true;
		String[] registers = null;
		boolean allowStack = false;
		
		VaporProgram program;
		try 
		{
			program = VaporParser.run(new InputStreamReader(in), 1, 1,
					java.util.Arrays.asList(ops),
					allowLocals, registers, allowStack);
			
			parseHead(program.dataSegments);
			functionHandler(program.functions);
		} 
		catch (ProblemException ex) 
		{
			err.println(ex.getMessage());
			return null;
		}

		return program;
	}
	
	public static void parseHead(VDataSegment[] datasegments)
	{
		for(VDataSegment dataseg : datasegments)
		{
			String mutable = "const";
			if(dataseg.mutable == true)
				mutable = "var";
				
			emit(mutable + " " + dataseg.ident);
			
			emit_blank++;
			for(VOperand val : dataseg.values)
			{
				emit(val.toString());
			}
			emit_blank--;
		}
		emit("");
	}

	
	public static void main(String args[]) throws Exception
	{
		InputStream inputStream;
		try 
		{
			inputStream = System.in;
			PrintStream errorStream = System.err;
			VaporProgram program = parseVapor(inputStream, errorStream);
		} 
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void functionHandler(VFunction[] funcs) throws Exception
	{	
		for(VFunction func : funcs)
		{
			ArrayList<VInstr> instrArray = new ArrayList<VInstr>();
			
			if(IF_PRINT_VISITOR)
			{
				/** Print Code **/
				int n = 0;
				PrintVisitor pv = new PrintVisitor();
				pv.init_func(func);
				pv.printLabels();
				pv.print_params();
				for(VInstr j : func.body)
				{
					instrArray.add(j);
					j.accept(new Integer(n), pv);
					n++;
				}
			}
			
			FlowGraphBuilder fgb = new FlowGraphBuilder(func);
			for(VInstr j : func.body)
			{
				j.accept(new Integer(0), fgb);			
			}
			//fgb.printGraph();
			fgb.get_graph().graph_handle_liveness();
			fgb.get_graph().add_parameter(func);
			
			SecondPass spi = new SecondPass(fgb.get_graph(),func);
			spi.input.EmitFunctionHead();
			for(VInstr j : func.body)
			{
				j.accept(spi);			
			}
			
		}
	}
}
