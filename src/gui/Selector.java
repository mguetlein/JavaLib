package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import util.ImageLoader;
import util.SwingUtil;

public abstract class Selector<T> extends JPanel
{
	Class<T> clazz;
	LinkedHashMap<String, Vector<T>> elements = new LinkedHashMap<String, Vector<T>>();

	//	DefaultListModel searchListModel = new DefaultListModel();
	//	JList searchList = new JList(searchListModel);

	DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
	DefaultTreeModel searchTreeModel = new DefaultTreeModel(root);
	JTree searchTree = new JTree(searchTreeModel);

	DefaultListModel selectListModel = new DefaultListModel();
	JList selectList = new JList(selectListModel);

	JButton addButton = new JButton("add");
	JButton remButton = new JButton("remove");

	public static final String PROPERTY_SELECTION_CHANGED = "PROPERTY_SELECTION_CHANGED";
	public static final String PROPERTY_HIGHLIGHTING_CHANGED = "PROPERTY_HIGHLIGHTING_CHANGED";
	public static final String PROPERTY_TRY_ADDING_INVALID = "PROPERTY_TRY_ADDING_INVALID";

	public Selector(Class<T> clazz, String rootName)
	{
		this.clazz = clazz;
		root.setUserObject(rootName);
		buildLayout();
		addListeners();
	}

	//	DescriptionListCellRenderer searchListRenderer = new DescriptionListCellRenderer()
	//	{
	//		@SuppressWarnings("unchecked")
	//		@Override
	//		public ImageIcon getIcon(Object value)
	//		{
	//			return Selector.this.getIcon((T) value);
	//		}
	//	};

	public abstract boolean isValid(T elem);

	public abstract ImageIcon getIcon(T elem);

	public abstract ImageIcon getCategoryIcon(String name);

	private void buildLayout()
	{
		//		searchListRenderer.setDescriptionSizeOffset(0);
		//		searchListRenderer.setDescriptionSpaceTop(5);
		//		searchListRenderer.setDescriptionFontStyle(Font.BOLDITALIC);
		//		searchList.setCellRenderer(searchListRenderer);

		searchTree.setCellRenderer(new DefaultTreeCellRenderer()
		{

			@Override
			public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
					boolean leaf, int row, boolean hasFocus)
			{
				JLabel l = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
				switch (node.getPath().length)
				{
					case 3:
						l.setIcon(Selector.this.getIcon((T) node.getUserObject()));
						break;
					case 2:
						ImageIcon icon = Selector.this.getCategoryIcon((String) node.getUserObject());
						if (icon != null)
							l.setIcon(icon);
						break;
				}
				return l;
			}
		});

