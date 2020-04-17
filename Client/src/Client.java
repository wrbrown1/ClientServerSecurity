import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Client {

    private SSLSocket socket = null;
    private DataInputStream input = null;
    private DataInputStream inputFromServer = null;
    private DataOutputStream output = null;
    //static Scanner scanner = new Scanner(System.in);
    //static JFrame frame = new JFrame("GUI");

    public Client(String address, int port) throws IOException{

        socket = new SSLSocket(address, port) {
            @Override
            public String[] getSupportedCipherSuites() {
                return new String[0];
            }

            @Override
            public String[] getEnabledCipherSuites() {
                return new String[0];
            }

            @Override
            public void setEnabledCipherSuites(String[] strings) {

            }

            @Override
            public String[] getSupportedProtocols() {
                return new String[0];
            }

            @Override
            public String[] getEnabledProtocols() {
                return new String[0];
            }

            @Override
            public void setEnabledProtocols(String[] strings) {

            }

            @Override
            public SSLSession getSession() {
                return null;
            }

            @Override
            public void addHandshakeCompletedListener(HandshakeCompletedListener handshakeCompletedListener) {

            }

            @Override
            public void removeHandshakeCompletedListener(HandshakeCompletedListener handshakeCompletedListener) {

            }

            @Override
            public void startHandshake() throws IOException {

            }

            @Override
            public void setUseClientMode(boolean b) {

            }

            @Override
            public boolean getUseClientMode() {
                return false;
            }

            @Override
            public void setNeedClientAuth(boolean b) {

            }

            @Override
            public boolean getNeedClientAuth() {
                return false;
            }

            @Override
            public void setWantClientAuth(boolean b) {

            }

            @Override
            public boolean getWantClientAuth() {
                return false;
            }

            @Override
            public void setEnableSessionCreation(boolean b) {

            }

            @Override
            public boolean getEnableSessionCreation() {
                return false;
            }
        };
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
            userInput = input.readLine();
            if(serverResponse.equals("Enter your password: ") || serverResponse.equals("Enter the patient's password: ")){
                output.writeUTF(getMd5(userInput));
            }else{
                output.writeUTF(Encrypt(userInput));
            }
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
    public static String getMd5(String input)
    {
        try {

            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest(input.getBytes());
            BigInteger x = new BigInteger(1, digest);
            String hash = x.toString(16);
            while (hash.length() < 32) {
                hash = "0" + hash;
            }
            return hash;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client("192.168.1.205", 5000);
        //String s = "Test";
        //System.out.println("Your HashCode Generated by MD5 is: " + getMd5(s));
        //ed54b2c479f8c85bf0a8aa68b7e75b00
    }
}
