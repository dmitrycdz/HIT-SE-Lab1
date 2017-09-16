package basis;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;
import static java.lang.System.out;

public class GraphProcessor {
	public static DirectedGraph generateGraph(String fileName) {
		Scanner in;
		String pre, post;
		DirectedGraph graph = new DirectedGraph();
		try {
			in = new Scanner(new FileInputStream(fileName));
			pre = parseText(in.next());
			if (pre != null) {
				graph.addVertex(pre);
			}
			while (in.hasNext()) {
				post = parseText(in.next());
				if (post != null ) {
					graph.addVertex(post);
					graph.addEdge(pre, post);
					pre = post;
				}
			}
		} catch (FileNotFoundException e) {
			System.exit(0);
		}
		return graph;
	}
	
	public static String parseText(String str) {
		StringBuffer sb = new StringBuffer();
		if (str != null) {
			for (int i = 0; i < str.length(); ++i) {
				char c = str.charAt(i);
				if (Character.isLetter(c)) {
					sb.append(Character.toLowerCase(c));
				}
			}
		}
		return (sb.toString().equals("")) ? null : sb.toString();
	}
	
	public static void main(String[] args) {
		String fileName;
		Scanner in = new Scanner(System.in);
		out.print("请输入文件名：");
		fileName= in.next();
		DirectedGraph graph = generateGraph(fileName);
		out.println(graph.getVertexNumber());
		out.println(graph.getEdgeNumber());
		out.println(graph.getVertices());
		out.println(graph);
		in.close();
	}
}
