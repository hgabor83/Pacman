import java.util.*;
import java.util.stream.Collectors;
import java.io.*;
import java.math.*;

/**
 * Grab the pellets as fast as you can!
 **/

class Cell {
	private int id;
	private int x;
	private int y;
	private char value;
	private boolean walkable;
	private int f, g, h;
	private int parentX, parentY;

	public Cell(int id, int x, int y, char value) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.value = value;
		this.walkable = true;
		this.f = 10000;
		this.g = 10000;
		this.h = 10000;
		this.parentX = 0;
		this.parentY = 0;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public boolean isWalkable() {
		return walkable;
	}

	public void setWalkable(boolean walkable) {
		this.walkable = walkable;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public char getValue() {
		return value;
	}

	public void setValue(char value) {
		this.value = value;
	}

	public int getF() {
		return f;
	}

	public void setF(int f) {
		this.f = f;
	}

	public int getG() {
		return g;
	}

	public void setG(int g) {
		this.g = g;
	}

	public int getH() {
		return h;
	}

	public void setH(int h) {
		this.h = h;
	}

	public int getParentX() {
		return parentX;
	}

	public void setParentX(int parentX) {
		this.parentX = parentX;
	}

	public int getParentY() {
		return parentY;
	}

	public void setParentY(int parentY) {
		this.parentY = parentY;
	}

}

class Pac {
	private int id;
	private boolean mine;
	private int x;
	private int y;
	private String typeId;
	private int speedTurnsLeft;
	private int abilityCooldown;
	private char dir;

	public Pac(int id, boolean mine, int x, int y, String typeId, int speedTurnsLeft, int abilityCooldown) {
		this.id = id;
		this.mine = mine;
		this.x = x;
		this.y = y;
		this.typeId = typeId;
		this.speedTurnsLeft = speedTurnsLeft;
		this.abilityCooldown = abilityCooldown;
		this.dir = '?';
	}

	public int getId() {
		return id;
	}

	public boolean isMine() {
		return mine;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getSpeedTurnsLeft() {
		return speedTurnsLeft;
	}

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public int getAbilityCooldown() {
		return abilityCooldown;
	}

	public char getDir() {
		return dir;
	}

	public void setDir(char dir) {
		this.dir = dir;
	}

	public String getNearestPellet(List<Pellet> pelletList, List<Pellet> pelletMemoryList, char[][] cellValues,
			char myDir, char chosenDir) {
		int[] nearestPellet = new int[2];
		int[] firstStepToNearestPellet = new int[2];
		int targetId = -1;
		int[] nextPellet = new int[2];
		// System.err.println("");
		System.err.println("Pellet search for actual Pac: " + chosenDir);
		int distance = 0;
		int minDistance = 1000;
		List<Integer> pathToPellet = new ArrayList<Integer>();
		double heuristic = 0;
		double minHeuristic = 1000;
		boolean foundNextPellet = false;
		int targetValue = 1;
		int pelletValue = 1;
		int searchLimitN = 8;
		int searchLimitM = 100;
		int pValue = 10;

		int[] src = new int[] { this.getX(), this.getY() };
		int[] dest = new int[] { 0, 0 };
		boolean skip = false;

		/*
		 * List<Pellet> pelletSumList = new ArrayList<Pellet>();
		 * pelletSumList.addAll(pelletList); for (Pellet pm : pelletMemoryList) { skip =
		 * false; for (Pellet ps : pelletSumList) { if (ps.getX() == pm.getX() &&
		 * ps.getY() == pm.getY()) { skip = true; break; } } if (!skip)
		 * pelletSumList.add(pm); }
		 */
		// pelletSumList.addAll(pelletMemoryList);

		// Long startTime = System.currentTimeMillis();
		// Long actualTime = 0L;
		// Long consumedTime = 0L;
		// if (pelletSumList.size() < 15)
		// searchLimitN = 100; // complexity
		// else
		// searchLimitN = 8;
		// for (Pellet p : pelletSumList) {
		for (Pellet p : pelletList) {
			if (p.getValue() == 10 && this.getId() != p.getNearestPacId())
				continue;
			// actualTime = System.currentTimeMillis();
			// consumedTime = actualTime - startTime;
			// if (consumedTime > 20)
			// break;
			dest[0] = p.getX();
			dest[1] = p.getY();
			pelletValue = p.getValue();
			if (pelletValue == 10 || pelletList.size() < 10)
				searchLimitN = 100; // find path to superpellets at any cost
			else
				searchLimitN = 10;
			if (!p.isTargeted() && (Math.abs(src[0] - dest[0]) + Math.abs(src[1] - dest[1])) < searchLimitN) {
				// System.err.println("Actual pellet: " + dest[0] + " " + dest[1]);
				pathToPellet = Player.aStarSearch(cellValues, src, dest);
				if (pathToPellet != null) {
					distance = pathToPellet.size();
					Pac pf = new Pac(1000, true, 0, 0, "FAKE", 0, 0);
					pf.calcDir(src[0], src[1], dest[0], dest[1]);
					if (p.getValue() == 10)
						pValue = 20;
					else
						pValue = 1;
					if (pf.getDir() == chosenDir)
						heuristic = (distance * 1.0 / pValue) * 0.99;
					else
						heuristic = distance * 1.0 / pValue;
					// System.err.println("H value for pellet: " + p.getX() + " " + p.getY() + " " +
					// heuristic);
					if (heuristic < minHeuristic) {
						targetValue = pelletValue;
						minHeuristic = heuristic;
						minDistance = distance;
						nearestPellet[0] = p.getX();
						nearestPellet[1] = p.getY();
						firstStepToNearestPellet[0] = Player
								.getCell(pathToPellet.get(0), cellValues.length, cellValues[0].length).getX();
						firstStepToNearestPellet[1] = Player
								.getCell(pathToPellet.get(0), cellValues.length, cellValues[0].length).getY();
						targetId = p.getId();
					}
				}
			}
		}

		// System.err.println("After normal search, nearest pellet is: " +
		// nearestPellet[0] + " " + nearestPellet[1]);

		// not superpellet
		if (targetValue != 10) {
			Pac pf = new Pac(1000, true, 0, 0, "FAKE", 0, 0);
			pf.calcDir(src[0], src[1], nearestPellet[0], nearestPellet[1]);
			chosenDir = pf.getDir();

			do {
				foundNextPellet = false;
				switch (chosenDir) {
				case 'N':
					nextPellet[0] = nearestPellet[0];
					nextPellet[1] = nearestPellet[1] - 1;
					break;

				case 'S':
					nextPellet[0] = nearestPellet[0];
					nextPellet[1] = nearestPellet[1] + 1;
					break;

				case 'W':
					nextPellet[0] = nearestPellet[0] - 1;
					nextPellet[1] = nearestPellet[1];
					break;

				case 'E':
					nextPellet[0] = nearestPellet[0] + 1;
					nextPellet[1] = nearestPellet[1];
					break;

				default:
					break;
				}

				// System.err.println("Pellet in investigation: " + nextPellet[0] + " " +
				// nextPellet[1]);
				for (Pellet p : pelletList) {
					if (p.getX() == nextPellet[0] && p.getY() == nextPellet[1]) {
						// System.err.println("Found Pellet in investigation: " + nextPellet[0] + " " +
						// nextPellet[1]);
						foundNextPellet = true;
						nearestPellet[0] = nextPellet[0];
						nearestPellet[1] = nextPellet[1];
						targetId = p.getId();
						minDistance++;
						break;
					}
				}

			} while (foundNextPellet);
		}

		for (Pellet p : pelletList) {
			if (p.getId() == targetId) {
				p.setTargeted(true);
			}
		}

		return nearestPellet[0] + ";" + nearestPellet[1] + ";" + firstStepToNearestPellet[0] + ";"
				+ firstStepToNearestPellet[1] + ";" + minDistance + ";" + targetValue;

	}

	public String getNearestEnemy(List<Pac> enemyPacList, char[][] cellValues) {
		String toChange = "";
		int distance = 0, minDistance = 100;
		String enemyType = "";
		int enemyId = 0;
		int enemyX = 0, enemyY = 0;
		List<Integer> pathToEnemy = new ArrayList<Integer>();
		double heuristic = 0;
		double minHeuristic = 1000;

		int[] src = new int[] { this.getX(), this.getY() };
		int[] dest = new int[] { 0, 0 };

		for (Pac p : enemyPacList) {
			// System.err.println("Actual investigated enemy: " + p.getId());
			dest[0] = p.getX();
			dest[1] = p.getY();
			pathToEnemy = Player.aStarSearch(cellValues, src, dest);
			if (pathToEnemy != null) {
				// System.err.println("Have path to enemy: " + p.getId());
				distance = pathToEnemy.size();
				if (distance < minDistance) {
					// System.err.println(
					// "Current nearest enemy: " + p.getId() + " " + p.getX() + " " + p.getY() + " "
					// + distance);
					minDistance = distance;
					enemyType = p.getTypeId();
					enemyId = p.getId();
					enemyX = p.getX();
					enemyY = p.getY();
				}
			}
		}

		if (minDistance <= 5 && minDistance > 0) {
			System.err.println("My pac is in danger!");
			System.err.println("E type, id, x, y, dist: " + enemyType + " " + enemyId + " " + enemyX + " " + enemyY
					+ " " + minDistance);
			switch (enemyType) {
			case "ROCK":
				toChange = "PAPER";
				break;

			case "PAPER":
				toChange = "SCISSORS";
				break;

			case "SCISSORS":
				toChange = "ROCK";
				break;

			default:
				break;
			}
		}

		return toChange + ";" + enemyX + ";" + enemyY + ";" + minDistance + ";" + enemyId;
	}

	public void calcDir(int prevX, int prevY, int x, int y) {
		if (prevX == x) {
			if (prevY < y)
				this.setDir('S');
			else if (prevY > y)
				this.setDir('N');
			else
				this.setDir(' ');
		} else if (prevY == y) {
			if (prevX < x)
				this.setDir('E');
			else if (prevX > x)
				this.setDir('W');
			else
				this.setDir(' ');
		}
	}

	public char getChosenDir(int height, int width, char[][] cellValues) {
		int[] nextCell = new int[2];
		int[] northLast = new int[2];
		int[] southLast = new int[2];
		int[] westLast = new int[2];
		int[] eastLast = new int[2];

		int myX = this.getX();
		int myY = this.getY();
		int north = 0, south = 0, west = 0, east = 0;
		// =========counting
		nextCell[0] = myX;
		nextCell[1] = myY;
		north = 0;
		do {
			nextCell[1] = nextCell[1] - 1;
			if (Player.isValid(nextCell[0], nextCell[1], height, width)
					&& cellValues[nextCell[1]][nextCell[0]] == 'o') {
				north++;
			}
		} while (Player.isValid(nextCell[0], nextCell[1], height, width)
				&& cellValues[nextCell[1]][nextCell[0]] != '#');

		nextCell[0] = myX;
		nextCell[1] = myY;
		south = 0;
		do {
			nextCell[1] = nextCell[1] + 1;
			if (Player.isValid(nextCell[0], nextCell[1], height, width)
					&& cellValues[nextCell[1]][nextCell[0]] == 'o') {
				south++;
			}
		} while (Player.isValid(nextCell[0], nextCell[1], height, width)
				&& cellValues[nextCell[1]][nextCell[0]] != '#');

		nextCell[0] = myX;
		nextCell[1] = myY;
		west = 0;
		do {
			nextCell[0] = nextCell[0] - 1;
			if (Player.isValid(nextCell[0], nextCell[1], height, width)
					&& cellValues[nextCell[1]][nextCell[0]] == 'o') {
				west++;
			}
		} while (Player.isValid(nextCell[0], nextCell[1], height, width)
				&& cellValues[nextCell[1]][nextCell[0]] != '#');

		nextCell[0] = myX;
		nextCell[1] = myY;
		east = 0;
		do {
			nextCell[0] = nextCell[0] + 1;
			if (Player.isValid(nextCell[0], nextCell[1], height, width)
					&& cellValues[nextCell[1]][nextCell[0]] == 'o') {
				east++;
			}
		} while (Player.isValid(nextCell[0], nextCell[1], height, width)
				&& cellValues[nextCell[1]][nextCell[0]] != '#');

		System.err.println("ESWN: " + east + " " + south + " " + west + " " + north);
		// search for the best move
		char chosenDir = 'E';
		int max = east;

		if (south > max) {
			max = south;
			chosenDir = 'S';
		}
		if (west > max) {
			max = west;
			chosenDir = 'W';
		}
		if (north > max) {
			max = north;
			chosenDir = 'N';
		}

		switch (chosenDir) {
		case 'E':
			nextCell[0] = eastLast[0];
			nextCell[1] = eastLast[1];
			break;

		case 'S':
			nextCell[0] = southLast[0];
			nextCell[1] = southLast[1];
			break;

		case 'W':
			nextCell[0] = westLast[0];
			nextCell[1] = westLast[1];
			break;

		case 'N':
			nextCell[0] = northLast[0];
			nextCell[1] = northLast[1];
			break;

		default:
			break;
		}

		return chosenDir;
	}
}

class Pellet {
	private int id;
	private int x;
	private int y;
	private int value;
	private boolean targeted;
	private int nearestPacId;

