package ep1;

import fr.uga.pddl4j.encoding.CodedProblem;
import fr.uga.pddl4j.planners.statespace.AbstractStateSpacePlanner;
import fr.uga.pddl4j.util.Plan;
import fr.uga.pddl4j.planners.Planner;
import fr.uga.pddl4j.planners.statespace.StateSpacePlanner;
import fr.uga.pddl4j.planners.ProblemFactory;
import fr.uga.pddl4j.parser.ErrorManager;
import fr.uga.pddl4j.heuristics.relaxation.Heuristic;
import fr.uga.pddl4j.planners.statespace.search.strategy.AStar;
import fr.uga.pddl4j.planners.statespace.search.strategy.StateSpaceStrategy;
import fr.uga.pddl4j.heuristics.relaxation.Heuristic;
import fr.uga.pddl4j.planners.statespace.search.strategy.AStar;
import fr.uga.pddl4j.planners.statespace.search.strategy.Node;
import fr.uga.pddl4j.planners.statespace.search.strategy.StateSpaceStrategy;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * This class implements a simple forward planner based on A* algorithm.
 *
 * @author D. Pellier
 * @version 1.0 - 06.06.2018
 */
public final class ASP extends AbstractStateSpacePlanner {

	/*
	 * The arguments of the planner.
	 */
	private Properties arguments;

	/**
	 * Creates a new ASP planner with the default parameters.
	 *
	 * @param arguments the arguments of the planner.
	 */
	public ASP(final Properties arguments) {
		super();
		this.arguments = arguments;
	}

	/**
	 * Solves the planning problem and returns the first solution search found.
	 *
	 * @param problem the problem to be solved.
	 * @return a solution search or null if it does not exist.
	 */
	@Override
	public Plan search(final CodedProblem problem) {
	  int timeout = (int) this.arguments.get(Planner.TIMEOUT);
	  double weight = (double) arguments.get(StateSpacePlanner.WEIGHT);         
	  StateSpaceStrategy astar = new AStar(timeout, Heuristic.Type.FAST_FORWARD, weight);
	  Node goalNode = astar.searchSolutionNode(problem);
	  Planner.getLogger().trace(problem.toString(goalNode));
	  return astar.extractPlan(goalNode, problem);
	}
	
	/**
	 * The main method of the <code>ASP</code> example. The command line syntax is
	 * as follow:
	 * <p>
	 * 
	 * <pre>
	 * usage of ASP:
	 *
	 * OPTIONS   DESCRIPTIONS
	 *
	 * -o <i>str</i>   operator file name
	 * -f <i>str</i>   fact file name
	 * -w <i>num</i>   the weight used in the a star search (preset: 1)
	 * -t <i>num</i>   specifies the maximum CPU-time in seconds (preset: 300)
	 * -h              print this message
	 *
	 * </pre>
	 * </p>
	 *
	 * @param args the arguments of the command line.
	 */
	public static void main(String[] args) {
		args = new String[4];
		args[0] = "-o";
		args[1] = "C:\\Users\\Avell\\eclipse-workspace\\TPIA\\src\\ep1\\domain.pddl";
		args[2] = "-f";
		args[3] = "C:\\Users\\Avell\\eclipse-workspace\\TPIA\\src\\ep1\\problem.pddl";
		final Properties arguments = ASP.parseCommandLine(args);
		if (arguments == null) {
			ASP.printUsage();
			System.exit(0);
		}

		final ASP planner = new ASP(arguments);
		final ProblemFactory factory = ProblemFactory.getInstance();

		File domain = (File) arguments.get(Planner.DOMAIN);
		File problem = (File) arguments.get(Planner.PROBLEM);
		ErrorManager errorManager = null;
		try {
			errorManager = factory.parse(domain, problem);
		} catch (IOException e) {
			Planner.getLogger().trace("\nunexpected error when parsing the PDDL planning problem description.");
			System.exit(0);
		}

		if (!errorManager.isEmpty()) {
			errorManager.printAll();
			System.exit(0);
		} else {
			Planner.getLogger().trace("\nparsing domain file done successfully");
			Planner.getLogger().trace("\nparsing problem file done successfully\n");
		}

		final CodedProblem pb = factory.encode();
//		for (int i = 0; i < pb.getOperators().size(); i++) {
//			for (int j = 0; j < pb.getRelevantFacts().size(); j++) {
//				System.out.println(pb.getOperators().get(i).getName() + " op, "
//		    		    + pb.getRelevantFacts().get(j).getVariable()+ " facts)\n");
//			}
//			
//		}
//		
		Planner.getLogger().trace("\nencoding problem done successfully (" + pb.getOperators().size() + " ops, "
				+ pb.getRelevantFacts().size() + " facts)\n");

		if (!pb.isSolvable()) {
			Planner.getLogger()
					.trace(String.format("goal can be simplified to FALSE." + "no search will solve it%n%n"));
			System.exit(0);
		}
		
		final Plan plan = planner.search(pb);
		if (plan != null) {
		  // Print plan information
		  Planner.getLogger().trace(String.format("%nfound plan as follows:%n%n" + pb.toString(plan)));
		  Planner.getLogger().trace(String.format("%nplan total cost: %4.2f%n%n", plan.cost()));
		} else {
		  Planner.getLogger().trace(String.format(String.format("%nno plan found%n%n")));
		}

	}

