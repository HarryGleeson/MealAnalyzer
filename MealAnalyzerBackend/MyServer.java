import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import javax.imageio.ImageIO;

/* Server.java: This class takes a byte array representing an image from the android application,
 * converts it back to an image, saves it and uses it to run the back end Python classes. Then, each
 * identified food, its corresponding weight and the total fat in the meal are sent back to the application.
 */ 

public class MyServer{
	public static final long CURRENT_TIME_MILLIS = System.currentTimeMillis();

	public static void main(String[] args) throws IOException{
		String imagePath = receiveImage();
		String[] toSend = runBackend(imagePath);
		sendToClient(toSend);
	}
	
	public static String receiveImage() throws IOException {
		ServerSocket serverSocket = new ServerSocket(4442);
		System.out.println("| Server Started...\t|\n| IP: "+InetAddress.getLocalHost().getHostAddress()+"\t|\n| Port: "+serverSocket.getLocalPort()+"\t\t|\n-------------------------");
		Socket socket = serverSocket.accept();
		DataInputStream dIn = new DataInputStream(socket.getInputStream());
		serverSocket.close();
		System.out.println("Image Recieved");
		int length = dIn.readInt();
		
		byte[] image = new byte[length];
		dIn.readFully(image, 0, image.length);
		InputStream in = new ByteArrayInputStream(image);
		BufferedImage bImage = ImageIO.read(in);
		String filePath = ("UploadedImages/");
	    filePath+=CURRENT_TIME_MILLIS+".jpg"; //Current time used as filename to ensure it is unique
		File outputfile = new File(filePath);
		ImageIO.write(bImage, "jpg", outputfile);
		String imagePath = outputfile.getAbsolutePath();
		return imagePath;
	}
	
	public static String[] runBackend(String imagePath) {
		String s = null;
		String[] output = new String[50];
		int i=0;
		try {
			// using the Runtime exec method:
        		String execute = "PythonConfig/bin/python3.5 src/main.py ";
        		//execute += "--image "+imagePath ;
        		execute+="--image UploadedImages/sample.jpg";
        		Process p = Runtime.getRuntime().exec(execute);
            
            BufferedReader stdInput = new BufferedReader(new 
                 InputStreamReader(p.getInputStream()));

            // read the output from the command
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
                output[i]=s;
                i++;
            	}
            
            int results = Integer.parseInt(output[i-1]);
            int y = 0;
            String[] toSend = new String[results+1];
            for(int t = i-(results+2); t<i-1; t++) {
        	  		System.out.println("output["+t+"]:"+output[t]);
        	  		toSend[y] = output[t];
        	  		y++;
            }
            return toSend;
        }
	    catch (IOException e) {
	    		System.out.println("exception happened - here's what I know: ");
	    		e.printStackTrace();
	        System.exit(-1);
	    }
		return null;
	}
	
	public static void sendToClient(String[] toSend) {
		try {
			Socket sendSocket = new Socket("192.168.0.16", 4444);
			ObjectOutputStream out = new ObjectOutputStream(sendSocket.getOutputStream());
			out.writeObject(toSend);  
            out.close();
			sendSocket.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
}
