import java.awt.print.Printable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;


public class ExcuteCodes {
	private ArrayList<String> codes;

	public ExcuteCodes(ArrayList<String> codes) {
		this.codes = codes;
	}

	
	public String excute(){
		HashMap<String, String> map = new HashMap<String, String>();
		ArrayList<String> conditions = new ArrayList<String>();
		for(int i= 0; i< codes.size(); i++ ){
			String code = codes.get(i);
			String condition = cla(code,map);
			/*
			System.out.println("code: "+code);
			System.out.println("map: "+map);
			System.out.println("condition:"+condition);
			*/
			if (condition != null) {
				conditions.add(condition);
			}
		}
		//System.out.println("");
		
		
		//output condition
		String conditionLine = "condition(s): ";
		for (int i = 0; i < conditions.size(); i++) {
			String c = conditions.get(i);
			//System.out.println(c);
			String sign = getSign(c);
			if (sign=="") {
				System.out.println("error: wrong path condition setting");
				return null;
			}
			String[] strs = c.split(sign);
			String left = strs[0];
			String right = strs[1];
			// reorder conditions in the form n Op x to the other way round
			if (right.equals("x") && isNumeric(left)){
				//System.out.println(c);
				left = strs[1];
				right = strs[0];
				sign = reverse(sign);
				c = left+sign+right;
				conditions.set(i, c);
			}
			
			
			conditionLine += conditions.get(i) + " && ";
		}
		conditionLine = Utils.deleteSign(conditionLine);
		if (conditions.size()>1 && simpleConditions(conditions)){
			conditionLine = "condition(s): ";
			ArrayList<String> sme = new ArrayList<String>();
			ArrayList<String> gt = new ArrayList<String>();
		    for (int i=0; i<conditions.size(); i++){
		    	if (getSign(conditions.get(i)).equals("<")||getSign(conditions.get(i)).equals("<=")){
		    		sme.add(conditions.get(i));
		    	}
		    	else gt.add(conditions.get(i));
		    }
		    //System.out.println("sme: "+sme);
		    //System.out.println("gt: "+gt);
		    int small = 0; int large = 0;
		    if (!sme.isEmpty()){
		        int extreme = Integer.parseInt(sme.get(0).split(getSign(sme.get(0)))[1]);
		        String sign = getSign(sme.get(0));
		        if(sme.size()>1){
		        	for (int i=1; i<sme.size(); i++) {
		        		String c = sme.get(i);
		        		int value = Integer.parseInt(c.split(getSign(c))[1]);
		        		if (value==extreme && getSign(c).equals("<")) 
		        			sign = "<";
		        		else if(value<extreme){
		        			extreme = value;
		        			sign = getSign(c);
		        		}
		        	}
		        }
		        small = extreme;
		        conditionLine += "x"+sign+String.valueOf(extreme);
		    }
		    if (!gt.isEmpty()){
		        int extreme = Integer.parseInt(gt.get(0).split(getSign(gt.get(0)))[1]);
		        String sign = getSign(gt.get(0));
		        if(gt.size()>1){
		        	for (int i=1; i<gt.size(); i++) {
		        		String c = gt.get(i);
		        		int value = Integer.parseInt(c.split(getSign(c))[1]);
		        		if (value==extreme && getSign(c).equals(">")) 
		        			sign = ">";
		        		else if(value>extreme){
		        			extreme = value;
		        			sign = getSign(c);
		        		}
		        	}
		        }
		        large = extreme;
		        conditionLine += " && x"+sign+String.valueOf(extreme);
		  }
		  if (small>large) conditionLine+="an infeasible path";
		}
		//output vars
		Iterator<Map.Entry<String, String>> entries = map.entrySet().iterator();  
		

		
		
		String varLine = "   variable values: ";
		ArrayList<String> vars = new ArrayList<String>();	
		
		String function = "";
		
		for (Entry<String, String> entry : map.entrySet()) { 
			if (!entry.getKey().equals("x")&&!entry.getKey().equals(" ")){
			    varLine += entry.getKey() + "=" + entry.getValue() + " && ";
			    vars.add(entry.getKey() + "=" + entry.getValue());
			}
            if ("y".equals(entry.getKey())){
            	function = entry.getValue();
            }
		}  
		//System.out.println(function);
		varLine = Utils.deleteSign(varLine);
		
		String period = "   periodicity: "+periodicityDetect(function);

		if (!conditionLine.trim().isEmpty() & !varLine.trim().isEmpty()) {
			return conditionLine + "\n" + varLine+"\n"+period;
		}else if (varLine.trim().isEmpty()) {
			return conditionLine;
		}else if (conditionLine.trim().isEmpty()) {
			return varLine;
		}else {
			return null;
		}
	}

