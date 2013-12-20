package UsrVisitor;

import java.util.Map.Entry;
import java.util.Stack;

import type.syntaxtree.ArrayType;
import type.syntaxtree.BooleanType;
import type.syntaxtree.ClassDeclaration;
import type.syntaxtree.ClassExtendsDeclaration;
import type.syntaxtree.FormalParameter;
import type.syntaxtree.Goal;
import type.syntaxtree.Identifier;
import type.syntaxtree.IntegerType;
import type.syntaxtree.MainClass;
import type.syntaxtree.MethodDeclaration;
import type.syntaxtree.Type;
import type.syntaxtree.VarDeclaration;
import type.visitor.DepthFirstRetVisitor;
import Symbol.ClassTable;
import Symbol.GoalTable;
import Symbol.MethodTable;
import Symbol.ParamType;
import Symbol.Table;
import Symbol.VarType;

public class MyVisitor extends DepthFirstRetVisitor<String>{
	public GoalTable SymbolTable = new GoalTable();
	public Table currentTable;
	public Stack<Table> stk = new Stack<Table>();
	
	public void printSymbolTable()
	{
		for(Entry<String, ClassTable> i : SymbolTable.getClassList().entrySet()) 
		{
			System.out.println(i.getKey() + "@" + i.getValue().getLineNumber());
			for(Entry<String, VarType> j : i.getValue().getVarList().entrySet())
			{
				System.out.println("  " + j.getKey() + " " + j.getValue().get() + '@' + j.getValue().getInfo() + '@' + j.getValue().getLineNumber());
			}
			for(Entry<String, MethodTable> j : i.getValue().getMethod().entrySet())
			{
				System.out.println("  " + j.getValue().getRetType() + ' ' + j.getKey() + "@" + j.getValue().getLineNumber());
				for(ParamType k : j.getValue().getParamList())
				{
					System.out.println("    " + k.getName() + " " + k.getType().get() + '@' + k.getType().getInfo() + "@" + k.getType().getLineNumber());
				}
				for(Entry<String, VarType> k : j.getValue().getLocalList().entrySet())
				{
					System.out.println("    " + k.getKey() + " " + k.getValue().get() + '@' + k.getValue().getInfo() + "@" + k.getValue().getLineNumber());
				}
			}
		}
	}
	
	public GoalTable getSymbolTable()
	{
		return SymbolTable;
	}
	
	public String visit(final Goal n) {
		
		stk.push(SymbolTable);
		
		// f0 -> MainClass()
		n.f0.accept(this);
		
		// f1 -> ( TypeDeclaration() )*
		n.f1.accept(this);
		
		// f2 -> <EOF>
		n.f2.accept(this);
		
		SymbolTable = (GoalTable)stk.pop();
		
		return null;
	}
	
	public String visit(final MainClass n) 
	{
		Table MainClassTable = new ClassTable();
		stk.push(MainClassTable);
		
		String RetString = new String();
		
	    // f0 -> "class"
	    n.f0.accept(this);
	    // f1 -> Identifier()
	    n.f1.accept(this);
	    
	    RetString = n.f1.f0.tokenImage;
	    
	    // f2 -> "{"
	    n.f2.accept(this);
	    
	    Table MainMethod = new MethodTable();
	    stk.push(MainMethod);
	    
	    // f3 -> "public"
	    n.f3.accept(this);
	    // f4 -> "static"
	    n.f4.accept(this);
	    // f5 -> "void"
	    n.f5.accept(this);
	    // f6 -> "main"
	    n.f6.accept(this);
	    
	    ((MethodTable)stk.peek()).set("void",n.f6.beginLine);
	    
	    // f7 -> "("
	    n.f7.accept(this);
	    // f8 -> "String"
	    n.f8.accept(this);
	    // f9 -> "["
	    n.f9.accept(this);
	    // f10 -> "]"
	    n.f10.accept(this);
	    
	    // f11 -> Identifier()
	    n.f11.accept(this);
	    
	    String ParamName = n.f11.f0.tokenImage;
	    int LineNumber = n.f11.f0.beginLine;
	    VarType value = new VarType();
	    value.set("String[]", "Parameter",LineNumber);
	    stk.peek().put(ParamName, value);

	    // f12 -> ")"
	    n.f12.accept(this);
	    // f13 -> "{"
	    n.f13.accept(this);
	    // f14 -> ( VarDeclaration() )*
	    n.f14.accept(this);
	    // f15 -> ( Statement() )*
	    n.f15.accept(this);
	    // f16 -> "}"
	    n.f16.accept(this);
	    
	    Table temp = stk.pop();
	    stk.peek().put("main", temp);
	    
	    // f17 -> "}"
	    n.f17.accept(this);
	    
	    currentTable = stk.pop();
	    ((ClassTable)currentTable).setLineNumber(n.f1.f0.beginLine);
		stk.peek().put(RetString, currentTable);
		//System.out.println("MainClass: " + RetString);
	    
	    return RetString;
	}
	
