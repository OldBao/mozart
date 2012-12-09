package edu.buaa;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MozartChoice {
	public static void main(String[] argc){
        String name = "([^\\d][^\\s]*)";
		Pattern pattern = Pattern.compile("^\\([" +
										  "(\\s*" + name + "\\s*)," +
										  "|(\\s*" + name + "\\s*)]*" +
										  "\\)");
        Pattern namePattern = Pattern.compile(name);
        Matcher nameMatcher = namePattern.matcher("zhanggx");
        Matcher matcher = pattern.matcher("(hello, world,)");
        
        Pattern sCodePattern = 
        		Pattern.compile("input(.*);\\n" +
        						"output(.*);\\n"+
        						"action\\((.*)(\\(.*\\))\\);?$");
        Matcher codematcher = sCodePattern.matcher("input(a,b,c);\n" +
        										   "output(c,d);\n"+
        										   "action(" +
        										   "openConnection();" +
        										   "send();" +
        										   "fuck();" +
        										   "(" +
        										   "a," +
        										   "b" +
        										   "))");
        
        if (codematcher.find()){
            System.out.println("name Found\n");
        	for( int i = 0; i <= codematcher.groupCount(); i++){
            	System.out.print("group " + i + ":");
        		System.out.println(codematcher.group(i));
        	}
        }
	}
}
