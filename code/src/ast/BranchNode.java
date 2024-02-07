package ast;

import java.util.ArrayList;
import java.util.Random;

import lib.Functions;

public class BranchNode implements Node{

	private String role;
	private ArrayList<String> outRole;
	private boolean branch;
	private ArrayList<String> rates;
	private ArrayList<Node> updates;
	private ArrayList<Node> preconditions;
	private ArrayList<Node> statements;

	public BranchNode(String _role, ArrayList<String> _outRole, boolean _branch, ArrayList<String> _rates, ArrayList<Node> _preconditions, ArrayList<Node> _updates, ArrayList<Node> _statements) {
		role = _role;
		outRole = _outRole;
		branch = _branch;
		rates = _rates;
		preconditions = _preconditions;
		updates = _updates;
		statements = _statements;
	}

	@Override
	public String toPrint() {
		// TODO Auto-generated method stub
		return "branch";
	}

	@Override
	public String generateCode(ArrayList<Node> mods, int index, int maxIndex, boolean isCtmc, ArrayList<String> labels, String prot) {

		Functions funs = new Functions();
		String roleTmp = funs.changeIndex(role,index,maxIndex);
		ArrayList<String> outRolesTmp = new ArrayList<String>();
		int varAdd = 0;
		for(String el : outRole) {
			outRolesTmp.add(Functions.changeIndex(el,index,maxIndex));
		}

		int iA = -1;
		for(int i=0; i<mods.size(); i++) {
			if(mods.get(i).toPrint().equals(roleTmp)) {
				iA = i;
			}
		}
		String statementA = null;
		String label = "";
		ArrayList<String> stats = new ArrayList<String>();
		for(String el : outRolesTmp) {
			stats.add(null);
		}
		boolean sameRole = false;
		ArrayList<Integer> allA = new ArrayList<Integer>();
		ArrayList<Integer> allB = new ArrayList<Integer>();
		int stateCtmc = -1;
		ArrayList<Integer> statesB = new ArrayList<Integer>();
		for(int k=0; k<rates.size(); k++) {
			String stAprob = "";
			boolean firstLabel = false;
			sameRole = false;
			for(String el : outRolesTmp) {
				if(el.equals(roleTmp)) {
					sameRole = true;
				}
			}
			boolean stateUsed = false;
			if(!sameRole && (!isCtmc) || isCtmc) {
				while(!firstLabel) {
					String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
					StringBuilder salt = new StringBuilder();
					Random rnd = new Random();
					int size = 5;
					while (salt.length() < size) { // length of the random string.
						int indexLabel = (int) (rnd.nextFloat() * SALTCHARS.length());
						salt.append(SALTCHARS.charAt(indexLabel));
					}
					label = salt.toString();					
					if(labels==null) {
						labels = new ArrayList<String>();
						labels.add(label);
						firstLabel = true;
					}
					else {
						if(!labels.contains(label)) {
							firstLabel = true;
							labels.add(label);
						}
					}
				}
			}
			int checkStateUsed = -1;
			int stateA = ((ModuleNode) mods.get(iA)).getValueRecursion(prot);

			if(stateA == -1) {
				stateA = ((ModuleNode) mods.get(iA)).getMaxState();
				((ModuleNode) mods.get(iA)).setValueRecursion(prot,stateA);

			}
			else {

				int stateTmpNew = ((ModuleNode) mods.get(iA)).getNewState(prot);

				//if(((ModuleNode) mods.get(iA)).getMaxValueRecursion(prot)<=stateTmpNew && ((ModuleNode) mods.get(iA)).getNewState(prot)!=-1) {
				if(((ModuleNode) mods.get(iA)).getLastState()==-1) {
					if(stateTmpNew!=-1) {
						stateA = stateTmpNew;
					}
					else {
						stateA = ((ModuleNode) mods.get(iA)).getMaxValueRecursion(prot)+1;
					}
					checkStateUsed = stateA;
					((ModuleNode) mods.get(iA)).setLastState(stateA);
				}

				else {
					if(((ModuleNode) mods.get(iA)).getMaxValueRecursion(prot)<((ModuleNode) mods.get(iA)).getNewState(prot)) {
						stateA = ((ModuleNode) mods.get(iA)).getNewState(prot);
						stateA++;
						while((stateA)>=((ModuleNode) mods.get(iA)).getNewState(prot)) {
							((ModuleNode) mods.get(iA)).removeNewState(prot);

						}

					}
					else {

						stateA = ((ModuleNode) mods.get(iA)).getMaxValueRecursion(prot);
					}
				}


			}
			if(isCtmc && k==0) {
				stateCtmc = stateA;
			}

			if(isCtmc) {
				statementA = null;
			}
			int indexRate = rates.get(k).indexOf("*");
			String rateA = funs.changeIndex(rates.get(k).substring(0,indexRate),index,maxIndex);
			boolean ifte = false;
			for(String el : ((ModuleNode) mods.get(iA)).getCommands()) {
				if(el.contains("IFTE")) {
					statementA = el.substring(0,el.indexOf("IFTE"));
					ifte = true;
				}
			}

			if(statementA == null && (isCtmc || sameRole)) {
				if(isCtmc ) {
					if(rates.size()>1) {

						statementA = "[" + label + "] (" + roleTmp + "=" + stateCtmc + ") -> " ;

					}
					else {

						statementA = "[" + label + "] (" + roleTmp + "=" + stateA + ") -> " ;

					}
				}
				else {
					statementA = "[" + label + "] (" + roleTmp + "=" + stateA + ") -> " ;
				}
			}
			else if(statementA == null && !isCtmc) {
				statementA = "[] (" + roleTmp + "=" + stateA + ") -> " ;
				stAprob = "[" + label + "] (" + roleTmp + "="  ;

			}
			else if(statementA!=null && !isCtmc){
				if(!sameRole) {
					stAprob = "[" + label + "] (" + roleTmp + "="  ;
				}
				if(!ifte) {
					statementA = statementA  + " + ";
				}

			}
			String tmpStr = roleTmp+"=";
			int indexTmp = statementA.indexOf(tmpStr);
			int stateNewA = Character.getNumericValue(statementA.charAt(indexTmp+tmpStr.length()))+1;

			if(stateNewA == checkStateUsed) {
				((ModuleNode) mods.get(iA)).removeNewState(prot);

			}
			statementA = statementA + rateA + " : ";

			String upA = "";
			String genUpdates = updates.get(k).generateCode(mods,index,maxIndex,isCtmc,labels,prot);
			if(!genUpdates.equals(" ")) {
				int indexUp = genUpdates.indexOf("&&");
				if(!genUpdates.substring(0,indexUp).equals(" ")) {
					upA = Functions.returnStringNewIndex(genUpdates.substring(0,indexUp),index,maxIndex)+"&";

				}
			}
			boolean roleAContained = false;
			for(String tmp : statements.get(k).getRoles()) {
				if(roleTmp.equals(funs.changeIndex(tmp,index,maxIndex))) {
					roleAContained = true;
				}
			}						

			if(statements.get(k) instanceof RecNode){

				int stateRec = ((ModuleNode) mods.get(iA)).getValueRecursion(statements.get(k).toPrint());
				if(stateRec == -1) {

					((ModuleNode) mods.get(iA)).setValueRecursion(statements.get(k).toPrint(),stateA+k+1);
					allA.add(stateA+k+1);
					stateA = stateA + k +1;
				}
				else {

					allA.add(stateA);
					stateA = stateRec;
				}

			}
			else if( (!(statements.get(k) instanceof EndNode) && roleAContained) || !isCtmc) { 

				stateA = ((ModuleNode) mods.get(iA)).getMaxState() + k+1;
				allA.add(((ModuleNode) mods.get(iA)).getMaxState()+k+1);
			}
			if(statements.get(k) instanceof EndNode || statements.get(k) instanceof RecNode) {
				((ModuleNode) mods.get(iA)).setLastState(-1);
			}
			if(isCtmc || sameRole) {

				statementA = statementA + upA + "(" + roleTmp + "'=" ;
				if(statements.get(k) instanceof EndNode) {
					statementA = statementA + "TBD" ;
					allA.add(stateA+1);
				}
				if(!(statements.get(k) instanceof RecNode)) {
					stateA = stateA + 1 ;
				}

				statementA = statementA + stateA;
				allA.add(stateA);

				statementA = statementA +")";

			}

			else {

				statementA = statementA + "(" + roleTmp + "'=" + (stateA) +")";
				if(statements.get(k) instanceof EndNode) {
					stAprob = stAprob + (stateA) + ") -> 1 : " + upA + "(" + roleTmp + "'=" + "TBD" +")";
					allA.add(stateA+1);

				}
				else {
					stAprob = stAprob + (stateA) + ") -> 1 : " + upA + "(" + roleTmp + "'=" + (stateA+rates.size()) +")";
					allA.add(stateA+rates.size());

				}
				stateA = stateA  + 1;

			}

			int oldState = -1;
			for(int kk=0; kk<outRolesTmp.size(); kk++) {

				if(!sameRole) {

					String stBprob = "";
					allB = new ArrayList<Integer>();
					int iB = -1;
					for(int i=0; i<mods.size(); i++) {
						if(mods.get(i).toPrint().equals(outRolesTmp.get(kk))) {
							iB = i;
						}
					}

					int indexRateB = rates.get(0).indexOf("*");
					String rateB = funs.changeIndex(rates.get(k).substring(indexRate+1,rates.get(k).length()),index,maxIndex);

					String statementB = stats.get(kk);
					boolean ifteB = false;
					for(String el2 : ((ModuleNode) mods.get(iB)).getCommands()) {
						if(el2.contains("IFTE")) {
							statementB = el2.substring(0,el2.indexOf("IFTE"));
							ifteB = true;
						}
					}
					int stateB = ((ModuleNode) mods.get(iB)).getValueRecursion(prot);

					if(stateB == -1) {
						stateB = ((ModuleNode) mods.get(iB)).getMaxState();
						((ModuleNode) mods.get(iB)).setValueRecursion(prot,stateB);
					}
					else {

						if(((ModuleNode) mods.get(iB)).getMaxValueRecursion(prot)<=((ModuleNode) mods.get(iB)).getNewState(prot) && ((ModuleNode) mods.get(iB)).getNewState(prot)!=-1) {
							stateB = ((ModuleNode) mods.get(iB)).getNewState(prot);
							((ModuleNode) mods.get(iB)).setValueRecursion(prot,stateB);
							((ModuleNode) mods.get(iB)).removeNewState(prot);



						}
						else {
							stateB = ((ModuleNode) mods.get(iB)).getMaxValueRecursion(prot);
						}

					}

					if(isCtmc && k==0 ) {
						statesB.add(kk,stateB);
					}

					if(isCtmc ) {
						if(rates.size()==1) {
							statementB = "[" + label + "] (" + outRolesTmp.get(kk) + "=" + stateB + ") -> " ;
						}
						else {
							statementB = "[" + label + "] (" + outRolesTmp.get(kk) + "=" + statesB.get(kk) + ") -> " ;
							if(k>0) {
								allB.add(stateB);
							}

						}
					}
					else if(statementB == null && !isCtmc) {
						statementB = "[] (" + outRolesTmp.get(kk) + "=" + stateB + ") -> " ;
						stBprob = "[" + label + "] (" + outRolesTmp.get(kk) + "="  ;
					}
					else if(statementB!=null && !isCtmc){
						if(!sameRole) {
							stBprob = "[" + label + "] (" + outRolesTmp.get(kk) + "="  ;
						}
						if(!ifte) {
							statementB = statementB  + " + ";
						}
					}
					statementB = statementB + rateB + " : ";

					String upB = "";

					if(!genUpdates.equals(" ")) {
						int indexUp = genUpdates.indexOf("&&");
						if(!genUpdates.substring(indexUp+2,genUpdates.length()).equals(" ")) {
							upB = Functions.returnStringNewIndex(genUpdates.substring(indexUp+2,genUpdates.length()),index,maxIndex)+"&";
						}
					} 
					boolean roleBContained = false;
					for(String tmp : statements.get(k).getRoles()) {
						if(outRolesTmp.get(kk).equals(funs.changeIndex(tmp,index,maxIndex))) {
							roleBContained = true;
						}
					}
					if(statements.get(k) instanceof RecNode){

						int stateRec = ((ModuleNode) mods.get(iB)).getValueRecursion(statements.get(k).toPrint());
						if(stateRec == -1) {
							((ModuleNode) mods.get(iB)).setValueRecursion(statements.get(k).toPrint(),stateB+k);
							allB.add(stateB+k);
							stateB = stateB + k ;
						}
						else {
							allB.add(stateB);
							stateB = stateRec;

						}


					}
					else if( (!(statements.get(k) instanceof EndNode) && roleBContained) || !isCtmc) { 

						stateB = ((ModuleNode) mods.get(iB)).getMaxState()+1;
						allB.add(stateB);

					}
					if(isCtmc ) {
						statementB = statementB + upB + "(" + outRolesTmp.get(kk) + "'=" ;
						if(statements.get(k) instanceof EndNode) {
							statementB = statementB + "TBD" ;
							allB.add(stateB+1);

						}
						else {
							statementB = statementB + stateB;
						}
						statementB = statementB +")";
					}

					else {

						statementB = statementB + "(" + outRolesTmp.get(kk) + "'=" + (stateB) +")";
						if(statements.get(k) instanceof EndNode) {
							stBprob = stBprob + (stateB) + ") -> 1 : " + upB + "(" + outRolesTmp.get(kk) + "'=" + "TBD" +")";
							allB.add(stateB+1);

						}
						else {
							stBprob = stBprob + (stateB) + ") -> 1 : " + upB + "(" + outRolesTmp.get(kk) + "'=" + (stateB+rates.size()) +")";
							allB.add(stateB+rates.size());

						}
						stateB = stateB  + 1;

					}

					if(isCtmc || k==rates.size()-1) {
						((ModuleNode) mods.get(iB)).addCommand(statementB+";");
					}
					if(!isCtmc) {
						((ModuleNode) mods.get(iB)).addCommand(stBprob+";");
					}
					for(String el : ((ModuleNode) mods.get(iB)).getCommands()) {
						if(el.contains("IFTE")) {
							((ModuleNode) mods.get(iB)).getCommands().remove(el);
							break;
						}
					}
					for(int el : allB) {
						((ModuleNode) mods.get(iB)).setNewState(prot, el);

					}
				}

			}

			if(isCtmc || k==rates.size()-1) {
				((ModuleNode) mods.get(iA)).addCommand(statementA+";");
			}
			if(!isCtmc && !sameRole) {
				((ModuleNode) mods.get(iA)).addCommand(stAprob+";");
			}
			for(String el : ((ModuleNode) mods.get(iA)).getCommands()) {
				if(el.contains("IFTE")) {
					((ModuleNode) mods.get(iA)).getCommands().remove(el);
					break;
				}
			}

		}


		for(int el : allA) {
			((ModuleNode) mods.get(iA)).setNewState(prot, el);
		}


		for(int k=0; k<statements.size(); k++) {
			for(String el : outRolesTmp) {
				if(el.equals(roleTmp)) {
					sameRole = true;
				}
			}
			/*for(String el : outRole) {
				if(!role.equals(el) && !(statements.get(k) instanceof EndNode) && statements.get(k).getRoles().contains(el)) {
					int iB = -1;
					for(int i=0; i<mods.size(); i++) {
						if(mods.get(i).toPrint().equals(funs.changeIndex(el,index,maxIndex))) {
							iB = i;
						}
					}
					((ModuleNode) mods.get(iB)).setState();
					((ModuleNode) mods.get(iB)).setValueRecursion(prot, ((ModuleNode) mods.get(iB)).getState());
				}
			}*/

			statements.get(k).generateCode(mods,index,maxIndex,isCtmc,labels,prot);

		}


		return null;
	}

	@Override
	public ArrayList<String> getRoles() {
		ArrayList<String> roles = new ArrayList<String>();
		for(String el : outRole) {
			roles.add(el);
		}
		roles.add(role);
		return roles;
	}
}