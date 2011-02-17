package project2;

import java.io.PrintWriter;

public class Write implements IAction {
	public Write(int id, int value) {
		this.id = id;
		this.value = value;
	}	
	
	@Override
	public int getType() {
		return 1;
	}

	public int getValue() {
		return value;
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
	
	public int reply(int code) {
		writer.println(code);
		return code;
	}

	@Override
	public int compareTo(IAction o) {
		return getType() - o.getType();
	}
	
	private int value;
	private int id;
	private PrintWriter writer;
}
