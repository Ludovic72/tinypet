package foo;

import java.io.IOException;
import java.util.Date;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

@WebServlet(name = "CreatePetitionServlet", urlPatterns = { "/create-petition" })
public class CreatePetitionServlet extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        // Récupérer les paramètres de la requête
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        Date date = new Date();  // Utiliser la date actuelle
        int numberOfSignatures = 0;  // Initialiser le nombre de signatures à 0

        // Créer une entité Datastore pour la pétition
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity petitionEntity = new Entity("Petition");
        petitionEntity.setProperty("date", date);
        petitionEntity.setProperty("title", title);
        petitionEntity.setProperty("description", description);
        petitionEntity.setProperty("numberOfSignatures", numberOfSignatures);

        datastore.put(petitionEntity);

        // Répondre à la requête HTTP
        response.getWriter().print("<h2>Petition Created Successfully</h2>");
        response.getWriter().print("<p>Title: " + title + "</p>");
        response.getWriter().print("<p>Description: " + description + "</p>");
        response.getWriter().print("<p>Date: " + date + "</p>");
        response.getWriter().print("<p>Number of Signatures: " + numberOfSignatures + "</p>");
    }
}