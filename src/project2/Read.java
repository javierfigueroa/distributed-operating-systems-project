package project2;

import java.io.PrintWriter;

public class Read implements IAction{	
	
	public Read(int id) {
		this.id = id;
	}
	
	@Override
	public int getType() {
		return 0;
	}

	public int getId() {
		return id;
	}

	public void setWriter(PrintWriter writer) {
		this.writer = writer;
	}

	public PrintWriter getWriter() {
		return writer;
	}
	
	public int reply(int code, String message) {
		writer.println(code + " " + message);
		return code;
	}

	@Override
	public int compareTo(IAction o) {
		return getType() - o.getType();
	}

	private int id;
	private PrintWriter writer;
}
