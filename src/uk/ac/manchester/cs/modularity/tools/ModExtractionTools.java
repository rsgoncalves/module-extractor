package uk.ac.manchester.cs.modularity.tools;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.parameters.Imports;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
 * Time: 10:59:16 AM
 */
public class ModExtractionTools {

    // =============== Set manager and factory ================================================
    public static final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    public static final OWLDataFactory factory = manager.getOWLDataFactory();

    // =============== Timers and statistics variables ========================================
    protected static Map<Integer,Long> timers = new HashMap<Integer, Long>();
    protected static int[][] stat    = new int[2][];
    protected static int[][] statRef = new int[2][];

    public static void startTime(){
        startTime(0);
    }

    public static void startTime(int timerNumber) {
        long time = System.currentTimeMillis();
        timers.put(timerNumber, time);
    }

    public static String stopTime() {
        return stopTime(0);
    }

    public static String stopTime(int timerNumber) {
        if (timers.containsKey(timerNumber)) {
            long time2 = System.currentTimeMillis() - timers.get(timerNumber);
            String centis = "" + (time2 / 10) % 100;
            String secs = "" + (time2 / 1000) % 60;
            String mins = "" + time2 / 60000;
            if (centis.length() < 2) centis = "0" + centis;
            if (secs.length() < 2) secs = "0" + secs;
            if (mins.length() < 2) mins = "0" + mins;
            return mins + "m" + secs + "." + centis + "s";

        }
        else {
            return "Timer number " + timerNumber + " has not been started.";
        }
    }

    public static long getTime(int timerNumber) {
        return System.currentTimeMillis() - timers.get(timerNumber);
    }

    protected static String percentRatio(int a, int b){
        int pr = Math.round(1000*(float)a/(float)b);
        int pre = pr/10;
        int post = pr%10;
        return pre + "." + post;
    }

    protected static String prettyPrint(int refType){
        String s = "";

        s = s + "\nNumber of entities:          " + stat[0][0];
        if (refType == 2) s = s + " (" + percentRatio(stat[0][0], statRef[0][0]) + "%)";

        for (EntitiesStat entStat : EntitiesStat.values()) {
            if (stat[0][entStat.ordinal()+1] != 0){
                s = s + "\n   " + entStat.toString(25, ":") + stat[0][entStat.ordinal()+1];
                if (refType == 2) s = s + " (" + percentRatio(stat[0][entStat.ordinal()+1], statRef[0][entStat.ordinal()+1]) + "%)";
            }
        }

        s = s+ "\n";
        s = s + "\nNumber of axioms:            " + stat[1][0];
        if (refType == 2) s = s + " (" + percentRatio(stat[1][0], statRef[1][0]) + "%)";

        for (AxiomsStat axStat : AxiomsStat.values()) {
            if (stat[1][axStat.ordinal()+1] != 0){
                s = s + "\n   " + axStat.toString(25, ":") + stat[1][axStat.ordinal()+1];
                if (refType == 2) s = s + " (" + percentRatio(stat[1][axStat.ordinal()+1], statRef[1][axStat.ordinal()+1]) + "%)";
            }
        }

        if (refType == 1) {
            for (int i = 0; i < 2; i++) {
                statRef[i] = new int[stat[i].length];
                System.arraycopy(stat[i], 0, statRef[i], 0, stat[i].length);
            }
        }

        return s;
    }

    public static int[] getAxiomsStat(Set<OWLAxiom> axs){
        int[] stat = new int[AxiomsStat.values().length + 1];

        stat[0] = axs.size();
        for (int i = 1; i < stat.length; i++) {
            stat[i] = 0;
        }

        int ignoredAxiomCount = 0;
        for (OWLAxiom ax : axs){
            boolean found = false;
            for (AxiomsStat axStat : AxiomsStat.values()){
                if (axStat.cls.isAssignableFrom(ax.getClass())) {
                    stat[axStat.ordinal()+1]++;
                    found = true;
                    break;
                }
            }
            if (!found){
                ignoredAxiomCount++;
                System.err.println("Axiom not found: " + ax);
            }
        }

        if (ignoredAxiomCount > 0) {
            System.err.println("!!! " + ignoredAxiomCount + " " + "AXIOMS NOT FOUND !!!");
        }
        return stat;
    }

    public static int[] getEntitiesStat(Set<OWLEntity> ents){
        int[] stat = new int[EntitiesStat.values().length + 1];

        stat[0] = ents.size();
        for (int i = 1; i < stat.length; i++) {
            stat[i] = 0;
        }

        int ignoredEntityCount = 0;
        for (OWLEntity ent : ents){
            boolean found = false;
            for (EntitiesStat entStat : EntitiesStat.values()){
                if (entStat.cls.isAssignableFrom(ent.getClass())) {
                    stat[entStat.ordinal()+1]++;
                    found = true;
                    break;
                }
            }
            if (!found){
                ignoredEntityCount++;
                System.err.println("Entity not found: " + ent + "   " + ent.getClass());
            }
        }

        if (ignoredEntityCount > 0) {
            System.err.println("!!! " + ignoredEntityCount + " " + "ENTITIES NOT FOUND !!!");
        }
        return stat;
    }

    public static String getStatisticsSetRef(OWLOntology ontology){
        return getStatisticsSetRef(ontology.getSignature(Imports.INCLUDED), ontology.getAxioms());
    }

    public static String getStatisticsSetRef(Set<OWLEntity> ents, Set<OWLAxiom> axs){
        stat[0] = getEntitiesStat(ents);
        stat[1] = getAxiomsStat(axs);
        return prettyPrint(1);
    }

    public static String getStatisticsGetRef(OWLOntology ontology){
        return getStatisticsGetRef(ontology.getSignature(Imports.INCLUDED), ontology.getAxioms());
    }

    public static String getStatisticsGetRef(Set<OWLEntity> ents, Set<OWLAxiom> axs){
        stat[0] = getEntitiesStat(ents);
        stat[1] = getAxiomsStat(axs);
        return prettyPrint(2);
    }

    public static String prefix(String text) {
    	startTime();
        return "----------------------------------------------------------------------------\n" + text;
    }

    public static String suffix() {
        return "Time elapsed: " + stopTime() + "\n";
    }

    public static OWLOntology loadOntology(String ontologyName) throws OWLOntologyCreationException {
        return manager.loadOntology(IRI.create(ontologyName));
    }

}