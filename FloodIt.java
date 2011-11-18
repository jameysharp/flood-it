import java.io.*;
import java.util.*;

public class FloodIt
{
	private static final boolean DEBUG = false;

	private final StringBuilder path = new StringBuilder();
	private final Set<Node> visited = new HashSet<Node>();
	private final Map<Character,Integer> remaining = new HashMap<Character,Integer>();

	// The board must be clear within 25 moves, so there's no point
	// searching past that depth. However, this solver counts the
	// root node as a move, and this bound needs to be set higher
	// than the highest depth we want to consider; so add 2.
	private int bestDepth = 25 + 2;

	private FloodIt(Node root)
	{
		totalArea(root);
		visited.clear();
	}

	private static class Move extends HashSet<Node>
	{
		private int area = 0;

		public Move()
		{
		}

		public Move(Move base)
		{
			super(base);
			area = base.area;
		}

		@Override
		public boolean add(Node node)
		{
			boolean ret = super.add(node);
			if(ret)
				area += node.getArea();
			return ret;
		}

		public int getArea()
		{
			return area;
		}
	}

	private Character completable(Map<Character,Move> frontier)
	{
		for(Map.Entry<Character,Move> entry : frontier.entrySet())
			if(remaining.get(entry.getKey()).equals(entry.getValue().getArea()))
				return entry.getKey();
		return null;
	}

	private void search(Map<Character,Move> base, char mergeColor)
	{
		path.append(mergeColor);
		Move merges = base.get(mergeColor);
		remaining.put(mergeColor, remaining.get(mergeColor) - merges.getArea());

		int colors = 0;
		for(int count : remaining.values())
			if(count > 0)
				++colors;

		if(colors == 0)
		{
			System.out.println(path);
			bestDepth = path.length();
		}
		else if(path.length() + colors >= bestDepth)
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
					if(!visited.contains(next))
					{
						Move nodes = frontier.get(next.color);
						if(nodes == null)
						{
							nodes = new Move();
							frontier.put(next.color, nodes);
						}
						nodes.add(next);
					}

			visited.addAll(merges);
			Character completable = completable(frontier);
			if(completable != null)
				search(frontier, completable);
			else
				for(Character color : frontier.keySet())
					search(frontier, color);
			visited.removeAll(merges);
		}

		remaining.put(mergeColor, remaining.get(mergeColor) + merges.getArea());
		path.setLength(path.length() - 1);
	}

	private void totalArea(Node root)
	{
		if(!visited.add(root))
			return;
		Integer count = remaining.get(root.color);
		count = (count == null) ? root.getArea() : count + root.getArea();
		remaining.put(root.color, count);
		for(Node n : root)
			totalArea(n);
	}

	private static void printGraph(int depth, Node root, Set<Node> visited)
	{
		if(visited.contains(root))
			return;
		visited.add(root);
		System.out.print(root.id + ":\t");
		for(int i = 0; i < depth; ++i)
			System.out.print("  ");
		System.out.print(root.color + " (" + root.getArea() + ") ->");
		for(Node n : root)
			System.out.print(" " + n.id);
		System.out.println();
		for(Node n : root)
			printGraph(depth + 1, n, visited);
	}

	public static void main(String[] args)
		throws IOException
	{
		Node root = Node.readBoard(System.in);
		if(DEBUG)
			printGraph(0, root, new HashSet<Node>());

		Move rootMove = new Move();
		rootMove.add(root);
		new FloodIt(root).search(Collections.singletonMap(root.color, rootMove), root.color);
	}
}
