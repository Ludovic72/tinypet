package foo;

public class Utilisateur {
    private String mail;

    Utilisateur(String mail){
        this.mail = mail;
    }

    public String getMail(){
        return this.mail;
    }

    public void setMail(String mail){
        this.mail = mail;
    }
}