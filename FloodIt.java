import java.io.*;
import java.util.*;

public class FloodIt
{
	private static final boolean DEBUG = false;
	private static final int TABLE_SIZE = 1 << 19;
	static {
		assert TABLE_SIZE == (TABLE_SIZE & -TABLE_SIZE);
	}

	private final StringBuilder path = new StringBuilder();
	private final BitSet visited = new BitSet();
	private final Map<Character,Integer> remaining = new HashMap<Character,Integer>();

	private final BitSet[] transposePosition = new BitSet[TABLE_SIZE];
	private final int[] transposeValue = new int[TABLE_SIZE];

	// The board must be clear within 25 moves, so there's no point
	// searching past that depth. However, this solver counts the
	// root node as a move, and this bound needs to be set higher
	// than the highest depth we want to consider; so add 2.
	private int bestDepth = 25 + 2;

	public FloodIt(Node root)
	{
		if(DEBUG)
			root.printGraph();
		totalArea(root);
		visited.clear();
		Move rootMove = new Move(root.color);
		rootMove.add(root);
		search(Collections.singletonMap(root.color, rootMove), root.color);
	}

	private static class Move extends HashSet<Node>
	{
		public final char color;
		private int area = 0;

		public Move(char color)
		{
			this.color = color;
		}

		public Move(Move base)
		{
			color = base.color;
			addAll(base);
			assert area == base.area;
		}

		@Override
		public boolean add(Node node)
		{
			assert node.color == color;
			boolean ret = super.add(node);
			if(ret)
				area += node.getArea();
			return ret;
		}

		public int getArea()
		{
			return area;
		}

		public String toString()
		{
			return color + "(" + area + ")";
		}
	}

	private Character completable(Map<Character,Move> frontier)
	{
		for(Map.Entry<Character,Move> entry : frontier.entrySet())
			if(remaining.get(entry.getKey()).equals(entry.getValue().getArea()))
				return entry.getKey();
		return null;
	}

	/** Sort moves in descending order by area. */
	private static final Comparator<Move> bestMove = new Comparator<Move>() {
		public int compare(Move m1, Move m2)
		{
			return m2.getArea() - m1.getArea();
		}
	};

	private void toggleVisited(Iterable<Node> nodes)
	{
		for(Node n : nodes)
			visited.flip(n.id);
	}

	private int search(Map<Character,Move> base, char mergeColor)
	{
		path.append(mergeColor);
		Move merges = base.get(mergeColor);
		remaining.put(mergeColor, remaining.get(mergeColor) - merges.getArea());
		toggleVisited(merges);

		int colors = 0;
		for(int count : remaining.values())
			if(count > 0)
				++colors;

		int heuristic = colors;
		int hash = visited.hashCode() & (TABLE_SIZE - 1);
		if(transposePosition[hash] != null && transposePosition[hash].equals(visited))
			heuristic = transposeValue[hash];

		int bestSubtree = heuristic;

		if(colors == 0)
		{
			System.out.println(path);
			bestDepth = path.length();
		}
		else if(path.length() + heuristic >= bestDepth)
		{
			if(DEBUG)
				System.out.println(path + "...");
		}
		else
		{
			Map<Character,Move> frontier = new HashMap<Character,Move>();
			for(Map.Entry<Character,Move> entry : base.entrySet())
				if(entry.getKey() != mergeColor)
					frontier.put(entry.getKey(), new Move(entry.getValue()));
			for(Node merge : merges)
				for(Node next : merge)
					if(!visited.get(next.id))
					{
						Move nodes = frontier.get(next.color);
						if(nodes == null)
						{
							nodes = new Move(next.color);
							frontier.put(next.color, nodes);
						}
						nodes.add(next);
					}

			Character completable = completable(frontier);
			if(completable != null)
				bestSubtree = search(frontier, completable) + 1;
			else
			{
				Move[] moves = frontier.values().toArray(new Move[frontier.size()]);
				Arrays.sort(moves, bestMove);
				bestSubtree = Integer.MAX_VALUE;
				for(Move move : moves)
				{
					int subtree = search(frontier, move.color);
					if(subtree < bestSubtree)
						bestSubtree = subtree;
				}
				++bestSubtree;

				if(transposePosition[hash] == null || transposePosition[hash].size() >= visited.size())
				{
					transposePosition[hash] = (BitSet) visited.clone();
					transposeValue[hash] = bestSubtree;
				}
			}
		}

		toggleVisited(merges);
		remaining.put(mergeColor, remaining.get(mergeColor) + merges.getArea());
		path.setLength(path.length() - 1);
		return bestSubtree;
	}

	private void totalArea(Node root)
	{
		if(visited.get(root.id))
			return;
		visited.set(root.id);
		Integer count = remaining.get(root.color);
		count = (count == null) ? root.getArea() : count + root.getArea();
		remaining.put(root.color, count);
		for(Node n : root)
			totalArea(n);
	}

	public static void main(String[] args)
		throws IOException
	{
		new FloodIt(Node.readBoard(System.in));
	}
}
