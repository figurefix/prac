package figurefix.prac.lab.branch;

public class BranchEditConflict extends Exception {
	
	BranchEditConflict(String msg) {
		super(msg);
	}
	
	BranchEditConflict(Exception cause) {
		super(cause);
	}
}
