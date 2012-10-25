package edu.buaa.utils;

import java.util.HashSet;
import java.util.Set;

public class IDFactory {
	private IDFactory(){
		mIdSet = new HashSet<Integer>();
	}
	
	public static IDFactory getInstance(){
		if (mInstance == null)
			mInstance= new IDFactory();
		
		return mInstance;
	}
	
	public String getRandomId(){
		int fuckID = 0;
		do {
			for (int i = 0; i < ID_LENGTH; i++){
				fuckID += (int)(Math.random() * 10);
				fuckID *= 10;
			}
		}while(mIdSet.contains(fuckID));
		mIdSet.add(fuckID);
		
		return "ID" + fuckID;
	}

	private static int ID_LENGTH = 5;
	private static IDFactory mInstance;
	private static Set<Integer> mIdSet;
}
