package environment;

public enum Direction {
	UP(0, -1), DOWN(0, 1), LEFT(-1, 0), RIGHT(1, 0);

	private Coordinate vector;

	Direction(int x, int y) {
		vector = new Coordinate(x, y);
	}

	public Coordinate getVector() {
		return vector;
	}

	public static void main(String[] args) {
		Direction test = UP;
		String dir = test.toString();
		System.out.println(Direction.valueOf(dir));
	}
}
