package heuristicas;

import fr.uga.pddl4j.heuristics.relaxation.Heuristic;

public interface NovaHeuristica extends Heuristic {

    enum Type {

        /**
         * The type for the <code>AdjustedSum</code> heuristic.
         */
        AJUSTED_SUM,
        /**
         * The type for the <code>AdjustedSum2</code> heuristic.
         */
        AJUSTED_SUM2,
        /**
         * The type for the <code>AdjustedSum2M</code> heuristic.
         */
        AJUSTED_SUM2M,
        /**
         * The type for the <code>Combo</code> heuristic.
         */
        COMBO,
        /**
         * The type for the <code>Max</code> heuristic.
         */
        MAX,
        /**
         * The type for the <code>Min Cost</code> heuristic.
         */
        MIN_COST,
        /**
         * The type for the <code>FastForward</code> heuristic.
         */
        FAST_FORWARD,
        /**
         * The type for the <code>SetLevel</code> heuristic.
         */
        SET_LEVEL,
        /**
         * The type for the <code>Sum</code> heuristic.
         */
        SUM,
        /**
         * The type for the <code>SumMutex</code> heuristic.
         */
        SUM_MUTEX,
        /**
         * The type for the <code>SumMutex</code> heuristic.
         */
        HL,
    }
}
