package ui;

import java.util.ArrayList;
import java.util.HashSet;

import basis.DirectedGraph;
import basis.Vertex;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class QueryWindowController {
	@FXML private TextField word1TextField;
	@FXML private TextField word2TextField;
	@FXML private TextArea textArea;
	@FXML private Button queryButton;
	
	private DirectedGraph graph;
	
	void initGraph(DirectedGraph graph) {
		this.graph = graph;
	}
	
	@FXML
	protected void handleQueryButtonClicked(MouseEvent e) {
		System.out.println(graph);
		String word1 = word1TextField.getText();
		String word2 = word2TextField.getText();
		if (word1 == "" && word2 != "") {
			textArea.setText("请输入单词1！");
			return;
		} else if (word1 != "" && word2 == "") {
			textArea.setText("请输入单词2！");
			return;
		} else if (word1 == "" && word2 == "") {
			textArea.setText("请输入单词1和单词2！");
			return;
		} else {
			String output = queryBridgeWords(word1, word2);
			System.out.println(output);
			System.out.println("*****");
			System.out.println(graph);
			textArea.setText(output);
		}
	}
	
	private String queryBridgeWords(String word1, String word2) {
		ArrayList<Vertex> vertices = this.graph.getVertices();
		Vertex a = null;
		Vertex b = null;
		for (Vertex v : vertices) {
			if (v.name.equals(word1)) {
				a = v;
			}
			if (v.name.equals(word2)) {
				b = v;
			}
		}
		if (a == null || b == null) {
			return "No word1 or word2 in the graph!";
		}
		HashSet<Vertex> successors = a.successors;
		HashSet<Vertex> predecessors = b.predecessors;
		HashSet<Vertex> intersection = new HashSet<>();
		intersection.addAll(successors);
		intersection.retainAll(predecessors);
		if (intersection.size() == 0) {
			return "No bridge words from word1 to word2!";
		}
		String wordsName = intersection.toString();
		return "The bridge words from word1 to word2 are: " + wordsName.substring(1, wordsName.length() - 1) + ".";
	}
}
