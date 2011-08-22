package gui;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

public class TreeUtil
{
	public static List<DefaultMutableTreeNode> getLeafs(DefaultMutableTreeNode node)
	{
		List<DefaultMutableTreeNode> leafs = new ArrayList<DefaultMutableTreeNode>();
		Enumeration<?> children = node.children();
		while (children.hasMoreElements())
		{
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) children.nextElement();
			if (child.isLeaf())
				leafs.add(child);
			else
				for (DefaultMutableTreeNode defaultMutableTreeNode : getLeafs(child))
					leafs.add(defaultMutableTreeNode);
		}
		return leafs;
	}

	public static DefaultMutableTreeNode getChild(DefaultMutableTreeNode node, Object childUserObject)
	{
		Enumeration<?> children = node.children();
		while (children.hasMoreElements())
		{
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) children.nextElement();
			if (child.getUserObject().equals(childUserObject))
				return child;
		}
		return null;
	}
}
