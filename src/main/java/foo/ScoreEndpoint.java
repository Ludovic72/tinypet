package foo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.config.Nullable;					
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.UnauthorizedException;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@Api(name = "myApi",
     version = "v1",
     audiences = {"927375242383-t21v9ml38tkh2pr30m4hqiflkl3jfohl.apps.googleusercontent.com", 
	 "681751757032-j0sin0l00292r7racqo06aaqj1e22obs.apps.googleusercontent.com"},
  	 clientIds = {"927375242383-t21v9ml38tkh2pr30m4hqiflkl3jfohl.apps.googleusercontent.com",
        "681751757032-j0sin0l00292r7racqo06aaqj1e22obs.apps.googleusercontent.com",
/*"588506488220-pfvirp57eoumio5l6bsdbbcao2f9t68s.apps.googleusercontent.com "*/},

     namespace =
     @ApiNamespace(
		   ownerDomain = "helloworld.example.com",
		   ownerName = "helloworld.example.com",
		   packagePath = "")
     )
	 

public class ScoreEndpoint {

	Random r = new Random();

    // remember: return Primitives and enums are not allowed. 
	@ApiMethod(name = "getRandom", httpMethod = HttpMethod.GET)
	public RandomResult random() {
		return new RandomResult(r.nextInt(6) + 1);
	}

	@ApiMethod(name = "hello", httpMethod = HttpMethod.GET)
	public User Hello(User user) throws UnauthorizedException {
        if (user == null) {
			throw new UnauthorizedException("Invalid credentials");
		}
        System.out.println("Yeah:"+user.toString());
		return user;
	}


	@ApiMethod(name = "scores", httpMethod = HttpMethod.GET)
	public List<Entity> scores() {
		Query q = new Query("Score").addSort("score", SortDirection.DESCENDING);

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = datastore.prepare(q);
		List<Entity> result = pq.asList(FetchOptions.Builder.withLimit(100));
		return result;
	}

	@ApiMethod(name = "topscores", httpMethod = HttpMethod.GET)
	public List<Entity> topscores() {
		Query q = new Query("Score").addSort("score", SortDirection.DESCENDING);

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = datastore.prepare(q);
		List<Entity> result = pq.asList(FetchOptions.Builder.withLimit(10));
		return result;
	}

	@ApiMethod(name = "myscores", httpMethod = HttpMethod.GET)
	public List<Entity> myscores(@Named("name") String name) {
		Query q = new Query("Score").setFilter(new FilterPredicate("name", FilterOperator.EQUAL, name)).addSort("score",
				SortDirection.DESCENDING);
        //Query q = new Query("Score").setFilter(new FilterPredicate("name", FilterOperator.EQUAL, name));

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = datastore.prepare(q);
		List<Entity> result = pq.asList(FetchOptions.Builder.withLimit(10));
		return result;
	}

	@ApiMethod(name = "addScore", httpMethod = HttpMethod.GET)
	public Entity addScore(@Named("score") int score, @Named("name") String name) {

		Entity e = new Entity("Score", "" + name + score);
		e.setProperty("name", name);
		e.setProperty("score", score);

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		datastore.put(e);

		return e;
	}

	@ApiMethod(name = "postMessage", httpMethod = HttpMethod.POST)
	public Entity postMessage(PostMessage pm) {

		Entity e = new Entity("Post"); 
		e.setProperty("owner", pm.owner);
		e.setProperty("url", pm.url);
		e.setProperty("body", pm.body);
		e.setProperty("likec", 0);
		e.setProperty("date", new Date());

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
		datastore.put(e);
		txn.commit();
		return e;
	}

	@ApiMethod(name = "mypost", httpMethod = HttpMethod.GET)
	public CollectionResponse<Entity> mypost(@Named("name") String name, @Nullable @Named("next") String cursorString) {

	    Query q = new Query("Post").setFilter(new FilterPredicate("owner", FilterOperator.EQUAL, name));

	    // https://cloud.google.com/appengine/docs/standard/python/datastore/projectionqueries#Indexes_for_projections
	    //q.addProjection(new PropertyProjection("body", String.class));
	    //q.addProjection(new PropertyProjection("date", java.util.Date.class));
	    //q.addProjection(new PropertyProjection("likec", Integer.class));
	    //q.addProjection(new PropertyProjection("url", String.class));

	    // looks like a good idea but...
	    // generate a DataStoreNeedIndexException -> 
	    // require compositeIndex on owner + date
	    // Explosion combinatoire.
	    // q.addSort("date", SortDirection.DESCENDING);
	    
	    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	    PreparedQuery pq = datastore.prepare(q);
	    
	    FetchOptions fetchOptions = FetchOptions.Builder.withLimit(2);
	    
	    if (cursorString != null) {
		fetchOptions.startCursor(Cursor.fromWebSafeString(cursorString));
		}
	    
	    QueryResultList<Entity> results = pq.asQueryResultList(fetchOptions);
	    cursorString = results.getCursor().toWebSafeString();
	    
	    return CollectionResponse.<Entity>builder().setItems(results).setNextPageToken(cursorString).build();
	    
	}
    
