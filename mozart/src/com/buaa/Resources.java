package com.buaa;

import java.io.IOException;
import java.io.InputStream;

public class Resources {
	public static void main(String[] args){
		InputStream in = ClassLoader.getSystemResourceAsStream("a.txt");
		try {
			System.out.print(in.read());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
