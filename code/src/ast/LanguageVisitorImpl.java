package ast;

import java.util.ArrayList;
import java.util.HashMap;

import lib.Pair;
import parser.LanguageBaseVisitor;
import parser.LanguageParser.*;

public class LanguageVisitorImpl extends LanguageBaseVisitor<Node>{

	private ArrayList<String> protocolsNames = new ArrayList<String>();


	@Override
	public Node visitProtocol(ProtocolContext ctx) {
		Node preamble = null;
		if(ctx.preamble()!=null) {
			preamble = visitPreamble(ctx.preamble());
		}
		
		int valueVar = -1;
		if(ctx.varDef()!=null) {
			valueVar = Integer.valueOf(ctx.varDef().INTEGER().getText());
		}
		ArrayList<Pair<String,Integer>> vars = null;
		ArrayList<Pair<String,String>> roles = null;
		HashMap<String,ArrayList<String>> varRoles = null;

		if(ctx.roleDef()!=null) {
			roles = new ArrayList<Pair<String,String>>();
			varRoles = new HashMap<String,ArrayList<String>>();
			for(RoleDefContext el : ctx.roleDef()) {
				String role = "";
				if(el.role().roleGroup()!=null) {
					role = el.role().roleGroup().ID().getText();
					roles.add(new Pair<String,String>(el.role().roleGroup().ID().getText(),"1"));
				}
				if(el.role().roleIndex()!=null) { 

					roles.add(new Pair<String,String>(el.role().roleIndex().ID().getText()+"["+el.role().roleIndex().index().CHAR().getText()+"]",el.indexSpec().upperBound.getText()));
					role = el.role().roleIndex().ID().getText()+"["+el.role().roleIndex().index().CHAR().getText()+"]";
				}
				ArrayList<String> tmpVars = null;
				if(el.roleSpec()!=null && el.roleSpec().size()>0) {
					tmpVars = new ArrayList<String>();
					for(RoleVarContext tmp : el.roleSpec(0).roleVar()) {
						tmpVars.add(tmp.DOUBLE_STRING().getText());
					}
				}
				varRoles.put(role,tmpVars);
			}
		}
		ArrayList<Node> modules = new ArrayList<Node>();
		for(String el : varRoles.keySet()) {
			if(el.contains("[i]")) {
				for(int i=1; i<=valueVar; i++) {
					String name = el.replace("[i]",Integer.toString(i));
					ArrayList<String> modVars = null;
					if(varRoles.get(el)!=null) {
						modVars = new ArrayList<String>();
						for(String var : varRoles.get(el)) {
							String tmpVar = var.substring(1,var.length()-1).replace("[i]",Integer.toString(i));
							modVars.add(tmpVar);
						}
					}
					modules.add(new ModuleNode(name,modVars));
				}
			}
			else {
				ArrayList<String> modVars = null;
				if(varRoles.get(el)!=null) {
					modVars = new ArrayList<String>();
					for(String var : varRoles.get(el)) {
						modVars.add(var.substring(1,var.length()-1));
					}
				}
				modules.add(new ModuleNode(el,modVars));
			}
		}
		ArrayList<Node> protocols = new ArrayList<Node>();
		for(int i=0; i<ctx.protocolID().size(); i++) {
			String name = ctx.protocolID().get(i).getText();
			protocolsNames.add(name);
			ArrayList<Node> blockStat = new ArrayList<Node>();
			for(StatementContext el : ctx.blockStatement().get(i).statement()) {
				blockStat.add(visitStatement(el));
			}
			protocols.add(new ProtocolNode(name,blockStat));
		}
		return new ProgramNode(preamble,modules,protocols,valueVar);
	}

	@Override 
	public Node visitPreamble(PreambleContext ctx){
		ArrayList<String> vars = new ArrayList<String>();
		for(VariableDeclContext el : ctx.variableDecl()) {
			vars.add(el.getText());
		}
		boolean isCtmc = false;
		if(ctx.modelType()!=null) {
			isCtmc = true;
		}
		return new PreambleNode(vars, isCtmc);
	}

	
	@Override
	public Node visitStatement(StatementContext ctx) {
		if(ctx.branch()!=null) {
			return visitBranch(ctx.branch());
		}
		else if(ctx.ifThenElse()!=null) {
			return visitIfThenElse(ctx.ifThenElse());
		}
		else if(ctx.rec()!=null) {
			return visitRec(ctx.rec());
		}
		else if(ctx.end()!=null) {
			return visitEnd(ctx.end());
		}
		return visitInternalAction(ctx.internalAction());
	}


	@Override 
	public Node visitEnd(EndContext ctx) {
		ArrayList<Node> roles = null;
		if(ctx.roles!=null) {
			roles = new ArrayList<Node>();
			for(RoleContext el : ctx.roles) {
				roles.add(visitRole(el));
			}
		}
		return new EndNode(roles);
	}
	@Override 
	public Node visitRec(RecContext ctx) {
		return new RecNode(ctx.protocolID().ID().getText());
	}


