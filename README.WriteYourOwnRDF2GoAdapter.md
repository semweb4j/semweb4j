How to write an RDF2GO adapter for your triple store
====================================================

First you need to decide if you want to implement only a triple store adapter or a quad store adapter.

A triple store in RDF2Go is a _Model_ (s,p,o), a quad store is a _ModelSet_ (c,s,p,o).
  * c - context URI
  * s - subject URI or blank node
  * p - property URI
  * o - object URI, blank node or literal

A _ModelSet_ always has the ability to be used as a set of named models (c -> s, p, o).

Walk-through of Sesame 2.3.1 Implementation
-------------------------------------------

You need to extend _AbstractModel_ and implement the abstract methods.

Second, you need a _StaticBinding_ [(see source)](org.semweb4j.rdf2go.impl.sesame/src/main/java/org/ontoware/rdf2go/impl/StaticBinding.java) to register your implementation. This allows the _RDF2Go_ class to create models based on your adapter.

The _RepositoryModel_ [(see source)](org.semweb4j.rdf2go.impl.sesame/src/main/java/org/openrdf/rdf2go/RepositoryModel.java) of the Sesame/OpenRDF 2.3.1 adapter is less than 900 lines long and in production use.
