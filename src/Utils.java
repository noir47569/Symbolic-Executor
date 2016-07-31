
public class Utils {


	public static String deleteBracket (String result) {
		result = result.trim();
		if (result.startsWith("(") && result.endsWith(")")) {
			result = result.substring(1, result.length() - 1);
		}
		return result;
	}
	public static String addBracket (String result) {
		result = result.trim();
		return "(" + result + ")";
	}
	
	public static String deleteSign (String result) {
		if (result.endsWith("&& ")) {
			return result.substring(0, result.length()-3);
		}
		return result;
	}
	
	public static boolean isSurBracket (String result) {
		result = result.trim();
		if (result.startsWith("(") && result.endsWith(")")) {
			return true;
		}
		return false;
	}
	
	public static boolean redundant(String str){
		str = str.trim();
		if (str.startsWith("(")&& str.endsWith(")")){
			str = str.substring(1,str.length()-1);
			if (str.startsWith("(")&& str.endsWith(")")){
				return true;
			}
		}
		return false;
	}
}
