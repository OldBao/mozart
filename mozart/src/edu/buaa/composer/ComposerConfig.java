package edu.buaa.composer;

public class ComposerConfig {
	public static int WS_STUB_PORT = 9000;
    public static String SERVER_ADDR = "192.168.106.199";
   // public static String SERVER_ADDR = "localhost";
    public static String CONN_NAME = "conn";
    
    public static String SML_PATH = "V:/smls/";
    public static String BOOL_DECL_PATH = SML_PATH + "bool.sml";
    public static String SWRL_DECL_PATH = SML_PATH + "swrl.sml";
    
    public static String STRING_ENCODING = "stringEncode";
    public static String INTEGER_ENCODING = "integerEncode";
    public static String BOOL_ENCODING = "boolEncode";
    
    public static String STRING_DECODING = "stringDecode";
    public static String INTEGER_DECODING = "integerDecode";
    public static String BOOL_DECODING = "boolDecode";
}
