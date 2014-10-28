
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class agent {

	static int task;
	static char player;
	static char opponent;
	static int cutoffDepth;
	static StringBuilder builder = new StringBuilder();

	public static void main(String[] args) {

		board newNode = inputData();
		char player1 = player;
		char opponent1 = opponent;

		switch (task) {

		// greedy
		case 1:
			getChildren(newNode, player1, opponent1);
			greedy(newNode);
			break;

		// minimax
		case 2:
			minimax(newNode, player1, opponent1);
			break;
		// alpha-beta pruning
		case 3:
			abPruning(newNode, player1, opponent1);
			break;

		default:
			System.out.println("Incorrect task");
		}

	}

	// input data from file
	public static board inputData() {

		int count = 0;
		board eNode = new board();

		FileReader inputFile;
		try {
			inputFile = new FileReader(
					"input.txt");
			BufferedReader bufferReader = new BufferedReader(inputFile);
			String line;

			while ((line = bufferReader.readLine()) != null) {
				count++;
				if (count == 1) {
					task = Integer.parseInt(line);
				} else if (count == 2) {
					player = line.charAt(0);
					if (player == 'X') {
						opponent = 'O';
					} else {
						opponent = 'X';
					}
				} else if (count == 3) {
					cutoffDepth = Integer.parseInt(line);
				} else if (count >= 4 && count < 12) {
					for (int i = 0; i < 8; i++) {
						int row = count - 4;
						// System.out.println("line.charAt("+i+")"+line.charAt(i));
						boardCell eCell = new boardCell();
						eCell.color = line.charAt(i);
						eCell.r = row;
						eCell.c = i;
						eNode.bNode[row][i] = eCell;
					}
				}

			}
			bufferReader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int[][] weightNode = { { 99, -8, 8, 6, 6, 8, -8, 99 },
				{ -8, -24, -4, -3, -3, -4, -24, -8 },
				{ 8, -4, 7, 4, 4, 7, -4, 8 }, { 6, -3, 4, 0, 0, 4, -3, 6 },
				{ 6, -3, 4, 0, 0, 4, -3, 6 }, { 8, -4, 7, 4, 4, 7, -4, 8 },
				{ -8, -24, -4, -3, -3, -4, -24, -8 },
				{ 99, -8, 8, 6, 6, 8, -8, 99 }, };

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				eNode.bNode[i][j].weight = weightNode[i][j];
			}
		}

		// System.out.println(task);
		// System.out.println(player1);
		// System.out.println(cutoffDepth);
		// for(int i=0;i<8;i++) {
		// for(int j=0;j<8;j++) {
		// System.out.print(eNode.bNode[i][j].color);
		// }
		// System.out.print("\n");
		// }
		return eNode;
	}

	// --------------------------------------------------------------------------
	// input done
	// --------------------------------------------------------------------------

	// --------------------------------------------------------------------------
	// output
	// --------------------------------------------------------------------------

	public static void write_data(board B) {

		try {
			PrintWriter out = new PrintWriter(
					new BufferedWriter(
							new FileWriter(
									"output.txt",
									false)));

			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					// System.out.print(B.bNode[i][j].color);
					out.print(B.bNode[i][j].color);
				}
				// System.out.print("\n");
				out.println();
			}

			if (task == 2) {
				out.println("Node,Depth,Value");
			} else if (task == 3) {
				out.println("Node,Depth,Value,Alpha,Beta");
			}

			if (task != 1) {
				String log = builder.toString();

				String[] line = log.split("\\n");
				int len = line.length;
				int count = 0;
				while (count != len) {

					out.println(line[count]);
					count++;
				}
			}
			out.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// --------------------------------------------------------------------------
	// output done
	// --------------------------------------------------------------------------

	// --------------------------------------------------------------------------
	// greedy algorithm
	// --------------------------------------------------------------------------

	public static void evalFunc(board nextMove, char player1, char opponent1) {
		// List<board> children = nextMove.children;

		int wplayer1 = 0;
		int wopponent1 = 0;

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {

				if (nextMove.bNode[i][j].color == player1) {

					wplayer1 = wplayer1 + nextMove.bNode[i][j].weight;
					// System.out.print("Eval weight player : "+ wplayer1);

				} else if (nextMove.bNode[i][j].color == opponent1) {

					wopponent1 = wopponent1 + nextMove.bNode[i][j].weight;
					// System.out.print("Eval weight player : "+ wopponent1);

				}

				// System.out.print(nextMove.bNode[i][j].color);
			}
			// System.out.print("\n");
		}
		// finding eval func value for each child board
		nextMove.evalWeight = wplayer1 - wopponent1;
		// System.out.print("Eval weight : "+ nextMove.evalWeight);

	}

	public static void greedy(board nextMove) {

		for (board childB : nextMove.children) {

			evalFunc(childB, player, opponent);
		}
		// finding highest eval value among children boards

		int countChildren = countChildren(nextMove);
		/*
		 * for (board cBoard : newBoard.children) { countChildren++; }
		 */
		if (countChildren != 0) {

			board evalValB = nextMove.children.get(0);
			int maxEvalVal = nextMove.children.get(0).evalWeight;
			for (board childEvalB : nextMove.children) {

				// display(childEvalB);

				if (childEvalB.evalWeight > maxEvalVal) {
					maxEvalVal = childEvalB.evalWeight;
					evalValB = childEvalB;
				}// else if clause for if eval value is equals. run double for
					// loop
					// to get the cell which comes first
				else if (childEvalB.evalWeight == maxEvalVal) {
					outerloop: for (int i = 0; i < 8; i++) {
						for (int j = 0; j < 8; j++) {
							if ((i == childEvalB.diffr)
									&& (j == childEvalB.diffc)) {
								maxEvalVal = childEvalB.evalWeight;
								evalValB = childEvalB;
								break outerloop;
							} else if ((i == evalValB.diffr)
									&& (j == evalValB.diffc)) {
								break outerloop;
							}
						}
					}

				}
			}
			// System.out.println("next state");
			// display(evalValB);
			write_data(evalValB);
		} else {
			// System.out.println("next state");
			// display(nextMove);
			write_data(nextMove);
		}
	}

	// --------------------------------------------------------------------------
	// greedy algorithm over
	// --------------------------------------------------------------------------

	// --------------------------------------------------------------------------
	// minimax algorithm
	// --------------------------------------------------------------------------

	public static void minimax(board newBoard, char player1, char opponent1) {

		newBoard.depth = 0;
		newBoard.v = Integer.MIN_VALUE;

		recursiveGetChildren(newBoard, player1, opponent1);

		maxValue(newBoard, player1, opponent1);

		int countChildren = countChildren(newBoard);
		/*
		 * for (board cBoard : newBoard.children) { countChildren++; }
		 */
		if (countChildren != 0) {

			board evalValB = newBoard.children.get(0);
			double maxEvalVal = newBoard.children.get(0).v;
			for (board childEvalB : newBoard.children) {

				// display(childEvalB);

				if (childEvalB.v > maxEvalVal) {
					maxEvalVal = childEvalB.evalWeight;
					evalValB = childEvalB;
				}// else if clause for if eval value is equals. run double for
					// loop
					// // to get the cell which comes first else
				if (childEvalB.v == maxEvalVal) {
					outerloop: for (int i = 0; i < 8; i++) {
						for (int j = 0; j < 8; j++) {
							if ((i == childEvalB.diffr)
									&& (j == childEvalB.diffc)) {
								maxEvalVal = childEvalB.v;
								evalValB = childEvalB;
								break outerloop;
							} else if ((i == evalValB.diffr)
									&& (j == evalValB.diffc)) {
								break outerloop;
							}
						}
					}

				}

			}

			// System.out.println("next state");
			// display(evalValB);
			write_data(evalValB);
		} else {
			// System.out.println("next state");
			// display(newBoard);
			write_data(newBoard);
		}

	}

	public static board maxValue(board mmBoard, char player1, char opponent1) {
		// getChildren(mmBoard, player1, opponent1);

		int count = countChildren(mmBoard);
		/*
		 * for (board child : mmBoard.children) { count++; }
		 */
		if (count == 0 || mmBoard.depth == cutoffDepth) {
			evalFunc(mmBoard, player1, opponent1);
			int dc = mmBoard.diffc;
			int dr = mmBoard.diffr;
			mmBoard.v = mmBoard.evalWeight;

			// System.out.println("\nMax Terminal Node");
			if (mmBoard.depth == 0) {

				/*
				 * System.out.println("root," + mmBoard.depth + "," +
				 * mmBoard.evalWeight);
				 */
				builder.append("root," + mmBoard.depth + ","
						+ mmBoard.evalWeight + "\n");
			} else {

				if ((mmBoard.depth < cutoffDepth && mmBoard.diffr == -1 && mmBoard.diffc == -1)
						|| ((mmBoard.depth == cutoffDepth
								&& mmBoard.diffr == -1 && mmBoard.diffc == -1))) {

					/*
					 * System.out.println("pass," + mmBoard.depth + "," +
					 * mmBoard.evalWeight);
					 */
					builder.append("pass," + mmBoard.depth + ","
							+ mmBoard.evalWeight + "\n");

				} else {

					/*
					 * System.out.println((char) (dc + 1 + 96) + "" + (dr + 1) +
					 * "," + mmBoard.depth + "," + mmBoard.evalWeight);
					 */
					builder.append((char) (dc + 1 + 96) + "" + (dr + 1) + ","
							+ mmBoard.depth + "," + mmBoard.evalWeight + "\n");

				}
			}
			return mmBoard;
		}

		board v = new board();
		v.v = Integer.MIN_VALUE;
		int dc = mmBoard.diffc;
		int dr = mmBoard.diffr;

		// System.out.println("\nMax outside for");

		if (mmBoard.depth == 0) {
			/*
			 * System.out.println("root," + mmBoard.depth + "," +
			 * InfOrNot(v.v));
			 */
			builder.append("root," + mmBoard.depth + "," + InfOrNot(v.v) + "\n");

		} else if ((mmBoard.noChildren == 0 && mmBoard.diffc == -1 && mmBoard.diffc == -1)
				|| (mmBoard.diffc == -1 && mmBoard.diffc == -1)) {
			// use of the part || (mmBoard.diffc == -1 && mmBoard.diffc == -1)
			// above?????

			/*
			 * System.out.println("pass," + mmBoard.depth + "," +
			 * InfOrNot(v.v));
			 */
			builder.append("pass," + mmBoard.depth + "," + InfOrNot(v.v) + "\n");

		} else {
			/*
			 * System.out.println((char) (dc + 1 + 96) + "" + (dr + 1) + "," +
			 * mmBoard.depth + "," + InfOrNot(v.v));
			 */
			builder.append((char) (dc + 1 + 96) + "" + (dr + 1) + ","
					+ mmBoard.depth + "," + InfOrNot(v.v) + "\n");
		}

		for (board childBoard : mmBoard.children) {

			// System.out.println("\nMax for Enter");

			v = max(v, minValue(childBoard, player1, opponent1));

			// System.out.println("\nMax for");
			if (mmBoard.depth == 0) {
				/*
				 * System.out.println("root," + mmBoard.depth + "," +
				 * InfOrNot(v.v));
				 */
				builder.append("root," + mmBoard.depth + "," + InfOrNot(v.v)
						+ "\n");

			} else if (mmBoard.diffr == -1 && mmBoard.diffc == -1) {
				/*
				 * System.out.println("pass," + mmBoard.depth + "," +
				 * InfOrNot(v.v));
				 */
				builder.append("pass," + mmBoard.depth + "," + InfOrNot(v.v)
						+ "\n");

			} else {
				/*
				 * System.out.println((char) (mmBoard.diffc + 1 + 96) + "" +
				 * (mmBoard.diffr + 1) + "," + mmBoard.depth + "," +
				 * InfOrNot(v.v));
				 */
				builder.append((char) (mmBoard.diffc + 1 + 96) + ""
						+ (mmBoard.diffr + 1) + "," + mmBoard.depth + ","
						+ InfOrNot(v.v) + "\n");

			}
		}
		return v;

	}

	public static board minValue(board mmBoard, char player1, char opponent1) {

		int count = countChildren(mmBoard);
		/*
		 * for (board child : mmBoard.children) { count++; }
		 */
		if (count == 0 || mmBoard.depth == cutoffDepth) {
			evalFunc(mmBoard, player1, opponent1);
			int dc = mmBoard.diffc;
			int dr = mmBoard.diffr;
			mmBoard.v = mmBoard.evalWeight;

			// System.out.println("\nMin Terminal Node");
			if (mmBoard.depth == 0) {
				/*
				 * System.out.println("root," + mmBoard.depth + "," +
				 * mmBoard.evalWeight);
				 */
				builder.append("root," + mmBoard.depth + ","
						+ mmBoard.evalWeight + "\n");

			} else {
				if ((mmBoard.depth < cutoffDepth && mmBoard.diffr == -1 && mmBoard.diffc == -1)
						|| ((mmBoard.depth == cutoffDepth
								&& mmBoard.diffr == -1 && mmBoard.diffc == -1))) {
					/*
					 * System.out.println("pass," + mmBoard.depth + "," +
					 * mmBoard.evalWeight);
					 */
					builder.append("pass," + mmBoard.depth + ","
							+ mmBoard.evalWeight + "\n");

				} else {
					/*
					 * System.out.println((char) (dc + 1 + 96) + "" + (dr + 1) +
					 * "," + mmBoard.depth + "," + mmBoard.evalWeight);
					 */
					builder.append((char) (dc + 1 + 96) + "" + (dr + 1) + ","
							+ mmBoard.depth + "," + mmBoard.evalWeight + "\n");

				}
			}
			return mmBoard;
		}

		board v = new board();
		v.v = Integer.MAX_VALUE;
		int dc = mmBoard.diffc;
		int dr = mmBoard.diffr;

		// System.out.println("\nMin for outside");

		if (mmBoard.depth == 0) {
			/*
			 * System.out.println("root," + mmBoard.depth + "," +
			 * InfOrNot(v.v));
			 */
			builder.append("root," + mmBoard.depth + "," + InfOrNot(v.v) + "\n");

		} else if ((mmBoard.noChildren == 0 && mmBoard.diffc == -1 && mmBoard.diffc == -1)
				|| (mmBoard.diffc == -1 && mmBoard.diffc == -1)) {
			// use of the part || (mmBoard.diffc == -1 && mmBoard.diffc == -1)
			// above?????
			/*
			 * System.out.println("pass," + mmBoard.depth + "," +
			 * InfOrNot(v.v));
			 */
			builder.append("pass," + mmBoard.depth + "," + InfOrNot(v.v) + "\n");

		} else {
			/*
			 * System.out.println((char) (dc + 1 + 96) + "" + (dr + 1) + "," +
			 * mmBoard.depth + "," + InfOrNot(v.v));
			 */
			builder.append((char) (dc + 1 + 96) + "" + (dr + 1) + ","
					+ mmBoard.depth + "," + InfOrNot(v.v) + "\n");

		}

		for (board childBoard : mmBoard.children) {

			// System.out.println("\nMin for Enter");
			v = min(v, maxValue(childBoard, player1, opponent1));

			// System.out.println("\nMin for");

			if (mmBoard.depth == 0) {
				/*
				 * System.out.println("root," + mmBoard.depth + "," +
				 * InfOrNot(v.v));
				 */
				builder.append("root," + mmBoard.depth + "," + InfOrNot(v.v)
						+ "\n");

			} else if (mmBoard.diffr == -1 && mmBoard.diffc == -1) {
				/*
				 * System.out.println("pass," + mmBoard.depth + "," +
				 * InfOrNot(v.v));
				 */
				builder.append("pass," + mmBoard.depth + "," + InfOrNot(v.v)
						+ "\n");

			} else {
				/*
				 * System.out.println((char) (mmBoard.diffc + 1 + 96) + "" +
				 * (mmBoard.diffr + 1) + "," + mmBoard.depth + "," +
				 * InfOrNot(v.v));
				 */
				builder.append((char) (mmBoard.diffc + 1 + 96) + ""
						+ (mmBoard.diffr + 1) + "," + mmBoard.depth + ","
						+ InfOrNot(v.v) + "\n");

			}
		}
		return v;
	}

	public static board max(board v, board w) {
		if (w.v > v.v) {
			return w;
		} else if (w.v < v.v) {
			return v;
		} else {
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					if ((i == w.diffr) && (j == w.diffc)) {
						return w;
					} else if ((i == v.diffr) && (j == v.diffc)) {
						return v;
					}
				}
			}
		}
		return null;
	}

	public static board min(board v, board w) {
		if (w.v < v.v) {
			return w;
		} else if (w.v > v.v) {
			return v;
		} else {
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					if ((i == w.diffr) && (j == w.diffc)) {
						return w;
					} else if ((i == v.diffr) && (j == v.diffc)) {
						return v;
					}
				}
			}
		}
		return null;
	}

	// --------------------------------------------------------------------------
	// minimax algorithm over
	// --------------------------------------------------------------------------

	// --------------------------------------------------------------------------
	// alpha-beta pruning algorithm
	// --------------------------------------------------------------------------

	public static void abPruning(board newBoard, char player1, char opponent1) {

		recursiveGetChildren(newBoard, player1, opponent1);

		maxABpruning(newBoard, player1, opponent1, Integer.MIN_VALUE,
				Integer.MAX_VALUE);

		int countChildren = countChildren(newBoard);
		/*
		 * for (board cBoard : newBoard.children) { countChildren++; }
		 */
		if (countChildren != 0) {

			board evalValB = newBoard.children.get(0);
			double maxEvalVal = newBoard.children.get(0).v;
			for (board childEvalB : newBoard.children) {

				// display(childEvalB);

				if (childEvalB.v > maxEvalVal) {
					maxEvalVal = childEvalB.v;
					evalValB = childEvalB;
				}// else if clause for if eval value is equals. run double for
					// loop
					// to get the cell which comes first
				else if (childEvalB.v == maxEvalVal) {
					outerloop: for (int i = 0; i < 8; i++) {
						for (int j = 0; j < 8; j++) {
							if ((i == childEvalB.diffr)
									&& (j == childEvalB.diffc)) {
								maxEvalVal = childEvalB.v;
								evalValB = childEvalB;
								break outerloop;
							} else if ((i == evalValB.diffr)
									&& (j == evalValB.diffc)) {
								break outerloop;
							}
						}
					}

				}

			}
			// System.out.println("next state");
			// display(evalValB);
			write_data(evalValB);
		} else {
			// System.out.println("next state");
			// display(newBoard);
			write_data(newBoard);
		}

	}

	public static board maxABpruning(board newBoard, char player1,
			char opponent1, int a, int b) {

		int count = countChildren(newBoard);
		/*
		 * for (board child : newBoard.children) { count++; }
		 */
		if (count == 0 || newBoard.depth == cutoffDepth) {
			evalFunc(newBoard, player1, opponent1);
			int dc = newBoard.diffc;
			int dr = newBoard.diffr;
			newBoard.v = newBoard.evalWeight;
			// System.out.println("\nMax Terminal Node");
			if (newBoard.depth == 0) {
				/*
				 * System.out.println("root," + newBoard.depth + "," +
				 * newBoard.evalWeight + "," + InfOrNot(a) + "," + InfOrNot(b));
				 */
				builder.append("root," + newBoard.depth + ","
						+ newBoard.evalWeight + "," + InfOrNot(a) + ","
						+ InfOrNot(b) + "\n");

			} else {

				if ((newBoard.depth < cutoffDepth && newBoard.diffr == -1 && newBoard.diffc == -1)
						|| ((newBoard.depth == cutoffDepth
								&& newBoard.diffr == -1 && newBoard.diffc == -1))) {
					/*
					 * System.out.println("pass," + newBoard.depth + "," +
					 * newBoard.evalWeight + "," + InfOrNot(a) + "," +
					 * InfOrNot(b));
					 */
					builder.append("pass," + newBoard.depth + ","
							+ newBoard.evalWeight + "," + InfOrNot(a) + ","
							+ InfOrNot(b) + "\n");

				} else {
					/*
					 * System.out.println((char) (dc + 1 + 96) + "" + (dr + 1) +
					 * "," + newBoard.depth + "," + newBoard.evalWeight + "," +
					 * InfOrNot(a) + "," + InfOrNot(b));
					 */
					builder.append((char) (dc + 1 + 96) + "" + (dr + 1) + ","
							+ newBoard.depth + "," + newBoard.evalWeight + ","
							+ InfOrNot(a) + "," + InfOrNot(b) + "\n");

				}
			}
			return newBoard;
		}

		board v = new board();
		v.v = Integer.MIN_VALUE;
		int dc = newBoard.diffc;
		int dr = newBoard.diffr;
		// System.out.println("\nMax outside for");

		if (newBoard.depth == 0) {
			/*
			 * System.out.println("root," + newBoard.depth + "," + InfOrNot(v.v)
			 * + "," + InfOrNot(a) + "," + InfOrNot(b));
			 */
			builder.append("root," + newBoard.depth + "," + InfOrNot(v.v) + ","
					+ InfOrNot(a) + "," + InfOrNot(b) + "\n");

		} else if ((newBoard.noChildren == 0 && newBoard.diffc == -1 && newBoard.diffc == -1)
				|| (newBoard.diffc == -1 && newBoard.diffc == -1)) {

			// use of the || (newBoard.diffc == -1 && newBoard.diffc == -1) part
			// above?????
			/*
			 * System.out.println("pass," + newBoard.depth + "," + InfOrNot(v.v)
			 * + "," + InfOrNot(a) + "," + InfOrNot(b));
			 */
			builder.append("pass," + newBoard.depth + "," + InfOrNot(v.v) + ","
					+ InfOrNot(a) + "," + InfOrNot(b) + "\n");

		} else {
			/*
			 * System.out.println((char) (dc + 1 + 96) + "" + (dr + 1) + "," +
			 * newBoard.depth + "," + InfOrNot(v.v) + "," + InfOrNot(a) + "," +
			 * InfOrNot(b));
			 */
			builder.append((char) (dc + 1 + 96) + "" + (dr + 1) + ","
					+ newBoard.depth + "," + InfOrNot(v.v) + "," + InfOrNot(a)
					+ "," + InfOrNot(b) + "\n");

		}

		for (board childBoard : newBoard.children) {
			// childBoard.depth = newBoard.depth + 1;

			// System.out.println("\nMax for");
			// System.out.println((char)(childBoard.diffc+1+96)+""+(childBoard.diffr+1)+","+childBoard.depth+","+(int)v.v);

			v = max(v, minABpruning(childBoard, player1, opponent1, a, b));

			// System.out.println("\nMax for");

			if (v.v >= b) {
				if (newBoard.depth == 0) {
					/*
					 * System.out.println("root," + newBoard.depth + "," +
					 * InfOrNot(v.v) + "," + InfOrNot(a) + "," + InfOrNot(b));
					 */
					builder.append("root," + newBoard.depth + ","
							+ InfOrNot(v.v) + "," + InfOrNot(a) + ","
							+ InfOrNot(b) + "\n");

				} else if (newBoard.diffr == -1 && newBoard.diffc == -1) {

					/*
					 * System.out.println("pass," + newBoard.depth + "," +
					 * InfOrNot(v.v) + "," + InfOrNot(a) + "," + InfOrNot(b));
					 */
					builder.append("pass," + newBoard.depth + ","
							+ InfOrNot(v.v) + "," + InfOrNot(a) + ","
							+ InfOrNot(b) + "\n");

				} else {
					/*
					 * System.out.println((char) (newBoard.diffc + 1 + 96) + ""
					 * + (newBoard.diffr + 1) + "," + newBoard.depth + "," +
					 * InfOrNot(v.v) + "," + InfOrNot(a) + "," + InfOrNot(b));
					 */
					builder.append((char) (newBoard.diffc + 1 + 96) + ""
							+ (newBoard.diffr + 1) + "," + newBoard.depth + ","
							+ InfOrNot(v.v) + "," + InfOrNot(a) + ","
							+ InfOrNot(b) + "\n");

				}
				return v;
			}

			a = a > v.v ? a : v.v;

			if (newBoard.depth == 0) {
				/*
				 * System.out .println("root," + newBoard.depth + "," +
				 * InfOrNot(v.v) + "," + InfOrNot(a) + "," + InfOrNot(b));
				 */
				builder.append("root," + newBoard.depth + "," + InfOrNot(v.v)
						+ "," + InfOrNot(a) + "," + InfOrNot(b) + "\n");

			} else if (newBoard.diffr == -1 && newBoard.diffc == -1) {

				/*
				 * System.out .println("pass," + newBoard.depth + "," +
				 * InfOrNot(v.v) + "," + InfOrNot(a) + "," + InfOrNot(b));
				 */
				builder.append("pass," + newBoard.depth + "," + InfOrNot(v.v)
						+ "," + InfOrNot(a) + "," + InfOrNot(b) + "\n");

			} else {
				/*
				 * System.out .println((char) (newBoard.diffc + 1 + 96) + "" +
				 * (newBoard.diffr + 1) + "," + newBoard.depth + "," +
				 * InfOrNot(v.v) + "," + InfOrNot(a) + "," + InfOrNot(b));
				 */
				builder.append((char) (newBoard.diffc + 1 + 96) + ""
						+ (newBoard.diffr + 1) + "," + newBoard.depth + ","
						+ InfOrNot(v.v) + "," + InfOrNot(a) + "," + InfOrNot(b)
						+ "\n");

			}
			// System.out.println("A, B max:"+ a+","+b);
		}
		return v;

	}

	public static board minABpruning(board newBoard, char player1,
			char opponent1, int a, int b) {

		int count = countChildren(newBoard);
		/*
		 * for (board child : newBoard.children) { count++; }
		 */
		if (count == 0 || newBoard.depth == cutoffDepth) {
			evalFunc(newBoard, player1, opponent1);
			int dc = newBoard.diffc;
			int dr = newBoard.diffr;
			newBoard.v = newBoard.evalWeight;

			// System.out.println("\nMin Terminal Node");
			if (newBoard.depth == 0) {
				/*
				 * System.out.println("root," + newBoard.depth + "," +
				 * newBoard.evalWeight + "," + InfOrNot(a) + "," + InfOrNot(b));
				 */
				builder.append("root," + newBoard.depth + ","
						+ newBoard.evalWeight + "," + InfOrNot(a) + ","
						+ InfOrNot(b) + "\n");

			} else {
				// System.out.println((char) (dc + 1 + 96) + "" + (dr + 1));
				if ((newBoard.depth < cutoffDepth && newBoard.diffr == -1 && newBoard.diffc == -1)
						|| ((newBoard.depth == cutoffDepth
								&& newBoard.diffr == -1 && newBoard.diffc == -1))) {
					/*
					 * System.out.println("pass," + newBoard.depth + "," +
					 * newBoard.evalWeight + "," + InfOrNot(a) + "," +
					 * InfOrNot(b));
					 */
					builder.append("pass," + newBoard.depth + ","
							+ newBoard.evalWeight + "," + InfOrNot(a) + ","
							+ InfOrNot(b) + "\n");

				} else {
					/*
					 * System.out.println((char) (dc + 1 + 96) + "" + (dr + 1) +
					 * "," + newBoard.depth + "," + newBoard.evalWeight + "," +
					 * InfOrNot(a) + "," + InfOrNot(b));
					 */
					builder.append((char) (dc + 1 + 96) + "" + (dr + 1) + ","
							+ newBoard.depth + "," + newBoard.evalWeight + ","
							+ InfOrNot(a) + "," + InfOrNot(b) + "\n");

				}
			}
			return newBoard;
		}

		board v = new board();
		v.v = Integer.MAX_VALUE;
		int dc = newBoard.diffc;
		int dr = newBoard.diffr;

		// System.out.println("\nMin for outside");
		if (newBoard.depth == 0) {
			/*
			 * System.out.println("root," + newBoard.depth + "," + InfOrNot(v.v)
			 * + "," + InfOrNot(a) + "," + InfOrNot(b));
			 */
			builder.append("root," + newBoard.depth + "," + InfOrNot(v.v) + ","
					+ InfOrNot(a) + "," + InfOrNot(b) + "\n");

		} else if ((newBoard.noChildren == 0 && newBoard.diffc == -1 && newBoard.diffc == -1)
				|| (newBoard.diffc == -1 && newBoard.diffc == -1)) {
			// use of the || (newBoard.diffc == -1 && newBoard.diffc == -1) part
			// above?????
			/*
			 * System.out.println("pass," + newBoard.depth + "," + InfOrNot(v.v)
			 * + "," + InfOrNot(a) + "," + InfOrNot(b));
			 */
			builder.append("pass," + newBoard.depth + "," + InfOrNot(v.v) + ","
					+ InfOrNot(a) + "," + InfOrNot(b) + "\n");

		} else {
			/*
			 * System.out.println((char) (dc + 1 + 96) + "" + (dr + 1) + "," +
			 * newBoard.depth + "," + InfOrNot(v.v) + "," + InfOrNot(a) + "," +
			 * InfOrNot(b));
			 */
			builder.append((char) (dc + 1 + 96) + "" + (dr + 1) + ","
					+ newBoard.depth + "," + InfOrNot(v.v) + "," + InfOrNot(a)
					+ "," + InfOrNot(b) + "\n");

		}

		for (board childBoard : newBoard.children) {
			// childBoard.depth = newBoard.depth + 1;

			// System.out.println("\nMin for");
			// System.out.println((char)(childBoard.diffc+1+96)+""+(childBoard.diffr+1)+","+childBoard.depth+","+(int)v.v);
			v = min(v, maxABpruning(childBoard, player1, opponent1, a, b));

			// System.out.println("\nMin for");

			if (v.v <= a) {
				if (newBoard.depth == 0) {
					/*
					 * System.out.println("root," + newBoard.depth + "," +
					 * InfOrNot(v.v) + "," + InfOrNot(a) + "," + InfOrNot(b));
					 */
					builder.append("root," + newBoard.depth + ","
							+ InfOrNot(v.v) + "," + InfOrNot(a) + ","
							+ InfOrNot(b) + "\n");

				} else if (newBoard.diffr == -1 && newBoard.diffc == -1) {

					/*
					 * System.out.println("pass," + newBoard.depth + "," +
					 * InfOrNot(v.v) + "," + InfOrNot(a) + "," + InfOrNot(b));
					 */
					builder.append("pass," + newBoard.depth + ","
							+ InfOrNot(v.v) + "," + InfOrNot(a) + ","
							+ InfOrNot(b) + "\n");

				} else {
					/*
					 * System.out.println((char) (newBoard.diffc + 1 + 96) + ""
					 * + (newBoard.diffr + 1) + "," + newBoard.depth + "," +
					 * InfOrNot(v.v) + "," + InfOrNot(a) + "," + InfOrNot(b));
					 */
					builder.append((char) (newBoard.diffc + 1 + 96) + ""
							+ (newBoard.diffr + 1) + "," + newBoard.depth + ","
							+ InfOrNot(v.v) + "," + InfOrNot(a) + ","
							+ InfOrNot(b) + "\n");

				}
				return v;
			}

			b = b > v.v ? v.v : b;
			if (newBoard.depth == 0) {
				/*
				 * System.out .println("root," + newBoard.depth + "," +
				 * InfOrNot(v.v) + "," + InfOrNot(a) + "," + InfOrNot(b));
				 */
				builder.append("root," + newBoard.depth + "," + InfOrNot(v.v)
						+ "," + InfOrNot(a) + "," + InfOrNot(b) + "\n");

			} else if (newBoard.diffr == -1 && newBoard.diffc == -1) {

				/*
				 * System.out .println("pass," + newBoard.depth + "," +
				 * InfOrNot(v.v) + "," + InfOrNot(a) + "," + InfOrNot(b));
				 */
				builder.append("pass," + newBoard.depth + "," + InfOrNot(v.v)
						+ "," + InfOrNot(a) + "," + InfOrNot(b) + "\n");

			} else {
				/*
				 * System.out .println((char) (newBoard.diffc + 1 + 96) + "" +
				 * (newBoard.diffr + 1) + "," + newBoard.depth + "," +
				 * InfOrNot(v.v) + "," + InfOrNot(a) + "," + InfOrNot(b));
				 */
				builder.append((char) (newBoard.diffc + 1 + 96) + ""
						+ (newBoard.diffr + 1) + "," + newBoard.depth + ","
						+ InfOrNot(v.v) + "," + InfOrNot(a) + "," + InfOrNot(b)
						+ "\n");

			}
			// System.out.println("A, B min:"+ a+","+b);

		}
		return v;

	}

	// --------------------------------------------------------------------------
	// alpha-beta pruning algorithm over
	// --------------------------------------------------------------------------

	/*
	 * public static void positionalOrder(board newBoard) {
	 * 
	 * // ArrayList<board> child = new ArrayList<board>();
	 * 
	 * board temp = null; int countChildren = countChildren(newBoard); //for
	 * (board childBoard : newBoard.children) { // countChildren++; //} int
	 * count = 0; for (int i = 0; i < 8; i++) { for (int j = 0; j < 8; j++) {
	 * for (board childBoard : newBoard.children) { if (i == childBoard.diffr &&
	 * j == childBoard.diffc && countChildren != 0) { temp =
	 * newBoard.children.remove(count); newBoard.children.add(temp);
	 * countChildren--; } count++; } count = 0; } }
	 * 
	 * // int count1=0; // for(board childB : child) { // temp =
	 * child.remove(count1); // newBoard.children.add(temp); // count1++; // }
	 * 
	 * }
	 */

	public static String InfOrNot(int x) {
		if (x == Integer.MAX_VALUE) {
			return "Infinity";
		} else if (x == Integer.MIN_VALUE) {
			return "-Infinity";
		} else {
			return Integer.toString(x);
		}

	}

	public static int checkInfinity(int x) {
		if (x == Integer.MAX_VALUE || x == Integer.MIN_VALUE) {
			return 1;
		}
		return 0;
	}

	public static int countChildren(board newBoard) {
		int countChildren = 0;
		for (board childB : newBoard.children) {
			countChildren++;
		}
		return countChildren;
	}

	public static int allSameDisk(board newBoard) {
		int countX = 0;
		int countO = 0;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (newBoard.bNode[i][j].color == 'X') {
					countX++;
				} else if (newBoard.bNode[i][j].color == 'O') {
					countO++;
				}
			}
		}

		if (countX == 0 || countO == 0) {
			return 1;
		}
		return 0;
	}

	static board noCpBoard = null;
	static board noCcBoard = null;

	public static void recursiveGetChildren(board newBoard, char player1,
			char opponent1) {
		int depth = newBoard.depth;

		if (allSameDisk(newBoard) == 1) {
			return;
		}

		if (depth == cutoffDepth) {
			return;
		} else if (depth % 2 == 0) {

			getChildren(newBoard, player1, opponent1);
			// System.out.println("Depth:" + depth);

			if ((noCpBoard != null && noCcBoard != null)
					&& (noCpBoard.noChildren == 0 && noCcBoard.noChildren == 0)) {
				return;
			}

			int countChildren = countChildren(newBoard);
			/*
			 * for (board childB : newBoard.children) { countChildren++; }
			 */
			if (countChildren == 0) {
				return;
			}
			noCpBoard = newBoard;
			for (board childB : newBoard.children) {
				// display(childB);
				noCcBoard = childB;
				if (allSameDisk(childB) == 1) {
					return;
				}
				recursiveGetChildren(childB, player1, opponent1);
			}

		} else if (depth % 2 == 1) {

			getChildren(newBoard, opponent1, player1);

			if (noCpBoard.noChildren == 0 && noCcBoard.noChildren == 0) {
				return;
			}
			// System.out.println("Depth:" + depth);
			int countChildren = countChildren(newBoard);
			/*
			 * for (board childB : newBoard.children) { countChildren++; }
			 */
			if (countChildren == 0) {
				return;
			}
			noCpBoard = newBoard;
			for (board childB : newBoard.children) {
				// display(childB);
				noCcBoard = childB;
				if (allSameDisk(childB) == 1) {
					return;
				}
				recursiveGetChildren(childB, player1, opponent1);
			}

		}
		return;
	}

	public static void getChildren(board newNode, char player1, char opponent1) {

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {

				if (newNode.bNode[i][j].color == player1) {
					boardCell newCell = newNode.bNode[i][j];

					// cells up
					boardCell adjCellup = up(newCell, newNode);
					while (adjCellup != null && adjCellup.color == opponent1) {
						// go in each direction until blank or end and make node
						// for that move by player1
						adjCellup = up(adjCellup, newNode);

						// function - send parameters newNode i j adjr adjc
						if (adjCellup != null && adjCellup.color == '*') {
							makeNewChildBoard(newNode, i, j, adjCellup.r,
									adjCellup.c, player1, opponent1);
						}
					}

					// cells down
					boardCell adjCelldown = down(newCell, newNode);
					while (adjCelldown != null
							&& adjCelldown.color == opponent1) {
						// go in each direction until blank or end and make node
						// for that move by player1
						adjCelldown = down(adjCelldown, newNode);

						// function - send parameters newNode i j adjr adjc
						if (adjCelldown != null && adjCelldown.color == '*') {
							makeNewChildBoard(newNode, i, j, adjCelldown.r,
									adjCelldown.c, player1, opponent1);
						}
					}
					// cells left
					boardCell adjCellleft = left(newCell, newNode);
					while (adjCellleft != null
							&& adjCellleft.color == opponent1) {
						// go in each direction until blank or end and make node
						// for that move by player1
						adjCellleft = left(adjCellleft, newNode);

						// function - send parameters newNode i j adjr adjc
						if (adjCellleft != null && adjCellleft.color == '*') {
							makeNewChildBoard(newNode, i, j, adjCellleft.r,
									adjCellleft.c, player1, opponent1);
						}
					}
					// cells right
					boardCell adjCellright = right(newCell, newNode);
					while (adjCellright != null
							&& adjCellright.color == opponent1) {
						// go in each direction until blank or end and make node
						// for that move by player1
						adjCellright = right(adjCellright, newNode);

						// function - send parameters newNode i j adjr adjc
						if (adjCellright != null && adjCellright.color == '*') {
							makeNewChildBoard(newNode, i, j, adjCellright.r,
									adjCellright.c, player1, opponent1);
						}
					}

					// cells dialeftup
					boardCell adjCellDLup = diagonalLeftUp(newCell, newNode);
					while (adjCellDLup != null
							&& adjCellDLup.color == opponent1) {
						// go in each direction until blank or end and make node
						// for that move by player1
						adjCellDLup = diagonalLeftUp(adjCellDLup, newNode);

						// function - send parameters newNode i j adjr adjc
						if (adjCellDLup != null && adjCellDLup.color == '*') {
							makeNewChildBoard(newNode, i, j, adjCellDLup.r,
									adjCellDLup.c, player1, opponent1);
						}
					}

					// cells diarightup
					boardCell adjCellDRup = diagonalRightUp(newCell, newNode);
					while (adjCellDRup != null
							&& adjCellDRup.color == opponent1) {
						// go in each direction until blank or end and make node
						// for that move by player1
						adjCellDRup = diagonalRightUp(adjCellDRup, newNode);

						// function - send parameters newNode i j adjr adjc
						if (adjCellDRup != null && adjCellDRup.color == '*') {
							makeNewChildBoard(newNode, i, j, adjCellDRup.r,
									adjCellDRup.c, player1, opponent1);
						}
					}

					// cells dialeftdown
					boardCell adjCellDLdown = diagonalLeftDown(newCell, newNode);
					while (adjCellDLdown != null
							&& adjCellDLdown.color == opponent1) {
						// go in each direction until blank or end and make node
						// for that move by player1
						adjCellDLdown = diagonalLeftDown(adjCellDLdown, newNode);

						// function - send parameters newNode i j adjr adjc
						if (adjCellDLdown != null && adjCellDLdown.color == '*') {
							makeNewChildBoard(newNode, i, j, adjCellDLdown.r,
									adjCellDLdown.c, player1, opponent1);
						}
					}

					// cells diarightdown
					boardCell adjCellDRdown = diagonalRightDown(newCell,
							newNode);
					while (adjCellDRdown != null
							&& adjCellDRdown.color == opponent1) {
						// go in each direction until blank or end and make node
						// for that move by player1
						adjCellDRdown = diagonalRightDown(adjCellDRdown,
								newNode);

						// function - send parameters newNode i j adjr adjc
						if (adjCellDRdown != null && adjCellDRdown.color == '*') {
							makeNewChildBoard(newNode, i, j, adjCellDRdown.r,
									adjCellDRdown.c, player1, opponent1);
						}
					}
				}
			}
		}
		// System.out.println("Children or Not:"+newNode.children);
		int countChildren = countChildren(newNode);
		/*
		 * for (board childB : newNode.children) { countChildren++; }
		 */

		if (countChildren == 0) {
			// System.out.println("No children");
			makeNewChildBoard(newNode, -1, -1, -1, -1, player1, opponent1);
			newNode.noChildren = 0;
		}
		// System.out.println("Children or Not"+newNode.children);
		Collections.sort(newNode.children, new boardComparator());

	}

	// makes the board with the new move by player1
	public static void makeNewChildBoard(board newNode, int parentR,
			int parentC, int adjr, int adjc, char player1, char opponent1) {
		board childUpBoard = new board();

		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				boardCell childUpBoardCell = new boardCell();
				childUpBoardCell.r = newNode.bNode[x][y].r;
				childUpBoardCell.c = newNode.bNode[x][y].c;
				childUpBoardCell.color = newNode.bNode[x][y].color;
				childUpBoardCell.weight = newNode.bNode[x][y].weight;

				childUpBoard.bNode[x][y] = childUpBoardCell;
				// System.out.print(childUpBoard.bNode[x][y].color);
			}
			// System.out.print("\n");
		}

		// System.out.println("i: "+adjr+ " j: "+adjc);
		if (adjr != -1 && adjc != -1) {
			childUpBoard.bNode[adjr][adjc].color = player1;
		}
		childUpBoard.diffr = adjr;
		childUpBoard.diffc = adjc;
		childUpBoard.depth = newNode.depth + 1;

		// ---------------------switching opponent1 color to player1 color
		// between
		// two player1 color cells
		if (adjr != -1 && adjc != -1) {

			int i = adjr;
			int j = adjc;
			if (childUpBoard.bNode[i][j].color == player1) {
				boardCell newCell = childUpBoard.bNode[i][j];

				// cells up
				boardCell adjCellup = up(newCell, childUpBoard);
				// System.out.println("new cell color: "+newCell.color +
				// newCell.r + newCell.c);
				while (adjCellup != null && adjCellup.color == opponent1) {

					adjCellup = up(adjCellup, childUpBoard);

					// System.out.println("Down color: "+adjCellup.color
					// +
					// adjCellup.r + adjCellup.c);
					if (adjCellup != null && adjCellup.color == player1) {
						int adjCellRow = adjCellup.r;
						for (int xyz = i; xyz > adjCellRow; --xyz) {
							// System.out.println("UP");
							childUpBoard.bNode[xyz][j].color = player1;
						}
					}
				}

				// cells down
				boardCell adjCelldown = down(newCell, childUpBoard);
				while (adjCelldown != null && adjCelldown.color == opponent1) {

					adjCelldown = down(adjCelldown, childUpBoard);

					if (adjCelldown != null && adjCelldown.color == player1) {
						int adjCellRow = adjCelldown.r;
						for (int xyz = i; xyz < adjCellRow; ++xyz) {
							// System.out.println("Down");
							childUpBoard.bNode[xyz][j].color = player1;
						}
					}
				}
				// cells left
				boardCell adjCellleft = left(newCell, childUpBoard);
				while (adjCellleft != null && adjCellleft.color == opponent1) {

					adjCellleft = left(adjCellleft, childUpBoard);

					if (adjCellleft != null && adjCellleft.color == player1) {
						int adjCellCol = adjCellleft.c;
						for (int xyz = j; xyz > adjCellCol; --xyz) {
							// System.out.println("left");
							childUpBoard.bNode[i][xyz].color = player1;
						}
					}
				}
				// cells right
				boardCell adjCellright = right(newCell, childUpBoard);
				while (adjCellright != null && adjCellright.color == opponent1) {

					adjCellright = right(adjCellright, childUpBoard);

					if (adjCellright != null && adjCellright.color == player1) {
						int adjCellCol = adjCellright.c;
						for (int xyz = j; xyz < adjCellCol; ++xyz) {
							// System.out.println("right");
							childUpBoard.bNode[i][xyz].color = player1;
						}
					}
				}

				// cells dialeftup
				boardCell adjCellDLup = diagonalLeftUp(newCell, childUpBoard);
				while (adjCellDLup != null && adjCellDLup.color == opponent1) {

					adjCellDLup = diagonalLeftUp(adjCellDLup, childUpBoard);

					if (adjCellDLup != null && adjCellDLup.color == player1) {
						// int adjCellRow = adjCellDLup.r;
						int adjCellCol = adjCellDLup.c;
						for (int abc = i, xyz = j; xyz > adjCellCol; --abc, --xyz) {
							// System.out.println("dialeft up");
							childUpBoard.bNode[abc][xyz].color = player1;
						}
					}
				}

				// cells diarightup
				boardCell adjCellDRup = diagonalRightUp(newCell, childUpBoard);
				while (adjCellDRup != null && adjCellDRup.color == opponent1) {

					adjCellDRup = diagonalRightUp(adjCellDRup, childUpBoard);

					if (adjCellDRup != null && adjCellDRup.color == player1) {
						// int adjCellRow = adjCellDRup.r;
						int adjCellCol = adjCellDRup.c;
						for (int abc = i, xyz = j; xyz < adjCellCol; --abc, ++xyz) {
							// System.out.println("dia right up");
							childUpBoard.bNode[abc][xyz].color = player1;
						}
					}
				}

				// cells dialeftdown
				boardCell adjCellDLdown = diagonalLeftDown(newCell,
						childUpBoard);
				while (adjCellDLdown != null
						&& adjCellDLdown.color == opponent1) {

					adjCellDLdown = diagonalLeftDown(adjCellDLdown,
							childUpBoard);

					if (adjCellDLdown != null && adjCellDLdown.color == player1) {
						// int adjCellRow = adjCellDLdown.r;
						int adjCellCol = adjCellDLdown.c;
						for (int abc = i, xyz = j; xyz > adjCellCol; ++abc, --xyz) {
							// System.out.println("dia left down");
							childUpBoard.bNode[abc][xyz].color = player1;
						}
					}
				}

				// cells diarightdown
				boardCell adjCellDRdown = diagonalRightDown(newCell,
						childUpBoard);
				while (adjCellDRdown != null
						&& adjCellDRdown.color == opponent1) {

					adjCellDRdown = diagonalRightDown(adjCellDRdown,
							childUpBoard);

					if (adjCellDRdown != null && adjCellDRdown.color == player1) {
						// int adjCellRow = adjCellDRdown.r;
						int adjCellCol = adjCellDRdown.c;
						for (int abc = i, xyz = j; xyz < adjCellCol; ++abc, ++xyz) {
							// System.out.println("dia right down");
							childUpBoard.bNode[abc][xyz].color = player1;
						}
					}
				}
			}
		}
		// -------------------------------------------------------------------------------------------

		// display(childUpBoard);

		// adding children board to parent
		newNode.children.add(childUpBoard);
	}

	// Display board
	public static void display(board B) {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				System.out.print(B.bNode[i][j].color);
				// builder.append(B.bNode[i][j].color);
			}
			System.out.print("\n");
			// builder.append("\n");
		}
		// System.out.print("Eval Func: " + B.evalWeight + "\n");
		System.out.print("\n");
	}

	// ----------------------------------------------------------------------------------------------
	// boardCell movements
	// ---------------------------------------------------------------------------------------------
	public static boardCell up(boardCell bCell, board bNode) {
		int row = bCell.r;
		int col = bCell.c;
		if (row == 0) {
			return null;
		}
		return bNode.bNode[row - 1][col];
	}

	public static boardCell down(boardCell bCell, board bNode) {
		int row = bCell.r;
		int col = bCell.c;
		if (row == 7) {
			return null;
		}
		return bNode.bNode[row + 1][col];
	}

	public static boardCell left(boardCell bCell, board bNode) {
		int row = bCell.r;
		int col = bCell.c;
		if (col == 0) {
			return null;
		}
		return bNode.bNode[row][col - 1];
	}

	public static boardCell right(boardCell bCell, board bNode) {
		int row = bCell.r;
		int col = bCell.c;
		if (col == 7) {
			return null;
		}
		return bNode.bNode[row][col + 1];
	}

	public static boardCell diagonalLeftUp(boardCell bCell, board bNode) {
		int row = bCell.r;
		int col = bCell.c;
		if (row == 0 || col == 0) {
			return null;
		}
		return bNode.bNode[row - 1][col - 1];
	}

	public static boardCell diagonalRightUp(boardCell bCell, board bNode) {
		int row = bCell.r;
		int col = bCell.c;
		if (row == 0 || col == 7) {
			return null;
		}
		return bNode.bNode[row - 1][col + 1];
	}

	public static boardCell diagonalLeftDown(boardCell bCell, board bNode) {
		int row = bCell.r;
		int col = bCell.c;
		if (row == 7 || col == 0) {
			return null;
		}
		return bNode.bNode[row + 1][col - 1];
	}

	public static boardCell diagonalRightDown(boardCell bCell, board bNode) {
		int row = bCell.r;
		int col = bCell.c;
		if (row == 7 || col == 7) {
			return null;
		}
		return bNode.bNode[row + 1][col + 1];
	}

	// -----------------------------------------------------------------------------------------

}

class boardCell {
	int weight;
	// int colorNum;
	char color;
	int r;
	int c;
}

class board {

	boardCell[][] bNode;
	int evalWeight;
	int diffr;
	int diffc;
	int depth;
	int v;

	int noChildren;
	ArrayList<board> children = new ArrayList<board>();

	board() {
		bNode = new boardCell[8][8];

		diffr = -1;
		diffc = -1;
		noChildren = 1;
		/*
		 * for(int i=0;i<8;i++) { for(int j=0;j<8;j++) { bNode[i][j].color =
		 * 'x'; bNode[i][j].weight = 0; } }
		 */
	}
}

class boardComparator implements Comparator<board> {
	public int compare(board other1, board other2) {
		int v = other1.diffr - other2.diffr;

		if (v == 0) {
			return other1.diffc - other2.diffc;
		} else
			return v;
	}

}

/*
 * class minimaxValueBoard { double v; board mBoard;
 * ArrayList<minimaxValueBoard> children = new ArrayList<minimaxValueBoard>();
 * 
 * minimaxValueBoard() { mBoard = new board(); v = Double.NEGATIVE_INFINITY; } }
 */
class graph {
	ArrayList<board> nodeList = new ArrayList<board>();
}