	public Pellet(int id, int x, int y, int value) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.value = value;
		this.targeted = false;
		this.nearestPacId = 0;
	}

	public int getNearestPacId() {
		return nearestPacId;
	}

	public void setNearestPacId(int pacId) {
		this.nearestPacId = pacId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getValue() {
		return value;
	}

	public boolean isTargeted() {
		return targeted;
	}

	public void setTargeted(boolean targeted) {
		this.targeted = targeted;
	}

}

class Player {

	public static Cell[][] cellDetails = new Cell[17][35];

	// A Utility Function to check whether the given cell is
	// blocked or not
	public static boolean isUnBlocked(char[][] cellValues, int x, int y) {
		// Returns true if the cell is not blocked else false
		if (cellValues[y][x] == 'o' || cellValues[y][x] == ' ' || cellValues[y][x] == 'N')
			return (true);
		else
			return (false);
	}

	// A Utility Function to check whether given cell (x, y)
	// is a valid cell or not.
	public static boolean isValid(int x, int y, int height, int width) {
		// Returns true if row number and column number
		// is in range
		// System.err.println("Is valid?: " + x + " " + y);
		return (x >= 0) && (x < width) && (y >= 0) && (y < height);
	}

	// calc enemy direction from our point of view
	public static String enemyDirFromUs(int x, int y, int enemyX, int enemyY) {
		String dir;
		if (x == enemyX) {
			if (enemyY < y)
				dir = "N";
			else
				dir = "S";
		} else if (y == enemyY) {
			if (enemyX < x)
				dir = "W";
			else
				dir = "E";
		} else if (enemyY < y && enemyX < x)
			dir = "NW";
		else if (enemyY < y && enemyX > x)
			dir = "NE";
		else if (enemyY > y && enemyX < x)
			dir = "SW";
		else if (enemyY > y && enemyX > x)
			dir = "SE";
		else
			dir = "";
		return dir;

	}

	// A Utility Function to check whether destination cell has
	// been reached or not
	public static boolean isDestination(int x, int y, int[] dest) {
		if (x == dest[0] && y == dest[1])
			return (true);
		else
			return (false);
	}

	// A Utility Function to trace the path from the source
	// to destination
	public static List<Integer> tracePath(int[] src, int[] dest) {
		// "The Path is"
		int xs = src[0];
		int ys = src[1];
		int xd = dest[0];
		int yd = dest[1];
		int x = xd;
		int y = yd;
		int xa = 0;
		int ya = 0;

		List<Integer> path = new ArrayList<Integer>();
		do {
			xa = 0;
			ya = 0;
			path.add(cellDetails[y][x].getId());
			xa = cellDetails[y][x].getParentX();
			ya = cellDetails[y][x].getParentY();
			x = xa;
			y = ya;
		} while (!(x == xs && y == ys));

		Collections.reverse(path);

		return path;
	}

	// get a cell with id
	public static Cell getCell(int id, int height, int width) {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (cellDetails[y][x].getId() == id)
					return cellDetails[y][x];
			}
		}
		return null;
	}

	// A Utility Function to calculate the 'h' heuristics.
	public static int calculateHValue(int x, int y, int[] dest) {
		// Return using the distance formula
		return Math.abs(x - dest[0]) + Math.abs(y - dest[1]);
	}