	public String visit(final ClassDeclaration n) 
	{	
		Table classDecTable = new ClassTable();
		stk.push(classDecTable);
		
		// f0 -> "class"
		n.f0.accept(this);
	    // f1 -> Identifier()
	    n.f1.accept(this);
	    
	    String className = n.f1.f0.tokenImage;
	    
	    // f2 -> "{"
	    n.f2.accept(this);
	    // f3 -> ( VarDeclaration() )*
	    n.f3.accept(this);
	    // f4 -> ( MethodDeclaration() )*
	    n.f4.accept(this);
	    // f5 -> "}"
	    n.f5.accept(this);
	    
	    classDecTable = stk.pop();
	    ((ClassTable)classDecTable).setLineNumber(n.f1.f0.beginLine);
	    if(((GoalTable)stk.peek()).get(className) != null)
	    {
	    	try {
				throw new TypeException("Class Redefined " + n.f1.f0.beginLine);
			} catch (TypeException e) {
				System.out.println(e);
			}
	    	System.exit(2);
	    }
	    stk.peek().put(className, classDecTable);
	    
	    return null;
	}
	
	public String visit(final ClassExtendsDeclaration n) {
		// f0 -> "class"
	    n.f0.accept(this);
	    // f1 -> Identifier()
	    n.f1.accept(this);
	    
	    String className = n.f1.f0.tokenImage;
	    
	    // f2 -> "extends"
	    n.f2.accept(this);
	    // f3 -> Identifier()
	    n.f3.accept(this);
	    
	    String extendsName = n.f3.f0.tokenImage;
	    
	    Table classDecTable = new ClassTable();
	    
	    ((ClassTable)classDecTable).setLineNumber(n.f0.beginLine);
	    ((ClassTable)classDecTable).setEx(extendsName);
	    
		stk.push(classDecTable);
	    
	    // f4 -> "{"
	    n.f4.accept(this);
	    // f5 -> ( VarDeclaration() )*
	    n.f5.accept(this);
	    // f6 -> ( MethodDeclaration() )*
	    n.f6.accept(this);
	    // f7 -> "}"
	    n.f7.accept(this);
	    
	    classDecTable = stk.pop();
	    ((ClassTable)classDecTable).setLineNumber(n.f1.f0.beginLine);
	    
	    /*put the variables and methods into the new class from the parent class*/
	    for(Entry<String,VarType> i : ((GoalTable)stk.peek()).get(extendsName).getVarList().entrySet())
	    {
	    	if(!((ClassTable)classDecTable).getVarList().containsKey(i.getKey()))
	    	{
	    		((ClassTable)classDecTable).getVarList().put(i.getKey(), i.getValue());
	    	}
	    }
	    for(Entry<String,MethodTable> i : ((GoalTable)stk.peek()).get(extendsName).getMethod().entrySet())
	    {
	    	if(!((ClassTable)classDecTable).getMethod().containsKey(i.getKey()))
	    	{
	    		((ClassTable)classDecTable).getMethod().put(i.getKey(), i.getValue());
	    	}
	    }
	    
	    if(((GoalTable)stk.peek()).get(className) != null)
	    {
	    	try {
				throw new TypeException("Class Redefined " + n.f1.f0.beginLine);
			} catch (TypeException e) {
				System.out.println(e);
			}
	    	System.exit(2);
	    }
	    stk.peek().put(className, classDecTable);
	    
	    return null;
	}
	
