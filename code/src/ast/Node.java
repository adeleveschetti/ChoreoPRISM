package ast;

import java.util.ArrayList;

public interface Node {
	String toPrint();
	String generateCode(ArrayList<Node> mods, int index, int maxIndex, boolean isCtmc, ArrayList<String> labels, String prot);	
	ArrayList<String> getRoles();
}