	// A Function to find the shortest path between
	// a given source cell to a destination cell according
	// to A* Search Algorithm
	public static List<Integer> aStarSearch(char[][] cellValues, int[] src, int[] dest) {

		// final path from src to dest
		List<Integer> path = new ArrayList<Integer>();

		// Create a closed list and initialise it to false which means
		// that no cell has been included yet
		List<Integer> closedList = new ArrayList<Integer>();

		// Declare a 2D array of structure to hold the details
		// of that cell

		int x, y;
		int width = cellValues[0].length;
		int height = cellValues.length;
		int id = -1;

		for (y = 0; y < height; y++) {
			for (x = 0; x < width; x++) {
				id++;
				cellDetails[y][x] = new Cell(id, x, y, cellValues[y][x]);
			}
		}

		// Initialising the parameters of the starting node
		x = src[0];
		y = src[1];
		cellDetails[y][x].setParentX(x);
		cellDetails[y][x].setParentY(y);

		int srcId = cellDetails[y][x].getId();
		// System.err.println("=========== A* ==========");
		// System.err.println("Source details: " + x + " " + y + " " + srcId);

		/*
		 * Create an open list, where f = g + h, and x, y are the row and column index
		 * of that cell
		 */
		List<Integer> openList = new ArrayList<Integer>();

		// Put the starting cell on the open list and set its
		// 'f' as 0
		openList.add(srcId);

		// We set this boolean value as false as initially
		// the destination is not reached.
		boolean foundDest = false;
		int currentId = -1;
		// To store the 'g', 'h' and 'f' of the 4 successors
		int gNew, hNew, fNew;

		while (openList.size() != 0) {
			// Remove this cell from the open list
			currentId = openList.get(0);
			openList.remove(0);

			x = getCell(currentId, cellValues.length, cellValues[0].length).getX();
			y = getCell(currentId, cellValues.length, cellValues[0].length).getY();

			// Add this cell to the closed list
			closedList.add(currentId);

			/*
			 * Generating all the 4 successor of this cell Cell-->Popped Cell (i, j) N -->
			 * North (i-1, j) S --> South (i+1, j) E --> East (i, j+1) W --> West (i, j-1)
			 */

			// ----------- 1st Successor (North) ------------

			// Only process this cell if this is a valid one
			if (isValid(x, y - 1, height, width) == true) {
				// System.err.println("Valid!" + x + " " + (y - 1));
				// If the destination cell is the same as the
				// current successor
				if (isDestination(x, y - 1, dest) == true) {
					// System.err.println("Destination found!");
					// Set the Parent of the destination cell
					cellDetails[y - 1][x].setParentX(x);
					cellDetails[y - 1][x].setParentY(y);
					// "The destination cell is found";
					path = tracePath(src, dest);
					foundDest = true;
					return path;
				}
				// If the successor is already on the closed
				// list or if it is blocked, then ignore it.
				// Else do the following
				else if (closedList.lastIndexOf(cellDetails[y - 1][x].getId()) == -1
						&& isUnBlocked(cellValues, x, y - 1) == true) {
					gNew = cellDetails[y][x].getG() + 1;
					hNew = calculateHValue(x, y - 1, dest);
					fNew = gNew + hNew;

					// System.err.println("Adding successor");

					// If it isn’t on the open list, add it to
					// the open list. Make the current cell
					// the parent of this cell. Record the
					// f, g, and h costs of the square cell
					// OR
					// If it is on the open list already, check
					// to see if this path to that cell is better,
					// using 'f' cost as the measure.
					// System.err.println("F value: " + cellDetails[y - 1][x].getF());
					if (cellDetails[y - 1][x].getF() == 10000 || cellDetails[y - 1][x].getF() > fNew) {
						openList.add(cellDetails[y - 1][x].getId());
						// System.err.println("Adding successor: " + x + " " + (y - 1));
						// Update the details of this cell
						cellDetails[y - 1][x].setF(fNew);
						cellDetails[y - 1][x].setG(gNew);
						cellDetails[y - 1][x].setH(hNew);
						cellDetails[y - 1][x].setParentX(x);
						cellDetails[y - 1][x].setParentY(y);
					}
				}
			}

			// ----------- 2nd Successor (South) ------------

			// Only process this cell if this is a valid one
			if (isValid(x, y + 1, height, width) == true) {
				// System.err.println("Valid!" + x + " " + (y + 1));
				// If the destination cell is the same as the
				// current successor
				if (isDestination(x, y + 1, dest) == true) {
					// System.err.println("Destination found!");
					// Set the Parent of the destination cell
					cellDetails[y + 1][x].setParentX(x);
					cellDetails[y + 1][x].setParentY(y);
					// "The destination cell is found";
					path = tracePath(src, dest);
					foundDest = true;
					return path;
				}
				// If the successor is already on the closed
				// list or if it is blocked, then ignore it.
				// Else do the following
				else if (closedList.lastIndexOf(cellDetails[y + 1][x].getId()) == -1
						&& isUnBlocked(cellValues, x, y + 1) == true) {
					gNew = cellDetails[y][x].getG() + 1;
					hNew = calculateHValue(x, y + 1, dest);
					fNew = gNew + hNew;

					// System.err.println("Adding successor");

					// If it isn’t on the open list, add it to
					// the open list. Make the current cell
					// the parent of this cell. Record the
					// f, g, and h costs of the square cell
					// OR
					// If it is on the open list already, check
					// to see if this path to that cell is better,
					// using 'f' cost as the measure.
					if (cellDetails[y + 1][x].getF() == 10000 || cellDetails[y + 1][x].getF() > fNew) {
						openList.add(cellDetails[y + 1][x].getId());

						// Update the details of this cell
						cellDetails[y + 1][x].setF(fNew);
						cellDetails[y + 1][x].setG(gNew);
						cellDetails[y + 1][x].setH(hNew);
						cellDetails[y + 1][x].setParentX(x);
						cellDetails[y + 1][x].setParentY(y);
					}
				}
			}

			// ----------- 3rd Successor (East) ------------

			// Only process this cell if this is a valid one
			if (isValid(x + 1, y, height, width) == true) {
				// System.err.println("Valid!" + (x + 1) + " " + y);
				// If the destination cell is the same as the
				// current successor
				if (isDestination(x + 1, y, dest) == true) {
					// System.err.println("Destination found!");
					// Set the Parent of the destination cell
					cellDetails[y][x + 1].setParentX(x);
					cellDetails[y][x + 1].setParentY(y);
					// "The destination cell is found";
					path = tracePath(src, dest);
					foundDest = true;
					return path;
				}
				// If the successor is already on the closed
				// list or if it is blocked, then ignore it.
				// Else do the following
				else if (closedList.lastIndexOf(cellDetails[y][x + 1].getId()) == -1
						&& isUnBlocked(cellValues, x + 1, y) == true) {
					gNew = cellDetails[y][x].getG() + 1;
					hNew = calculateHValue(x + 1, y, dest);
					fNew = gNew + hNew;

					// System.err.println("Adding successor");

					// If it isn’t on the open list, add it to
					// the open list. Make the current cell
					// the parent of this cell. Record the
					// f, g, and h costs of the square cell
					// OR
					// If it is on the open list already, check
					// to see if this path to that cell is better,
					// using 'f' cost as the measure.
					if (cellDetails[y][x + 1].getF() == 10000 || cellDetails[y][x + 1].getF() > fNew) {
						openList.add(cellDetails[y][x + 1].getId());

						// Update the details of this cell
						cellDetails[y][x + 1].setF(fNew);
						cellDetails[y][x + 1].setG(gNew);
						cellDetails[y][x + 1].setH(hNew);
						cellDetails[y][x + 1].setParentX(x);
						cellDetails[y][x + 1].setParentY(y);
					}
				}
			}

			// ----------- 3rd Successor (West) ------------

			// Only process this cell if this is a valid one
			if (isValid(x - 1, y, height, width) == true) {
				// System.err.println("Valid!" + (x - 1) + " " + y);
				// If the destination cell is the same as the
				// current successor
				if (isDestination(x - 1, y, dest) == true) {
					// System.err.println("Destination found!");
					// Set the Parent of the destination cell
					cellDetails[y][x - 1].setParentX(x);
					cellDetails[y][x - 1].setParentY(y);
					// "The destination cell is found";
					path = tracePath(src, dest);
					foundDest = true;
					return path;
				}
				// If the successor is already on the closed
				// list or if it is blocked, then ignore it.
				// Else do the following
				else if (closedList.lastIndexOf(cellDetails[y][x - 1].getId()) == -1
						&& isUnBlocked(cellValues, x - 1, y) == true) {
					gNew = cellDetails[y][x].getG() + 1;
					hNew = calculateHValue(x - 1, y, dest);
					fNew = gNew + hNew;

					// System.err.println("Adding successor");

					// If it isn’t on the open list, add it to
					// the open list. Make the current cell
					// the parent of this cell. Record the
					// f, g, and h costs of the square cell
					// OR
					// If it is on the open list already, check
					// to see if this path to that cell is better,
					// using 'f' cost as the measure.
					if (cellDetails[y][x - 1].getF() == 10000 || cellDetails[y][x - 1].getF() > fNew) {
						openList.add(cellDetails[y][x - 1].getId());

						// Update the details of this cell
						cellDetails[y][x - 1].setF(fNew);
						cellDetails[y][x - 1].setG(gNew);
						cellDetails[y][x - 1].setH(hNew);
						cellDetails[y][x - 1].setParentX(x);
						cellDetails[y][x - 1].setParentY(y);
					}
				}
			}
		}

		// When the destination cell is not found and the open
		// list is empty, then we conclude that we failed to
		// reach the destination cell. This may happen when the
		// there is no way to destination cell (due to blockages)
		// if (foundDest == false)
		// System.err.println("Failed to find the Destination Cell!");

		return null;
	}

