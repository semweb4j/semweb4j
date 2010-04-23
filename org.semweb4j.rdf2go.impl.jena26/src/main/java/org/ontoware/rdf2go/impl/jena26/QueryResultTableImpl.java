package org.ontoware.rdf2go.impl.jena26;

import java.util.ArrayList;
import java.util.List;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;

public class QueryResultTableImpl implements QueryResultTable
{

  private final List<String> varnames;

  private final QueryExecution qexec;

  public QueryResultTableImpl(Query query, Model jenaModel) throws ModelRuntimeException
  {

    if (!query.isSelectType())
    {
      throw new ModelRuntimeException("The given query is not a SELECT query");
    }
    // else
    this.varnames = new ArrayList<String>();
    for (Object o : query.getResultVars())
    {
      this.varnames.add((String) o);
    }
    this.qexec = QueryExecutionFactory.create(query, jenaModel);
  }

  public List<String> getVariables()
  {
    return this.varnames;
  }

  public ClosableIterator<QueryRow> iterator()
  {
    ResultSet results = this.qexec.execSelect();
    return new QueryIterator(this, results);
  }

}
