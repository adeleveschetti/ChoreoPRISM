package ast;

import java.util.ArrayList;

public class ActionNode implements Node{
	private String actionA ;
	private String actionB;
	
	public ActionNode(String _actionA, String _actionB) {
		actionA = _actionA;
		actionB = _actionB;
	}

	@Override
	public String toPrint() {
		if(actionB.equals(" ")) {
			actionB = " ";
		}
		return actionA+"&&"+actionB;
	}
	
	@Override
	public ArrayList<String> getRoles(){
		return new ArrayList<String>();
	}

	@Override
	public String generateCode(ArrayList<Node> mods, int index, int maxIndex, boolean isCtmc, ArrayList<String> labels, String prot) {
		if(actionB==null || actionB.equals(" ") ) {
			actionB = " ";
		}
		return actionA+"&&"+actionB;
	}
}
