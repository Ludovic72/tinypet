package foo;

import java.util.ArrayList;
import java.util.List;

public class Signataire {
    public List<String> emails;
    public String pid, token;
    public boolean free;
    public int curseur;

public Signataire(){
    emails = new ArrayList<String>();
    free = true;
}

 public Signataire(List<String> emails, String pid, boolean free) {
    this.emails = emails;
    this.pid = pid;
    this.free = free;
}
public void setToken(String token){
    this.token = token;
}

public String getToken(){
    return token;
}

public List<String> getEmails() {
    return emails;
}

public void setEmails(List<String> emails) {
    this.emails = emails;
}

public String getPid() {
    return pid;
}

public void setPid(String pid) {
    this.pid = pid;
}

public boolean isFree() {
    return free;
}

public void setFree(boolean free) {
    this.free = free;
}
}
