import java.io.*;
import java.util.*;

public class FloodIt
{
	private static void search(StringBuilder path, Map<Character,Set<Node>> base, char mergeColor, Set<Node> visited)
	{
		Set<Node> merges = base.get(mergeColor);

		Map<Character,Set<Node>> frontier = new HashMap<Character,Set<Node>>();
		for(Map.Entry<Character,Set<Node>> entry : base.entrySet())
			if(entry.getKey() != mergeColor)
				frontier.put(entry.getKey(), new HashSet<Node>(entry.getValue()));
		for(Node merge : merges)
			for(Node next : merge)
				if(!visited.contains(next))
				{
					Set<Node> nodes = frontier.get(next.color);
					if(nodes == null)
					{
						nodes = new HashSet<Node>();
						frontier.put(next.color, nodes);
					}
					nodes.add(next);
				}

		if(frontier.isEmpty())
		{
			System.out.println(path);
			return;
		}

		visited.addAll(merges);
		for(Character color : frontier.keySet())
		{
			path.append((char) color);
			search(path, frontier, color, visited);
			path.setLength(path.length() - 1);
		}
		visited.removeAll(merges);
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

		search(new StringBuilder(), Collections.singletonMap(root.color, Collections.singleton(root)), root.color, new HashSet<Node>());
	}
}
