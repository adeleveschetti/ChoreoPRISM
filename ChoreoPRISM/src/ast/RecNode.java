package ast;

import java.util.ArrayList;

public class RecNode implements Node{

	public String protocolName ;
	
	RecNode(String _name){
		protocolName = _name;
	}
	
	@Override
	public String toPrint() {
		return protocolName;
	}

	@Override
	public String generateCode(ArrayList<Node> mods, int index, int maxIndex, boolean isCtmc, ArrayList<String> labels, String prot) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<String> getRoles() {
		// TODO Auto-generated method stub
		return new ArrayList<String>();
	}

}