	@ApiMethod(name = "getPost",
		   httpMethod = ApiMethod.HttpMethod.GET)
	public CollectionResponse<Entity> getPost(User user, @Nullable @Named("next") String cursorString)
			throws UnauthorizedException {

		if (user == null) {
			throw new UnauthorizedException("Invalid credentials");
		}

		Query q = new Query("Post").
		    setFilter(new FilterPredicate("owner", FilterOperator.EQUAL, user.getEmail()));

		// Multiple projection require a composite index
		// owner is automatically projected...
		// q.addProjection(new PropertyProjection("body", String.class));
		// q.addProjection(new PropertyProjection("date", java.util.Date.class));
		// q.addProjection(new PropertyProjection("likec", Integer.class));
		// q.addProjection(new PropertyProjection("url", String.class));

		// looks like a good idea but...
		// require a composite index
		// - kind: Post
		//  properties:
		//  - name: owner
		//  - name: date
		//    direction: desc

		// q.addSort("date", SortDirection.DESCENDING);

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = datastore.prepare(q);

		FetchOptions fetchOptions = FetchOptions.Builder.withLimit(2);

		if (cursorString != null) {
			fetchOptions.startCursor(Cursor.fromWebSafeString(cursorString));
		}

		QueryResultList<Entity> results = pq.asQueryResultList(fetchOptions);
		cursorString = results.getCursor().toWebSafeString();

		return CollectionResponse.<Entity>builder().setItems(results).setNextPageToken(cursorString).build();
	}

	@ApiMethod(name = "postMsg", httpMethod = HttpMethod.POST)
	public Entity postMsg(User user, PostMessage pm) throws UnauthorizedException {

		if (user == null) {
			throw new UnauthorizedException("Invalid credentials");
		}

		Entity e = new Entity("Post", Long.MAX_VALUE-(new Date()).getTime()+":"+user.getEmail());
		e.setProperty("owner", user.getEmail());
		e.setProperty("url", pm.url);
		e.setProperty("body", pm.body);
		e.setProperty("likec", 0);
		e.setProperty("date", new Date());

///		Solution pour pas projeter les listes
//		Entity pi = new Entity("PostIndex", e.getKey());
//		HashSet<String> rec=new HashSet<String>();
//		pi.setProperty("receivers",rec);
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
		datastore.put(e);
//		datastore.put(pi);
		txn.commit();
		return e;
	}

	//---------------------------------------------------------------------------------------------------------------------------------------
	
	@ApiMethod(name = "petitions", httpMethod = HttpMethod.GET)
	public List<Entity> petitions() {
		Query q = new Query("Petition").addSort("name", SortDirection.DESCENDING);

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = datastore.prepare(q);
		List<Entity> result = pq.asList(FetchOptions.Builder.withLimit(100));
		return result;
	}

	@ApiMethod(name = "topPetition", httpMethod = HttpMethod.GET)
	public List<Entity> topPetition() {
		Query q = new Query("Petition").addSort("auteur", SortDirection.DESCENDING);

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = datastore.prepare(q);
		List<Entity> result = pq.asList(FetchOptions.Builder.withLimit(100));
		return result;
	}//Good
	
	@ApiMethod(name = "petitionTag", httpMethod = HttpMethod.GET)
	public List<Entity> petitionTag(@Named("tag") String tag) throws Exception {//petitions

			Query q = new Query("Petition").setFilter(new FilterPredicate("tag", FilterOperator.EQUAL, tag));
			
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			PreparedQuery pq = datastore.prepare(q);
			List<Entity> result = pq.asList(FetchOptions.Builder.withLimit(10));
			return result;
	}


	@ApiMethod(name = "mypetition", httpMethod = HttpMethod.GET)
	public List<Entity> mypetition(@Named("token") String token) throws Exception {//petitions

			// Vérification que le paramètre name n'est pas null ou vide
			if (token == null || token.isEmpty()) {
				throw new IllegalArgumentException("Le paramètre 'token' est obligatoire.");
			}
			GoogleIdToken.Payload payload = TokenVerifier.verifyToken(token);
			
			if (payload == null) {
				throw new IllegalArgumentException("Token invalide.");
			}

			String email = payload.getEmail();
			Query q = new Query("Petition").setFilter(new FilterPredicate("auteur", FilterOperator.EQUAL, email))
			.addSort("auteur", SortDirection.ASCENDING)
			.addSort("nom", SortDirection.ASCENDING)
			.addSort("nbSign", SortDirection.DESCENDING)
			.addSort("dateCreationP", SortDirection.DESCENDING)
			.addSort("tag", SortDirection.ASCENDING);
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			PreparedQuery pq = datastore.prepare(q);
			List<Entity> result = pq.asList(FetchOptions.Builder.withLimit(10));
			return result;

	}//Good

	
	@ApiMethod(name = "getSignataire", httpMethod = HttpMethod.GET)
	public Entity getSignataire(Signataire signataire) throws Exception {

		GoogleIdToken.Payload payload =  TokenVerifier.verifyToken(signataire.token);
		String email = payload.getEmail();
		return null;
	}

