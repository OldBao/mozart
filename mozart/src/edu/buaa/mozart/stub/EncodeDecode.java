package edu.buaa.mozart.stub;

import java.io.*;

public class EncodeDecode
{
    public static ByteArrayInputStream encode(String toConvert)
    {
        return new ByteArrayInputStream(toConvert.getBytes());
    }
    
    public static String decodeString(ByteArrayOutputStream toConvert)
    {
        return toConvert.toString();
    }
}

