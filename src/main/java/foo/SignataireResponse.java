package foo;

import java.util.List;

import com.google.appengine.api.datastore.Entity;

public class SignataireResponse {
    
    private List<Entity> entities;
    private String nextCursor;

    public SignataireResponse(List<Entity> entities, String nextCursor) {
        this.entities = entities;
        this.nextCursor = nextCursor;
    }

    // Getters and setters
    public List<Entity> getEntities() {
        return entities;
    }

    public void setEntities(List<Entity> entities) {
        this.entities = entities;
    }

    public String getNextCursor() {
        return nextCursor;
    }

    public void setNextCursor(String nextCursor) {
        this.nextCursor = nextCursor;
    }
}