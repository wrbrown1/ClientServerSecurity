import java.util.ArrayList;

public class CertificateAuthority {

    ArrayList<Certificate> trustedCertificates;

    public CertificateAuthority() {
        trustedCertificates = new ArrayList<>();
        trustedCertificates .add(new Certificate("Will Brown", "WRBJ042995", "123-WER67", "March 31st, 2020", "Certificate Authority Inc.", "1958-29UXY2W"));
        trustedCertificates .add(new Certificate("Rachel Caroline", "RCS103190", "756-DGJT", "March 30th, 2020", "We Check CertificatesTM", "5557-FGF03B0"));
        trustedCertificates .add(new Certificate("Mr. John", "J088995", "122-DDD5", "April 10th, 2020", "Totally Real Certificate Checker Inc.", "4888-ASFBFFS"));
        trustedCertificates .add(new Certificate("Mrs. Jane", "WW046895", "555-WER97", "March 20th, 2020", "Certificate Authority Inc.", "2744-D9D9FFD"));
    }

    public ArrayList<Certificate> getTrustedCertificates(){
        return trustedCertificates;
    }
}
