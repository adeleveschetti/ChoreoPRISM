package ast;

import java.util.ArrayList;

import lib.Functions;

public class LoopNode implements Node{

	private String message ;
	private String indexIteration; // index of the iteration
	private String upperBound; // upperbound
	private String op ; // operation (!= ; = ; < ; > ; <= ; >=)
	private String role ;

	public LoopNode(String mex, String index, String bound, String operation, String r){
		message = mex;
		indexIteration = index;
		upperBound = bound;
		op = operation;
		role = r;
	}

	@Override
	public String toPrint() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<String> getRoles(){
		ArrayList<String> roles = new ArrayList<String>();
		roles.add(role);
		return roles;
	}
	
	@Override
	public String generateCode(ArrayList<Node> mods, int index, int maxIndex, boolean isCtmc, ArrayList<String> labels, String prot) {
		String toRet = "";
		Functions funs = new Functions();
		int roleIndex = Functions.newIndex(role,index,maxIndex);
		if(roleIndex == -1) {
			roleIndex = index;
		}
		if(op.equals("!=")) {
			for(int i=1; i<=maxIndex; i++) {
				if(i!=roleIndex) {
					toRet = toRet + Functions.returnStringNewIndex(message,i,maxIndex) + "&"; 
				}
			}
		}
		return toRet;
	}


}
