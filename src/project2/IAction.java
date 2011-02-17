package project2;

import java.io.PrintWriter;

public interface IAction extends Comparable<IAction> {
	int getType();
	PrintWriter getWriter();
	void setWriter(PrintWriter writer);
}