	public static int[] findAValidRandomCell(char[][] cellValues) {
		Random r = new Random();
		int height = cellValues.length;
		int width = cellValues[0].length;
		int[] randomValidCell = new int[] { 0, 0 };
		do {
			randomValidCell[0] = r.nextInt(width);
			randomValidCell[1] = r.nextInt(height);
		} while (!isValid(randomValidCell[0], randomValidCell[1], height, width)
				|| !isUnBlocked(cellValues, randomValidCell[0], randomValidCell[1]));
		return randomValidCell;
	}

	public static int[] findANearestNotKnownCell(char[][] cellValues, int fromX, int fromY,
			List<Pellet> pelletMemoryList, List<Pellet> NKPelletList) {
		int height = cellValues.length;
		int width = cellValues[0].length;
		int[] nearestNKValidCell = new int[] { 0, 0 };
		int searchLimitN = 10;
		int distance = 0;
		int minDistance = 100;
		int src[] = new int[2];
		src[0] = fromX;
		src[1] = fromY;
		int dest[] = new int[2];
		List<Integer> pathToCell = new ArrayList<Integer>();

		int x = 0, y = 0;
		List<Pellet> pelletSumList = new ArrayList<Pellet>();
		pelletSumList.addAll(pelletMemoryList);
		pelletSumList.addAll(NKPelletList);
		int targetId = -1;

		for (Pellet p : pelletSumList) {
			// dest[0] = x;
			// dest[1] = y;
			// if (pelletSumList.size() < 10)
			// searchLimitN = 100;
			// else
			// searchLimitN = 10;
			// if (!p.isTargeted() && (Math.abs(src[0] - dest[0]) + Math.abs(src[1] -
			// dest[1])) < searchLimitN) {
			// pathToCell = Player.aStarSearch(cellValues, src, dest);
			// if (pathToCell != null) {
			// distance = pathToCell.size();

			x = p.getX();
			y = p.getY();
			distance = Math.abs(fromX - x) + Math.abs(fromY - y);

			// dont go where we stands
			if (!p.isTargeted() && distance < minDistance && distance != 0) {
				minDistance = distance;
				nearestNKValidCell[0] = x;
				nearestNKValidCell[1] = y;
				targetId = p.getId();
			}
		}
		// }
		// }
		// }

		for (

		Pellet p : pelletSumList) {
			if (p.getId() == targetId) {
				p.setTargeted(true);
			}
		}

		System.err.println("NKC: " + nearestNKValidCell[0] + " " + nearestNKValidCell[1]);
		return nearestNKValidCell;

	}

	public static int[] findAValidNearestCell(char[][] cellValues, int x, int y, int enemyX, int enemyY) {
		int height = cellValues.length;
		int width = cellValues[0].length;
		int[] nearestValidCell = new int[] { 0, 0 };

		// System.err.println("EE: " + enemyX + " " + enemyY);

		// to north
		if (!enemyDirFromUs(x, y, enemyX, enemyY).contains("N") && isValid(x, y - 1, height, width)
				&& isUnBlocked(cellValues, x, y - 1)) {
			nearestValidCell[0] = x;
			nearestValidCell[1] = y - 1;
			// to south
		} else if (!enemyDirFromUs(x, y, enemyX, enemyY).contains("S") && isValid(x, y + 1, height, width)
				&& isUnBlocked(cellValues, x, y + 1)) {
			nearestValidCell[0] = x;
			nearestValidCell[1] = y + 1;
			// to west
		} else if (!enemyDirFromUs(x, y, enemyX, enemyY).contains("W") && isValid(x - 1, y, height, width)
				&& isUnBlocked(cellValues, x - 1, y)) {
			nearestValidCell[0] = x - 1;
			nearestValidCell[1] = y;
			// to east
		} else if (!enemyDirFromUs(x, y, enemyX, enemyY).contains("E") && isValid(x + 1, y, height, width)
				&& isUnBlocked(cellValues, x + 1, y)) {
			nearestValidCell[0] = x + 1;
			nearestValidCell[1] = y;
		}

		enemyX = x;
		enemyY = y;
		x = nearestValidCell[0];
		y = nearestValidCell[1];

		// System.err.println("EE2: " + enemyX + " " + enemyY);

		// lets check the next step as well, better to run from speedy enemy
		// to north
		// if enemy is not on north
		if (!enemyDirFromUs(x, y, enemyX, enemyY).contains("N") && isValid(x, y - 1, height, width)
				&& isUnBlocked(cellValues, x, y - 1)) {
			nearestValidCell[0] = x;
			nearestValidCell[1] = y - 1;
			// to south
		} else if (!enemyDirFromUs(x, y, enemyX, enemyY).contains("S") && isValid(x, y + 1, height, width)
				&& isUnBlocked(cellValues, x, y + 1)) {
			nearestValidCell[0] = x;
			nearestValidCell[1] = y + 1;
			// to west
		} else if (!enemyDirFromUs(x, y, enemyX, enemyY).contains("W") && isValid(x - 1, y, height, width)
				&& isUnBlocked(cellValues, x - 1, y)) {
			nearestValidCell[0] = x - 1;
			nearestValidCell[1] = y;
			// to east
		} else if (!enemyDirFromUs(x, y, enemyX, enemyY).contains("E") && isValid(x + 1, y, height, width)
				&& isUnBlocked(cellValues, x + 1, y)) {
			nearestValidCell[0] = x + 1;
			nearestValidCell[1] = y;
		}

		return nearestValidCell;
	}

	public static int findNearestPacId(int x, int y, List<Pac> pacList, char[][] cellValues) {
		int distance = 0;
		int minDistance = 100;
		int pacId = 0;
		int src[] = new int[2];
		src[0] = x;
		src[1] = y;
		int dest[] = new int[2];
		List<Integer> pathToPac = new ArrayList<Integer>();

		for (Pac pac : pacList) {
			dest[0] = pac.getX();
			dest[1] = pac.getY();
			pathToPac = Player.aStarSearch(cellValues, src, dest);
			if (pathToPac != null) {
				distance = pathToPac.size();
				if (distance < minDistance) {
					minDistance = distance;
					pacId = pac.getId();
				}
			}
		}

		return pacId;
	}

