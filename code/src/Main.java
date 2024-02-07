import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import ast.LanguageVisitorImpl;
import ast.Node;
import parser.LanguageLexer;
import parser.LanguageParser;

public class Main {

	public static void main(String[] args) throws Exception {
		String fileName = "./generated-sources/example";
		FileInputStream is = new FileInputStream(fileName+".language");
		@SuppressWarnings("deprecation")
		ANTLRInputStream input = new ANTLRInputStream(is);
		LanguageLexer lexer = new LanguageLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		LanguageParser parser = new LanguageParser(tokens);
		ParseTree t = parser.protocol();
		LanguageVisitorImpl visitor = new LanguageVisitorImpl();
		Node ast = visitor.visit(t);
		String code = ast.generateCode(null,0,0,false,null,null);
		File file = new File(fileName+".prism");
		if (!file.exists()) {
			file.createNewFile();
		}
		BufferedWriter output = new BufferedWriter(new FileWriter(file));
		output.write(code);
		output.close();
	}

}
