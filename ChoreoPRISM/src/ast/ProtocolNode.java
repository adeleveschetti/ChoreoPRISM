package ast;

import java.util.ArrayList;

public class ProtocolNode implements Node{

	public String id;
	public ArrayList<Node> statements ;
	public int stateInit;
	
	ProtocolNode(String _id, ArrayList<Node> _statements){
		id = _id;
		statements = _statements;
	}
	
	@Override
	public String toPrint() {
		return id;
	}

	@Override
	public String generateCode(ArrayList<Node> mods, int index, int maxIndex, boolean isCtmc, ArrayList<String> labels, String prot) {
		for(Node el : statements) {
			for(int i=1;i<=maxIndex; i++) {
				el.generateCode(mods,i,maxIndex,isCtmc,labels,prot);
			}
		}
		return null;
	}

	@Override
	public ArrayList<String> getRoles() {
		// TODO Auto-generated method stub
		return null;
	}

}
