package ast;

import java.util.ArrayList;

public class RoleNode implements Node{

	private String name;
	
	public RoleNode(String _name) {
		name = _name;
	}

	@Override
	public String toPrint() {
		return name;
	}
	
	

	@Override
	public String generateCode(ArrayList<Node> mods, int index, int maxIndex, boolean isCtmc, ArrayList<String> labels, String prot) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<String> getRoles() {
		// TODO Auto-generated method stub
		return null;
	}

}
