package UsrVisitor;

import java.util.ArrayList;
import java.util.Stack;
import java.util.Map.Entry;

import Symbol.*;
import type.syntaxtree.*;
import type.visitor.DepthFirstRetVisitor;

public class TypeCheckVisitor extends DepthFirstRetVisitor<String>{
	int lineNumber = 0;
	GoalTable symbolTable;
	Stack<Object> stk = new Stack<Object>();
	String ClassNo = new String();
	String MethodNo = new String();
	boolean identifierHandle = false;
	
	public TypeCheckVisitor()
	{
		ClassNo = null;
		MethodNo = null;
	}
	
	public void setSymbolTable(GoalTable SymbolTable)
	{
		symbolTable = SymbolTable;
	}
	
	public String visit(final Expression n) {
		// f0 -> . %0 AndExpression()
		// .. .. | %1 CompareExpression()
		// .. .. | %2 PlusExpression()
		// .. .. | %3 MinusExpression()
		// .. .. | %4 TimesExpression()
		// .. .. | %5 ArrayLookup()
		// .. .. | %6 ArrayLength()
		// .. .. | %7 MessageSend()
		// .. .. | %8 PrimaryExpression()
		identifierHandle = true;
		n.f0.accept(this);
		identifierHandle = true;
		String retVal = n.f0.choice.accept(this);
		identifierHandle = false;
		return retVal;
				
	}

	public String visit(final AndExpression n) {
		// f0 -> PrimaryExpression()
		String type1 = n.f0.accept(this);
		// f1 -> "&&"
		n.f1.accept(this);
		// f2 -> PrimaryExpression()
		String type2 = n.f2.accept(this);
		
		try{
			if(!type1.equals("boolean") || !type2.equals("boolean"))
			{
				throw new TypeException("Type Not Match at line" + ((NodeToken)n.f0.f0.choice).beginLine);
				//System.out.println("Type Not Match at line" + ((NodeToken)n.f0.f0.choice).beginLine);
			}	
		}
		catch(TypeException e){
			System.out.println(e);
		}
		return "boolean";
	}
	
	  public String visit(final CompareExpression n) {
	    // f0 -> PrimaryExpression()
		String type1 = n.f0.accept(this);
	    // f1 -> "<"
	    n.f1.accept(this);
	    // f2 -> PrimaryExpression()
	    String type2 = n.f2.accept(this);
	    
	    try{
			if(!type1.equals("int") || !type2.equals("int"))
			{
				throw new TypeException("Type Not Match at line" + ((NodeToken)n.f0.f0.choice).beginLine);
				//System.out.println("Type Not Match at line" + ((NodeToken)n.f0.f0.choice).beginLine);
			}	
		}
		catch(TypeException e){
			System.out.println(e);
		}
	    return "boolean";
	  }
	
	  public String visit(final PlusExpression n) {
		// f0 -> PrimaryExpression()
		String type1 = n.f0.accept(this);
		// f1 -> "+"
		n.f1.accept(this);
		// f2 -> PrimaryExpression()
		String type2 = n.f2.accept(this);
		
		try{
			if(!type1.equals("int") || !type2.equals("int"))
			{
				throw new TypeException("Type Not Match at line" + ((NodeToken)n.f0.f0.choice).beginLine);
				//System.out.println("Type Not Match at line" + ((NodeToken)n.f0.f0.choice).beginLine);
			}	
		}
		catch(TypeException e){
			System.out.println(e);
		}
		return "int";
	  }
	
	  public String visit(final MinusExpression n) {
	    // f0 -> PrimaryExpression()
	    String type1 = n.f0.accept(this);
	    // f1 -> "-"
	    n.f1.accept(this);
	    // f2 -> PrimaryExpression()
	    String type2 = n.f2.accept(this);
	    try{
			if(!type1.equals("int") || !type2.equals("int"))
			{
				throw new TypeException("Type Not Match at line" + ((NodeToken)n.f0.f0.choice).beginLine);
				//System.out.println("Type Not Match at line" + ((NodeToken)n.f0.f0.choice).beginLine);
			}	
		}
		catch(TypeException e){
			System.out.println(e);
		}
	    return "int";
	  }
	
	  public String visit(final TimesExpression n) {
	    // f0 -> PrimaryExpression()
	    String type1 = n.f0.accept(this);
	    // f1 -> "*"
	    n.f1.accept(this);
	    // f2 -> PrimaryExpression()
	    String type2 = n.f2.accept(this);
	    
	    try{
			if(!type1.equals("int") || !type2.equals("int"))
			{
				throw new TypeException("Type Not Match at line" + n.f1.beginLine);
				//System.out.println("Type Not Match at line" + ((NodeToken)n.f0.f0.choice).beginLine);
			}	
		}
		catch(TypeException e){
			System.out.println(e);
		}
	    
	    return "int";
	  }
	
