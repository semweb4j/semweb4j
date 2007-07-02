package org.ontoware.semweb4j.lessons.lesson2;

import java.util.HashSet;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDFS;

/* 
 * maintains a model to provide tagging & naming people
 * this class provides access to these methods:
 * * void tagURI(URI, URI or String) - inverse: untagURI
 * * void nameURI(URI, URI or String) - inverse: unnameURI
 * * HashSet<URI> personsByName(String) - same scope: tagsByName
 * * URI singlePersonByName(String) - same scope: singleTagByName
 * * HashSet<String> getAllNames(URI) - weaker: getAllNames()
 * * HashSet<URI> getAllTags(URI) - weaker: getAllTags()
 * * HashSet<URI> getTaggedWith(URI or String)
 */
public class Step8 {

	private Model model;

	private URI hasTag;

	private URI hasName;

	private URI Person;

	private URI Tag;

	private Step8() throws ModelRuntimeException {

		model = RDF2Go.getModelFactory().createModel();
		model.open();

		// setting up local "namespace"
		hasName = model.createURI("http://xmlns.com/foaf/0.1/#term_name");
		hasTag = model.createURI("http://example.com/relations#hasTag");
		Person = model.createURI("http://example.com/classes#Person");
		Tag = model.createURI("http://example.com/classes#Tag");
	}

	private void classifyURI(URI resource, URI Class) throws ModelRuntimeException {
		model.addStatement(resource, RDFS.Class, Class);
	}

	public void tagURI(URI resource, String tag) throws ModelRuntimeException {
		tagURI(resource, singleTagByName(tag));
	}

	/*
	 * tags a person with a given tag @param resource the URI of the person
	 * @param tag the new (additional) tag
	 */
	public void tagURI(URI resource, URI tag) throws ModelRuntimeException {
		model.addStatement(resource, hasTag, tag);
	}

	public void untagURI(URI resource, String tag) throws ModelRuntimeException {
		// two tags with the same name may lead to errors here
		untagURI(resource, singleTagByName(tag));
	}

	/*
	 * the inverse of tagURI @param resource the URI of the person @param tag
	 * the tag to remove
	 */
	public void untagURI(URI resource, URI tag) throws ModelRuntimeException {
		model.removeStatement(resource, hasTag, tag);
	}

	/*
	 * names a person use this to add different spellings of names to persons or
	 * tags @param resource the URI of the person or tag @param name the new
	 * (additional) name
	 */
	public void nameURI(URI resource, String name) throws ModelRuntimeException {
		model.addStatement(resource, hasName, name);
	}

	/*
	 * inverse of nameURI use this to remove misspelled names @param resource
	 * the URI of the person or tag @param name the name to remove
	 */
	public void unnameURI(URI resource, String name) throws ModelRuntimeException {
		model.removeStatement(resource, hasName, name);
	}

	/*
	 * returns all URIs of persons with a given name @param name
	 */
	public HashSet<URI> personsByName(String name) throws ModelRuntimeException {
		return resourcesByName(name, Person);
	}

	/*
	 * returns all URIs of tags with a given name @param name
	 */
	public HashSet<URI> tagsByName(String name) throws ModelRuntimeException {
		return resourcesByName(name, Tag);
	}

	private HashSet<URI> resourcesByName(String name, URI Class)
			throws ModelRuntimeException {
		HashSet<URI> results = new HashSet<URI>();
		for (QueryRow row : model
				.sparqlSelect("SELECT ?resource WHERE {?resource <"
						+ RDFS.Class + "> " + Class.toSPARQL() + " . ?resource <"
						+ hasName + "> \"" + name + "\"}")) {
			results.add((URI) row.getValue("resource"));
		}
		return results;
	}

	/*
	 * returns a single URI of a person with a given name if no such person
	 * exists, one will be created and named if more than one person with the
	 * given name exists, one is arbitrarily chosen. @param name
	 */
	public URI singlePersonByName(String name) throws ModelRuntimeException {
		return singleResourceByName(name, Person);
	}

	public URI singleTagByName(String name) throws ModelRuntimeException {
		return singleResourceByName(name, Tag);
	}

