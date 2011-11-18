import java.io.*;
import java.util.*;

public class FloodIt
{
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

	private static Character completable(Map<Character,Move> frontier, Map<Character,Integer> remaining)
	{
		for(Map.Entry<Character,Move> entry : frontier.entrySet())
			if(remaining.get(entry.getKey()).equals(entry.getValue().getArea()))
				return entry.getKey();
		return null;
	}

	private static void search(StringBuilder path, Map<Character,Move> base, char mergeColor, Map<Character,Integer> remaining, Set<Node> visited)
	{
		path.append(mergeColor);
		Move merges = base.get(mergeColor);

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

		if(frontier.isEmpty())
		{
			System.out.println(path);
			path.setLength(path.length() - 1);
			return;
		}

		remaining.put(mergeColor, remaining.get(mergeColor) - merges.getArea());
		visited.addAll(merges);
		Character completable = completable(frontier, remaining);
		if(completable != null)
			search(path, frontier, completable, remaining, visited);
		else
			for(Character color : frontier.keySet())
				search(path, frontier, color, remaining, visited);
		visited.removeAll(merges);
		remaining.put(mergeColor, remaining.get(mergeColor) + merges.getArea());

		path.setLength(path.length() - 1);
	}

	private static void totalArea(Map<Character,Integer> total, Set<Node> visited, Node root)
	{
		if(!visited.add(root))
			return;
		Integer count = total.get(root.color);
		count = (count == null) ? root.getArea() : count + root.getArea();
		total.put(root.color, count);
		for(Node n : root)
			totalArea(total, visited, n);
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
		printGraph(0, root, new HashSet<Node>());

		Map<Character,Integer> totalArea = new HashMap<Character,Integer>();
		totalArea(totalArea, new HashSet<Node>(), root);

		Move rootMove = new Move();
		rootMove.add(root);
		search(new StringBuilder(), Collections.singletonMap(root.color, rootMove), root.color, totalArea, new HashSet<Node>());
	}
}
