package parser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import cs132.util.ProblemException;
import cs132.vapor.ast.VBuiltIn.Op;
import cs132.vapor.ast.VDataSegment;
import cs132.vapor.ast.VFunction;
import cs132.vapor.ast.VInstr;
import cs132.vapor.ast.VOperand.Static;
import cs132.vapor.ast.VVarRef.Local;
import cs132.vapor.ast.VaporProgram;
import cs132.vapor.parser.VaporParser;


public class VM2MIPS 
{
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
	
    public static VaporProgram parseVapor(InputStream in, PrintStream err) throws IOException
    {
    	Op[] ops = {Op.Add, Op.Sub, Op.MulS, Op.Eq, Op.Lt, Op.LtS, Op.PrintIntS, Op.HeapAllocZ, Op.Error};
    	boolean allowLocals = false;
    	String[] registers = {"v0", "v1","a0", "a1", "a2", "a3",
         "t0", "t1", "t2", "t3", "t4", "t5", "t6", "t7",
         "s0", "s1", "s2", "s3", "s4", "s5", "s6", "s7","t8"};
        
    	boolean allowStack = true;

        VaporProgram program;
        try 
        {
        	program = VaporParser.run(new InputStreamReader(in), 1, 1, java.util.Arrays.asList(ops), allowLocals, registers, allowStack);
        } 
        catch (ProblemException ex) 
        {
        	err.println(ex.getMessage());
        	return null;
        }
        
        return program;
    }
    
    public static void main(String args[])
    {
    	InputStream inputStream;
    	try 
    	{
    		inputStream = System.in;
    		PrintStream errorStream = System.err;
    		VaporProgram program = parseVapor(inputStream, errorStream);
                    
    		DataHandler(program.dataSegments);
    		TextHandler(program.functions);
            emit_tail_info();
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
    
    private static void DataHandler(VDataSegment[] vdatasegments)
    {
		emit(".data");
    	for(VDataSegment dataseg : vdatasegments)
    	{
    		emit(dataseg.ident + ":");
    		for(Static i : dataseg.values)
    		{
    			emit_blank ++;
    			emit(i.toString().substring(1, i.toString().length()));
    			emit_blank --;
    		}
			emit("");
    	}
    	emit("");
    }
    
    private static void TextHandler(VFunction[] vfunctions)
    {
    	emit(".text\n");
    	
    	emit_blank ++;
    	emit("jal Main");
    	emit("li $v0 10");
    	emit("syscall");
    	emit("");
    	emit_blank --;
    	
    	for(VFunction vfunc : vfunctions)
    	{
            emit(vfunc.ident + ":"); //function name
    		
            emit_blank ++;
            emit("sw $fp -8($sp)"); //store frame pointer
            emit("move $fp $sp"); //set new frame pointer
            int stack_alloc_num = vfunc.stack.local + vfunc.stack.out;
            emit("subu $sp $sp " + Integer.toString(8 + 4 * stack_alloc_num)); //allocate stack space
            emit("sw $ra -4($fp)");//store return address
            emit_blank --;
            
            instrVisitor iv = new instrVisitor(vfunc);
            for(VInstr instr : vfunc.body)
            {
            	try {
					instr.accept(iv);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
    	
            emit("");
    	}
    }
    
    private static void emit_print()
    {
    	emit("_print:");
    	emit_blank ++;
    	emit("li $v0 1   # syscall: print integer");
    	emit("syscall");
    	emit("la $a0 _newline");
    	emit("li $v0 4   # syscall: print string");
    	emit("syscall");
    	emit("jr $ra");
    	emit_blank --;
    	emit("");
    }
    
    private static void emit_error()
    {
    	emit("_error:");
    	emit_blank ++;
    	emit("li $v0 4   # syscall: print string");
    	emit("syscall");
    	emit("li $v0 10   # syscall: exit");
    	emit("syscall");
    	emit_blank --;
    	emit("");
    }
    
    private static void emit_heapAlloc()
    {
    	emit("_heapAlloc:");
    	emit_blank ++;
    	emit("li $v0 9   # syscall: sbrk");
    	emit("syscall");
    	emit("jr $ra");
    	emit_blank --;
    	emit("");
    }
    
    private static void emit_tail_info()
    {
    	emit_print();
    	emit_error();
    	emit_heapAlloc();
    	emit(".data");
    	emit(".align 0");
    	emit("_newline: .asciiz \"\\n\"");
    	emit("_str0: .asciiz \"null pointer\\n\"");
    }
    
}
