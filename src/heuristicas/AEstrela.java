package heuristicas;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;

import javax.swing.event.EventListenerList;

import fr.uga.pddl4j.encoding.CodedProblem;
import fr.uga.pddl4j.planners.statespace.search.strategy.Node;
import fr.uga.pddl4j.planners.statespace.search.strategy.NodeComparator;
import fr.uga.pddl4j.util.BitOp;
import fr.uga.pddl4j.util.BitState;
import fr.uga.pddl4j.util.MemoryAgent;
import fr.uga.pddl4j.util.Plan;
import fr.uga.pddl4j.util.SequentialPlan;
import fr.uga.pddl4j.util.SolutionEvent;
import fr.uga.pddl4j.util.SolutionListener;

public class AEstrela {
    EventListenerList solutionListenerList = new EventListenerList();
    /**
     * The heuristic of the planner.
     */
    private Heuristica.Type heuristic;

    /**
     * The heuristic weight.
     */
    private double weight;

    /**
     * The timeout for the search in second.
     */
    private int timeout;

    /**
     * The time spend to find a solution.
     */
    private long searchingTime;

    /**
     * The amount of memory used for the search.
     */
    private long memoryUsed;

    /**
     * The number of explored nodes.
     */
    private int exploredNodes;

    /**
     * The number of pending nodes.
     */
    private int pendingNodes;

    /**
     * The number of created nodes.
     */
    private int createdNodes;

    /**
     * Returns the heuristicType to use to solve the planning problem.
     *
     * @return the heuristicType to use to solve the planning problem.
     */
    public final Heuristica.Type getHeuristicType() {
        return this.heuristic;
    }
    /**
     * Creates a new AStar search strategy with default parameters.
     *
     */
    public AEstrela(Heuristica.Type heuristic, double weight, int timeout) {
        this.heuristic = heuristic;
        this.weight = weight;
        this.timeout = timeout;
        this.searchingTime = 0;
        this.memoryUsed = 0;
    }

    /**
     * Solves the planning problem and returns the first solution search found.
     *
     * @param codedProblem the problem to be solved. The problem cannot be null.
     * @return a solution search or null if it does not exist.
     */
    public Node search(final CodedProblem codedProblem) {
        
    	Objects.requireNonNull(codedProblem);
        final long begin = System.currentTimeMillis();
		final NovaHeuristica heuristic = GeraHeuristica.createHeuristic(this.heuristic, codedProblem);
        // Get the initial state from the planning problem
        final BitState init = new BitState(codedProblem.getInit());
        // Initialize the closed list of nodes (store the nodes explored)
        final Map<BitState, Node> closeSet = new HashMap<>();
        final Map<BitState, Node> openSet = new HashMap<>();
        // Initialize the opened list (store the pending node)
        final double currWeight = getWeight();
        // The list stores the node ordered according to the A* (getFValue = g + h) function
        final PriorityQueue<Node> open = new PriorityQueue<>(100, new NodeComparator(currWeight));
        // Creates the root node of the tree search
        final Node root = new Node(init, null, -1, 0,
            heuristic.estimate(init, codedProblem.getGoal()));
        // Adds the root to the list of pending nodes
        open.add(root);
        openSet.put(init, root);
        System.out.println(heuristic.getClass());
        this.resetNodesStatistics();
        Node solution = null;
        final int timeout = getTimeout();
        long time = 0;
        // Start of the search
        while (!open.isEmpty() && solution == null && time < timeout) {
            // Pop the first node in the pending list open
            final Node current = open.poll();
            openSet.remove(current);
            closeSet.put(current, current);
            // If the goal is satisfy in the current node then extract the search and return it
            if (current.satisfy(codedProblem.getGoal())) {
                solution = current;
                fireSolution(new SolutionEvent(this, solution, codedProblem));
            } else {
                // Try to apply the operators of the problem to this node
                int index = 0;
                for (BitOp op : codedProblem.getOperators()) {
                    // Test if a specified operator is applicable in the current state
                    if (op.isApplicable(current)) {
                        Node state = new Node(current);
                        this.setCreatedNodes(this.getCreatedNodes() + 1);
                        // Apply the effect of the applicable operator
                        // Test if the condition of the effect is satisfied in the current state
                        // Apply the effect to the successor node
                        op.getCondEffects().stream().filter(ce -> current.satisfy(ce.getCondition())).forEach(ce ->
                            // Apply the effect to the successor node
                            state.apply(ce.getEffects())
                        );
                        final double g = current.getCost() + op.getCost();
                        Node result = openSet.get(state);
                        if (result == null) {
                            result = closeSet.get(state);
                            if (result != null) {
                                if (g < result.getCost()) {
                                    result.setCost(g);
                                    result.setParent(current);
                                    result.setOperator(index);
                                    result.setDepth(current.getDepth() + 1);
                                    open.add(result);
                                    openSet.put(result, result);
                                    closeSet.remove(result);
                                }
                            } else {
                                state.setCost(g);
                                state.setParent(current);
                                state.setOperator(index);
                                state.setHeuristic(heuristic.estimate(state, codedProblem.getGoal()));
                                state.setDepth(current.getDepth() + 1);
                                open.add(state);
                                openSet.put(state, state);
                            }
                        } else if (g < result.getCost()) {
                            result.setCost(g);
                            result.setParent(current);
                            result.setOperator(index);
                            result.setDepth(current.getDepth() + 1);
                        }

                    }
                    index++;
                }
            }
            // Compute the searching time
            time = System.currentTimeMillis() - begin;
        }

        this.setExploredNodes(closeSet.size());
        this.setPendingNodes(openSet.size());
        this.setMemoryUsed(MemoryAgent.getDeepSizeOf(closeSet) + MemoryAgent.getDeepSizeOf(openSet));
        this.setSearchingTime(time);

        // return the search computed or null if no search was found
        return solution;
    }
    
    
    
    
    
