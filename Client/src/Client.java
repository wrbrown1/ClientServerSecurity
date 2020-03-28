import javax.swing.*;
import java.net.*;
import java.io.*;
import java.util.Random;
import java.util.Scanner;

public class Client {

    private Socket socket = null;
    private DataInputStream input = null;
    private DataInputStream inputFromServer = null;
    private DataOutputStream output = null;
    static Scanner scanner = new Scanner(System.in);
    static JFrame frame = new JFrame("GUI");

    public Client(String address, int port) throws IOException{

        socket = new Socket(address, port);
        input = new DataInputStream(System.in);
        inputFromServer = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        output = new DataOutputStream(socket.getOutputStream());
        String userInput = "";
        String serverResponse = "";
        SSLProtocol();
        Certificate c = new Certificate("Will Brown", "WRBJ042995", "123-WER67", "March 31st, 2020", "Certificate Authority Inc.", "1958-29UXY2W");
        c.setPublicKey(Encrypt(c.getPublicKey()));//Public key cryptography
        userInput = c.toString();
        output.writeUTF(c.toString());
        //this is what I'd imagine the GUI would be most concerned with
        //this while loop reads a string from the server, the sends a string to the server
        //serverResponse = string read from server
        //userInput = string sent to server
        while(!userInput.equals("Disconnect")){
            serverResponse = Decrypt(inputFromServer.readUTF());
            System.out.println(serverResponse);
            userInput = Encrypt(input.readLine());
            output.writeUTF(userInput);
        }

        socket.close();
        input.close();
        output.close();
    }

    public static String Encrypt(String string){
        if(string.equals("") || string == null){
            return "";
        }
        String encryptedString = "";
        String[] stringArray = string.split("");
        for(int i = 0; i < stringArray.length; i++){
            String value = stringArray[i];
            int charValue = value.charAt(0);
            String next = String.valueOf((char)(charValue + 1));
            if(value.equals("z")){
                next = "a";
            }else if(value.equals("Z")){
                next = "A";
            }else if(value.equals("9")){
                next = "0";
            }else if(value.equals("@")){
                next = "@";
            }else if(value.equals("/")){
                next = "/";
            }else if(value.equals(".")){
                next = ".";
            }else if(value.equals("-")){
                next = "-";
            }else if(value.equals(",")){
                next = ",";
            }
            encryptedString += next;
        }
        return encryptedString;
    }

    public static String Decrypt(String string){
        if(string.equals("") || string == null){
            return "";
        }
        String decryptedString = "";
        String[] stringArray = string.split("");
        for(int i = 0; i < stringArray.length; i++){
            String value = stringArray[i];
            int charValue = value.charAt(0);
            String next = String.valueOf((char)(charValue - 1));
            if(value.equals("a")){
                next = "z";
            }else if(value.equals("A")){
                next = "Z";
            }else if(value.equals("0")){
                next = "9";
            }else if(value.equals("@")){
                next = "@";
            }else if(value.equals("/")){
                next = "/";
            } else if(value.equals(".")){
                next = ".";
            }else if(value.equals("-")){
                next = "-";
            }else if(value.equals(",")){
                next = ",";
            }
            decryptedString += next;
        }
        return decryptedString;
    }

    public void SSLProtocol() throws IOException {
        String handshakeString = inputFromServer.readUTF();
        String decHandShakeString = Decrypt(handshakeString);
        output.writeUTF(decHandShakeString);
    }

    public static void main(String[] args) throws IOException {
        //System.out.print("Enter the IP addess of the server you wish to connect to: ");
        //String IP = scanner.next();
        Client client = new Client("192.168.1.250", 5000);
    }
}