	  public String visit(final ArrayLookup n) {
	    // f0 -> PrimaryExpression()
	    String type1 = n.f0.accept(this);
	    // f1 -> "["
	    n.f1.accept(this);
	    // f2 -> PrimaryExpression()
	    String type2 = n.f2.accept(this);
	    // f3 -> "]"
	    n.f3.accept(this);
	    
	    try{
			if(!type1.equals("int[]") || !type2.equals("int"))
			{
				throw new TypeException("Type Not Match at line" + n.f1.beginLine);
				//System.out.println("Type Not Match at line" + ((NodeToken)n.f0.f0.choice).beginLine);
			}	
		}
		catch(TypeException e){
			System.out.println(e);
		}
	    
	    return "int";
	  }
	
	  public String visit(final ArrayLength n) {
	    // f0 -> PrimaryExpression()
	    String type1 = n.f0.accept(this);
	    // f1 -> "."
	    n.f1.accept(this);
	    // f2 -> "length"
	    n.f2.accept(this);
	  
	    try{
			if(!type1.equals("int[]"))
			{
				throw new TypeException("Type Not Match at line" + ((NodeToken)n.f0.f0.choice).beginLine);
				//System.out.println("Type Not Match at line" + ((NodeToken)n.f0.f0.choice).beginLine);
			}	
		}
		catch(TypeException e){
			System.out.println(e);
		}
	    return "int";
	  }
	
	  public String visit(final MessageSend n) {
	    // f0 -> PrimaryExpression()
	    String className = n.f0.accept(this);
	    // f1 -> "."
	    n.f1.accept(this);
	    
	    
	    identifierHandle = false;
	    // f2 -> Identifier()
	    n.f2.accept(this);
	    identifierHandle = true;
	    
	    String methodName = n.f2.f0.tokenImage;
	    
	    MethodTable methodTable = symbolTable.get(className).getMethod().get(methodName);
	    
	    String retType = methodTable.getRetType();
	    
	    ArrayList<ParamType> paramType = methodTable.getParamList();
	    ArrayList<String> expressionListType = new ArrayList<String>();
	    stk.push(expressionListType);
	    
	    // f3 -> "("
	    n.f3.accept(this);
	    // f4 -> ( ExpressionList() )?
	    n.f4.accept(this);
	    // f5 -> ")"
	    n.f5.accept(this);
	    
	    expressionListType = (ArrayList<String>)stk.pop();
	    
	    try{
	    	if(paramType.size() != expressionListType.size())
	    		throw new TypeException("Parameter Not Match at line" + n.f1.beginLine);
	    	
		    for(int i = 0; i < paramType.size(); i++)
		    {
		    	if(!paramType.get(i).getType().get().equals(expressionListType.get(i)))
		    	{
		    		if(symbolTable.get(expressionListType.get(i)) != null)
		    		{
		    			if(symbolTable.get(expressionListType.get(i)).getEx().equals(paramType.get(i).getType().get()))
		    			{
		    				continue;
		    			}
		    		}
		    		System.out.println("1 " + paramType.get(i).getType().get() + "?"+ expressionListType.get(i));
		    		throw new TypeException("Parameter Not Match at line" + n.f1.beginLine);
		    	}
		    }
		}
	    catch(TypeException e)
	    {
	    	System.out.println(e);
	    }
	    
	    return retType;
	  }
	
	  public String visit(final ExpressionList n) {
		  
		  // f0 -> Expression()
		  String type1 = n.f0.accept(this);
		  
		  ((ArrayList<String>)stk.peek()).add(type1);
		  
		  // f1 -> ( ExpressionRest() )*
		  n.f1.accept(this);
		  
		  return null;
	  }
	
	  public String visit(final ExpressionRest n) {
		  // f0 -> ","
		  n.f0.accept(this);
		  // f1 -> Expression()
		  String type1 = n.f1.accept(this);

		  ((ArrayList<String>)stk.peek()).add(type1);
		  
		  return null;
	  }
	
