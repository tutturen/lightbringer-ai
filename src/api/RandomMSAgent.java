package api;

import java.util.Random;

import org.sat4j.core.*;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.reader.DimacsReader;
import org.sat4j.reader.Reader;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

/**
 * This agent uncovering positions randomly. It obviously does not use a SAT
 * solver.
 * 
 */
public class RandomMSAgent extends MSAgent {

    private Random rand = null;

    @Override
    public boolean solve() {

        final int MAXVAR = 100;
        final int NBCLAUSES = 2;

        ISolver solver = SolverFactory.newDefault();
        Reader reader = new DimacsReader(solver);

        solver.newVar(MAXVAR);
        solver.setExpectedNumberOfClauses(NBCLAUSES);
        // Feed the solver using Dimacs format, using arrays of int
        // (best option to avoid dependencies on SAT4J IVecInt)
        for (int i = 0; i < NBCLAUSES; i++) {
            int[] clauseOne = { 1, -3, 4, 1 };
            int[] clauseTwo = { 1, 2 };

            // while int [] clause = {1, -3, 7, 0}; is not fine
            try {
                solver.addClause(new VecInt(clauseOne));
                solver.addClause(new VecInt(clauseTwo));

            } catch (ContradictionException e) {
                e.printStackTrace();
            }
        }

        // we are done. Working now on the IProblem interface
        IProblem problem = solver;
        try {
            if (problem.isSatisfiable()) {
                System.out.println("Satisfiable!");
                System.out.println(reader.decode(problem.model()));
            } else {
                System.out.println("Not satisfiable!");
            }
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        this.rand = new Random();
        int numOfRows = this.field.getNumOfRows();
        int numOfCols = this.field.getNumOfCols();
        int x, y, feedback;
        do {
            x = rand.nextInt(numOfCols);
            y = rand.nextInt(numOfRows);
            feedback = field.uncover(x, y);
            System.out.println("x: " + x + ", y: " + y);
            System.out.println(this.field);

        } while (feedback >= 0 && !field.solved());

        return field.solved();
    }

}
