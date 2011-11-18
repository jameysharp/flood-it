import java.io.*;
import java.util.*;

public class Node implements Iterable<Node>
{
	public final int id;
	public final char color;
	private final Set<Node> edges = new HashSet<Node>();
	private int area = 1;

	private Node(int id, char color)
	{
		this.id = id;
		this.color = color;
	}

	@Override
	public Iterator<Node> iterator()
	{
		return Collections.unmodifiableSet(edges).iterator();
	}

	public int getArea()
	{
		return area;
	}

	private void add(Node other)
	{
		assert this != other;
		edges.add(other);
		other.edges.add(this);
	}

	private void mergeWith(Node other)
	{
		if(this == other)
			return;
		assert color == other.color;
		area += other.area;
		for(Node n : other)
		{
			n.edges.remove(other);
			add(n);
		}
	}

	@Override
	public int hashCode()
	{
		return id;
	}

	@Override
	public boolean equals(Object other)
	{
		return this == other;
	}

	public static Node readBoard(InputStream in)
		throws IOException
	{
		int count = 0;
		Node rootNode = null;
		Iterator<Node> lastRow = null;
		ListIterator<Node> row = new ArrayList<Node>().listIterator();
		int ch;
		while((ch = System.in.read()) != -1)
		{
			if(ch == '\n')
			{
				assert lastRow == null || !lastRow.hasNext() : "rows must be the same length";
				ListIterator<Node> tmp = row;
				row = new ArrayList<Node>(row.nextIndex()).listIterator();
				while(tmp.hasPrevious())
					tmp.previous();
				lastRow = tmp;
				continue;
			}

			Node cur = null;
			Node above = null;
			Node left = null;
			if(lastRow != null)
			{
				above = lastRow.next();
				if(above.color == ch)
				{
					cur = above;
					above = null;
				}
			}
			if(row.hasPrevious())
			{
				left = row.previous();
				row.next();
				if(left.color == ch)
				{
					if(cur == null)
						cur = left;
					else
					{
						cur.mergeWith(left);
						if(rootNode == left)
							rootNode = cur;
						while(row.hasPrevious())
							if(row.previous().color != ch)
							{
								row.next();
								break;
							}
						while(row.hasNext())
						{
							row.next();
							row.set(cur);
						}
					}
					left = null;
				}
			}
			if(cur == null)
				cur = new Node(++count, (char) ch);
			else
				++cur.area;
			if(left != null)
				cur.add(left);
			if(above != null)
				cur.add(above);
			row.add(cur);
			if(rootNode == null)
				rootNode = cur;
		}
		return rootNode;
	}
}
