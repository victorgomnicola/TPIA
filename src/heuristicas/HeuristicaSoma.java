package heuristicas;

import fr.uga.pddl4j.encoding.CodedProblem;
import fr.uga.pddl4j.planners.statespace.search.strategy.Node;
import fr.uga.pddl4j.util.BitExp;
import fr.uga.pddl4j.util.BitState;

public class HeuristicaSoma extends HeuristicaRelaxada {
    /**
     * The serial version id of the class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new <code>SUM_ID</code> heuristic for a specified planning problem.
     *
     * @param problem the planning problem.
     * @throws NullPointerException if <code>problem == null</code>.
     */
    public HeuristicaSoma (final CodedProblem problem) {
        super(problem);
        super.setAdmissible(false);
    }

    /**
     * Return the distance to the goal state from the specified state. If the return value is
     * <code>Integer.MAX_VALUE</code>, it means that the goal is unreachable from the specified
     * state. More precisely, this method returns the level of the planning graph where all the
     * propositions of the goal are reached without any mutex or <code>Integer.MAX_VALUE</code>
     * otherwise.
     *
     * @param state the state from which the distance to the goal must be estimated.
     * @param goal  the goal expression.
     * @return the distance to the goal state from the specified state or
     * <code>Integer.MAX_VALUE</code> if the goal is unreachable from the specified state.
     * @throws NullPointerException if <code>state == null</code>.
     */
    @Override
    public int estimate(final BitState state, final BitExp goal) {
        super.setGoal(goal);
        this.expandRelaxedPlanningGraph(state);
        return super.isGoalReachable() ? super.getSumValue() : Integer.MAX_VALUE;
    }

    /**
     * Return the estimated distance to the goal to reach the specified state. If the return value is
     * <code>DOUBLE.MAX_VALUE</code>, it means that the goal is unreachable from the specified
     * state.
     *
     * @param node the state from which the distance to the goal must be estimated.
     * @param goal the goal expression.
     * @return the distance to the goal state from the specified state.
     */
    @Override
    public double estimate(final Node node, final BitExp goal) {
        return estimate((BitState) node, goal);
    }
}
