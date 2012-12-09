package edu.buaa.mozart.ML;

import java.text.ParseException;

import edu.buaa.mozart.notes.ComposeException;

import junit.framework.TestCase;


public class CodeSegmentTest extends TestCase {
	String mCode = "input(a,b);\n" +
			  "output(c,d);\n" + 
			  "action(\n" +
			  "let\n" +
			  "val mozart_ws=\"http://owls.buaa.edu.cn\";\n" +
			  "in\n" +
			  "openConnection(\"localhost\", 9000);\n" +
			  "(a,b)\n"+
			  "end)";
	private CodeSegment getFixtures(){
		try {
            CodeSegment cs = new CodeSegment();
            cs.addInput("a");
            cs.addInput("b");
            cs.addTransferMap("c", "a");
            cs.addTransferMap("d", "b");
            cs.addConstant("mozart_ws", "\"http://owls.buaa.edu.cn\"");
            cs.addAction("openConnection(\"localhost\", 9000)");
            return cs;
		} catch (ComposeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return null;
	}
    
    public void testParse(){
    	CodeSegment cs = getFixtures();
        super.assertEquals(mCode, cs.toString());
    }
}