	private String cla(String code,HashMap<String, String> map) {
		if (code =="-") {return ("");}
		if (code.endsWith(";")) {
			code = code.replace(";", "");
		}
		if (code.contains(":=")){
			String[] vars = code.split(":=");
			String key = vars[0].trim(); // x
			String value = vars[1].trim();  // x+1024
			Calculate calculate = new Calculate();
			
			// translate string into list, e.g. "x+1024" into {"x","+","1024"}
			ArrayList result = calculate.getStringList(value); 
			
			// translate infix to postfix, e.g. from a+b*c+(d*e+f)*g into abc*+de*f+g*+
			result = calculate.getPostOrder(result); 
			String out = calculate.calculateVar(result,map); 
			map.put(key, out);
		}else {
			// negation case
			String out = "";
			Boolean negation = false;
			if (code.trim().startsWith("!")) {
				negation = true;
				code = code.substring(1);
		    }
			code =  Utils.deleteBracket(code);
			String split = getSign(code);
			String[] splitCodes = code.split(split);
			String pre = splitCodes[0];
			String back = splitCodes[1];
			pre = getCal(pre,map);
			back = getCal(back,map);
			if (negation) {
				split = negate(split);
				out += pre + split + back;
			} else {
				out += pre + split + back ;
			}
			return out;
		}	
		return null;
	}
	
    private String negate(String split){
    	switch(split){
    	case ">": return "<=";
    	case "<": return ">=";
    	case ">=": return "<";
    	case "<=": return ">";
    	default: return "";
    	}
    }
    
    private String reverse(String sign){
    	switch(sign){
    	case ">": return "<";
    	case "<": return ">";
    	case ">=": return "<=";
    	case "<=": return ">=";
    	default: return "";
    	}
    }
    
	private String getSign(String code){
		if (code.contains("<=")) return("<=");
		if (code.contains(">=")) return(">=");
		if (code.contains(">")) return(">");
		if (code.contains("<")) return("<");
		if (code.contains("=")) return("=");
		return ("");
	}
	
    private boolean simpleConditions(ArrayList<String> conditions){
    	for (int i=0; i<conditions.size(); i++){
    		String[] c = conditions.get(i).split(getSign(conditions.get(i)));
    		if (!(c[0].equals("x") && isNumeric(c[1]))) return false;
    	}
    	return true;
    }
    
	private String getCal(String value,HashMap<String, String> map) {
		Calculate calculate = new Calculate();
		ArrayList result = calculate.getStringList(value); // string to list
		result = calculate.getPostOrder(result); // infix to postfix
		String out = calculate.calculateVar(result,map); // calculate
		return out;
	}
	
	private String periodicityDetect(String function){
		if (function.startsWith("s")||function.startsWith("c")||function.startsWith("t")){
			if (function.endsWith(")")){
				String inside = function.substring(2,function.length()-1);
				return ("y("+inside+ ") ="+"y("+inside+"+2pi)");
			}
		}
		return "(default result) not applicable";
	}
    
	
	private boolean isNumeric(String str){
		try{
			double d = Double.parseDouble(str);
		}
		catch(NumberFormatException nfe){
			return false;
		}
		return true;
	}
}
