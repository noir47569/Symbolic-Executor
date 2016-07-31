
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;

public class ParseJava {

	public static int index_if = 0;
	public static int condition_flag = 0;

	public static void main(String[] args) {
		String fileName = "input6";
		CodeBlock root = new CodeBlock("-");
		readFile(root, fileName);
		//System.out.println(root.toString());
		outAllBranch(root);

	}

	private static boolean isOverWrite(String line, ArrayList<String> outList) {
		for (int i = 0; i < outList.size(); i++) {
			if (line.equals(outList.get(i))) {
				return true;
			}
		}
		return false;
	}

	private static void outAllBranch(CodeBlock root) {
		ArrayList<String> outList = new ArrayList<String>();
		String all = "";
		for (int i = 0; i <= Math.pow(2, index_if)-1; i++) {
			ArrayList<String> codes = new ArrayList<String>();
			outBranch(i, root, codes);
			
			//System.out.println("i="+i+", codes: "+codes);
			
			ExcuteCodes exc = new ExcuteCodes(codes);
			String result = exc.excute();
			//System.out.println("result: "+result);
			if (result != null) {
				if (i == 1) {
					all = result;
					outList.add(result);
				} else if (!isOverWrite(result, outList)) {
					all += "\n||\n" + result;
					outList.add(result);

				}
			}
		}
		//System.out.print(all);
		System.out.println("paths:(input variable x, output variable y)\n");
		for (int i=0; i<outList.size(); i++){
			System.out.println((i+1)+": "+outList.get(i));
			System.out.println("");
		}

	}
 
	// output the i-th branch
	private static void outBranch(int i, CodeBlock root, ArrayList<String> codes) {
		visit(root.getChildren(), i, codes);
	}

	private static void visit(ArrayList<CodeBlock> arrayList, int iteration, ArrayList<String> codes) {
        // searching through all children
		for (int i = 0; i < arrayList.size(); i++) {
			CodeBlock current = arrayList.get(i);
			// if current child is a IF statement
			if (current.getFlag().equals("if")) {
				// bitwise AND
				if ((current.condition_flag & iteration) > 0) {
					codes.add(current.getCondition());
				// current path does not include this IF condition, therefore include its negation
				} else {
					if (current.getCondition().trim().startsWith("!")) {
						codes.add(current.getCondition().substring(1));
					} else {
						codes.add("! " + current.getCondition());
					}

				}
			} 
            // current expression is not a IF/ELSE statement, so must be included
			else if (!current.getFlag().equals("else")) {
                codes.add(current.getCondition());
			}
            // an included IF condition, search whether its children are in this path
			if (current.getFlag().equals("if") && ((current.condition_flag & iteration) > 0)) {
				visit(current.getChildren(), iteration, codes);
			// current child is an ELSE statement
			} else if (current.getFlag().equals("else") && ((arrayList.get(i - 1).condition_flag & iteration) == 0)) {
				visit(current.getChildren(), iteration, codes);
			}
		}
	}

	private static void readFile(CodeBlock root, String fileName) {
		BufferedReader br = null;
		CodeBlock current = root;
		try {
			br = new BufferedReader(new FileReader(new File(fileName)));
			String line = null;
			while ((line = br.readLine()) != null) {
				
				// leading and trailing whitespace omitted
				line = line.trim();
				
				// ignore blank lines and comments. 
				if (line.startsWith("/*")){
					while (!line.contains("*/")){
						line = br.readLine();
					}
					line = line.substring(line.indexOf("*/")+2);
					line = line.trim();
				}
				
				if (line.length() == 0 || line.startsWith("//")) {
					continue;
				}

				current = parseLine(current, line);
			}
			//System.out.println(current.toString());
		} catch (IOException e) {
			System.out.println("File I/O error,file may not exist!");
		} finally {
			try {
				br.close();
			} catch (Exception e) {
				System.out.println("File I/O error,close file error!");
			}
		}
	}

	private static CodeBlock parseLine(CodeBlock current, String line) {

		if (line.startsWith("if") && line.endsWith("{")) {
			int beginIndex = line.indexOf("(");
			int endIndex = line.lastIndexOf(")");
			String condition = line.substring(beginIndex + 1, endIndex );
			CodeBlock newNode = new CodeBlock(condition);
			
			// represent conditions using bits 1(first if condition), 10(second if condition), 100, etc.
			if (condition_flag == 0) {
				condition_flag = 1;
			} else {
				condition_flag = condition_flag << 1;
			}
			index_if += 1;
			newNode.condition_flag = condition_flag;
			newNode.setFlag("if");
			current.getChildren().add(newNode);
            
			newNode.setParent(current);
			return newNode;
		}

		if (line.endsWith("{") && line.contains("else")) {
			CodeBlock newNode = new CodeBlock("-");
			current.getChildren().add(newNode);
			newNode.setParent(current);
			newNode.setFlag("else");
			return newNode;
		}

		if (line.startsWith("}")) {
			return current.getParent();

		}

		CodeBlock newNode = new CodeBlock(line);
		current.getChildren().add(newNode);

		return current;

	}

}
