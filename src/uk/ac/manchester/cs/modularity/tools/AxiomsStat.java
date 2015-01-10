package uk.ac.manchester.cs.modularity.tools;

import org.semanticweb.owlapi.model.OWLAnnotationAxiom;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyAxiom;

/**
Copyright 2009 Thomas Schneider

This file is part of OWL-ME: OWL Module Extractor.

OWL-ME: OWL Module Extractor is free software: you can redistribute it and/or 
modify it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

OWL-ME: OWL Module Extractor is distributed in the hope that it will be 
useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with OWL-ME: OWL Module Extractor.  If not, see 
<http://www.gnu.org/licenses/>.
*/

/**
 * Created by IntelliJ IDEA.
 * User: Thomas Schneider
 * Date: Oct 1, 2009
 * Time: 11:37:36 AM
 */
public enum AxiomsStat {
    SUBCLASSAXS  (OWLSubClassOfAxiom.class,               "subclass axioms"),
    EQCLASSAXS   (OWLEquivalentClassesAxiom.class,        "equiv-classes axioms"),
    DISJCLASSAXS (OWLDisjointClassesAxiom.class,          "disj-classes axioms"),
    SUBPROPAXS   (OWLSubPropertyAxiom.class,              "sub-OP axioms"),
    INVOPAXS     (OWLInverseObjectPropertiesAxiom.class,  "inverse-OP axioms"),
    FUNCOPAXS    (OWLFunctionalObjectPropertyAxiom.class, "func-OP axioms"),
    FUNCDPAXS    (OWLFunctionalDataPropertyAxiom.class,   "func-DP axioms"),
    OPDOMAINAXS  (OWLObjectPropertyDomainAxiom.class,     "OP domain axioms"),
    OPRANGEAXS   (OWLObjectPropertyRangeAxiom.class,      "OP range axioms"),
    DPDOMAINAXS  (OWLDataPropertyDomainAxiom.class,       "DP domain axioms"),
    DPRANGEAXS   (OWLDataPropertyRangeAxiom.class,        "DP range axioms"),
    CASSAXS      (OWLClassAssertionAxiom.class,           "class assertion axioms"),
    OPASSAXS     (OWLObjectPropertyAssertionAxiom.class,  "OP assertion axioms"),
    DPASSAXS     (OWLDataPropertyAssertionAxiom.class,    "DP assertion axioms"),
    DIFFINDAXS   (OWLDifferentIndividualsAxiom.class,     "different ind axioms"),
    ANNOTAXS     (OWLAnnotationAxiom.class,               "entity annot axioms"),
    IMPORTAXS    (OWLImportsDeclaration.class,            "imports declarations"),
    DECAXS       (OWLDeclarationAxiom.class,              "entity declarations"),
    SPCAXS       (OWLSubPropertyChainOfAxiom.class,       "subprop chain axioms");

    Class<?> cls;
    String name;

    AxiomsStat(Class<?> cls, String name){
        this.cls      = cls;
        this.name     = name;
    }
        protected static final String[] spaces = {"", " ", "  ", "   ", "    ", "     ", "      ", "       ", "        ", "         ", "          ", "           ", "            ", "             ", "              ", "               ", "                ", "                 ", "                  ", "                   ", "                    ", "                     ", "                      ", };

        public String toString() {
            return name;
        }

        public String toString(int tabPos) {
            return name + spaces[tabPos-name.length()];
        }

        public String toString(int tabPos, String colon) {
            return name.substring(0,1).toUpperCase() + name.substring(1) + colon + spaces[tabPos-name.length()];
        }
}