	public static void cleanMap(List<Pac> myPacList, List<Pellet> pelletList, List<Pellet> pelletMemoryList,
			List<Pellet> NKPelletList, int height, int width, char[][] cellValues) {
		int myX = 0;
		int myY = 0;
		int pacId = 0;
		int pmCount = -1;
		int pelletRemoveId = -1;
		int[] nextCell = new int[2];
		boolean noInfo = false;
		boolean weHavePacHere = false;
		boolean pelletIsThere = false;
		for (Pac pac : myPacList) {
			pacId = pac.getId();
			myX = pac.getX();
			myY = pac.getY();

			// remove current cell
			pelletRemoveId = -1;
			pmCount = -1;
			for (Pellet pm : pelletMemoryList) {
				pmCount++;
				if (pm.getX() == myX && pm.getY() == myY) {
					pelletRemoveId = pmCount;
					break;
				}
			}
			if (pelletRemoveId != -1)
				pelletMemoryList.remove(pmCount);

			pelletRemoveId = -1;
			pmCount = -1;
			for (Pellet pnk : NKPelletList) {
				pmCount++;
				if (pnk.getX() == myX && pnk.getY() == myY) {
					pelletRemoveId = pmCount;
					break;
				}
			}
			if (pelletRemoveId != -1)
				NKPelletList.remove(pmCount);

			// =========cleaning
			// clean to north
			nextCell[0] = myX;
			nextCell[1] = myY;
			// check the cells I see, so clean the pelletMemory
			noInfo = false;

			do {
				pelletRemoveId = -1;
				nextCell[1] = nextCell[1] - 1;

				weHavePacHere = false;
				for (Pac pac0 : myPacList) {
					if (nextCell[0] == pac0.getX() && nextCell[1] == pac0.getY()) {
						weHavePacHere = true;
					}
				}
				if (isValid(nextCell[0], nextCell[1], height, width) && cellValues[nextCell[1]][nextCell[0]] != '#') {
					// remove the pellets which can be seen if they are exists or not
					pelletIsThere = false;
					for (Pellet pn : pelletList) {
						if (pn.getX() == nextCell[0] && pn.getY() == nextCell[1]) {
							pelletIsThere = true;
							break;
						}
					}
					if (!pelletIsThere) {
						pmCount = -1;
						if (!weHavePacHere)
							cellValues[nextCell[1]][nextCell[0]] = ' ';
						for (Pellet pm : pelletMemoryList) {
							pmCount++;
							if (pm.getX() == nextCell[0] && pm.getY() == nextCell[1]) {
								pelletRemoveId = pmCount;
								break;
							}
						}
						if (pelletRemoveId != -1)
							pelletMemoryList.remove(pmCount);

						pelletRemoveId = -1;
						pmCount = -1;
						for (Pellet pnk : NKPelletList) {
							pmCount++;
							if (pnk.getX() == nextCell[0] && pnk.getY() == nextCell[1]) {
								pelletRemoveId = pmCount;
								break;
							}
						}
						if (pelletRemoveId != -1)
							NKPelletList.remove(pmCount);

					}
					// System.err.println("Mem clean noinfo, nextcell: " + noInfo + " " +
					// nextCell[0] + " " + nextCell[1]);
				} else
					noInfo = true;
			} while (!noInfo);

			// clean to south
			nextCell[0] = myX;
			nextCell[1] = myY;
			// check the cells I see, so clean the pelletMemory
			noInfo = false;

			do {
				pelletRemoveId = -1;
				nextCell[1] = nextCell[1] + 1;

				weHavePacHere = false;
				for (Pac pac0 : myPacList) {
					if (nextCell[0] == pac0.getX() && nextCell[1] == pac0.getY()) {
						weHavePacHere = true;
					}
				}
				if (isValid(nextCell[0], nextCell[1], height, width) && cellValues[nextCell[1]][nextCell[0]] != '#') {
					// System.err.println("DELETE: " + nextCell[0] + " " + nextCell[1]);
					// remove the pellets which can be seen if they are exists or not
					pelletIsThere = false;
					for (Pellet pn : pelletList) {
						if (pn.getX() == nextCell[0] && pn.getY() == nextCell[1]) {
							pelletIsThere = true;
							break;
						}
					}
					if (!pelletIsThere) {

						pmCount = -1;
						if (!weHavePacHere)
							cellValues[nextCell[1]][nextCell[0]] = ' ';
						for (Pellet pm : pelletMemoryList) {
							pmCount++;
							if (pm.getX() == nextCell[0] && pm.getY() == nextCell[1]) {
								pelletRemoveId = pmCount;
								break;
							}
						}
						if (pelletRemoveId != -1)
							pelletMemoryList.remove(pmCount);
						pelletRemoveId = -1;
						pmCount = -1;

						for (Pellet pnk : NKPelletList) {
							pmCount++;
							if (pnk.getX() == nextCell[0] && pnk.getY() == nextCell[1]) {
								pelletRemoveId = pmCount;
								break;
							}
						}
						if (pelletRemoveId != -1)
							NKPelletList.remove(pmCount);
					}
					// System.err.println("Mem clean noinfo, nextcell: " + noInfo + " " +
					// nextCell[0] + " " + nextCell[1]);
				} else
					noInfo = true;
			} while (!noInfo);

			// clean to west
			nextCell[0] = myX;
			nextCell[1] = myY;
			// check the cells I see, so clean the pelletMemory
			noInfo = false;
			do {
				pelletRemoveId = -1;
				nextCell[0] = nextCell[0] - 1;

				weHavePacHere = false;
				for (Pac pac0 : myPacList) {
					if (nextCell[0] == pac0.getX() && nextCell[1] == pac0.getY()) {
						weHavePacHere = true;
					}
				}

				if (isValid(nextCell[0], nextCell[1], height, width) && cellValues[nextCell[1]][nextCell[0]] != '#') {
					// remove the pellets which can be seen if they are exists or not
					pelletIsThere = false;
					for (Pellet pn : pelletList) {
						if (pn.getX() == nextCell[0] && pn.getY() == nextCell[1]) {
							pelletIsThere = true;
							break;
						}
					}
					if (!pelletIsThere) {

						pmCount = -1;
						if (!weHavePacHere)
							cellValues[nextCell[1]][nextCell[0]] = ' ';
						for (Pellet pm : pelletMemoryList) {
							pmCount++;
							if (pm.getX() == nextCell[0] && pm.getY() == nextCell[1]) {
								pelletRemoveId = pmCount;
								break;
							}
						}
						if (pelletRemoveId != -1)
							pelletMemoryList.remove(pmCount);

						pelletRemoveId = -1;
						pmCount = -1;
						for (Pellet pnk : NKPelletList) {
							pmCount++;
							if (pnk.getX() == nextCell[0] && pnk.getY() == nextCell[1]) {
								pelletRemoveId = pmCount;
								break;
							}
						}
						if (pelletRemoveId != -1)
							NKPelletList.remove(pmCount);

						// System.err.println("Mem clean noinfo, nextcell: " + noInfo + " " +
						// nextCell[0] + " " + nextCell[1]);
					}
				} else
					noInfo = true;
			} while (!noInfo);

			// clean to east
			nextCell[0] = myX;
			nextCell[1] = myY;
			// check the cells I see, so clean the pelletMemory
			noInfo = false;
			do {
				pelletRemoveId = -1;
				nextCell[0] = nextCell[0] + 1;
				weHavePacHere = false;
				for (Pac pac0 : myPacList) {
					if (nextCell[0] == pac0.getX() && nextCell[1] == pac0.getY()) {
						weHavePacHere = true;
					}
				}
				if (isValid(nextCell[0], nextCell[1], height, width) && cellValues[nextCell[1]][nextCell[0]] != '#') {
					// remove the pellets which can be seen if they are exists or not
					pelletIsThere = false;
					for (Pellet pn : pelletList) {
						if (pn.getX() == nextCell[0] && pn.getY() == nextCell[1]) {
							pelletIsThere = true;
							break;
						}
					}
					if (!pelletIsThere) {

						pmCount = -1;
						if (!weHavePacHere)
							cellValues[nextCell[1]][nextCell[0]] = ' ';
						for (Pellet pm : pelletMemoryList) {
							pmCount++;
							if (pm.getX() == nextCell[0] && pm.getY() == nextCell[1]) {
								pelletRemoveId = pmCount;
								break;
							}
						}
						if (pelletRemoveId != -1)
							pelletMemoryList.remove(pmCount);

						pelletRemoveId = -1;
						pmCount = -1;
						for (Pellet pnk : NKPelletList) {
							pmCount++;
							if (pnk.getX() == nextCell[0] && pnk.getY() == nextCell[1]) {
								pelletRemoveId = pmCount;
								break;
							}
						}
						if (pelletRemoveId != -1)
							NKPelletList.remove(pmCount);

					}
					// System.err.println("Mem clean noinfo, nextcell: " + noInfo + " " +
					// nextCell[0] + " " + nextCell[1]);
				} else
					noInfo = true;
			} while (!noInfo);

		}
	}

