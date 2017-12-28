package chat;
import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.Scanner;

public class Server {
	
	/*Static Names*/
	private static String name;
	private static String otherName;	
	
	public static void main (String[] args) throws Exception
	{
		Scanner s = new Scanner(System.in);
		while (name == null || name.isEmpty())
		{
			try
			{
				System.out.println("What name would you like to use?");
				name = s.nextLine();
				if (name.isEmpty())
				{
					System.out.println("Please enter a non-empty name! Try again.");
				}
				
			}
			catch (Exception e)
			{
				System.out.println("Please enter proper input!");
			}
		}
		
		System.out.println("Great name! Press Enter to begin connection attempt.");
		s.nextLine();
		
		RSA encryptionSystem = new RSA (1024);
		ServerSocket server = new ServerSocket(3000);
		Socket socket = server.accept();
	
		BufferedReader keyboardReader = new BufferedReader(new InputStreamReader(System.in));
		OutputStream out = socket.getOutputStream();
		PrintWriter write = new PrintWriter(out, true);
	
		InputStream in = socket.getInputStream();
		BufferedReader receiveReader = new BufferedReader(new InputStreamReader(in));
		
		
		//Share public key and modulus with other user!
		String receiveMsg = "", sendMsg = "";
		while(true)
		{
			try
			{
				if ((receiveMsg = receiveReader.readLine()) != null)
				{
					if (receiveMsg.startsWith("public-"))
					{
						encryptionSystem.setGivenPublicKey(new BigInteger(receiveMsg.substring(7)));
						write.println("public-" + encryptionSystem.getPublicKey());
						write.flush();
						receiveMsg = "";
						encryptionSystem.setReceivedPublic(true);
					}
					
					else if (receiveMsg.startsWith("modulus-"))
					{
						encryptionSystem.setGivenModulus(new BigInteger(receiveMsg.substring(8)));
						write.println("modulus-" + encryptionSystem.getModulus());
						write.flush();
						receiveMsg = "";
						encryptionSystem.setReceivedModulus(true);
					}
					
					else if (receiveMsg.startsWith("name-"))
					{
						otherName = receiveMsg.substring(5);
						receiveMsg = "";
						write.println("name-" + name);
						write.flush();
					}
				}
				if (encryptionSystem.isReceivedModulus() && encryptionSystem.isReceivedPublic() && otherName != null)
				{
					break;
				}
			}
		
			catch (SocketException se)
			{
				continue;
			}
		}
	
		System.out.println("Server ready for chat!");
		receiveMsg = "";
		sendMsg = "";
		System.out.println("public: " + encryptionSystem.getGivenPublicKey());
		System.out.println("modulus: " + encryptionSystem.getGivenModulus());
		System.out.println("\n\n\n\nConnected to " + otherName + "\nHave Fun Chatting securely over a 1024-bit RSA Encryption\n");
		
		while(true)
		{
			if ((receiveMsg = receiveReader.readLine()) != null)
			{
				String message;
				BigInteger decMsg;
				
				decMsg = encryptionSystem.decrypt(receiveMsg);
				message = encryptionSystem.convertByteArrToString(decMsg.toByteArray());
				
				if (!message.equalsIgnoreCase("q!"))
					System.out.println("$" + otherName + ": " + message);
				else
					break;
			}
			
			String encMsg = "";
			
			System.out.print("$" + name + ": ");
			sendMsg = keyboardReader.readLine();			
			encMsg = encryptionSystem.encrypt(sendMsg);		
			write.println(encMsg);
			write.flush();
			
			if (sendMsg.equalsIgnoreCase("q!"))
				break;
		}
		
		if (!sendMsg.equalsIgnoreCase("q!"))
			System.out.println("Your Friend has left the chat!");
		else
			System.out.println("You have quit the chat!");
		
		System.out.println("Come back soon!\\nPress ENTER to EXIT");
		keyboardReader.readLine();
		
		server.close();
		socket.close();
		s.close();
	}
}