package Java2Vapor;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Stack;

import type.syntaxtree.AllocationExpression;
import type.syntaxtree.AndExpression;
import type.syntaxtree.ArrayAllocationExpression;
import type.syntaxtree.ArrayAssignmentStatement;
import type.syntaxtree.ArrayLength;
import type.syntaxtree.ArrayLookup;
import type.syntaxtree.AssignmentStatement;
import type.syntaxtree.BracketExpression;
import type.syntaxtree.ClassDeclaration;
import type.syntaxtree.ClassExtendsDeclaration;
import type.syntaxtree.CompareExpression;
import type.syntaxtree.Expression;
import type.syntaxtree.ExpressionList;
import type.syntaxtree.ExpressionRest;
import type.syntaxtree.FalseLiteral;
import type.syntaxtree.Identifier;
import type.syntaxtree.IfStatement;
import type.syntaxtree.IntegerLiteral;
import type.syntaxtree.MainClass;
import type.syntaxtree.MessageSend;
import type.syntaxtree.MethodDeclaration;
import type.syntaxtree.MinusExpression;
import type.syntaxtree.NodeChoice;
import type.syntaxtree.NotExpression;
import type.syntaxtree.PlusExpression;
import type.syntaxtree.PrimaryExpression;
import type.syntaxtree.PrintStatement;
import type.syntaxtree.ThisExpression;
import type.syntaxtree.TimesExpression;
import type.syntaxtree.TrueLiteral;
import type.syntaxtree.WhileStatement;
import type.visitor.DepthFirstRetVisitor;
import Symbol.*;

public class Java2VaporVisitor extends DepthFirstRetVisitor<VarNType>{

	GoalTable symbolTable;
	int emitBlankNo = 0; 
	String emitBlank = new String("");
	String currentMethod = new String();
	String currentClass = new String();
	Integer tempNo = new Integer(0);
	Stack<ArrayList<VarNType>> stk = new Stack<ArrayList<VarNType>>();
	boolean IdentifierHandle = false;
	boolean ArrayAlloc = false;
	boolean AssignmentLeft = false;
	Integer LabelNo = new Integer(0);
	
	public void emit(String str)
	{
		emitBlank = "";
		for(int i = 0; i < emitBlankNo; i++)
		{
			emitBlank += "  ";
		}
		System.out.println(emitBlank + str);
	}
	public void emitEveryThing()
	{
		emitDeclaration();
	}
	public void emitArrayAllocation()
	{
		if(ArrayAlloc == false)
			return;
		
		emit("func ArrayAlloc(size)");
		emitBlankNo = 1;
		emit("bytes = MulS(size 4)");
		emit("bytes = Add(bytes 4)");
		emit("v = HeapAllocZ(bytes)");
		emit("[v] = size");
		emit("ret v");
		emitBlankNo = 0;
		
	}
	public void setSymbolTable(GoalTable SymbolTable)
	{
		symbolTable = SymbolTable;
	}
	public void emitDeclaration()
	{
		for(Entry<String,ClassTable> i : symbolTable.getClassList().entrySet())
		{
			if(i.getValue().checkMain())
				continue;
			
			emit("const" + " " + "vmt_" + i.getKey());
			
			if(symbolTable.getClassList().get(i.getKey()).checkExtend() == true)
			{
				emitBlankNo = 1;
				for(Entry<String,MethodTable> j : i.getValue().getMethod().entrySet())
				{
					emit(":"+i.getKey() + "." + j.getKey());
				}
				emit("");
				emitBlankNo = 0;
			}
		}
	}
	
	public VarNType visit(final NodeChoice n) 
	{
	    return n.choice.accept(this);
	}
	
	public VarNType visit(final MainClass n) 
	{
		// f0 -> "class"
		n.f0.accept(this);
		// f1 -> Identifier()
		n.f1.accept(this);
		
		currentClass = n.f1.f0.tokenImage;
		
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
		
		currentMethod = n.f6.tokenImage;
		
		emit("func Main()");
		emitBlankNo = 1;
		
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
		
		currentMethod = null;
		emit("ret");
		emitBlankNo = 0;
		
		// f17 -> "}"
		n.f17.accept(this);
		return null;
	}
	
