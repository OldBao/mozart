package edu.buaa.mozart.ML;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeSegment {
	private static String OUTPUT = "output";
    private static String INPUT  = "input";
    private static String ACTION = "action";
    
    private Input mInput;
    private Output mOutput;
    private Action mAction;
    
    public CodeSegment(){
    	mInput = new Input();
    	mOutput = new Output();
    	mAction = new Action();
    }
    
    public void addConstant(String key, String value){
    	mAction.addConstant(key, value);
    }
    
    public void addTransferMap(String output, String input) throws ParseException{
        if (!mInput.getTuple().exists(input))
        	throw new ParseException("not exist input", 0);
        if (!mOutput.getTuple().exists(output))
        	mOutput.getTuple().addVar(output);
        mAction.getTuple().addVar(input);
    }
    
    public void addAction(String action){
    	mAction.getActionLines().add(action);
    }
    
    public void addOutputAction(String actionVar) throws ParseException{
        mAction.getTuple().addVar(actionVar);
    }
    
    public void addInput(String input){
    	if (!mInput.getTuple().exists(input)){
    		mInput.getTuple().addVar(input);
    	}
    }
    @Override
    public String toString(){
    	return mInput.toString() + ";\n" +
    			mOutput.toString() + ";\n" + 
    			mAction.toString();
    }
    
    class Input {
    	private Tuple mTuple;

    	public Input(){
            mTuple = new Tuple();
    	}
		public Tuple getTuple() {
			return mTuple;
		}

		public void setTuple(Tuple mTuple) {
			this.mTuple = mTuple;
		}
        
        @Override
        public String toString(){
        	return INPUT + mTuple;
        }
    }
    
    class Output {
    	private Tuple mTuple;

    	public Output(){
            mTuple = new Tuple();
    	}
		public Tuple getTuple() {
			return mTuple;
		}

		public void setTuple(Tuple mTuple) {
			this.mTuple = mTuple;
		}
        
        @Override
        public String toString(){
        	return OUTPUT + mTuple;
        }
    }
    
    class Action {
    	private List<String> mActionLines;
    	private Tuple mTuple;
        private Map<String, String> mConstants;
        
        public Action(){
        	mActionLines = new ArrayList<String>();
        	mTuple = new Tuple();
        	mConstants = new HashMap<String, String>();
        }
        
		public List<String> getActionLines() {
			return mActionLines;
		}
		public void setActionLines(List<String> mActionLines) {
			this.mActionLines = mActionLines;
		}
        public void setActionLines(String string){
        	String[] lines = string.split(";\n");
            for(String line : lines)
            	mActionLines.add(line);
        }
		public Tuple getTuple() {
			return mTuple;
		}
		public void setTuple(Tuple mTuple) {
			this.mTuple = mTuple;
		}
        
        public void addConstant(String key, String value){
        	mConstants.put(key, value);
        }
        
        @Override
        public String toString(){
            StringBuilder tmp = new StringBuilder();
            tmp.append(ACTION + "(\n");
            tmp.append("let\n");
            
            for (Entry<String, String> constant : mConstants.entrySet()) {
            	tmp.append("val " + constant.getKey() + "=" + constant.getValue() + ";\n");
            }
            tmp.append("in\n");
        	for (String line : mActionLines) {
        		tmp.append(line + ";\n");
        	}
            tmp.append(mTuple);
            tmp.append("\nend)");
            return tmp.toString();
        }
    }
}
