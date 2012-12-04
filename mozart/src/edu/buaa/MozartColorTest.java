package edu.buaa;
public class MozartColorTest {
	
	public static void main(String[] args){
        StringBuilder builder = new StringBuilder();
        builder.append("fuck");
        builder.replace(builder.length() -1, builder.length(), ",");
        System.out.println(builder.toString());
	}
}
