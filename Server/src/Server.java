import java.net.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Server {

    private Socket socket = null;
    private ServerSocket serverSocket = null;
    private DataInputStream input = null;
    private DataOutputStream output = null;
    private ArrayList<User> users;
    private ArrayList<Patient> patients;
    private User currentUser;
    String userInput = "";
    Scanner in = new Scanner(System.in);
    int steps = 0;
    int IDcount = 0;
    int loginAttempts;
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    LocalDateTime now = LocalDateTime.now();

    public Server(int port) throws IOException{
        serverSocket = new ServerSocket(port);
        System.out.println("Server started. Waiting for client.");
        socket = serverSocket.accept();
        System.out.println("Client connected.");
        input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        output = new DataOutputStream(socket.getOutputStream());
        users = new ArrayList<>();
        patients = new ArrayList<>();
        SSLProtocol();
        CertificateAuthority ca = new CertificateAuthority();
        for(Certificate c : ca.trustedCertificates){
            c.setPublicKey(Encrypt(c.getPublicKey()));//Public key cryptography
        }
        userInput = input.readUTF();//read
        if(!checkCertificate(ca.getTrustedCertificates(), userInput)){
            socket.close();
            input.close();
            return;
        }
        PopulateDatabase();
        boolean connected = true;

        while(connected){
            if(steps == 0){
                output.writeUTF(Encrypt("Enter username: "));//write
                userInput = Decrypt(input.readUTF());//read
            }
            switch (steps){
                //case 0 checks if the username the user entered is actually in the database
                case 0:
                    if (!SearchForUsername(userInput)){
                        System.out.print("Username not found");
                    }else{
                        System.out.print("Username found.");
                        UpdateCurrentUser(userInput);
                        steps++;
                    }
                    break;
                //case 1 checks for the correct password and jumps to case 6 if it is incorrect, jumps to case 2 if it is
                case 1:
                    output.writeUTF(Encrypt("Enter your password: "));//write
                    userInput = input.readUTF();//read
                    if(!AuthenticatePassword(userInput)){
                        steps = 6;
                    }else{
                        steps++;
                    }
                    break;
                //case 2 checks the current user's privilege. If it is d/n it jumps to step 3. If it isn't it just displays the balance because the user is a patient
                case 2:
                    if(currentUser.getPrivilege().equals("d") || currentUser.getPrivilege().equals("n")){
                        output.writeUTF(Encrypt(dtf.format(now) + "\n-Employee Access Menu-\nView Patient Database(v)\nAdd/Change Patient Data(c)\nLogout(o)"));//write
                        userInput = Decrypt(input.readUTF());//read
                        steps++;
                    }else{
                        ShowBalance();
                    }
                    break;
                //case 3: if the input from case 2 is "v" display the database
                //        if the input from case 2 is "o" it jumps to case 0;
                //        if the input from case 2 is "c"(or anything else) it jumps to stwep 4
                case 3:
                    switch (userInput) {
                        case "v":
                            DisplayPatientDatabase();//write
                            userInput = Decrypt(input.readUTF());//read
                            break;
                        case "o":
                            steps = 0;
                            break;
                        default:
                            steps++;
                            break;
                    }
                    break;
                //case 4 displays a menu that lets the user add a patient or edit an existing patient's info
                case 4:
                    output.writeUTF(Encrypt("-Patient Database Editor Menu-\nAdd Patient(a)\nEdit Patient Data(e)\nBack(b)\nLogout(o)"));//write
                    userInput = Decrypt(input.readUTF());//read
                    steps++;
                    if(userInput.equals("b")){
                        steps = 2;
                    }
                    if(userInput.equals("o")){
                        steps = 0;
                    }
                    break;
                case 5:
                    if(userInput.equals("a")){
                        AddPatient();
                    }else if(userInput.equals("e")){
                        EditPatient();
                    }
                    steps--;
                    break;
                //case 6 checks for the correct password until the correct pw is entered or the user tries too many times
                case 6:
                    output.writeUTF(Encrypt("Invalid password, try again: "));//write
                    userInput = Decrypt(input.readUTF());//read
                    if(!AuthenticatePassword(userInput)){
                        if(loginAttempts > 5) connected = false;
                        loginAttempts++;
                    }else{
                        loginAttempts = 0;
                        steps = 2;
                    }
                    break;
            }
        }

        socket.close();
        input.close();
        System.out.println("Connection closed");
    }

    private boolean checkCertificate(ArrayList<Certificate> cA, String userInput) {
        for(Certificate c : cA){

            if(c.toString().equals(userInput)){
                return true;
            }
        }
        return false;
    }

    private void UpdateCurrentUser(String name){
        if(users.isEmpty()) return;
        for(int i = 0; i < users.size(); i++){
            if(users.get(i).getUsername().equals(name)){
                currentUser = users.get(i);
            }
        }
    }

    private boolean SearchForUsername(String name){
        boolean found = false;
        if (users.isEmpty()) return false;
        for(int i = 0; i < users.size(); i++){
            if(users.get(i).getUsername().equals(name)){
                found = true;
            }
        }
        return found;
    }

    private void RegisterUser(String name){
        User user = new User();
        user.setUsername(name);
        users.add(user);
        currentUser = user;
    }

    private boolean AuthenticatePassword(String password){
        return currentUser.getPassword().equals(password);
    }

    private void PopulateDatabase() {
        User drStrange = new User("BestAvenger123", "ed54b2c479f8c85bf0a8aa68b7e75b00", "d", 0001);
        User drSmith = new User("SmithRulez", "482c811da5d5b4bc6d497ffa98491e38", "d", 0002);
        User nurseJoy = new User("Poke-Nurse", "9ce44f88a25272b6d9cbb430ebbcfcf1", "n", 0003);
        User nurseJane = new User("JaneRulez", "482c811da5d5b4bc6d497ffa98491e38", "n", 0004);
        Patient Will = new Patient("Will", "Brown", "1433 Country Lake Dr, Greensboro NC", "336-707-0369", 23.45, "wrbrown", "7dfc6a6e223aeb240f43802fa9577e7a", "p", 1005);
        Patient Rachel = new Patient("Rachel", "Somerville", "1435 Country Lake Dr, Greensboro NC", "336-707-0000", 12.76, "rcSomerville", "afdec7005cc9f14302cd0474fd0f3c96", "p", 1006);
        Patient Sue = new Patient("Sue", "Brown", "1433 Country Lake Dr, Greensboro NC", "336-707-1234", 41.98, "smBrown", "809f97fbb4a52703c0f62d126fd64061", "p", 1007);
        Patient Bill = new Patient("Bill", "Brown", "1433 Country Lake Dr, Greensboro NC", "336-707-4321", 102.30, "brBrown", "809f97fbb4a52703c0f62d126fd64061", "p", 1008);
        Patient Debby = new Patient("Debby", "Somerville", "1234 Union Cross St, Rutherfordton NC", "555-555-1234", 2082.34, "dSomerville", "3f0aeac53756a7e444be52c4aaa4539f", "p", 1009);
        Patient Ed = new Patient("Ed", "Somerville", "1234 Union Cross St, Rutherfordton NC", "555-555-4321", 0.00, "eSomerville", "3f0aeac53756a7e444be52c4aaa4539f", "p", 1010);
        users.add(drStrange);
        users.add(drSmith);
        users.add(nurseJoy);
        users.add(nurseJane);
        users.add(Will);
        users.add(Rachel);
        users.add(Sue);
        users.add(Bill);
        users.add(Debby);
        users.add(Ed);
        patients.add(Will);
        patients.add(Rachel);
        patients.add(Sue);
        patients.add(Bill);
        patients.add(Debby);
        patients.add(Ed);
    }

    private void DisplayPatientDatabase() throws IOException{
        String out = "";
        for(int i = 0; i < patients.size(); i++){
            out += patients.get(i).ToString() + "\n";
        }
        output.writeUTF(Encrypt(out + "\n-Employee Access Menu-\nView Patient Database(v)\nAdd/Change Patient Data(c)\nLogout(o)"));
    }

    private void HospitalEmployeeInterface() throws IOException {
        if(userInput.equals("v")){
            DisplayPatientDatabase();
        }else if(userInput.equals("c")){
            steps++;
        }
    }

    private void PatientDatabaseEditor() throws IOException{
        output.writeUTF(Encrypt("-Patient Database Editor Menu-\nAdd Patient(a)\nEdit Patient Data(e)\nBack(b)\nLogout(o)"));
        userInput = Decrypt(input.readUTF());
        if(userInput.equals("a")){
            AddPatient();
        }else if(userInput.equals("e")){
            //EditPatient();
            steps++;
        }
    }

    private void AddPatient() throws IOException{
        Patient patient = new Patient();
        output.writeUTF(Encrypt("Enter the patient's first name: "));
        patient.setFirstName(Decrypt(input.readUTF()));
        output.writeUTF(Encrypt("Enter the patient's last name: "));
        patient.setLastName(Decrypt(input.readUTF()));
        output.writeUTF(Encrypt("Enter the patient's address: "));
        patient.setAddress(Decrypt(input.readUTF()));
        output.writeUTF(Encrypt("Enter the patient's phone number: "));
        patient.setPhoneNumber(Decrypt(input.readUTF()));
        output.writeUTF(Encrypt("Enter the patient's balance: "));
        patient.setBalance(Double.parseDouble(Decrypt(input.readUTF())));
        output.writeUTF(Encrypt("Enter the patient's username: "));
        String name = Decrypt(input.readUTF());
        boolean available = false;
        while(!available){
            for(int i = 0; i < users.size(); i++){
                if(name.equals(users.get(i).getUsername())){
                    output.writeUTF(Encrypt("Username is taken, try a different username: "));
                    name = Decrypt(input.readUTF());
                    i = 0;
                }else{
                    available = true;
                }
            }
        }
        patient.setUsername(name);
        output.writeUTF(Encrypt("Enter the patient's password: "));
        patient.setPassword(input.readUTF());
        patient.setID(1010 + IDcount);
        patients.add(patient);
        users.add(patient);
        IDcount++;
    }

    private void EditPatient() throws IOException{
        output.writeUTF(Encrypt("Enter the patient's ID: "));
        userInput = Decrypt(input.readUTF());
        Patient patient = null;
        int index = 0;
        for(int i = 0; i < patients.size(); i++){
            if(userInput.equals(Integer.toString(patients.get(i).getID()))){
                patient = patients.get(i);
                index = i;
            }
        }
        if(patient != null){
            output.writeUTF(Encrypt("Patient's current data:\n" + patient.ToString() + "\n-Patient Database Editor Menu-\nChange address(x)\nChange phone(y)\nChange balance(z)"));
            userInput = Decrypt(input.readUTF());
            switch (userInput) {
                case "x":
                    output.writeUTF(Encrypt("Enter new address: "));
                    userInput = Decrypt(input.readUTF());
                    patients.get(index).setAddress(userInput);
                    break;
                case "y":
                    output.writeUTF(Encrypt("Enter new phone number: "));
                    userInput = Decrypt(input.readUTF());
                    patients.get(index).setPhoneNumber(userInput);
                    break;
                case "z":
                    output.writeUTF(Encrypt("Enter new balance: "));
                    userInput = Decrypt(input.readUTF());
                    patients.get(index).setBalance(Double.parseDouble(userInput));
                    break;
                default:
                    break;
            }
        }else{
            System.out.println("No matching ID");
        }
    }

    private void ShowBalance() throws IOException{
        for(int i = 0; i < patients.size(); i++){
            if(patients.get(i).getUsername().equals(currentUser.getUsername())){
                output.writeUTF(Encrypt(dtf.format(now) + "\nCurrent balance: " + patients.get(i).getBalance() + "\nLog out(o)"));
                steps = 0;
            }
        }
    }

    public void SSLProtocol() throws IOException {
        String handshakeString = "";
        for(int i = 0; i < 10; i++){
            Random r = new Random();
            handshakeString += r.nextInt(10);
        }
        output.writeUTF(Encrypt(handshakeString));
        if(!handshakeString.equals(input.readUTF())){
            socket.close();
            input.close();
        }
    }

    private static String Encrypt(String string){
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

    private static String Decrypt(String string){
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
            }else if(value.equals(".")){
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

    public static void main(String[] args) throws IOException {
        Server server = new Server(5000);
    }
}