	private URI singleResourceByName(String name, URI Class)
			throws ModelRuntimeException {
		HashSet<URI> resources = resourcesByName(name, Class);
		URI resource;
		// if no resource with this name exists, create a new one
		if (resources.isEmpty()) {
			resource = model.newRandomUniqueURI();
			nameURI(resource, name);
			classifyURI(resource, Class);
		} else { // else, take one out of the HashSet resources
			resource = resources.iterator().next();
		}
		return resource;
	}

	/*
	 * returns all tags of a person @param resource the URI of the person
	 */
	@SuppressWarnings("unchecked")
	public HashSet<URI> getAllTags(URI resource) throws ModelRuntimeException {
		return getAllValues(resource, hasTag, false);
	}

	/*
	 * returns all tags
	 */
	public HashSet<URI> getAllTags() throws ModelRuntimeException {
		return getAllTags(null);
	}

	/*
	 * returns all names of a person or tag @param resource the URI of the
	 * person or tag
	 */
	@SuppressWarnings("unchecked")
	public HashSet<String> getAllNames(URI resource) throws ModelRuntimeException {
		return getAllValues(resource, hasName, true);
	}

	/*
	 * returns all names
	 */
	public HashSet<String> getAllNames() throws ModelRuntimeException {
		return getAllNames(null);
	}

	@SuppressWarnings("unchecked")
	private HashSet getAllValues(URI resource, URI property, boolean asString)
			throws ModelRuntimeException {
		String resstr;
		if (resource == null) {
			resstr = "?resource";
		} else {
			resstr = resource.toSPARQL();
		}
		HashSet results;
		if (asString) {
			results = new HashSet<String>();
		} else {
			results = new HashSet<URI>();
		}
		for (QueryRow row : model.sparqlSelect("SELECT ?value WHERE { "
				+ resstr + " <" + property + "> ?value }")) {
			if (asString) {
				results.add(row.getLiteralValue("value"));
			} else {
				results.add((URI) row.getValue("value"));
			}
		}
		return results;
	}

	public HashSet<URI> getTaggedWith(String tag) throws ModelRuntimeException {
		return getTaggedWith(singleTagByName(tag));
	}

	/*
	 * returns all URIs of persons who are tagged with tag @param tag
	 */
	public HashSet<URI> getTaggedWith(URI tag) throws ModelRuntimeException {
		HashSet<URI> results = new HashSet<URI>();
		for (QueryRow row : model
				.sparqlSelect("SELECT ?resource WHERE { ?resource <" + hasTag
						+ "> " + tag.toSPARQL() + " }")) {
			results.add((URI) row.getValue("resource"));
		}
		return results;
	}

	/*
	 * usage example
	 */
	public static void main(String[] args) throws ModelRuntimeException {
		// get an instance of our class
		Step8 inst = new Step8();

		// get URIs to play with
		URI max = inst.singlePersonByName("Max Völkel");
		URI konrad = inst.singlePersonByName("Konrad Völkel");
		URI james = inst.singlePersonByName("James Gosling");
		URI guido = inst.singlePersonByName("Guido van Rossum");

		// test naming
		inst.nameURI(max, "Max Völkl");
		inst.nameURI(max, "Max Voelkel");
		assert max.equals(inst.singlePersonByName("Max Völkl"));
		assert max.equals(inst.singlePersonByName("Max Völkel"));
		inst.unnameURI(max, "Max Völkl");
		System.out.println("synonyms for Max:");
		for (String name : inst.getAllNames(max)) {
			System.out.println(name);
		}

		// test tagging
		inst.tagURI(max, "Java");
		inst.tagURI(max, "Python");
		inst.untagURI(max, "Python");
		inst.tagURI(konrad, "Python");
		inst.tagURI(james, "Java");
		inst.tagURI(guido, "Python");
		System.out.println("Max' tags:");
		for (URI tag : inst.getAllTags(max)) {
			for (String name : inst.getAllNames(tag)) {
				System.out.println(name);
			}
		}
		System.out.println("all tags:");
		for (URI tag2 : inst.getAllTags()) {
			for (String name : inst.getAllNames(tag2)) {
				System.out.println(name);
			}
		}
		System.out.println("everybody tagged 'Java':");
		for (URI person : inst.getTaggedWith("Java")) {
			for (String name : inst.getAllNames(person)) {
				System.out.println(name);
			}
		}
	}

}
