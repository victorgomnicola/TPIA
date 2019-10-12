package heuristicas;

import fr.uga.pddl4j.encoding.CodedProblem;
import fr.uga.pddl4j.planners.statespace.search.strategy.Node;
import fr.uga.pddl4j.util.BitExp;
import fr.uga.pddl4j.util.BitState;

public class HLinha implements NovaHeuristica {

	
	@Override
	public int estimate(BitState arg0, BitExp arg1) {
		return 1;
	}

	@Override
	public double estimate(Node arg0, BitExp arg1) {
		return 1;
	}

	@Override
	public boolean isAdmissible() {
		return true;
	}

}
