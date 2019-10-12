package heuristicas;

/*
 * Copyright (c) 2011 by Damien Pellier <Damien.Pellier@imag.fr>.
 *
 * This file is part of PDDL4J library.
 *
 * PDDL4J is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PDDL4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PDDL4J.  If not, see <http://www.gnu.org/licenses/>
 */

import fr.uga.pddl4j.encoding.CodedProblem;

import java.io.Serializable;

/**
 * This classes implements useful methods to manipulate the heuristics.
 *
 * @author D. Pellier
 * @version 1.0 - 09.02.2011
 * @see Heuristic
 */
public final class GeraHeuristica implements Serializable {

    /**
     * The serial version id of the class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Private constructor just for prevent user to instantiate this class.
     */
    private GeraHeuristica() {
    }

    /**
     * Create an heuristic of a specified type.
     *
     * @param type    the type of the heuristic to create.
     * @param problem the problem for which the heuristic is created.
     * @return the heuristic created.
     * @throws NullPointerException if <code>type == null || problem == null</code>.
     */
    public static NovaHeuristica createHeuristic(final NovaHeuristica.Type type, final CodedProblem problem) {
        NovaHeuristica heuristica;
        
		if (type.equals(NovaHeuristica.Type.FAST_FORWARD)) {
            heuristica = new HeuristicaFastForward(problem);
        } else if (type.equals(NovaHeuristica.Type.SUM)) {
            heuristica = new HeuristicaSoma(problem);
        } else if (type.equals(NovaHeuristica.Type.SUM_MUTEX)) {
            heuristica = new HeuristicaSomaMutex(problem);
        } else if (type.equals(NovaHeuristica.Type.AJUSTED_SUM)) {
            heuristica = new HeuristicaSomaAjustada(problem);
        } else if (type.equals(NovaHeuristica.Type.AJUSTED_SUM2)) {
            heuristica = new HeuristicaSomaAjustada2(problem);
        } else if (type.equals(NovaHeuristica.Type.SET_LEVEL)) {
            heuristica = new HeuristicaSetLevel(problem);
        } else if (type.equals(NovaHeuristica.Type.MAX)) {
            heuristica = new HeuristicaMaxima(problem);
        } else if (type.equals(NovaHeuristica.Type.HL)) {
            heuristica = new HLinha();
        } else {
        	throw new UnsupportedOperationException();
        }
        return heuristica;
    }
}
