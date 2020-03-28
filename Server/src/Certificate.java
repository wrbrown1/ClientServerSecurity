public class Certificate {

    String owner;
    String signature;

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public void setIssuerName(String issuerName) {
        this.issuerName = issuerName;
    }

    public void setIssuerSignature(String issuerSignature) {
        this.issuerSignature = issuerSignature;
    }

    public String getOwner() {
        return owner;
    }

    public String getSignature() {
        return signature;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public String getIssuerName() {
        return issuerName;
    }

    public String getIssuerSignature() {
        return issuerSignature;
    }

    String publicKey;
    String expirationDate;
    String issuerName;
    String issuerSignature;

    public Certificate(String owner, String signature, String publicKey, String expirationDate, String issuerName, String issuerSignature) {
        this.owner = owner;
        this.signature = signature;
        this.publicKey = publicKey;
        this.expirationDate = expirationDate;
        this.issuerName = issuerName;
        this.issuerSignature = issuerSignature;
    }

    @Override
    public String toString(){
        return "Owner: " + this.owner + "\n" +
                "Signature: " + this.signature + "\n" +
                "Public Key: " + this.publicKey + "\n" +
                "Expiration date: " + this.expirationDate + "\n" +
                "Issuer Name: " + this.issuerName + "\n" +
                "Issuer Signature: " + this.issuerSignature + "\n";
    }
}