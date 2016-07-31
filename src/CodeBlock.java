import java.util.ArrayList;


public class CodeBlock {



	private ArrayList<CodeBlock> children = new ArrayList<CodeBlock>();
	private CodeBlock parent = null;
	private String condition = "-";
	private String flag = "-";
	public int condition_flag = 0;
	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}


	public CodeBlock(String condition) {
		this.condition = condition;
	}

	@Override
	public String toString() {
		
		return "CodeBlock [condition=" + condition + ", children=" + children 
				+ ", flag=" + flag + ", condition_flag=" + condition_flag+"]";
		
		
	}
	public ArrayList<CodeBlock> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<CodeBlock> children) {
		this.children = children;
	}

	public CodeBlock getParent() {
		return parent;
	}

	public void setParent(CodeBlock parent) {
		this.parent = parent;
	}


	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition){
		this.condition = condition;
	}

	

	
}