	@Override 
	public Node visitInternalAction(InternalActionContext ctx) {
		String role = visitRole(ctx.role()).toPrint();
		String rate = ctx.rate().DOUBLE_STRING().getText().substring(1,ctx.rate().DOUBLE_STRING().getText().length()-1);
		Node upds = visitMessage(ctx.message());
		Node stats = visitStatement(ctx.statement());
		return new InternalActionNode(rate,role,upds,stats);
	}
	
	@Override
	public Node visitActions(ActionsContext ctx) {

		String actionA = ctx.action.get(0).getText().substring(1,ctx.action.get(0).getText().length()-1);
		String actionB = null;
		if(ctx.action.size()>1) {
			actionB = ctx.action.get(1).getText().substring(1,ctx.action.get(1).getText().length()-1);
		}
		return new ActionNode(actionA,actionB);
	}



	@Override 
	public Node visitBranch(BranchContext ctx) {
		String role = visitRole(ctx.inputRole).toPrint();
		ArrayList<String> rolesOut = new ArrayList<String>();
		for(RoleContext el : ctx.outputRole) {
			rolesOut.add(visitRole(el).toPrint());
		}
		
		ArrayList<Node> stats = new ArrayList<Node>();
		ArrayList<String> rates = new ArrayList<String>();
		ArrayList<Node> messages = new ArrayList<Node>();
		ArrayList<Node> precs = new ArrayList<Node>();
		boolean isBranch = false;

		if(ctx.branchStat().size()>0) {
			for(BranchStatContext el : ctx.branchStat()) {
				stats.add(visitStatement(el.statement()));
				rates.add(el.rate().DOUBLE_STRING().getText().substring(1,el.rate().DOUBLE_STRING().getText().length()-1));
				if(el.updates().prec!=null) {
					precs.add(visitActions(el.updates().prec));
				}
				if(el.updates().upds != null) {
					messages.add(visitMessage(el.updates().upds));
				}	
				isBranch = el.BRANCH()!=null;
			}
			return new BranchNode(role,rolesOut,isBranch,rates,precs,messages,stats);
		}
		else {

			stats.add(visitStatement(ctx.commStat().statement()));
			rates.add(ctx.commStat().rate().DOUBLE_STRING().getText().substring(1,ctx.commStat().rate().DOUBLE_STRING().getText().length()-1));
			if(ctx.commStat().updates().prec!=null) {
				precs.add(visitActions(ctx.commStat().updates().prec));
			}
			if(ctx.commStat().updates().upds != null) {
				messages.add(visitMessage(ctx.commStat().updates().upds));
			}	
		}
		return new BranchNode(role,rolesOut,false,rates,precs,messages,stats);
	}



	@Override
	public Node visitMessage(MessageContext ctx) {
		Node actions = null;
		if(ctx.actions()!=null) {
			actions = visitActions(ctx.actions());
		}
		ArrayList<Node> loops = null;
		if(ctx.loop()!=null && ctx.loop().size()>0) {
			loops = new ArrayList<Node>();
			for(LoopContext el : ctx.loop()) {
				loops.add(visitLoop(el));
			}
		}
		String message = null;
		boolean beforeAction = false;
		if(ctx.first!=null) {
			beforeAction = true;
			message = ctx.first.getText().substring(1,ctx.first.getText().length()-1);
		}
		if(ctx.second!=null) {
			message = ctx.second.getText().substring(1,ctx.second.getText().length()-1);
		}
		return new MessageNode(actions,loops,message,beforeAction);
	}

	@Override
	public Node visitLoop(LoopContext ctx) {
		String role = "";
		if(ctx.role().roleIndex()!=null) {
			role = ctx.role().roleIndex().ID().getText()+"["+ctx.role().roleIndex().index().CHAR().getText()+"]";
		}
		else {
			role = ctx.role().roleGroup().ID().getText();
		}
		String message =  ctx.DOUBLE_STRING().getText().substring(1,ctx.DOUBLE_STRING().getText().length()-1);

		return new LoopNode(message,ctx.indexIteration.CHAR().getText(),ctx.upperBound.CHAR().getText(),ctx.op.getText(),role);
	}

	@Override
	public Node visitRole(RoleContext ctx) {
		String role = "" ;
		if(ctx.roleGroup()!=null) {
			role = ctx.roleGroup().ID().getText();
		}
		else {
			if(ctx.roleIndex().BRANCH()==null) {
				role = ctx.roleIndex().ID().getText()+"["+ctx.roleIndex().index().CHAR().getText()+"]";
			}
			else {
				role = ctx.roleIndex().ID().getText()+"["+ctx.roleIndex().index().CHAR().getText()+"+"+ctx.roleIndex().INTEGER().getText()+"]";
			}
		}
		return new RoleNode(role);
	}

	public Node visitIfThenElse(IfThenElseContext ctx) {
		Node elseStatement = null;
		if(ctx.elseStat!=null) {
			elseStatement = visitStatement(ctx.elseStat);
		}
		ArrayList<String> roles = new ArrayList<String>();
		for(RoleContext el : ctx.role()) {
			roles.add(el.getText());
		}
		ArrayList<String> conds = new ArrayList<String>();
		for(CondContext el : ctx.cond()) {
			conds.add(el.getText().substring(1,el.getText().length()-1));
		}
		return new IfThenElseNode(roles,conds,visitStatement(ctx.thenStat),elseStatement);
	}

}
