package edu.buaa.mozart.ML;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class Tuple {
    private List<String> mTuples;
    
    public Tuple(){
    	mTuples = new ArrayList<String>();
    }
    
    public void addVar(String var){
    	mTuples.add(var);
    }
    
    public void addTuple(Tuple t){
    	for (String var : t.mTuples){
    		if (!exists(var)){
    			mTuples.add(var);
    		} else {
    			System.out.println("add already exists tuple " + var);
    		}
    	}
    }
    
    public static Tuple parseFromString(String tuple) throws ParseException{
        Tuple newTuple = new Tuple(); 
    	String tmp = tuple.trim();
        if (!tmp.startsWith("(") || !tmp.endsWith(")"))
            throw new ParseException("Tuple invalid " + tuple, 0);
        
        String[] vars = tmp.substring(1, tmp.length() - 1).split(",");
        for (String var : vars) {
            if(var.trim().contains(" "))
            	throw new ParseException("Tuple invalid", 0);
        	newTuple.addVar(var.trim());
        }
        return newTuple;
    }
    
    public int size(){
    	return mTuples.size();
    }
    
    public boolean exists(String var){
    	for (String v : mTuples){
    		if (v.equals(var))
    			return true;
    	}
    	return false;
    }
    
    @Override
    public String toString(){
    	if (mTuples.size() == 0)
    		return "()";
    	
    	int i;
        StringBuilder tuplebuilder = new StringBuilder();
        tuplebuilder.append("(");
    	for (i = 0; i < mTuples.size() - 1; i++){
    		tuplebuilder.append(mTuples.get(i));
            tuplebuilder.append(",");
    	}
        tuplebuilder.append(mTuples.get(i));
        tuplebuilder.append(")");
        return tuplebuilder.toString();
    }

}
