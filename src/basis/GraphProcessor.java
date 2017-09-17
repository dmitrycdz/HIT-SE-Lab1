package basis;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class GraphProcessor {
	public static DirectedGraph generateGraph(String fileName) {
		Scanner in;
		String pre, post;
		DirectedGraph graph = new DirectedGraph();
		try {
			in = new Scanner(new FileInputStream(fileName));
			do {
				pre = parseText(in.next());				
			} while (pre == null && in.hasNext());
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
}
