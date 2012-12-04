package edu.buaa.mozart.stub;

import java.util.*;
import java.net.*;
import java.io.*;

public class JavaCPN implements JavaCPNInterface
{
    
    private Socket socket;
    private InputStream input;
    private OutputStream output;
    
    public JavaCPN()
    {
        socket = null;
        input = null;
        output = null;
    }
    
    public void connect(String hostName, int port) throws IOException, UnknownHostException
    {
        socket = new Socket(InetAddress.getByName(hostName), port);
        input = socket.getInputStream();
        output = socket.getOutputStream();
    }
    
    public void accept(int port) throws IOException
    {
        ServerSocket serverSocket = new ServerSocket(port);
        socket = serverSocket.accept();
        input = socket.getInputStream();
        output = socket.getOutputStream();
        serverSocket.close();
    }
    
    public void send(ByteArrayInputStream sendBytes) throws SocketException
    {
        // A byte array representing a data packet
        byte[] packet;
        
        // While there are more than 127 bytes still to send ...
        while (sendBytes.available() > 127)
        {
            // ... create a 128 byte packet, ...
            packet = new byte[128];
            
            // ... set the header to 255, ...
            packet[0] = (byte)255;
            
            // ... read 127 bytes from the sequence of bytes to send, ...
            sendBytes.read(packet, 1, 127);
            
            // ... and send the packet to the external process.
            try
            {
                output.write(packet);
                output.flush();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        
        // Create a packet for any remaining data
        packet = new byte[sendBytes.available() + 1];
        
        // Set the header appropriately
        packet[0] = (byte)(sendBytes.available());
        
        // Read the remaining bytes into the packet
        sendBytes.read(packet, 1, sendBytes.available());
        
        // Send the packet to the external process
        try
        {
            output.write(packet);
            output.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public ByteArrayOutputStream receive() throws SocketException
    {
        // The complete sequence of bytes received from the external process
        ByteArrayOutputStream receivedBytes = new ByteArrayOutputStream();
        
        // The header received from the external process
        int header = -1;
        
        // The number of payload bytes received from the external process for a packet
        int numberRead = 0;

        // The total number of payload bytes received from the external process for
        // a packet, if not all are received immediately.
        int totalNumberRead = 0;

        // The payload received from the external process for each packet
        byte[] payload;
        
        while(true)
        {
            // Read a header byte from the input stream
            try
            {
                header = (int)input.read();
            }
            catch (SocketException e)
            {
                throw new SocketException("Socket closed while blocking to receive header.");
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            
            // If the header shows that the socket has closed ...
            if (header == -1)
                // ... throw a SocketException
                throw new SocketException("Socket closed by external process.");
            
            // If the header indicates another packet to follow ...
            else if (header >= 127)
            {
                // ... create 127 bytes of payload storage ...
                payload = new byte[127];
            }
            
            // ... else create storage of the appropriate size
            else
                payload = new byte[header];
            
            // Read the payload bytes from the input stream

            // Reset the total bytes received to 0 for this iteration 
            totalNumberRead = 0;

            // Loop until all data has been read for this packet.
            while(totalNumberRead < payload.length && numberRead != -1) 
            {
                try
                {
                    // Try to read all bytes in this packet
                    numberRead = input.read(payload,totalNumberRead, payload.length - totalNumberRead);
            
                    // If some bytes were read ...
                    if(numberRead != -1)

                        // ... record this many bytes as having been read
                        totalNumberRead = totalNumberRead + numberRead; 

                }
                catch (SocketException e)
                {
                  throw new SocketException("Socket closed while receiving data.");
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            
            // If not all bytes could be read ...
            if ((totalNumberRead < header || numberRead == -1) && header != 255) 

                // ... throw a SocketException
                throw new SocketException("Error receiving data.");

            // Write the payload data to the complete sequence of received bytes
            receivedBytes.write(payload,0,payload.length);
            
            // If no more bytes to follow, break from the loop.
            if (header <= 127)
                break;
        }

        // Return the received bytes
        return receivedBytes;
    }
    
    public void disconnect() throws IOException
    {
        input.close();
        output.close();
        socket.close();
    }
}