		selectList.setCellRenderer(new DefaultListCellRenderer()
		{

			@Override
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
					boolean cellHasFocus)
			{
				JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				l.setIcon(Selector.this.getIcon((T) value));
				return l;
			}
		});

		searchTree.setVisibleRowCount(12);
		selectList.setVisibleRowCount(12);

		selectList.setFont(searchTree.getFont());

		setLayout(new BorderLayout(5, 5));

		JPanel buttons = new JPanel(new BorderLayout(5, 5));
		buttons.add(addButton, BorderLayout.WEST);
		buttons.add(remButton, BorderLayout.EAST);

		//		searchTree.setRootVisible(false);

		JPanel p = new JPanel(new GridLayout(0, 2, 10, 10));
		p.add(new JScrollPane(searchTree), BorderLayout.WEST);
		p.add(new JScrollPane(selectList), BorderLayout.EAST);

		add(p);
		add(buttons, BorderLayout.SOUTH);
	}

	public void setAddButtonText(String add)
	{
		addButton.setText(add);
	}

	public void setRemoveButtonText(String rem)
	{
		remButton.setText(rem);
	}

	private void addListeners()
	{
		searchTree.addTreeSelectionListener(new TreeSelectionListener()
		{

			@Override
			public void valueChanged(TreeSelectionEvent e)
			{
				if (searchTree.getSelectionPath() != null)
				{
					selectList.clearSelection();
					firePropertyChange(PROPERTY_HIGHLIGHTING_CHANGED, true, false);
				}
			}
		});
		selectList.addListSelectionListener(new ListSelectionListener()
		{
			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				if (selectList.getSelectedIndex() != -1)
				{
					searchTree.clearSelection();
					firePropertyChange(PROPERTY_HIGHLIGHTING_CHANGED, true, false);
				}
			}
		});
		addButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				List<T> invalidTries = new ArrayList<T>();
				for (TreePath elem : searchTree.getSelectionPaths())
				{
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) elem.getLastPathComponent();
					if (node.getPath().length == 3)
						if (isValid((T) node.getUserObject()))
							selectListModel.addElement(node.getUserObject());
						else
							invalidTries.add((T) node.getUserObject());
					else if (node.getPath().length == 2)
					{
						Enumeration<?> children = node.children();
						while (children.hasMoreElements())
						{
							DefaultMutableTreeNode leaf = (DefaultMutableTreeNode) children.nextElement();
							if (isValid((T) leaf.getUserObject()))
								selectListModel.addElement(leaf.getUserObject());
							else
								invalidTries.add((T) leaf.getUserObject());
						}
					}
				}
				firePropertyChange(PROPERTY_SELECTION_CHANGED, true, false);
				if (invalidTries.size() > 0)
					firePropertyChange(PROPERTY_TRY_ADDING_INVALID, null, invalidTries);
			}
		});
		remButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				for (Object elem : selectList.getSelectedValues())
					selectListModel.removeElement(elem);
				firePropertyChange(PROPERTY_SELECTION_CHANGED, true, false);
			}
		});
	}

	@SuppressWarnings("unchecked")
	public T getHighlightedElement()
	{
		if (searchTree.getSelectionPath() != null)
		{
			if (searchTree.getSelectionPath().getPath().length == 3)
				return (T) ((DefaultMutableTreeNode) searchTree.getSelectionPath().getLastPathComponent())
						.getUserObject();
		}
		else if (selectList.getSelectedIndex() != -1)
		{
			return (T) selectList.getSelectedValue();
		}
		return null;
	}

	public void setSelected(T elem)
	{
		boolean match = false;
		for (Vector<T> elems : elements.values())
			if (elems.contains(elem))
			{
				match = true;
				break;
			}
		if (match && selectListModel.indexOf(elem) == -1)
			selectListModel.addElement(elem);
	}

	public void setSelected(T[] elements)
	{
		for (T elem : elements)
			setSelected(elem);
	}

	@SuppressWarnings("unchecked")
	public T[] getSelected()
	{
		T a[] = (T[]) Array.newInstance(clazz, selectListModel.getSize());
		for (int i = 0; i < a.length; i++)
			a[i] = (T) selectListModel.getElementAt(i);
		return a;
	}

	@SuppressWarnings("unchecked")
	public T[] getSelected(String name)
	{
		List<T> selected = new ArrayList<T>();
		for (int i = 0; i < selectListModel.getSize(); i++)
		{
			if (elements.get(name).contains(selectListModel.getElementAt(i)))
				selected.add((T) selectListModel.getElementAt(i));
		}
		T a[] = (T[]) Array.newInstance(clazz, selected.size());
		return selected.toArray(a);
	}

	private void update()
	{
		root.removeAllChildren();
		for (String key : elements.keySet())
		{
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(key);
			addNodeToDefaultTreeModel(searchTreeModel, root, node);
			for (T elem : elements.get(key))
				addNodeToDefaultTreeModel(searchTreeModel, node, new DefaultMutableTreeNode(elem));
		}
	}

	private static void addNodeToDefaultTreeModel(DefaultTreeModel treeModel, DefaultMutableTreeNode parentNode,
			DefaultMutableTreeNode node)
	{
		treeModel.insertNodeInto(node, parentNode, parentNode.getChildCount());
		if (parentNode == treeModel.getRoot())
			treeModel.nodeStructureChanged((TreeNode) treeModel.getRoot());
	}

	public void clearElements()
	{
		elements.clear();
		update();
	}

	public void addElements(String name, T... elements)
	{
		addElementList(name, elements);
	}

	public void addElementList(String name, T[] elements)
	{
		Vector<T> v = new Vector<T>();
		for (T t : elements)
			v.add(t);
		this.elements.put(name, v);
		update();
	}

	public static void main(String args[])
	{
		Selector<String> sel = new Selector<String>(String.class, "Tiere")
		{
			public ImageIcon getIcon(String renderable)
			{
				return ImageLoader.DISTINCT;
			}

			@Override
			public boolean isValid(String elem)
			{
				return !elem.equals("Nicht-hinzufügbar");
			}

			@Override
			public ImageIcon getCategoryIcon(String name)
			{
				return ImageLoader.INFO;
			}
		};
		sel.addElements("Säugetiere", "Hund", "Katze", "Maus", "Nicht-hinzufügbar");
		sel.addElements("Fische", "Hai", "Kabeljau");
		sel.addElements("Vögel", "Spatz", "Adler", "Strauß", "Amsel");
		sel.addElements("Unmögliche Tiere");
		sel.addElements("Insekten", "Ameise1", "Ameise2", "Ameise3", "Ameise4", "Ameise5", "Ameise6", "Ameise7");
		SwingUtil.showInDialog(sel);
	}

}