	/**
	 * Parse the command line and return the planner's arguments.
	 *
	 * @param args the command line.
	 * @return the planner arguments or null if an invalid argument is encountered.
	 */
	private static Properties parseCommandLine(String[] args) {
		// Get the default arguments from the super class
		final Properties arguments = StateSpacePlanner.getDefaultArguments();
//        for (int i = 0; i < args.length; i++) {
//			System.out.println(args[i]);
//		}
		// Parse the command line and update the default argument value
		for (int i = 0; i < args.length; i += 2) {
			if ("-o".equalsIgnoreCase(args[i]) && ((i + 1) < args.length)) {
				if (!new File(args[i + 1]).exists())
					return null;
//                System.out.println("teste");
				arguments.put(Planner.DOMAIN, new File(args[i + 1]));
			} else if ("-f".equalsIgnoreCase(args[i]) && ((i + 1) < args.length)) {
				if (!new File(args[i + 1]).exists())
					return null;
				arguments.put(Planner.PROBLEM, new File(args[i + 1]));
			} else if ("-t".equalsIgnoreCase(args[i]) && ((i + 1) < args.length)) {
				final int timeout = Integer.parseInt(args[i + 1]) * 1000;
				if (timeout < 0)
					return null;
				arguments.put(Planner.TIMEOUT, timeout);
			} else if ("-w".equalsIgnoreCase(args[i]) && ((i + 1) < args.length)) {
				final double weight = Double.parseDouble(args[i + 1]);
				if (weight < 0)
					return null;
				arguments.put(StateSpacePlanner.WEIGHT, weight);
			} else {
				return null;
			}
		}
		// Return null if the domain or the problem was not specified
		return (arguments.get(Planner.DOMAIN) == null || arguments.get(Planner.PROBLEM) == null) ? null : arguments;
	}

	/**
	 * Print the usage of the ASP planner.
	 */
	private static void printUsage() {
		final StringBuilder strb = new StringBuilder();
		strb.append("\nusage of PDDL4J:\n").append("OPTIONS   DESCRIPTIONS\n")
				.append("-o <str>    operator file name\n").append("-f <str>    fact file name\n")
				.append("-w <num>    the weight used in the a star seach (preset: 1.0)\n")
				.append("-t <num>    specifies the maximum CPU-time in seconds (preset: 300)\n")
				.append("-h          print this message\n\n");
		Planner.getLogger().trace(strb.toString());
	}

}