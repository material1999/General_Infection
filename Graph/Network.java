package Graph;

import java.util.*;

public class Network {
	
	private Map<String, Node> nodemap;
	private Set<Node> nodes;
	private ArrayList<Edge> edges;
	
	public Network() {
		this.nodemap=new HashMap<String,Node>();
		this.nodes=new LinkedHashSet<Node>();
		this.edges=new ArrayList<Edge>();
	}
	
	public void addEdge(Edge a) {
		Node in=null;
		Node out=null;
	
		
		if(nodes.contains(a.getOut())) {	
			out=(Node)nodemap.get(a.getOut().getId());
		}else {
			nodes.add(a.getOut());
			out=a.getOut();
		}
		
		if(nodes.contains(a.getIn())) {
			in=(Node)nodemap.get(a.getIn().getId());
		}else {
			nodes.add(a.getIn());
			in=a.getIn();
		}
		a.setIn(in);
		a.setOut(out);
		//UNDIRECTED
		out.addNeighbour(in);
		in.addNeighbour(out);
		
		//DIRECTED
		out.addOutNeighbour(in);
		out.addOutEdge(a);
		in.addinEdge(a);
		
		edges.add(a);
		nodemap.put(in.getId(), in);
		nodemap.put(out.getId(),out);
	}

	public Map<String, Node> getNodemap() {
		return nodemap;
	}

	public void setNodemap(Map<String, Node> nodemap) {
		this.nodemap = nodemap;
	}

	public Set<Node> getNodes() {
		return nodes;
	}

	public void setNodes(Set<Node> nodes) {
		this.nodes = nodes;
	}

	public ArrayList<Edge> getEdges() {
		return edges;
	}

	public void setEdges(ArrayList<Edge> edges) {
		this.edges = edges;
	}
	
	public void notVisited() {
		for(Node n: nodes) {
			n.setState(States.NOTVISITED);
		}
	}
	
	public void deleteFv() {
		for(Node n: nodes) {
			n.setFv(0);
		}
	}
	
	public double getFvSum() {
		double sum=0.0;
		for(Node n: nodes) {
			sum+=n.getFv();
		}
		return sum;
	}


	public Node getNode(String n) {
		nodemap = this.getNodemap();
		Node node = nodemap.get(n);
		return node;
	}
	

}
