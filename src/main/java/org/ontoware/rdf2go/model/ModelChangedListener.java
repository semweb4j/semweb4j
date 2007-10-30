package org.ontoware.rdf2go.model;

import java.util.Iterator;

/**
 * A listener that gets informed when model change operations are executed.
 * This is intentionally simple, no Event objects are passed to make
 * this interface as simple as possible.
 * The implementations don't guarantee that you are informed when
 * statements are added or removed, but they should do.
 * If an implementation does not support listening in the native model,
 * you will get notified by the RDF2Go wrapper, if the manipulations 
 * are invoked via the wrapper.
 * @author sauermann
 */
public interface ModelChangedListener {
    
    public void addedStatement(Statement statement);
    
    public void addedStatements(Iterator<? extends Statement> statements);
    
    public void removedStatement(Statement statement);
    
    public void removedStatements(Iterator<? extends Statement> statements);
    
    public void performedUpdate(Diff diff);
}
