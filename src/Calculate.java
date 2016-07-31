// Adopted from yhhazr's implementation, 
// source: http://blog.csdn.net/yhhazr/article/details/7947962
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;


public class Calculate {
    
	
	
	public ArrayList<String> getStringList(String str) {
		ArrayList<String> result = new ArrayList<String>();
		String num = "";
		for (int i = 0; i < str.length(); i++) {
			if (Character.isDigit(str.charAt(i))) {
				num = num + str.charAt(i);
			} else {
				if (num != "") {
					result.add(num);
				}
				result.add(str.charAt(i) + "");
				num = "";
			}
		}
		if (num != "") {
			result.add(num);
		}
		return result;
	}
	
	// translate infix to postfix, e.g. from a+b*c+(d*e+f)*g into abc*+de*f+g*+
	public ArrayList<String> getPostOrder(ArrayList<String> inOrderList) {
		ArrayList<String> result = new ArrayList<String>();
		Stack<String> stack = new Stack<String>();
		for (int i = 0; i < inOrderList.size(); i++) {
			if (!isSign(inOrderList.get(i))) {
				result.add(inOrderList.get(i));
			} else {
				switch (inOrderList.get(i).charAt(0)) {
				case '(':
					stack.push(inOrderList.get(i));
					break;
				case ')':
					while (!stack.peek().equals("(")) {
						result.add(stack.pop());
					}
					stack.pop();
					if (!stack.isEmpty()&&!isBinaryOp(stack.peek())&&!("(".equals(stack.peek()))&&!(")".equals(stack.peek()))) 
						result.add(stack.pop());
					break;
				default:
					// if the priority of peek is greater than current, then pop it out before pushing current into the stack
					// compare returns true if priority of peek > current
					while (!stack.isEmpty() && compare(stack.peek(), inOrderList.get(i))) {
						result.add(stack.pop());
					}
					stack.push(inOrderList.get(i));
					break;
				}
			}
		}
		while (!stack.isEmpty()) {
			result.add(stack.pop());
		}
		return result;
	}

	
	String[] signs = { "+", "-", "*", "/", "(", ")","^","s","c","t","e","l"};
    String[] binarys = {"+","-", "*", "/","^"};

	public boolean isSign(String str) {
		for (int i = 0; i < signs.length; i++) {
			if (str.equals(signs[i])) {
				return true;
			}
		}
		return false;

	}

	public boolean isBinaryOp(String str){
		for (int i=0; i<binarys.length; i++){
			if (str.equals(binarys[i])){
				return true;
			}
		}
		return false;
	}
	
	// process: put numbers into stack, whenever an operation x is encountered, pull out the first 2 
	// elements a,b in the stack, calculate a x b and put it back into the stack.	
	public String calculateVar(ArrayList<String> postOrder, HashMap<String, String> map) {
		Stack stack = new Stack();
		for (int i = 0; i < postOrder.size(); i++) {
			if (!isSign(postOrder.get(i))) {
				// find the value in the map corresponding to the key(x,y,z,etc.)
				String value = getValue(postOrder.get(i),map);
				stack.push(value);
			} else {
				String str = postOrder.get(i);
				String res = "";
				if (isBinaryOp(str)){
					String back = (String) stack.pop();
					String front = (String) stack.pop();
					switch (postOrder.get(i).charAt(0)) {
					case '+':
						if (Utils.redundant(front)||Utils.deleteBracket(front).length()==1){
						    front = Utils.deleteBracket(front);
						}
						if (Utils.redundant(back)||Utils.deleteBracket(back).length()==1){
						    back = Utils.deleteBracket(back);
						}
						res = "(" + front + "+" + back + ")";
						break;
					case '-':
						if (Utils.redundant(front)||Utils.deleteBracket(front).length()==1){
						    front = Utils.deleteBracket(front);
						}
						if (Utils.redundant(back)||Utils.deleteBracket(back).length()==1){
						    back = Utils.deleteBracket(back);
						}
						res = "(" + front + "-" + back + ")";
						break;
					case '*':
						if (Utils.redundant(front)||Utils.deleteBracket(front).length()==1){
						    front = Utils.deleteBracket(front);
						}
						if (Utils.redundant(back)||Utils.deleteBracket(back).length()==1){
						    back = Utils.deleteBracket(back);
						}
						res = front + "*" + back;
				
						break;
					case '/':
						if (Utils.redundant(front)||Utils.deleteBracket(front).length()==1){
						    front = Utils.deleteBracket(front);
						}
						if (Utils.redundant(back)||Utils.deleteBracket(back).length()==1){
						    back = Utils.deleteBracket(back);
						}
						res = front + "/" + back;
		
						break;
					case '^':
						if (Utils.redundant(front)||Utils.deleteBracket(front).length()==1){
						    front = Utils.deleteBracket(front);
						}
						res = "("+front+"^"+back+")";
					}
				}
				else{
					String element = (String) stack.pop();
					element = Utils.deleteBracket(element);
					res = str+"("+element+")";
				}
				stack.push(res);
			}

		}
		String result = (String) stack.pop();
		if (result.startsWith("(") && result.endsWith(")")) {
			result = Utils.deleteBracket(result);
		}
		return result;
	}


	private String getValue(String key, HashMap<String, String> map) {
		
		try {
			int value = Integer.parseInt(key);
			return value + "";
		} catch ( NumberFormatException e) {
		
		}

		if (map.containsKey(key)) {
			return map.get(key);
		}else{
			map.put(key, key);
			return map.get(key);
		}
				
	}

	public boolean compare(String peek, String cur) {
		switch(peek){
		case "^":  if ("^".equals(cur)) return true;
		case "s":
		case "c":
		case "e":
		case "t":  if ("s".equals(cur) || "c".equals(cur) || "e".equals(cur)|| "t".equals(cur) || "^".equals(cur)) return true;
		case "*":
		case "/":  if (!("+".equals(cur) || "-".equals(cur))) return true;
		}
		return false;
	}
	
}