	public static void main(String args[]) {
		Scanner in = new Scanner(System.in);
		int width = in.nextInt(); // size of the grid
		int height = in.nextInt(); // top left corner is (x=0, y=0)

		// height=y width=x
		char[][] cellValues = new char[height][width];

		if (in.hasNextLine()) {
			in.nextLine();
		}

		List<Pellet> NKPelletList = new ArrayList<Pellet>();
		Pellet pel;
		int n = 0;
		// set map
		for (int y = 0; y < height; y++) {
			String row = in.nextLine(); // one line of the grid: space " " is floor, pound "#" is wall
			for (int x = 0; x < width; x++) {
				if (row.charAt(x) == ' ') {
					cellValues[y][x] = 'N'; // not have been;
					pel = new Pellet(1000 + n, x, y, 1);
					n++;
					NKPelletList.add(pel);
				} else
					cellValues[y][x] = '#';
			}
		}

		// check map

		/*
		 * System.err.println("========MAP========="); for (int yy = 0; yy < height;
		 * yy++) { for (int xx = 0; xx < width; xx++) { if (xx == width - 1)
		 * System.err.println(""); else System.err.print(cellValues[yy][xx]); } }
		 */

		List<Pac> myPacList = new ArrayList<Pac>();
		List<Pac> enemyPacList = new ArrayList<Pac>();
		List<Pellet> pelletList = new ArrayList<Pellet>();
		List<Pellet> pelletMemoryList = new ArrayList<Pellet>();
		int[] enemyPrevX = new int[] { 0, 0, 0, 0, 0 };
		int[] enemyPrevY = new int[] { 0, 0, 0, 0, 0 };
		int[] enemySpeed = new int[] { 0, 0, 0, 0, 0 };
		int[] myPrevX = new int[] { 0, 0, 0, 0, 0 };
		int[] myPrevY = new int[] { 0, 0, 0, 0, 0 };

		int turn = 0;
		long startTime, endTime;

		int myScore = 0;
		int opponentScore = 0;
		int visiblePacCount = 0;

		int pacId = 0;
		boolean mine = false;
		int x = 0, y = 0;
		String typeId = "";
		int speedTurnsLeft = 0;
		int abilityCooldown = 0;
		Pac p;
		int visiblePelletCount = 0;
		boolean memoryListContainsPellet = false;
		int value = 0;

		int index = -1;
		boolean stucked = false;
		int[] firstStepToNearestPelletX = new int[] { 0, 0, 0, 0, 0 };
		int[] firstStepToNearestPelletY = new int[] { 0, 0, 0, 0, 0 };
		List<Integer> pathToRandom = new ArrayList<Integer>();
		int[] src = new int[] { 0, 0 };
		int[] dest = new int[] { 0, 0 };
		Random r;
		int distanceToPellet = 0;
		int myPacCount = 0;
		int[] toX = new int[] { 0, 0, 0, 0, 0 };
		int[] toY = new int[] { 0, 0, 0, 0, 0 };
		int myX = 0;
		int myY = 0;
		int[] randomValidCell = new int[] { 0, 0 };
		int[] nearestValidCell = new int[] { 0, 0 };
		int[] nearestNKValidCell = new int[] { 0, 0 };
		int pelletId = -1;
		int pelletRemoveId = -1;

		int enemyX = 0;
		int enemyY = 0;
		int dist = 0;
		int enemyId = 0;
		char enemyDir = ' ';
		char myDir = ' ';
		String enemyType = "";

		String[] enemyInfoString = null;
		String toChange = "";
		boolean[] justSwitched = { false, false, false, false, false };
		boolean dirEscape = false;
		boolean pelletIsThere = false;
		int[] nextCell = new int[2];
		int pmCount = 0;
		boolean noInfo = false;
		int north = 0, south = 0, west = 0, east = 0;
		int[] northLast = new int[2];
		int[] southLast = new int[2];
		int[] westLast = new int[2];
		int[] eastLast = new int[2];
		boolean[] chasingMode = { false, false, false, false, false };
		int[] enemyLastPositionX = new int[] { 0, 0, 0, 0, 0 };
		int[] enemyLastPositionY = new int[] { 0, 0, 0, 0, 0 };
		boolean[] escapeMode = { false, false, false, false, false };
		boolean weHavePacHere = false;
		boolean haveToStay = false;
		char chosenDir = ' ';
		int pelletValue = 1;
		int[] chasingTurn = new int[] { 0, 0, 0, 0, 0 };
		int[] chasingCoolDown = new int[] { 5, 5, 5, 5, 5 };
		char[] prevDir = new char[] { ' ', ' ', ' ', ' ', ' ' };

		// game loop
		while (true) {
			startTime = System.currentTimeMillis();
			turn++;
			myPacList.clear();
			enemyPacList.clear();
			pelletList.clear();
			StringBuilder moveString = new StringBuilder();

			myScore = in.nextInt();
			opponentScore = in.nextInt();
			visiblePacCount = in.nextInt(); // all your pacs and enemy pacs in sight
			for (int i = 0; i < visiblePacCount; i++) {
				pacId = in.nextInt(); // pac number (unique within a team)
				mine = in.nextInt() != 0; // true if this pac is yours
				x = in.nextInt(); // position in the grid
				y = in.nextInt(); // position in the grid
				typeId = in.next(); // unused in wood leagues
				speedTurnsLeft = in.nextInt(); // unused in wood leagues
				abilityCooldown = in.nextInt(); // unused in wood leagues

				p = new Pac(pacId, mine, x, y, typeId, speedTurnsLeft, abilityCooldown);

				if (mine) {
					cellValues[y][x] = 'P';
					if (!typeId.equals("DEAD"))
						myPacList.add(p);
				} else if (!typeId.equals("DEAD"))
					enemyPacList.add(p);
			}
			visiblePelletCount = in.nextInt(); // all pellets in sight
			memoryListContainsPellet = false;
			for (int i = 0; i < visiblePelletCount; i++) {
				x = in.nextInt();
				y = in.nextInt();
				value = in.nextInt(); // amount of points this pellet is worth

				cellValues[y][x] = 'o';
				pel = new Pellet(i, x, y, value);
				if (value == 10) {
					pacId = findNearestPacId(x, y, myPacList, cellValues);
					pel.setNearestPacId(pacId);
				}
				pelletList.add(pel);
				memoryListContainsPellet = false;
				for (Pellet pellet : pelletMemoryList) {
					if (pellet.getX() == x && pellet.getY() == y) {
						memoryListContainsPellet = true;
						break;
					}
				}
				// dont add superpellet as it is always can be seen
				if (!memoryListContainsPellet && value != 10) {
					// System.err.println("ADDING: " + pel.getX() + " " + pel.getY());
					pelletMemoryList.add(pel);
				}
			}

			// check map

			/*
			 * System.err.println("========MAP========="); for (int yy = 0; yy < height;
			 * yy++) { for (int xx = 0; xx < width; xx++) { if (xx == width - 1)
			 * System.err.println(""); else System.err.print(cellValues[yy][xx]); } }
			 */

			index = -1;
			stucked = false;
			firstStepToNearestPelletX = new int[] { 0, 0, 0, 0, 0 };
			firstStepToNearestPelletY = new int[] { 0, 0, 0, 0, 0 };
			pathToRandom = new ArrayList<Integer>();
			src = new int[] { 0, 0 };
			dest = new int[] { 0, 0 };
			distanceToPellet = 0;
			myPacCount = 0;
			toX = new int[] { 0, 0, 0, 0, 0 };
			toY = new int[] { 0, 0, 0, 0, 0 };
			myX = 0;
			myY = 0;
			randomValidCell = new int[] { 0, 0 };
			nearestNKValidCell = new int[] { 0, 0 };
			enemyType = "";

			// for (Pac p : pacList) {
			// if (p.isMine()) {
			// myPacCount++;
			// }
			// }
			for (Pac pac : enemyPacList) {
				pacId = pac.getId();
				// System.err.println("====Enemy: " + pac.getId());
				// System.err.println("Prev coords: " + enemyPrevX[pacId] + " " +
				// enemyPrevY[pacId]);
				pac.calcDir(enemyPrevX[pacId], enemyPrevY[pacId], pac.getX(), pac.getY());
				enemyPrevX[pacId] = pac.getX();
				enemyPrevY[pacId] = pac.getY();

				// System.err.println(pac.getDir());
				switch (chosenDir) {
				case 'N':
					if (pac.getY() == enemyPrevY[pacId] + 1)
						enemySpeed[pacId] = 1;
					else if (pac.getY() == enemyPrevY[pacId] + 2)
						enemySpeed[pacId] = 2;
					enemySpeed[pacId] = 0;
					break;

				case 'S':
					if (pac.getY() == enemyPrevY[pacId] - 1)
						enemySpeed[pacId] = 1;
					else if (pac.getY() == enemyPrevY[pacId] - 2)
						enemySpeed[pacId] = 2;
					enemySpeed[pacId] = 0;
					break;

				case 'W':
					if (pac.getX() == enemyPrevX[pacId] - 1)
						enemySpeed[pacId] = 1;
					else if (pac.getX() == enemyPrevX[pacId] - 2)
						enemySpeed[pacId] = 2;
					enemySpeed[pacId] = 0;
					break;

				case 'E':
					if (pac.getX() == enemyPrevX[pacId] + 1)
						enemySpeed[pacId] = 1;
					else if (pac.getX() == enemyPrevX[pacId] + 2)
						enemySpeed[pacId] = 2;
					enemySpeed[pacId] = 0;
					break;

				default:
					break;
				}
				// System.err.println("Coords: " + enemyPrevX[pacId] + " " + enemyPrevY[pacId]);
			}

			/*
			 * System.err.println("%%%%%%%%%% PMEMOList BEF CL"); for (Pellet pellet :
			 * pelletMemoryList) { System.err.println(pellet.getX() + " " + pellet.getY());
			 * }
			 */

			/*
			 * System.err.println("%%%%%%%%%% PMEMOList AF CLE"); for (Pellet pellet :
			 * pelletMemoryList) { System.err.println(pellet.getX() + " " + pellet.getY());
			 * }
			 */

			// clean the map
			cleanMap(myPacList, pelletList, pelletMemoryList, NKPelletList, height, width, cellValues);

			for (Pac pac : myPacList) {
				pacId = pac.getId();
				myX = pac.getX();
				myY = pac.getY();
				stucked = false;
				if (myX == myPrevX[pacId] && myY == myPrevY[pacId] && !justSwitched[pacId]) {
					System.err.println("We have not just switched, but no moves, so stucked!");
					stucked = true;
				}
				justSwitched[pacId] = false;

				myDir = ' ';
				if (myPrevX[pacId] == myX && myPrevY[pacId] == myY)
					myDir = prevDir[pacId];
				else {
					pac.calcDir(myPrevX[pacId], myPrevY[pacId], myX, myY);
					myDir = pac.getDir();
				}
				prevDir[pacId] = myDir;

				System.err.println("=========Actual pac: " + pac.getId() + " " + myX + " " + myY + " " + myPrevX[pacId]
						+ " " + myPrevY[pacId] + " " + myDir);

				// get chosen dir
				chosenDir = pac.getChosenDir(height, width, cellValues);

				// get the nearest pellet
				pelletValue = 1;
				int[] nearestPellet = new int[2];
				String[] pelletInfoString = pac
						.getNearestPellet(pelletList, pelletMemoryList, cellValues, myDir, chosenDir).split(";");

				nearestPellet[0] = Integer.parseInt(pelletInfoString[0]);
				nearestPellet[1] = Integer.parseInt(pelletInfoString[1]);
				firstStepToNearestPelletX[pacId] = Integer.parseInt(pelletInfoString[2]);
				firstStepToNearestPelletY[pacId] = Integer.parseInt(pelletInfoString[3]);
				distanceToPellet = Integer.parseInt(pelletInfoString[4]);
				pelletValue = Integer.parseInt(pelletInfoString[5]);

				toX[pacId] = nearestPellet[0];
				toY[pacId] = nearestPellet[1];

				System.err.println("To: " + toX[pacId] + " " + toY[pacId]);
				// System.err.println("FS: " + firstStepToNearestPelletX[pacId] + " " +
				// firstStepToNearestPelletY[pacId]);
				if (toX[pacId] == 0 && toY[pacId] == 0) // no pellet in sight
				{
					nearestNKValidCell = findANearestNotKnownCell(cellValues, myX, myY, pelletMemoryList, NKPelletList);
					toX[pacId] = nearestNKValidCell[0];
					toY[pacId] = nearestNKValidCell[1];
					src[0] = myX;
					src[1] = myY;
					dest[0] = toX[pacId];
					dest[1] = toY[pacId];
					pathToRandom = Player.aStarSearch(cellValues, src, dest);
					if (pathToRandom != null) {
						firstStepToNearestPelletX[pacId] = Player
								.getCell(pathToRandom.get(0), cellValues.length, cellValues[0].length).getX();
						firstStepToNearestPelletY[pacId] = Player
								.getCell(pathToRandom.get(0), cellValues.length, cellValues[0].length).getY();
					}
					System.err.println("To: " + toX[pacId] + " " + toY[pacId]);
					// System.err.println(
					// "FS: " + firstStepToNearestPelletX[pacId] + " " +
					// firstStepToNearestPelletY[pacId]);
				}
				if (toX[pacId] == 0 && toY[pacId] == 0) // no pellet in sight
				{
					System.err.println("No pellet in sight, so go random!");
					randomValidCell = findAValidRandomCell(cellValues);
					toX[pacId] = randomValidCell[0];
					toY[pacId] = randomValidCell[1];
					src[0] = myX;
					src[1] = myY;
					dest[0] = toX[pacId];
					dest[1] = toY[pacId];
					pathToRandom = Player.aStarSearch(cellValues, src, dest);
					if (pathToRandom != null) {
						firstStepToNearestPelletX[pacId] = Player
								.getCell(pathToRandom.get(0), cellValues.length, cellValues[0].length).getX();
						firstStepToNearestPelletY[pacId] = Player
								.getCell(pathToRandom.get(0), cellValues.length, cellValues[0].length).getY();
					}
					System.err.println("To: " + toX[pacId] + " " + toY[pacId]);
					// System.err.println(
					// "FS: " + firstStepToNearestPelletX[pacId] + " " +
					// firstStepToNearestPelletY[pacId]);
				}
				// }
				// }
				// needed to change or just move

				enemyInfoString = pac.getNearestEnemy(enemyPacList, cellValues).split(";");
				toChange = enemyInfoString[0];

				enemyX = Integer.parseInt(enemyInfoString[1]);
				enemyY = Integer.parseInt(enemyInfoString[2]);
				dist = Integer.parseInt(enemyInfoString[3]);
				enemyId = Integer.parseInt(enemyInfoString[4]);
				enemyDir = ' ';

				// there is enemy
				if (!toChange.equals("")) {
					System.err
							.println("Nearest enemy is id xyd: " + enemyId + " " + enemyX + " " + enemyY + " " + dist);
					System.err.println("Tochange: " + toChange);

					for (Pac epac : enemyPacList) {
						if (epac.getId() == enemyId) {
							enemyDir = epac.getDir();
							enemyType = epac.getTypeId();
							break;
						}
					}
					System.err.println("Enemy dir: " + enemyDir);
				}

				// enemy is recently disappeared but we dont go back
				if (escapeMode[pacId]) {
					System.err.println("Escape in escape mode!!!");
					enemyX = myPrevX[pacId];
					enemyY = myPrevY[pacId];
					// System.err.println("EE mode ex,ey,x,y: " + enemyX + " " + enemyY + " " + myX
					// + " " + myY);
					nearestValidCell = findAValidNearestCell(cellValues, myX, myY, enemyX, enemyY);
					toX[pacId] = nearestValidCell[0];
					toY[pacId] = nearestValidCell[1];
					// System.err.println("EE to: " + toX[pacId] + " " + toY[pacId]);
					System.err.println("Nearest cell escape");
					moveString = moveString.append("MOVE " + pac.getId() + " " + toX[pacId] + " " + toY[pacId] + "|");
					escapeMode[pacId] = false;
				} else {
					// enemy is near and cannot change so tryin to escape, except if we are in the
					// same form
					if (dist <= 3 && !toChange.equals(pac.getTypeId()) && !(enemyX == 0 && enemyY == 0)
							&& pac.getAbilityCooldown() > 0 && !pac.getTypeId().equals(enemyType)) {
						System.err.println("Escape!!!");

						// dont go back in escape mode in case of disappearing enemy
						// System.err.println("======DEBUG: " + myX + " " + myY + " " + enemyX + " " +
						// enemyY);
						nearestValidCell = findAValidNearestCell(cellValues, myX, myY, enemyX, enemyY);
						toX[pacId] = nearestValidCell[0];
						toY[pacId] = nearestValidCell[1];
						if (nearestValidCell[0] == 0 && nearestValidCell[1] == 0) {
							// cannot move
							System.err.println("Cannot move!");
							toX[pacId] = myX;
							toY[pacId] = myY;
							toX[pacId] = nearestValidCell[0];
							toY[pacId] = nearestValidCell[1];
						}
						System.err.println("Nearest cell escape");
						moveString = moveString
								.append("MOVE " + pac.getId() + " " + toX[pacId] + " " + toY[pacId] + "|");
						escapeMode[pacId] = true;
						// }
						// max 5 turn chasing
					} else if (toChange.equals(pac.getTypeId()) && dist <= 3 && chasingTurn[pacId] <= 5
							&& chasingCoolDown[pacId] == 0) {
						// if we can, attack the enemy
						System.err.println("Attack the little bastard!");
						chasingTurn[pacId]++;
						if (chasingTurn[pacId] == 5)
							chasingCoolDown[pacId] = 5;
						toX[pacId] = (int) Math.signum(enemyX - myX) + enemyX;
						toY[pacId] = (int) Math.signum(enemyY - myY) + enemyY;
						// low speed and able to speed up and in the right form, speed up before the
						// chase
						if (pac.getSpeedTurnsLeft() == 0 && pac.getAbilityCooldown() == 0 && dist >= 1
								&& toChange.equals(pac.getTypeId())) {
							justSwitched[pacId] = true;
							// System.err.println("Set JustSwitched due to speed for actual pac pacId,
							// value: " + pacId
							// + " " + justSwitched[pacId]);
							moveString = moveString.append("SPEED " + pac.getId() + "|");
						} else if (isValid(toX[pacId], toY[pacId], height, width)
								&& isUnBlocked(cellValues, toX[pacId], toY[pacId]))
							moveString = moveString
									.append("MOVE " + pac.getId() + " " + toX[pacId] + " " + toY[pacId] + "|");
						else {
							toX[pacId] = enemyX;
							toY[pacId] = enemyY;
							if (!isValid(toX[pacId], toY[pacId], height, width)
									|| !isUnBlocked(cellValues, toX[pacId], toY[pacId])) {
								System.err.println("Chaising, but error, so random!");
								nearestNKValidCell = findANearestNotKnownCell(cellValues, myX, myY, pelletMemoryList,
										NKPelletList);
								toX[pacId] = nearestNKValidCell[0];
								toY[pacId] = nearestNKValidCell[1];
								if (toX[pacId] == 0 && toY[pacId] == 0) {
									randomValidCell = findAValidRandomCell(cellValues);
									toX[pacId] = randomValidCell[0];
									toY[pacId] = randomValidCell[1];
								}
							}
							moveString = moveString
									.append("MOVE " + pac.getId() + " " + toX[pacId] + " " + toY[pacId] + "|");
						}
						chasingMode[pacId] = false;
						enemyLastPositionX[pacId] = enemyX;
						enemyLastPositionY[pacId] = enemyY;
						// enemy disappeared but we go to the last known place of it
					} else if (chasingMode[pacId]) {
						System.err.println("Chasing with last known position!");
						moveString = moveString.append("MOVE " + pac.getId() + " " + enemyLastPositionX[pacId] + " "
								+ enemyLastPositionY[pacId] + "|");
						chasingMode[pacId] = false;
						enemyLastPositionX[pacId] = 0;
						enemyLastPositionY[pacId] = 0;
					} else if (!toChange.equals("") && !toChange.equals(pac.getTypeId())
							&& pac.getAbilityCooldown() == 0) {
						// && dist == enemySpeed[enemyId]) {
						System.err.println("I have to switch! " + toChange);
						justSwitched[pacId] = true;
						// System.err.println(
						// "Set JustSwitched for actual pac pacId, value: " + pacId + " " +
						// justSwitched[pacId]);
						moveString = moveString.append("SWITCH " + pac.getId() + " " + toChange + "|");
					} else // normal move
					if (distanceToPellet > 1 && (toChange.equals("") || toChange.equals(pac.getTypeId()))
							&& pac.getAbilityCooldown() == 0) {
						System.err.println("Speedy Gonzales!");
						justSwitched[pacId] = true;
						// System.err.println(
						// "Set JustSwitched due speedy gonzales, value: " + pacId + " " +
						// justSwitched[pacId]);
						moveString = moveString.append("SPEED " + pac.getId() + "|");
					} else {
						// {
						System.err.println("Normal move");
						chasingTurn[pacId] = 0;
						chasingCoolDown[pacId]--;
						// if current pac go to an other pac destination, dont move
						// System.err.println(
						// "Actual FS: " + firstStepToNearestPelletX[pacId] + " " +
						// firstStepToNearestPelletY[pacId]);
						haveToStay = false;
						for (int pc = 0; pc < pacId; pc++) {
							// System.err.println(
							// "Inv FS: " + firstStepToNearestPelletX[pc] + " " +
							// firstStepToNearestPelletY[pc]);
							if (firstStepToNearestPelletX[pacId] == firstStepToNearestPelletX[pc]
									&& firstStepToNearestPelletY[pacId] == firstStepToNearestPelletY[pc]) {
								System.err.println("Next step would be same, so stay!");
								toX[pacId] = myX;
								toY[pacId] = myY;
								haveToStay = true;
							}
						}
						if (haveToStay)
							moveString = moveString
									.append("MOVE " + pac.getId() + " " + toX[pacId] + " " + toY[pacId] + "|");
						else if (isValid(toX[pacId], toY[pacId], height, width)
								&& isUnBlocked(cellValues, toX[pacId], toY[pacId])) {
							// if stucked go random
							if (stucked) {
								System.err.println("Normal move, stucked, so random!");
								// System.err.println(myDir);
								switch (myDir) {
								case 'N':
									toX[pacId] = myX;
									toY[pacId] = myY + 1;
									break;

								case 'S':
									toX[pacId] = myX;
									toY[pacId] = myY - 1;
									break;

								case 'W':
									toX[pacId] = myX + 1;
									toY[pacId] = myY;
									break;

								case 'E':
									toX[pacId] = myX - 1;
									toY[pacId] = myY;
									break;

								default:
									toX[pacId] = 0;
									toY[pacId] = 0;
									break;
								}
								// System.err.println("Dir random: " + toX[pacId] + " " + toY[pacId]);
								/*
								 * nearestNKValidCell = findANearestNotKnownCell(cellValues, myX, myY,
								 * pelletMemoryList, NKPelletList); toX[pacId] = nearestNKValidCell[0];
								 * toY[pacId] = nearestNKValidCell[1];
								 */
								if (toX[pacId] == 0 && toY[pacId] == 0) {
									randomValidCell = findAValidRandomCell(cellValues);
									toX[pacId] = randomValidCell[0];
									toY[pacId] = randomValidCell[1];
									// System.err.println("Full random: " + toX[pacId] + " " + toY[pacId]);
								}
							}
							moveString = moveString
									.append("MOVE " + pac.getId() + " " + toX[pacId] + " " + toY[pacId] + "|");
						} else {
							nearestNKValidCell = findANearestNotKnownCell(cellValues, myX, myY, pelletMemoryList,
									NKPelletList);
							toX[pacId] = nearestNKValidCell[0];
							toY[pacId] = nearestNKValidCell[1];
							if (toX[pacId] == 0 && toY[pacId] == 0) {
								randomValidCell = findAValidRandomCell(cellValues);
								toX[pacId] = randomValidCell[0];
								toY[pacId] = randomValidCell[1];
							}
							System.err.println("Normal move but random!");
							moveString = moveString
									.append("MOVE " + pac.getId() + " " + toX[pacId] + " " + toY[pacId] + "|");
						}
					}

					myPrevX[pacId] = myX;
					myPrevY[pacId] = myY;
				}

				cellValues[firstStepToNearestPelletY[pacId]][firstStepToNearestPelletX[pacId]] = ' ';

				// remove this pellet from the memory list
				pelletId = -1;
				pelletRemoveId = -1;
				for (Pellet pellet : pelletMemoryList) {
					pelletId++;
					if (pellet.getX() == pac.getX() && pellet.getY() == pac.getY()) {
						// System.err.println("REMOVE: " + pellet.getX() + " " + pellet.getY());
						pelletRemoveId = pelletId;
						break;
					}
				}
				if (pelletRemoveId != -1)
					pelletMemoryList.remove(pelletRemoveId);

				// remove this pellet from the memory list
				pelletId = -1;
				pelletRemoveId = -1;
				for (Pellet pellet : NKPelletList) {
					pelletId++;
					if (pellet.getX() == pac.getX() && pellet.getY() == pac.getY()) {
						// System.err.println("REMOVE: " + pellet.getX() + " " + pellet.getY());
						pelletRemoveId = pelletId;
						break;
					}
				}
				if (pelletRemoveId != -1)
					NKPelletList.remove(pelletRemoveId);

			}

			endTime = System.currentTimeMillis();
			System.err.println("%%%%%%%%% Time elapsed: " + (endTime - startTime) + "%%%%%%%%%%");

			System.out.println(moveString);

			/*
			 * System.err.println("%%%%%%%%%% PList"); for (Pellet pellet : pelletList) {
			 * System.err.println(pellet.getX() + " " + pellet.getY()); }
			 * System.err.println("%%%%%%%%%% PMEMOList"); for (Pellet pellet :
			 * pelletMemoryList) { System.err.println(pellet.getX() + " " + pellet.getY());
			 * }
			 */

			// delete P from map
			for (int yy = 0; yy < height; yy++) {
				for (int xx = 0; xx < width; xx++) {
					if (cellValues[yy][xx] == 'P')
						cellValues[yy][xx] = ' ';
				}
			}

			// what is not eaten, is again free
			for (Pellet pp : pelletMemoryList) {
				pp.setTargeted(false);
			}

			// what is not eaten, is again free
			for (Pellet pp : NKPelletList) {
				pp.setTargeted(false);
			}
		}
	}

}