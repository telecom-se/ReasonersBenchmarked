package fr.ujm.tse.lt2c.satin.jena;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class JenaUtils {
	private JenaUtils() {

	}

	public static long forceMaterialization(final Model res) {
		long nbTriples = 0;
		StmtIterator smtIterator = res.listStatements();
		while (smtIterator.hasNext()) {
			smtIterator.next();
			nbTriples++;
		}
		return nbTriples;
	}

}
