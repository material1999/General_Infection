package Graph;

public class Edge{
	
	private Node out;
	private Node in;
	private double weight;
	private boolean alive;
	
	public Edge(Node out, Node in, double weight) {
		this.out = out;
		this.in = in;
		this.weight = weight;
		this.alive=true;
		
	}
	
	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	public Node getOut() {
		return out;
	}

	public void setOut(Node out) {
		this.out = out;
	}

	public Node getIn() {
		return in;
	}

	public void setIn(Node in) {
		this.in = in;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	@Override
	public String toString() {
		return "Edge [out=" + out + ", in=" + in + ", weight=" + weight + ", alive=" + alive + "]";
	}
	
}
