package basis;

import java.util.ArrayList;

public class DirectedGraph {
	private ArrayList<Vertex> vertices;
	
	public DirectedGraph() {
		this.vertices = new ArrayList<>();
	}
	
	public ArrayList<Vertex> getVertices() {
		return this.vertices;
	}
	
	public int getVertexNumber() {
		return this.vertices.size();
	}
	
	public int getEdgeNumber() {
		int number = 0;
		for (Vertex v : vertices) {
			number += v.successors.size();
		}
		return number;
	}
	
	public void addVertex(String name) {
		for (Vertex v : vertices) {
			if (v.name.equals(name)) {
				return;
			}
		}
		vertices.add(new Vertex(name));
	}
	
	public void addEdge(String head, String tail) {
		Vertex a = new Vertex();
		Vertex b = new Vertex();
        for (Vertex v : vertices) {
            if (v.name.equals(head)) {
            	a = v;
            }
            if (v.name.equals(tail)) {
            	b = v;
            }
        }
        if (a.successors.contains(b)) {
        	int weight = a.weights.get(b);
        	a.weights.replace(b, weight + 1);
        } else {
        	a.successors.add(b);
        	b.predecessors.add(a);
        	a.weights.put(b, 1);
        }
	}
	
	@Override
	public String toString() {
		String result = "";
		for (Vertex v : vertices) {
			result += v.toString() + ": " + v.successors.toString() + "\n";
		}
		return result;
	}
	
	@Override
	public int hashCode() {
		int code = 0;
		for (Vertex v : vertices) {
			code += v.hashCode();
		}
		return code;
	}
}
