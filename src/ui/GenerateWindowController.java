package ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;

import basis.DirectedGraph;
import basis.GraphProcessor;
import basis.Vertex;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class GenerateWindowController {
	@FXML private TextField textField;
	@FXML private TextArea textArea;
	@FXML private Button button;
	
	private DirectedGraph graph;
	
	void initGraph(DirectedGraph graph) {
		this.graph = graph;
	}
	
	@FXML 
	protected void handleGenerateButtonClicked(MouseEvent e) {
		String inputText = textField.getText();
		if (inputText.equals("")) {
			textArea.setText("请输入新文本！");
			return;
		}
		String newText = generateNewText(inputText);
		textArea.setText(newText);
	}
	
	private String generateNewText(String inputText) {
		String pre, post;
		Scanner scan = new Scanner(inputText);
		StringBuffer sb = new StringBuffer(inputText);
		HashSet<Vertex> bridgeWords = new HashSet<>();
		int fromIndex = 0;
		do {
			pre = GraphProcessor.parseText(scan.next());
		} while (pre == null && scan.hasNext());
		while (scan.hasNext()) {
			post = GraphProcessor.parseText(scan.next());
			if (post != null) {
				bridgeWords = queryBridgeWords(pre, post);
				String insertWord = randomSelect(bridgeWords).name;
				int insertIndex = sb.indexOf(post, fromIndex);
				if (insertWord != null) {
					sb.insert(insertIndex, insertWord + " ");
					fromIndex = insertIndex + insertWord.length() + 1;
				}
				pre = post;
			}
		}
		scan.close();
		return sb.toString();
	}
	
	private HashSet<Vertex> queryBridgeWords(String word1, String word2) {
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
			return null;
		}
		HashSet<Vertex> successors = a.successors;
		HashSet<Vertex> predecessors = b.predecessors;
		HashSet<Vertex> intersection = new HashSet<>();
		intersection.addAll(successors);
		intersection.retainAll(predecessors);
		return (intersection.size() == 0)? null : intersection;
	}
	
	private Vertex randomSelect(HashSet<Vertex> set) {
		if (set == null || set.size() == 0) {
			return null;
		}
		int item = new Random().nextInt(set.size());
		int i = 0;
		for (Vertex v : set) {
			if (i == item) {
				return v;
			}
			++i;
		}
		return null;
	}
}
