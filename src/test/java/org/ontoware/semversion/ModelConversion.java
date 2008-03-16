package org.ontoware.semversion;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.InputStream;

import org.junit.Test;
import org.ontoware.rdf2go.impl.jena24.ModelImplJena24;
import org.ontoware.rdf2go.util.ModelUtils;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * https://marcont.svn.sourceforge.net/svnroot/marcont/branches/MarcOnt2
 * @author Szymon
 */
public class ModelConversion {
    
    @Test
    public void test() throws Exception {
            
        Model m = ModelFactory.createDefaultModel();

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
		InputStream in = cl
				.getResourceAsStream("marcont/marcont.xml");
        m.read(in,"");

        OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, m);
        org.ontoware.rdf2go.model.Model m2 = new ModelImplJena24( ontModel );

        SemVersion semVersion = new SemVersion();
        semVersion.startup( new File("/test") );
        semVersion.deleteStore();

        semVersion.createUser("a","a");
        Session s1 = semVersion.login("a", "a");

        VersionedModel vm = s1.createVersionedModel("thread");
        m2.open();
        vm.commitRoot(m2, "a");
        m2.close();

        Version x = vm.getFirstVersion();

        org.ontoware.rdf2go.model.Model otherModel = x.getContent();

        Model newmodel = ModelFactory.createDefaultModel();
        OntModel ontoModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, newmodel);
        org.ontoware.rdf2go.model.Model rdf2goModel = new ModelImplJena24( ontoModel );

        rdf2goModel.open();
        otherModel.open();

        ModelUtils.copy(otherModel, rdf2goModel);
        
        rdf2goModel.writeTo(System.out);

        rdf2goModel.close();
        otherModel.close();

        s1.close();
        semVersion.shutdown();
        
        assertTrue(true);
    }
}