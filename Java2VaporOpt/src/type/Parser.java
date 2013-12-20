package type;

import UsrVisitor.MyVisitor;
import UsrVisitor.TypeCheckVisitor;
import type.syntaxtree.*;
import type.visitor.*;
import Java2Vapor.Java2VaporVisitor;
import Java2Vapor.VarNType;
import Symbol.GoalTable;

public class Parser 
{
	static GoalTable SymbolTable;
	
	public static void main(String args[])
	{
		//System.out.println("I'm starting!");
		MiniJavaParser Mjp = new MiniJavaParser(System.in);
		try {
			Goal goal = Mjp.Goal();
			DepthFirstRetVisitor<String> v = new MyVisitor();
			goal.accept(v);
			SymbolTable = ((MyVisitor)v).getSymbolTable();
			
			DepthFirstRetVisitor<String> v2 = new TypeCheckVisitor();
			((TypeCheckVisitor)v2).setSymbolTable(SymbolTable);
			goal.accept(v2);
			
			DepthFirstRetVisitor<VarNType> v3 = new Java2VaporVisitor();
			((Java2VaporVisitor)v3).setSymbolTable(SymbolTable);
			((Java2VaporVisitor)v3).emitDeclaration();
			goal.accept(v3);
			((Java2VaporVisitor)v3).emitArrayAllocation();
			
		}catch (ParseException e) {
			// TODO Auto-generated catch block
			System.out.println("bug!!");
			e.printStackTrace();
		}
	}
}
