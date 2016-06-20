package fr.ujm.tse.lt2c.satin.jena;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.management.RuntimeErrorException;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import com.hp.hpl.jena.vocabulary.ReasonerVocabulary;

import fr.ujm.tse.lt2c.satin.Benchmark;
import fr.ujm.tse.lt2c.satin.BenchmarkUtils;
import fr.ujm.tse.lt2c.satin.datamodel.AbstractInferer;
import fr.ujm.tse.lt2c.satin.datamodel.BenchResult;

public class JenaInferer extends AbstractInferer {

	/**
	 * @param datasetfile
	 * @param fragment
	 */
	public JenaInferer(final File datasetfile, final String fragment) {
		super(datasetfile, fragment);
	}

	private static Logger LOGGER = Logger.getLogger(JenaInferer.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.ujm.tse.lt2c.satin.Inferer#inferClosureFromModel()
	 */
	@Override
	public BenchResult inferClosureFromModel() {

		// 1- load model from file.
		final Model model = ModelFactory.createDefaultModel();
		try {
			final InputStream is = new FileInputStream(this.getDatasetfile());
			model.read(is, null, "N-TRIPLE");
		} catch (final FileNotFoundException e) {
			LOGGER.fatal("Cannot find");
			throw new RuntimeErrorException(new Error("Invalid dataset file."));
		}

		switch (this.getFragment()) {
		case Benchmark.RDFSDEFAULT: {
			return this.inferJenaRDFSDefault(model);
		}
		case Benchmark.RDFSFULL: {
			return this.inferJenaRDFSFull(model);
		}
		case Benchmark.RHODF: {
			return this.inferJenaRhoDf(model);
		}
		case Benchmark.RDFSPP: {
			return this.inferJenaRDFSPlusPlus(model);
		}
		default: {
			LOGGER.fatal("Unsupported fragment for Jena : "
					+ this.getFragment());
			throw new RuntimeErrorException(new Error(
					"Unsupported fragment for Jena"));
		}
		}
	}

	/**
	 * @param model
	 * @return
	 */
	private BenchResult inferJenaRDFSPlusPlus(final Model model) {
		// Create a simple RDFS++ Reasoner.
		final StringBuilder sb = new StringBuilder();
		sb.append("[eq_sym: (?s owl:sameAs ?o) notEqual(?s,?o)  ->  (?o owl:sameAs ?s)]");
		sb.append("[eq_trans: (?x owl:sameAs ?y)    (?y owl:sameAs ?z)    notEqual(?x,?y)notEqual(?y,?z) -> (?x owl:sameAs ?z)]");
		sb.append("[eq_rep-s: (?x owl:sameAs ?y) (?x ?p ?o)  notEqual(?x,?y)  ->  (?y ?p ?o)]");
		sb.append("[eq_rep-p: (?x owl:sameAs ?y) (?s ?x ?o)  notEqual(?x,?y)  ->  (?s ?y ?o)]");
		sb.append("[eq_rep-o: (?x owl:sameAs ?y) (?s ?p ?x)  notEqual(?x,?y)  ->  (?s ?p ?y)]");
		sb.append("[prp_dom: (?p rdfs:domain ?c) (?x ?p ?y)  ->  (?x rdf:type ?c)]");
		sb.append("[prp_rng: (?p rdfs:range ?c) (?x ?p ?y)  ->  (?y rdf:type ?c)]");
		sb.append("[prp_fp: (?p rdfs:type owl:FunctionalProperty) (?x ?p ?y1)  (?x ?p ?y2)   notEqual(?y1,y2)  ->  (?y1 owl:sameAs ?y2)]");
		sb.append("[prp_ifp: (?p rdfs:type owl:InverseFunctionalProperty) (?x1 ?p ?y)  (?x2 ?p ?y)   notEqual(?x1,?x2)  ->  (?x1 owl:sameAs ?x2)]");
		sb.append("[prp_symp: (?p rdf:type owl:SymmetricProperty) (?x ?p ?y)  ->  (?y ?p ?x)]");
		sb.append("[prp_trp: (?p rdf:type owl:TransitiveProperty) (?x ?p ?y)  (?y ?p ?z)  ->  (?x ?p ?z)]");
		sb.append("[prp_spo1: (?p1 rdfs:subPropertyOf ?p2) (?x ?p1 ?y)  ->  (?x ?p2 ?y)]");
		sb.append("[prp_eqp1: (?p1 owl:equivalentProperty ?p2) (?x ?p1 ?y)  ->  (?x ?p2 ?y)]");
		sb.append("[prp_eqp2: (?p1 owl:equivalentProperty ?p2) (?x ?p2 ?y)  ->  (?x ?p1 ?y)]");
		sb.append("[prp_inv1: (?p1 owl:inverseOf ?p2) (?x ?p1 ?y)  ->  (?y ?p2 ?x)]");
		sb.append("[prp_inv2: (?p1 owl:inverseOf ?p2) (?x ?p2 ?y)  ->  (?y ?p1 ?x)]");
		sb.append("[cax_sco: (?c1 rdfs:subClassOf ?c2) (?x rdf:type ?c1)  ->  (?x rdf:type ?c2)]");
		sb.append("[cax_eqc1: (?c1 owl:equivalentClass ?c2) notEqual(?c1,?c2) (?x rdf:type ?c1)  ->  (?x rdf:type ?c2)]");
		sb.append("[cax_eqc2: (?c1 owl:equivalentClass ?c2)  notEqual(?c1,?c2) (?x rdf:type ?c2)  ->  (?x rdf:type ?c1)]");
		sb.append("[scm_sco: (?c1 rdfs:subClassOf ?c2) (?c2 rdfs:subClassOf ?c3)  ->  (?c1 rdfs:subClassOf ?c3)]");
		sb.append("[scm_eqc1: (?c1 owl:equivalentClass ?c2)  notEqual(?c1,?c2)  -> (?c1 rdfs:subClassOf ?c2) (?c2 rdfs:subClassOf ?c1)]");
		sb.append("[scm_eqc2: (?c1 rdfs:subClassOf ?c2) (?c2 rdfs:subClassOf ?c1)   notEqual(?c1,?c2)  ->   (?c1 rdfs:equivalentClass ?c2)]");
		sb.append("[scm_spo: (?p1 rdfs:subPropertyOf ?p2) (?p2 rdfs:subPropertyOf ?p3)  ->   (?p1 rdfs:subPropertyOf ?p2)]");
		sb.append("[scm_eqp1: (?p1 owl:equivalentProperty ?p2)  notEqual(?p1,?p2)  -> (?p1 rdfs:subPropertyOf ?p2) (?p2 rdfs:subPropertyOf ?p1)]");
		sb.append("[scm_eqp2: (?p1 rdfs:subPropertyOf ?p2) (?p2 rdfs:subPropertyOf ?p1)  notEqual(?p1,?p2)  ->   (?p1 owl:equivalentProperty ?p2)]");
		sb.append("[scm_dom1: (?p1 rdfs:domain ?c1) (?c1 rdfs:subClassOf ?c2)  ->   (?p1 rdfs:domain ?c2)]");
		sb.append("[scm_dom2: (?p2 rdfs:domain ?c) (?p1 rdfs:subPropertyOf ?p2)  ->   (?p1 rdfs:domain ?c)]");
		sb.append("[scm_rng1: (?p1 rdfs:range ?c1)	(?c1 rdfs:subClassOf ?c2)  ->   (?p1 rdfs:range ?c2)]");
		sb.append("[scm_rng2: (?p2 rdfs:range ?c) (?p1 rdfs:subPropertyOf ?p2)  ->   (?p1 rdfs:range ?c)]");
		sb.append("[scm_sco: (?c1 rdfs:subClassOf ?c2) (?c2 rdfs:subClassOf ?c3)  ->   (?c1 rdfs:subClassOf ?c3)]");
		sb.append("[scm_spo: (?p1 rdfs:subPropertyOf ?p2) (?p2 rdfs:subPropertyOf ?p3)  ->   (?p1 rdfs:subPropertyOf ?p3)]");
		final long beforeInference = model.size();
		final GenericRuleReasoner reasoner = new GenericRuleReasoner(
				Rule.parseRules(sb.toString()));
		reasoner.setTransitiveClosureCaching(false);
		final long inferTime = System.nanoTime();
		final Model res = ModelFactory.createInfModel(reasoner, model);
		return new BenchResult((System.nanoTime() - inferTime) / 100000, false);
	}

	/**
	 * @param model
	 * @return
	 */
	private BenchResult inferJenaRhoDf(final Model model) {
		final StringBuilder sb = createRhoDFFragment();
		final long beforeInference = model.size();
		final GenericRuleReasoner reasoner = new GenericRuleReasoner(
				Rule.parseRules(sb.toString()));
		reasoner.setTransitiveClosureCaching(false);
		final long inferTime = System.nanoTime();
		final Model res = ModelFactory.createInfModel(reasoner, model);
		return new BenchResult((System.nanoTime() - inferTime) / 100000, false);
	}

	private StringBuilder createRhoDFFragment() {
		final StringBuilder sb = new StringBuilder();
		sb.append("[prp-rng:   (?x ?p ?y), (?p rdfs:range ?c) -> (?y rdf:type ?c)] ");
		sb.append("[prp-dom:   (?x ?p ?y), (?p rdfs:domain ?c) -> (?x rdf:type ?c)] ");
		sb.append("[scm-sco:   (?x rdfs:subClassOf ?y), (?y rdfs:subClassOf ?z) -> (?x rdfs:subClassOf ?z)] ");
		sb.append("[prp-spo1:  (?a ?p ?b), (?p rdfs:subPropertyOf ?q) -> (?a ?q ?b)] ");
		sb.append("[scm-spo:   (?x rdfs:subPropertyOf ?y), (?y rdfs:subPropertyOf ?z) -> (?x rdfs:subPropertyOf ?z)] ");
		sb.append("[scm-dom2:  (?z rdfs:domain ?x), (?y rdfs:subPropertyOf ?z) -> (?y rdfs:domain ?x)] ");
		sb.append("[scm-rng2:  (?z rdfs:range ?x), (?y rdfs:subPropertyOf ?z) -> (?y rdfs:range ?x)] ");
		sb.append("[cax-sco:   (?x rdfs:subClassOf ?y), (?a rdf:type ?x) -> (?a rdf:type ?y)] ");

		// From Jules's table of rhodf fragment which is actually not in rhodf:
		// sb.append("[scm-eqc2:  (?x rdfs:subClassOf ?y), (?y rdfs:subClassOf ?x) -> (?x owl:equivalentClass ?y)] ");
		// sb.append("[scm-dom1:  (?x rdfs:domain ?y), (?y rdfs:subClassOf ?z) -> (?x rdfs:domain ?z)] ");
		// sb.append("[scm-rng1:  (?x rdfs:range ?y), (?y rdfs:subClassOf ?z) -> (?x rdfs:range ?z)] ");
		// sb.append("[scm-eqp2:  (?x rdfs:subPropertyOf ?y), (?y rdfs:subPropertyOf ?x) -> (?x owl:equivalentProperty ?y)] ");

		return sb;
	}

	/**
	 * @param model
	 * @return
	 */
	private BenchResult inferJenaRDFSFull(final Model model) {
		final Reasoner reasoner = ReasonerRegistry.getRDFSReasoner();
		reasoner.setParameter(ReasonerVocabulary.PROPsetRDFSLevel,
				ReasonerVocabulary.RDFS_FULL);
		return JenaRunReasoningForReasoner(model, reasoner);
	}

	/**
	 * @param model
	 * @return
	 */
	private BenchResult inferJenaRDFSDefault(final Model model) {
		final Reasoner reasoner = ReasonerRegistry.getRDFSReasoner();
		reasoner.setParameter(ReasonerVocabulary.PROPsetRDFSLevel,
				ReasonerVocabulary.RDFS_DEFAULT);
		return JenaRunReasoningForReasoner(model, reasoner);
	}

	private BenchResult JenaRunReasoningForReasoner(final Model model,
			Reasoner reasoner) {
		final long inferTime = System.nanoTime();
		final Model res = ModelFactory.createInfModel(reasoner, model);
		long explicitTriples = model.size();
		long nbTriples = JenaUtils.forceMaterialization(res);

		return new BenchResult((System.nanoTime() - inferTime)
						/ BenchmarkUtils.NANO_TO_MILISEC_RATIO, false);
	}
}
