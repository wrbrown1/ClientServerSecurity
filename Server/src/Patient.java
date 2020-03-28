public class Patient extends User{

    private String firstName, lastName, address, phoneNumber;
    private double balance;

    public Patient(){
        firstName = "";
        lastName = "";
        address = "";
        phoneNumber = "";
        double balance = 0.0;
    }

    public Patient(String firstName, String lastName, String address, String phoneNumber, double balance, String username, String password, String privilege, int ID){
        super(username, password, privilege, ID);
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.balance = balance;
    }

    public String ToString(){
        return "Name: " + this.getFirstName() + " " + this.getLastName() + "\n" +
                "Address: " + this.getAddress() + "\n" +
                "Phone: " + this.getPhoneNumber() + "\n" +
                "ID: " + this.getID() + "\n" +
                "Balance: $" + this.getBalance();
    }

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return the phoneNumber
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * @param phoneNumber the phoneNumber to set
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * @return the balance
     */
    public double getBalance() {
        return balance;
    }

    /**
     * @param balance the balance to set
     */
    public void setBalance(double balance) {
        this.balance = balance;
    }
}