	public String visit(final VarDeclaration n) 
	{
		String nRes = null;
		
		// f0 -> Type()
		String key = n.f0.accept(this);
		//System.out.println("T_T: " + key);
		
		// f1 -> Identifier()
		n.f1.accept(this);
		String value = n.f1.f0.tokenImage;
		
		VarType vtp = new VarType();
		vtp.set(key, "Variable",n.f1.f0.beginLine);
		
		// f2 -> ";"
		n.f2.accept(this);
		
		if(stk.peek() instanceof ClassTable)
		{
			if(((ClassTable)stk.peek()).getVarList().get(value) != null)
		    {
		    	try {
					throw new TypeException("Variable Redefined " + n.f1.f0.beginLine);
				} catch (TypeException e) {
					System.out.println(e);
				}
		    	System.exit(2);
		    }
		}
		else if(stk.peek() instanceof MethodTable)
		{
			if(((MethodTable)stk.peek()).getLocalList().get(value) != null)
		    {
		    	try {
					throw new TypeException("Variable Redifined " + n.f1.f0.beginLine);
				} catch (TypeException e) {
					System.out.println(e);
				}
		    	System.exit(2);
		    }
		}
		stk.peek().put(value, vtp);
		
		return nRes;
	}
	
	public String visit(final Type n) {
		// f0 -> . %0 ArrayType()
		// .. .. | %1 BooleanType()
		// .. .. | %2 IntegerType()
		// .. .. | %3 Identifier()
		n.f0.accept(this);
		
		return n.f0.choice.accept(this);
	}
	
	public String visit(final ArrayType n) {
		// f0 -> "int"
		n.f0.accept(this);
		// f1 -> "["
		n.f1.accept(this);
		// f2 -> "]"
		n.f2.accept(this);
		return new String("int[]");
	}

	public String visit(final BooleanType n) {
		// f0 -> "boolean"
		n.f0.accept(this);
		return new String("boolean");
	}

	public String visit(final IntegerType n) {
		// f0 -> "int"
		n.f0.accept(this);
		return new String("int");
	}
	
	public String visit(final Identifier n) {
		// f0 -> <IDENTIFIER>
		n.f0.accept(this);
		return n.f0.tokenImage;
	}

	public String visit(final MethodDeclaration n) 
	{
		String MethodName = null;
		Table thisMethodTable = new MethodTable();
		stk.push(thisMethodTable);
		
		// f0 -> "public"
		n.f0.accept(this);
		
		// f1 -> Type()
		String RetType = n.f1.accept(this);
		
		// f2 -> Identifier()
		MethodName = n.f2.accept(this);
		
		((MethodTable)stk.peek()).set(RetType,n.f2.f0.beginLine);
		
		// f3 -> "("
		n.f3.accept(this);
		// f4 -> ( FormalParameterList() )?
		n.f4.accept(this);
		// f5 -> ")"
		n.f5.accept(this);
		// f6 -> "{"
		n.f6.accept(this);
		// f7 -> ( VarDeclaration() )*
		n.f7.accept(this);
		// f8 -> ( Statement() )*
		n.f8.accept(this);
		// f9 -> "return"
		n.f9.accept(this);
		// f10 -> Expression()
		n.f10.accept(this);
		// f11 -> ";"
		n.f11.accept(this);
		// f12 -> "}"
		n.f12.accept(this);
		
		thisMethodTable = stk.pop();
		
		if(((ClassTable)stk.peek()).getMethod().get(MethodName) != null)
	    {
	    	try {
				throw new TypeException("Method Redefined " + n.f2.f0.beginLine);
			} catch (TypeException e) {
				System.out.println(e);
			}
	    	System.exit(2);
	    }
		
		stk.peek().put(MethodName, thisMethodTable);
		
		return MethodName;
	}
	
	public String visit(final FormalParameter n) 
	{
		String nRes = null;
		
		// f0 -> Type()
		String key = n.f0.accept(this);
		
		// f1 -> Identifier()
		n.f1.accept(this);
		String value = n.f1.f0.tokenImage;
		
		VarType vtp = new VarType();
		vtp.set(key, "Parameter",n.f1.f0.beginLine);
		
		stk.peek().put(value, vtp);
		
		return nRes;
	}
}