	public VarNType visit(final ClassDeclaration n) 
	{
	    // f0 -> "class"
	    n.f0.accept(this);
	    // f1 -> Identifier()
	    n.f1.accept(this);
	    
	    currentClass = n.f1.f0.tokenImage;
	    
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

	public VarNType visit(final ClassExtendsDeclaration n) 
	{
	    // f0 -> "class"
	    n.f0.accept(this);
	    // f1 -> Identifier()
	    
	    currentClass = n.f1.f0.tokenImage;
	    
	    n.f1.accept(this);
	    // f2 -> "extends"
	    n.f2.accept(this);
	    // f3 -> Identifier()
	    n.f3.accept(this);
	    // f4 -> "{"
	    n.f4.accept(this);
	    // f5 -> ( VarDeclaration() )*
	    n.f5.accept(this);
	    // f6 -> ( MethodDeclaration() )*
	    n.f6.accept(this);
	    // f7 -> "}"
	    n.f7.accept(this);
	    
	    return null;
	}
	public VarNType visit(final MethodDeclaration n) 
	{
	    // f0 -> "public"
	    n.f0.accept(this);
	    // f1 -> Type()
	    n.f1.accept(this);
	    // f2 -> Identifier()
	    n.f2.accept(this);
	    
	    currentMethod = n.f2.f0.tokenImage;
	    String funcName = "func" + " " + currentClass + '.' + n.f2.f0.tokenImage;
	    tempNo = 0;
	    
	    // f3 -> "("
	    n.f3.accept(this);
	    // f4 -> ( FormalParameterList() )?
	    n.f4.accept(this);
	    // f5 -> ")"
	    n.f5.accept(this);
	    
	    String ParameterName = new String("(this");
	    MethodTable tempTable = symbolTable.getMethodTable(currentClass, currentMethod);
	    for(ParamType i : tempTable.getParamList())
	    {
	    	ParameterName += " " + i.getName();
	    }
	    ParameterName += ")";
	    
	    emit(funcName + ParameterName);
	    
	    emitBlankNo = 1;
	    
	    // f6 -> "{"
	    n.f6.accept(this);
	    // f7 -> ( VarDeclaration() )*
	    n.f7.accept(this);
	    // f8 -> ( Statement() )*
	    n.f8.accept(this);
	    
	    
	    // f9 -> "return"
	    n.f9.accept(this);
	    // f10 -> Expression()
	    VarNType retVarNType = n.f10.accept(this);
	    emit("ret " + retVarNType.varName);
	    
	    // f11 -> ";"
	    n.f11.accept(this);
	    // f12 -> "}"
	    n.f12.accept(this);
	    
	    currentMethod = null;
	    emitBlankNo = 0;
	    
	    return null;
	}
	
	public VarNType visit(final Expression n) 
	{
	    // f0 -> . %0 AndExpression()
	    // .. .. | %1 CompareExpression()
	    // .. .. | %2 PlusExpression()
	    // .. .. | %3 MinusExpression()
	    // .. .. | %4 TimesExpression()
	    // .. .. | %5 ArrayLookup()
	    // .. .. | %6 ArrayLength()
	    // .. .. | %7 MessageSend()
	    // .. .. | %8 PrimaryExpression()
	    return n.f0.accept(this);
	}

	public VarNType visit(final AndExpression n) {
	    // f0 -> PrimaryExpression()
	    VarNType t1 = n.f0.accept(this);
	    // f1 -> "&&"
	    n.f1.accept(this);
	    // f2 -> PrimaryExpression()
	    VarNType t2 = n.f2.accept(this);
	    
	    String retName = "t." + tempNo;
	    tempNo ++;
	    
	    emit(retName + " = " + "MulS(" + t1.varName + " " + t2.varName + ")");
	    
	    return new VarNType(retName,"boolean");
	}

	public VarNType visit(final CompareExpression n) {
	    // f0 -> PrimaryExpression()
	    VarNType t1 = n.f0.accept(this);
	    // f1 -> "<"
	    n.f1.accept(this);
	    // f2 -> PrimaryExpression()
	    VarNType t2 = n.f2.accept(this);
	    
	    String retName = "t." + tempNo;
	    tempNo ++;
	    
	    emit(retName + " = " + "LtS(" + t1.varName + " " + t2.varName + ")");
	    
	    return new VarNType(retName,"boolean");
	}

	public VarNType visit(final PlusExpression n) {
	    // f0 -> PrimaryExpression()
	    VarNType t1 = n.f0.accept(this);
	    // f1 -> "+"
	    n.f1.accept(this);
	    // f2 -> PrimaryExpression()
	    VarNType t2 = n.f2.accept(this);
	    
	    String retName = "t." + tempNo;
	    tempNo ++;
	    
	    emit(retName + " = " + "Add(" + t1.varName + " " + t2.varName + ")");
	    
	    return new VarNType(retName,"int");
	    
	}

	public VarNType visit(final MinusExpression n) {
	    // f0 -> PrimaryExpression()
	    VarNType t1 = n.f0.accept(this);
	    // f1 -> "-"
	    n.f1.accept(this);
	    // f2 -> PrimaryExpression()
	    VarNType t2 = n.f2.accept(this);
	    
	    String retName = "t." + tempNo;
	    tempNo ++;
	    
	    emit(retName + " = " + "Sub(" + t1.varName + " " + t2.varName + ")");
	    
	    return new VarNType(retName,"int");
	}

	public VarNType visit(final TimesExpression n) {
	    // f0 -> PrimaryExpression()
	    VarNType t1 = n.f0.accept(this);
	    // f1 -> "*"
	    n.f1.accept(this);
	    // f2 -> PrimaryExpression()
	    VarNType t2 = n.f2.accept(this);
	    String retName = "t." + tempNo;
	    tempNo ++;
	    
	    emit(retName + " = " + "MulS(" + t1.varName + " " + t2.varName + ")");
	    
	    return new VarNType(retName,"int");
	}

	public VarNType visit(final ArrayLookup n) {
	    // f0 -> PrimaryExpression()
	    VarNType t1 = n.f0.accept(this);
	    // f1 -> "["
	    n.f1.accept(this);
	    // f2 -> PrimaryExpression()
	    VarNType t2 = n.f2.accept(this);
	    // f3 -> "]"
	    n.f3.accept(this);
	    
	    String temp1 = "t." + tempNo;
	    tempNo ++;
	    emit(temp1 + " = "  + "MulS(" + t2.varName + " " + "4" + ")");
	    
	    String temp2 = "t." + tempNo;
	    tempNo ++;
	    emit(temp2 + " = " + "Add(" + temp1 + " " + "4" + ")");
	    
	    String temp3 = "t." + tempNo;
	    tempNo ++;
	    
	    String retName = "t." + tempNo;
	    tempNo ++;
	    
	    emit(temp3 + " = " +  "Add(" + t1.varName + " " + temp2 + ")");
	    
	    emit(retName + " = " +  "[" +  temp3 + "]");
	    //System.out.println("------in ArrayAlloc");
	    
	    return new VarNType(retName, "int");
	}

	public VarNType visit(final ArrayLength n) {
		// f0 -> PrimaryExpression()
		VarNType t1 = n.f0.accept(this);
		// f1 -> "."
		n.f1.accept(this);
		// f2 -> "length"
		n.f2.accept(this);
		
		String retName = "t." + tempNo;
		tempNo ++;
		
		emit(retName + " = " + "[" + t1.varName + "]");
		
		return new VarNType(retName, "int");
	}

	public VarNType visit(final MessageSend n) {
	    // f0 -> PrimaryExpression()
		VarNType t1 = n.f0.accept(this);
	    
		

		/* Which class belongs to */
		String ClassType = t1.varType;
		 
	    // f1 -> "."
	    n.f1.accept(this);
	    // f2 -> Identifier()
	    n.f2.accept(this);
	    
	    /* Get the Method Name */
	    String MethodName = n.f2.f0.tokenImage;
	    
	    /** OPT: No need to get the index **/
	    /* get the index of the method */
	    
	    
	    stk.push(new ArrayList<VarNType>());
	    // f3 -> "("
	    n.f3.accept(this);
	    // f4 -> ( ExpressionList() )?
	    n.f4.accept(this);
	    // f5 -> ")"
	    n.f5.accept(this);  
	    ArrayList<VarNType>tempArr = stk.pop();
	    
		/** OPT: No need to get the address of a class **/
		/* find the Address of Label for this class */
		String temp1;
		if(symbolTable.getClassList().get(t1.varType).checkExtend() == true){
			temp1 = "t." + tempNo;
			tempNo ++;
			emit(temp1 + " = " + "[" + t1.varName + "]");
		
			int MethodIndex = symbolTable.getClassList().get(ClassType).getMethodIndex(MethodName);
			emit(temp1 + " = " + "[" + temp1 + " + " + new Integer(MethodIndex * 4) + "]");
		}
		else
		{
			temp1 = ":" + ClassType + "." + MethodName;
		}
		
	    String Argu = new String();
	    
	    Argu += t1.varName;
	    
	    for(VarNType i : tempArr)
	    {
	    	Argu += " " + i.varName;
	    }
	    
	    String temp2 = "t." + tempNo;
	    tempNo ++;
	    
	    emit(temp2 + " = " + "call" + " " + temp1 + "(" + Argu + ")");
	    
	    return new VarNType(temp2,symbolTable.getClassList().get(ClassType).getMethod().get(MethodName).getRetType());
	}

	public VarNType visit(final ExpressionList n) {
		// f0 -> Expression()
		VarNType t1 = n.f0.accept(this);
		stk.peek().add(t1);
		// f1 -> ( ExpressionRest() )*
		n.f1.accept(this);
		
		return t1;
	}

	public VarNType visit(final ExpressionRest n) {
		// f0 -> ","
		n.f0.accept(this);
		// f1 -> Expression()
		VarNType t1 = n.f1.accept(this);
		stk.peek().add(t1);
	
		return t1;
	}
	
	public VarNType visit(final PrimaryExpression n) {
		// f0 -> . %0 IntegerLiteral()
		// .. .. | %1 TrueLiteral()
		// .. .. | %2 FalseLiteral()
		// .. .. | %3 Identifier()
		// .. .. | %4 ThisExpression()
		// .. .. | %5 ArrayAllocationExpression()
		// .. .. | %6 AllocationExpression()
		// .. .. | %7 NotExpression()
		// .. .. | %8 BracketExpression()
		IdentifierHandle = true;
		VarNType t1 =  n.f0.accept(this);
		IdentifierHandle = false;
		return t1;
	}

	public VarNType visit(final IntegerLiteral n) {
		// f0 -> <INTEGER_LITERAL>
		n.f0.accept(this);
		
		return new VarNType(n.f0.tokenImage, "int");
	
	}

	public VarNType visit(final TrueLiteral n) {
		// f0 -> "true"
		n.f0.accept(this);
		
		return new VarNType("1","boolean");
	}

	public VarNType visit(final FalseLiteral n) {
		// f0 -> "false"
		n.f0.accept(this);
		
		return new VarNType("0", "boolean");
	}

	public VarNType visit(final Identifier n) {
		// f0 -> <IDENTIFIER>
		n.f0.accept(this);
	
		String key = n.f0.tokenImage;
		
		if(IdentifierHandle == false)
			return new VarNType(key,"");
		else if(IdentifierHandle == true)
		{
			if(currentMethod != null)
			{
				if(symbolTable.get(currentClass).getMethod().get(currentMethod).getLocalList().get(key) != null)
				{
					return new VarNType(key,symbolTable.get(currentClass).getMethod().get(currentMethod).getLocalList().get(key).get());
				}
				
				for(ParamType i : symbolTable.get(currentClass).getMethod().get(currentMethod).getParamList())
				{
					
					if(i.getName().equals(key))
					{
						return new VarNType(key,i.getType().get());
					}
				}
			}
			
			if(symbolTable.get(currentClass).getVarList().get(key) != null)
			{
				String ClassMember = new String();
				int MemberIndex = symbolTable.getClassList().get(currentClass).getVarIndex(key);
				
				ClassMember = "[" + "this" + " + " + new Integer((MemberIndex + 1) * 4) + "]";
				
				if(AssignmentLeft == false)
				{	
					String tt1 = "t." + tempNo;
					tempNo ++;
					emit(tt1 + " = " + ClassMember);
					return new VarNType(tt1,symbolTable.get(currentClass).getVarList().get(key).get());
				}
				else
				{
					return new VarNType(ClassMember,symbolTable.get(currentClass).getVarList().get(key).get());
				}
			}
		}
		return new VarNType(key,"");
	}

	public VarNType visit(final ThisExpression n) {
		// f0 -> "this"
		n.f0.accept(this);
		
		return new VarNType("this", currentClass);
	}

	public VarNType visit(final ArrayAllocationExpression n) {
		// f0 -> "new"
		n.f0.accept(this);
		// f1 -> "int"
		n.f1.accept(this);
		// f2 -> "["
		n.f2.accept(this);
		// f3 -> Expression()
		VarNType t1 = n.f3.accept(this);
		
		// f4 -> "]"
		n.f4.accept(this);
		
		String temp1 = "t." + tempNo;
		tempNo ++;
		
		emit(temp1 + " = "  + "call " + ":ArrayAlloc(" + t1.varName + ")");
		
		ArrayAlloc = true;
		
		return new VarNType(temp1, "int[]");
	}

	public VarNType visit(final AllocationExpression n) {
		// f0 -> "new"
		n.f0.accept(this);
		// f1 -> Identifier()
		n.f1.accept(this);
		// f2 -> "("
		n.f2.accept(this);
		// f3 -> ")"
		n.f3.accept(this);
		
		String temp1 = "t." + tempNo;
		tempNo ++;
		
		String ClassName = n.f1.f0.tokenImage;
		
		int VariableNumber = symbolTable.getClassList().get(ClassName).getVariableNumber() + 1;
		Integer SizeOfVariables = new Integer(VariableNumber * 4);
		
		/*boolean ans = symbolTable.getClassList().get(currentClass).getMethod().get(currentMethod).setAddr(ClassName, temp1);
		
		if(ans == false)
		{
			symbolTable.getClassList().get(currentClass).getVarList().get(ClassName).setAddress(temp1);
		}*/
		
		if(VariableNumber == 1)
		{
			return new VarNType(null, ClassName);
		}
		
		emit(temp1 + " = " + "HeapAllocZ(" + SizeOfVariables + ")");
		
		emit("[" + temp1 + "]" + " = " + ":vmt_" + ClassName);
		
		return new VarNType(temp1, ClassName);
	}

	public VarNType visit(final NotExpression n) {
		// f0 -> "!"
		n.f0.accept(this);
		// f1 -> Expression()
		VarNType t1 = n.f1.accept(this);
	
		String temp1 = "t." + tempNo;
		tempNo ++;
		
		emit(temp1 + " = " + "Sub(" + "1 "+ t1.varName + ")");
		
		return new VarNType(temp1, "boolean");
	}

	public VarNType visit(final BracketExpression n) {
		// f0 -> "("
		n.f0.accept(this);
		// f1 -> Expression()
		VarNType t1 = n.f1.accept(this);
		// f2 -> ")"
		n.f2.accept(this);
	
		return t1;
	}
	
	public VarNType visit(final AssignmentStatement n) {
		// f0 -> Identifier()
		IdentifierHandle = true;
		AssignmentLeft = true;
	    VarNType t1 = n.f0.accept(this);
	    AssignmentLeft = false;
	    IdentifierHandle = false;
	    
	    // f1 -> "="
	    n.f1.accept(this);
	    // f2 -> Expression()
	    VarNType t2 = n.f2.accept(this);
	    // f3 -> ";"
	    n.f3.accept(this);
	    
	    emit(t1.varName + " = " + t2.varName);
	    
	    return null;
	}

	public VarNType visit(final ArrayAssignmentStatement n) {
		
		IdentifierHandle = true;
		// f0 -> Identifier()
		VarNType t1 = n.f0.accept(this);
		IdentifierHandle = false;
		// f1 -> "["
		n.f1.accept(this);
		// f2 -> Expression()
		VarNType t2 = n.f2.accept(this);
		// f3 -> "]"
		n.f3.accept(this);
		// f4 -> "="
		n.f4.accept(this);
		// f5 -> Expression()
		VarNType t3 = n.f5.accept(this);
		// f6 -> ";"
		n.f6.accept(this);
		
		String temp1 = "t." + tempNo;
		tempNo ++;
		
		emit(temp1 + " = " + "Add(" + t2.varName + " " + "1)");
		
		emit(temp1 + " = " + "MulS(" + temp1 + " " + "4)");
		
		String temp2 = "t." + tempNo;
		tempNo++;
		
		emit(temp2 + " = "  + t1.varName);
		
		String temp3 = "t." + tempNo;
		tempNo++;
		
		emit(temp3 + "=" + "Add(" + temp2 + " " + temp1 + ")");
		
		emit("[" + temp3 + "]" + " = " + t3.varName);
		
		return null;
	}

	public VarNType visit(final IfStatement n) {
		// f0 -> "if"
		n.f0.accept(this);
		// f1 -> "("
		n.f1.accept(this);
		// f2 -> Expression()
		VarNType t1 = n.f2.accept(this);
		// f3 -> ")"
		
		String Label0 = "ifLabel" + LabelNo;
		LabelNo ++;
		
		emit("if0 " + t1.varName + " goto :" + Label0);
		emitBlankNo += 1;
		n.f3.accept(this);
		// f4 -> Statement()
		n.f4.accept(this);
		
		String Label1 = "ifEnd" + LabelNo;
		LabelNo ++;
		
		emit("goto :" + Label1);
		emitBlankNo -= 1;
		
		// f5 -> "else"
		emit(Label0 + ":");
		emitBlankNo += 1;
		n.f5.accept(this);
		
		// f6 -> Statement()
		n.f6.accept(this);
		
		emitBlankNo -= 1;
		emit(Label1 + ":");
		
		return null;
	}

	public VarNType visit(final WhileStatement n) {
		// f0 -> "while"
		n.f0.accept(this);
		
		String Label0 = "whileLable" + LabelNo;
		LabelNo++;
		String Label1 = "whileLable" + LabelNo;
		LabelNo++;
		
		emit(Label0 + ":");
		
		// f1 -> "("
		n.f1.accept(this);
		// f2 -> Expression()
		VarNType t1 = n.f2.accept(this);
		// f3 -> ")"
		
		emit("if0 " + t1.varName + " goto :" + Label1);
		
		emitBlankNo += 1;
		n.f3.accept(this);
		// f4 -> Statement()
		n.f4.accept(this);
		
		emit("goto :" + Label0);
		emitBlankNo -= 1;
		emit(Label1 + ":");
		return null;
	}

	public VarNType visit(final PrintStatement n) {
	    // f0 -> "System.out.println"
	    n.f0.accept(this);
	    // f1 -> "("
	    n.f1.accept(this);
	    // f2 -> Expression()
	    VarNType t1 = n.f2.accept(this);
	    // f3 -> ")"
	    n.f3.accept(this);
	    // f4 -> ";"
	    n.f4.accept(this);
	  
	    emit("PrintIntS(" +  t1.varName + ")");
	    return null;
	}
}
