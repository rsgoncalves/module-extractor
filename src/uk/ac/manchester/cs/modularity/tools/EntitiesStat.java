package uk.ac.manchester.cs.modularity.tools;

import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

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
 * Time: 11:37:25 AM
 */
public enum EntitiesStat {
    CLASSES      (OWLClass.class,               "classes"),
    OPS          (OWLObjectProperty.class,      "object properties"),
    DPS          (OWLDataProperty.class,        "data properties"),
    INDS         (OWLIndividual.class,          "individuals"),
    DTS          (OWLDatatype.class,            "datatypes"),
    APS          (OWLAnnotationProperty.class,  "annotation props");

    Class<?> cls;
    String name;

    EntitiesStat(Class<?> cls, String name){
        this.cls      = cls;
        this.name     = name;
    }

    protected static final String[] spaces = {"", " ", "  ", "   ", "    ", "     ", "      ", "       ", "        ", "         ", "          ", "           ", "            ", "             ", "              ", "               ", "                ", "                 ", "                  ", "                   ", };

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