	@ApiMethod(name = "addPetition", httpMethod = HttpMethod.POST)
	public Entity addPetition(Petition petition) throws Exception {
		
		GoogleIdToken.Payload payload =  TokenVerifier.verifyToken(petition.token);
		String email = payload.getEmail();
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
		
		//String email = TokenVerifier.verifyToken(petition.getToken()).getEmail();
		Entity e = new Entity("Petition");
		e.setProperty("auteur", email);
		e.setProperty("nom", petition.nom);
		e.setProperty("description", petition.getDescription());
		e.setProperty("nbSign", 0);
		e.setProperty("dateCreationP", new Date());
		e.setProperty("tag", petition.tag);

		datastore.put(txn, e);
		txn.commit();

		return e;
	}

	@ApiMethod(name = "addSignataire", httpMethod = HttpMethod.POST)
	public Entity addSignataire(Signataire signataire) throws Exception {
    	// Vérifier le token et obtenir l'email
    	GoogleIdToken.Payload payload = TokenVerifier.verifyToken(signataire.token);
    	String email = payload.getEmail();

    	// Obtenir le service Datastore
    	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    	Query.Filter pidFilter = new Query.FilterPredicate("pid", Query.FilterOperator.EQUAL, signataire.pid);
    	Query query = new Query("Signataire").setFilter(pidFilter);
    	PreparedQuery pq = datastore.prepare(query);

    	// Vérifier si l'utilisateur a déjà signé la pétition
    	for (Entity entity : pq.asIterable()) {
        	List<String> emails = (List<String>) entity.getProperty("emails");
        	if (emails != null && emails.contains(email)) {
           	 	throw new IllegalArgumentException("L'utilisateur a déjà signé cette pétition.");
        	}
    	}

    	Transaction txn = datastore.beginTransaction();
    	try {
        	// Rechercher ou créer une entité Signataire
        	Entity signataireEntity = findOrCreateSignataire(datastore, signataire, email, pidFilter, txn);

        	// Rechercher l'entité Petition avec le pid donné
        	Key petitionKey = createPetitionKey(signataire.pid);
        	Entity petitionEntity = datastore.get(petitionKey);

        	// Incrémenter le nombre de signatures
        	long nbSign = (long) petitionEntity.getProperty("nbSign");
        	petitionEntity.setProperty("nbSign", nbSign + 1);
        	datastore.put(txn, petitionEntity);
        	txn.commit();
        	return signataireEntity;
    	} catch (Exception e) {
        	if (txn.isActive()) {
            	txn.rollback();
        	}
       	 	throw e;
    	}
	}

	private Entity findOrCreateSignataire(DatastoreService datastore, Signataire signataire, String email, Query.Filter pidFilter, Transaction txn) throws Exception {
    	Query.Filter freeFilter = new Query.FilterPredicate("free", Query.FilterOperator.EQUAL, true);
    	Query.Filter compositeFilter = Query.CompositeFilterOperator.and(pidFilter, freeFilter);
    	Query query = new Query("Signataire").setFilter(compositeFilter);
    	PreparedQuery pq = datastore.prepare(query);
    	Entity signataireEntity = pq.asSingleEntity();

    	if (signataireEntity != null) {
        
        	List<String> emails = (List<String>) signataireEntity.getProperty("emails");
        	if (emails == null) {
        	    emails = new ArrayList<>();
        	}
        	emails.add(email);
       	 	signataireEntity.setProperty("emails", emails);

        
        	if (emails.size() >= 40000) {
            	signataireEntity.setProperty("free", false);
        	}
    	} else {
        	// Aucun signataire avec de l'espace libre trouvé, en créer un nouveau
        	signataireEntity = new Entity("Signataire");
        	List<String> emails = new ArrayList<>();
        	emails.add(email);
        	signataireEntity.setProperty("emails", emails);
        	signataireEntity.setProperty("pid", signataire.pid);
        	signataireEntity.setProperty("free", true);
    	}
    	datastore.put(txn, signataireEntity);
    	return signataireEntity;
	}

	private Key createPetitionKey(String pid) {
    	try {
        	long pidLong = Long.parseLong(pid);
        	return KeyFactory.createKey("Petition", pidLong);
    	} catch (NumberFormatException e) {
        	return KeyFactory.createKey("Petition", pid);
    	}
	}
}
