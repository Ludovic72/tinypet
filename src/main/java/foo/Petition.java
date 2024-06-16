package foo;

public class Petition {
    public String auteur, nom, description, tag, dateCreationP, token;
    public int nbSignature;
    public Petition(){
      
    }

    public Petition(String auteur, String nom, String description, String tag, String dateCreationP, int nbSignature){
        this.auteur = auteur;
        this.nom = nom;
        this.description = description;
        this.tag = tag;
        this.dateCreationP = dateCreationP;
        this.nbSignature = nbSignature;
    }

    public String getToken(){
        return token;
    }
    
    public String setToken(){
      return token;
  }

    public String getAuteur(){
       return this.auteur; 
    }

    public String getDescription(){
        return this.description; 
     }

     public String getNom(){
        return this.nom; 
     }

     public String getTag(){
        return this.tag; 
     }

     public String getDateCreationP(){
        return this.dateCreationP;
    }
     public void setAuteur(String auteur){
        this.auteur = auteur;
     }
 
     public void setDescription(String description){
        this.description = description;
      }
 
      public void setNom(String nom){
        this.nom = nom;
      }
 
      public void setTag(String tag){
        this.tag = tag; 
      }
 
      public void setDateCreationP(String dateCreationP){
        this.dateCreationP = dateCreationP;
      }

      public void setNbSignature(int nbSignature){
        this.nbSignature = nbSignature;
      }

      public int getNbSignature(){
        return  this.nbSignature;
      }

}
