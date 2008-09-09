package org.ontoware.rdfreactor.generator;

import org.junit.Test;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.node.Resource;

import com.example.AAA;

public class TestTemplateTest {

	@Test
	public void testIt() {
		org.ontoware.rdf2go.model.Model m = RDF2Go.getModelFactory()
				.createModel();
		m.open();
		AAA a = new AAA(m, true);
		a.addLabel("ggg");
		m.dump();
		ClosableIterator<Resource> it = AAA.getAllInstances(m);
		while (it.hasNext()) {
			Resource elem = it.next();
			System.out.println("found as it "+elem);
		}
		for (AAA b : AAA.getAllInstances_as(m).asArray()) {
			System.out.println("found as array "+b);
			System.out.println(b.getAllLabel_as().asArray());
		}
		for (AAA b : AAA.getAllInstances_as(m).asList()) {
			System.out.println("found as list "+b);
			System.out.println(b.getAllLabel_as().asArray());
		}
	}

}