    /**
     * Sets the heuristicType to use to solved the problem.
     *
     * @param heuristicType the heuristicType to use to solved the problem. The heuristicType cannot be null.
     */
    public final void setHeuristicType(final Heuristica.Type heuristicType) {
        Objects.requireNonNull(heuristicType);
        this.heuristic = heuristicType;
    }

    /**
     * Returns the weight set to the heuristic.
     *
     * @return the weight set to the heuristic.
     */
    public final double getWeight() {
        return this.weight;
    }

    /**
     * Sets the wight of the heuristic.
     *
     * @param weight the weight of the heuristic. The weight must be positive.
     */
    public final void setWeight(final double weight) {
        this.weight = weight;
    }

    /**
     * Sets the time out of the planner.
     *
     * @param timeout the time allocated to the search in second. Timeout mus be positive.
     */
    public final void setTimeOut(final int timeout) {
        this.timeout = timeout;
    }

    /**
     * Returns the time out of the planner.
     *
     * @return the time out of the planner, i.e., the time allocated to the search in second.
     */
    public int getTimeout() {
        return this.timeout;
    }

    /**
     * Returns the time spend to find a solution.
     *
     * @return the time spend to find a solution.
     */
    public long getSearchingTime() {
        return searchingTime;
    }

    /**
     * Sets the time out of the planner.
     *
     * @param searchingTime the time allocated to the search in second. Timeout mus be positive.
     */
    public void setSearchingTime(final long searchingTime) {
        this.searchingTime = searchingTime;
    }

    /**
     * Returns the amount of memory used for the search.
     *
     * @return the amount of memory used for the search.
     */
    public long getMemoryUsed() {
        return this.memoryUsed;
    }

    /**
     * Sets the amount of memory used for the search.
     *
     * @param memoryUsed the amount of memory used for the search.
     */
    public void setMemoryUsed(final long memoryUsed) {
        this.memoryUsed = memoryUsed;
    }

    /**
     * Returns the number of explored nodes.
     *
     * @return the number of explored nodes.
     */
    public int getExploredNodes() {
        return this.exploredNodes;
    }

    /**
     * Sets the number of explored nodes.
     *
     * @param exploredNodes the number of explored nodes.
     */
    public void setExploredNodes(final int exploredNodes) {
        this.exploredNodes = exploredNodes;
    }

    /**
     * Returns the number of pending nodes.
     *
     * @return the number of pending nodes.
     */
    public int getPendingNodes() {
        return this.pendingNodes;
    }

    /**
     * Sets the number of pending nodes.
     *
     * @param pendingNodes the number of pending nodes.
     */
    public void setPendingNodes(final int pendingNodes) {
        this.pendingNodes = pendingNodes;
    }

    /**
     * Returns the number of created nodes.
     *
     * @return the number of created nodes.
     */
    public int getCreatedNodes() {
        return this.createdNodes;
    }

    /**
     * Sets the number of created nodes.
     *
     * @param createdNodes the number of created nodes.
     */
    public void setCreatedNodes(final int createdNodes) {
        this.createdNodes = createdNodes;
    }

    /**
     * Create a new search strategy.
     */

      /**
     * Search a solution node to a specified domain and problem.
     *
     * @param codedProblem the problem to be solved. The problem cannot be null.
     * @return the solution node or null.
     */
    public Node searchSolutionNode(final CodedProblem codedProblem) {
        Objects.requireNonNull(codedProblem);
        return search(codedProblem);
    }

    /**
     * Search a solution plan to a specified domain and problem.
     *
     * @param codedProblem the problem to be solved. The problem cannot be null.
     * @return the solution plan or null.
     */
    public Plan searchPlan(final CodedProblem codedProblem) {
    	
        Objects.requireNonNull(codedProblem);
        final Node solutionNode = search(codedProblem);
        if (solutionNode != null) {
            return extractPlan(solutionNode, codedProblem);
        } else {
            return null;
        }
    }

    /**
     * Extract a plan from a solution node for the specified planning problem.
     *
     * @param node    the solution node.
     * @param problem the problem to be solved.
     * @return the solution plan or null is no solution was found.
     */
    public SequentialPlan extractPlan(final Node node, final CodedProblem problem) {
        if (node != null) {
            Node n = node;
            final SequentialPlan plan = new SequentialPlan();
            while (n.getParent() != null) {
                final BitOp op = problem.getOperators().get(n.getOperator());
                plan.add(0, op);
                n = n.getParent();
            }
            return plan;
        } else {
            return null;
        }
    }

    /**
     * Reset Nodes statistics.
     */
    protected void resetNodesStatistics() {
        this.exploredNodes = 0;
        this.pendingNodes = 0;
        this.createdNodes = 0;
    }

    /**
     * Adds SolutionListener to the list of SolutionListener.
     *
     * @param listener the SolutionListener to add.
     */
    public void addSolutionListener(SolutionListener listener) {
        solutionListenerList.add(SolutionListener.class, listener);
    }

    /**
     * Removes SolutionListener to the list of SolutionListener.
     *
     * @param listener the SolutionListener to remove.
     */
    public void removeSolutionListener(SolutionListener listener) {
        solutionListenerList.remove(SolutionListener.class, listener);
    }

    /**
     * Processes SolutionEvent when one is fired.
     *
     * @param evt the solution event to process.
     */
    public void fireSolution(SolutionEvent evt) {
        Object[] listeners = solutionListenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == SolutionListener.class) {
                ((SolutionListener) listeners[i + 1]).newSolutionFound(evt);
            }
        }
    }
}