	  public String visit(final PrimaryExpression n) {
	    // f0 -> . %0 IntegerLiteral()
	    // .. .. | %1 TrueLiteral()
	    // .. .. | %2 FalseLiteral()
	    // .. .. | %3 Identifier()
	    // .. .. | %4 ThisExpression()
	    // .. .. | %5 ArrayAllocationExpression()
	    // .. .. | %6 AllocationExpression()
	    // .. .. | %7 NotExpression()
	    // .. .. | %8 BracketExpression()
	    n.f0.accept(this);
	    
	    return n.f0.choice.accept(this);
	  }
	
	  public String visit(final IntegerLiteral n) {
	    // f0 -> <INTEGER_LITERAL>
	    n.f0.accept(this);
	    return "int";
	  }
	
	  public String visit(final TrueLiteral n) {
	    // f0 -> "true"
	    n.f0.accept(this);
	    return "boolean";
	  }
	
	  public String visit(final FalseLiteral n) {
	    // f0 -> "false"
	    n.f0.accept(this);
	    return "boolean";
	  }
	
	  public String visit(final Identifier n) {
	    // f0 -> <IDENTIFIER>
	    n.f0.accept(this);
	    
	    if(identifierHandle == false)
	    	return null;
	    
	    if(ClassNo == null)
	    {
	    	return null;
	    }
	    
		String id = n.f0.tokenImage;
		
		try
		{
			if(MethodNo != null)
			{
				if(symbolTable.get(ClassNo).getMethod().get(MethodNo).getLocalList().get(id) != null)
				{
					return symbolTable.get(ClassNo).getMethod().get(MethodNo).getLocalList().get(id).get();
				}
				
				for(ParamType i : symbolTable.get(ClassNo).getMethod().get(MethodNo).getParamList())
				{
					
					if(i.getName().equals(id))
					{
						return i.getType().get();
					}
				}
			}
			
			if(symbolTable.get(ClassNo).getVarList().get(id) != null)
			{
				return symbolTable.get(ClassNo).getVarList().get(id).get();
			}
			System.out.println("2 "  +"@" +id);
			throw new TypeException("Parameter Not Exist" + n.f0.beginLine);
		}
		catch(TypeException e)
		{
			System.out.println(e);
		}
		
		try{throw new TypeException("Parameter Undefined at line " + n.f0.beginLine);}
		catch(TypeException e){System.out.println(e);}
		
		System.exit(1);
		
		return "Undefined";
	  }
	
	  public String visit(final ThisExpression n) {
	    // f0 -> "this"
	    n.f0.accept(this);
	    return ClassNo;
	    
	  }
	
	  public String visit(final ArrayAllocationExpression n) {
	    // f0 -> "new"
	    n.f0.accept(this);
	    // f1 -> "int"
	    n.f1.accept(this);
	    // f2 -> "["
	    n.f2.accept(this);
	    // f3 -> Expression()
	    String type1 = n.f3.accept(this);
	    // f4 -> "]"
	    n.f4.accept(this);
	    
	    try{
			if(!type1.equals("int"))
			{
				throw new TypeException("Type Not Match at line" + n.f0.beginLine);
				//System.out.println("Type Not Match at line" + ((NodeToken)n.f0.f0.choice).beginLine);
			}	
		}
		catch(TypeException e){
			System.out.println(e);
		} 
	    return "int[]";
	  }
	
	  public String visit(final AllocationExpression n) 
	  {
		  
		identifierHandle = false;
	    // f0 -> "new"
	    n.f0.accept(this);
	    // f1 -> Identifier()
	    n.f1.accept(this);
	    identifierHandle = true;
	    
	    String id = n.f1.f0.tokenImage;
	    
	    // f2 -> "("
	    n.f2.accept(this);
	    // f3 -> ")"
	    n.f3.accept(this);
	    
	    try{
	    	if(symbolTable.get(id) == null)
	    	{
	    		throw new TypeException("Type Not Match at line" + n.f0.beginLine);
	    	}
	    }
	    catch(TypeException e)
	    {
	    	System.out.println(e);
	    }
	    return id;
	  }
	
	  public String visit(final NotExpression n) {
	    // f0 -> "!"
	    n.f0.accept(this);
	    
	    // f1 -> Expression()
	    String type1 = n.f1.accept(this);
	    
	    try{
			if(!type1.equals("boolean"))
			{
				throw new TypeException("Type Not Match at line" + n.f0.beginLine);
				//System.out.println("Type Not Match at line" + ((NodeToken)n.f0.f0.choice).beginLine);
			}	
		}
		catch(TypeException e){
			System.out.println(e);
		} 
	    
	    return "boolean";
	  }
	
	  public String visit(final BracketExpression n) {
	    // f0 -> "("
	    n.f0.accept(this);
	    // f1 -> Expression()
	    String type = n.f1.accept(this);
	    // f2 -> ")"
	    n.f2.accept(this);
	    
	    return type;
	  }
	  
	  public String visit(final AssignmentStatement n) 
	  {
		  	identifierHandle = true;
		    // f0 -> Identifier()
		    String type1 = n.f0.accept(this);
		    identifierHandle = false;
		   
		    // f1 -> "="
		    n.f1.accept(this);
		    // f2 -> Expression()
		    String type2 = n.f2.accept(this);
		    // f3 -> ";"
		    n.f3.accept(this);
		    
		    try{
				if(!type1.equals(type2))
				{
					throw new TypeException("Type Not Match at line" + n.f0.f0.beginLine);
					//System.out.println("Type Not Match at line" + ((NodeToken)n.f0.f0.choice).beginLine);
				}	
			}
			catch(TypeException e){
				System.out.println(e);
			}
		    
		    return null;
	  }

	  public String visit(final ArrayAssignmentStatement n) 
	  {
		
		identifierHandle = true;
		// f0 -> Identifier()
		String type1 = n.f0.accept(this);
		identifierHandle = false;
		// f1 -> "["
		n.f1.accept(this);
		// f2 -> Expression()
		n.f2.accept(this);
		// f3 -> "]"
		
		n.f3.accept(this);
		// f4 -> "="
		n.f4.accept(this);
		// f5 -> Expression()
		String type2 = n.f5.accept(this);
		// f6 -> ";"
		n.f6.accept(this);
		
		try{
			if(!type1.equals("int[]") || !type2.equals("int"))
			{
				throw new TypeException("Type Not Match at line" + n.f0.f0.beginLine);
				//System.out.println("Type Not Match at line" + ((NodeToken)n.f0.f0.choice).beginLine);
			}	
		}
		catch(TypeException e){
			System.out.println(e);
		}
		return null;
	  }
	  public String visit(final MainClass n) 
	  {
		// f0 -> "class"
		n.f0.accept(this);
		// f1 -> Identifier()
		n.f1.accept(this);
		
		ClassNo = n.f1.f0.tokenImage;
		// f2 -> "{"
		n.f2.accept(this);
		// f3 -> "public"
		n.f3.accept(this);
		// f4 -> "static"
		n.f4.accept(this);
		// f5 -> "void"
		n.f5.accept(this);
		// f6 -> "main"
		n.f6.accept(this);
		
		MethodNo = "main";
		
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
		// f17 -> "}"
		n.f17.accept(this);
		
		return null;
	  }
	  public String visit(final ClassDeclaration n) 
	  {
			// f0 -> "class"
			n.f0.accept(this);
			// f1 -> Identifier()
			n.f1.accept(this);
			
			ClassNo = n.f1.f0.tokenImage;
			MethodNo = null;
			
			// f2 -> "{"
			n.f2.accept(this);
			// f3 -> ( VarDeclaration() )*
			n.f3.accept(this);
			// f4 -> ( MethodDeclaration() )*
			n.f4.accept(this);
			// f5 -> "}"
			n.f5.accept(this);
	  
			return null;
	  }
	  public String visit(final MethodDeclaration n) 
	  {
		    // f0 -> "public"
		    n.f0.accept(this);
		    // f1 -> Type()
		    n.f1.accept(this);
		    // f2 -> Identifier()
		    n.f2.accept(this);
		    
		    MethodNo = n.f2.f0.tokenImage;
		    
		    String type1 = symbolTable.get(ClassNo).getMethod().get(MethodNo).getRetType();
		    
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
		    String type2 = n.f10.accept(this);		   
		    
		    try{
				if(!type1.equals(type2))
				{
					throw new TypeException("Type Not Match at line" + n.f0.beginLine);
					//System.out.println("Type Not Match at line" + ((NodeToken)n.f0.f0.choice).beginLine);
				}	
			}
			catch(TypeException e){
				System.out.println(e);
			}
		    
		    // f11 -> ";"
		    n.f11.accept(this);
		    // f12 -> "}"
		    n.f12.accept(this);
		    
		    MethodNo = null;
		    
		    return null;
	  }
	  public String visit(final Goal n) 
	  {
		    // f0 -> MainClass()
		    n.f0.accept(this);
		    // f1 -> ( TypeDeclaration() )*
		    n.f1.accept(this);
		    // f2 -> <EOF>
		    n.f2.accept(this);
		    
		    return null;
	  }
}
