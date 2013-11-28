package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
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
import util.ListUtil;
import util.SwingUtil;

public abstract class Selector<T> extends JPanel
{
	Class<T> clazz;

	private static class Category
	{
		private String s;

		public Category(String s)
		{
			this.s = s;
		}

		public String toString()
		{
			return s;
		}

		public boolean equals(Object o)
		{
			return o instanceof Category && ((Category) o).s.equals(s);
		}
	}

	//	LinkedHashMap<Category[], Vector<T>> elements = new LinkedHashMap<Category[], Vector<T>>();

	//	DefaultListModel searchListModel = new DefaultListModel();
	//	JList searchList = new JList(searchListModel);

	DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
	DefaultTreeModel searchTreeModel = new DefaultTreeModel(root);
	JTree searchTree = new JTree(searchTreeModel);

	DefaultListModel selectListModel = new DefaultListModel();
	JList selectList = new JList(selectListModel);

	JButton addButton = new JButton("add");
	JButton remButton = new JButton("remove");

	boolean selfUpdateSelection = false;
	String highlightedCategory = null;
	T highlightedElement = null;

	public static final String PROPERTY_SELECTION_CHANGED = "PROPERTY_SELECTION_CHANGED";
	public static final String PROPERTY_HIGHLIGHTING_CHANGED = "PROPERTY_HIGHLIGHTING_CHANGED";
	public static final String PROPERTY_TRY_ADDING_INVALID = "PROPERTY_TRY_ADDING_INVALID";
	public static final String PROPERTY_EMPTY_ADD = "PROPERTY_EMPTY_ADD";
	public static final String PROPERTY_EMPTY_REMOVE = "PROPERTY_EMPTY_REMOVE";

	public Selector(Class<T> clazz, String rootName, int visibleRowCount)
	{
		this.clazz = clazz;
		root.setUserObject(rootName);
		buildLayout(visibleRowCount);
		addListeners();
	}

	@Override
	public synchronized void addKeyListener(KeyListener l)
	{
		searchTree.addKeyListener(l);
		selectList.addKeyListener(l);
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

	public abstract String getString(T elem);

	public abstract Icon getIcon(T elem);

	public abstract ImageIcon getCategoryIcon(String name);

	private void buildLayout(int visibleRowCount)
	{
		//		searchListRenderer.setDescriptionSizeOffset(0);
		//		searchListRenderer.setDescriptionSpaceTop(5);
		//		searchListRenderer.setDescriptionFontStyle(Font.BOLDITALIC);
		//		searchList.setCellRenderer(searchListRenderer);

		searchTree.setCellRenderer(new DefaultTreeCellRenderer()
		{
			@SuppressWarnings("unchecked")
			@Override
			public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
					boolean leaf, int row, boolean hasFocus)
			{
				JLabel l = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
				if (clazz.isInstance(node.getUserObject()))
				{
					l.setText(Selector.this.getString((T) node.getUserObject()));
					l.setIcon(Selector.this.getIcon((T) node.getUserObject()));
				}
				else if (Category.class.isInstance(node.getUserObject()))
				{
					ImageIcon icon = Selector.this.getCategoryIcon(node.getUserObject().toString());
					if (icon != null)
						l.setIcon(icon);
				}
				return l;
			}
		});

		selectList.setCellRenderer(new DefaultListCellRenderer()
		{

			@SuppressWarnings("unchecked")
			@Override
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
					boolean cellHasFocus)
			{
				JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				l.setText(Selector.this.getString((T) value));
				l.setIcon(Selector.this.getIcon((T) value));
				return l;
			}
		});

		searchTree.setVisibleRowCount(visibleRowCount);
		selectList.setVisibleRowCount(visibleRowCount);

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
				if (selfUpdateSelection)
					return;
				selfUpdateSelection = true;
				selectList.clearSelection();
				updateHighlight();
				selfUpdateSelection = false;
			}
		});
		selectList.addListSelectionListener(new ListSelectionListener()
		{
			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				if (selfUpdateSelection)
					return;
				selfUpdateSelection = true;
				searchTree.clearSelection();
				updateHighlight();
				selfUpdateSelection = false;
			}
		});
		addButton.addActionListener(new ActionListener()
		{
			@SuppressWarnings("unchecked")
			@Override
			public void actionPerformed(ActionEvent e)
			{
				List<T> elements = new ArrayList<T>();

				if (!searchTree.isSelectionEmpty())
					for (TreePath elem : searchTree.getSelectionPaths())
					{
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) elem.getLastPathComponent();
						if (clazz.isInstance(node.getUserObject()))
							elements.add((T) node.getUserObject());
						else if (Category.class.isInstance(node.getUserObject()))
							for (DefaultMutableTreeNode leaf : TreeUtil.getLeafs(node))
								elements.add((T) leaf.getUserObject());
					}

				List<T> selected = new ArrayList<T>();
				List<T> invalid = new ArrayList<T>();
				for (T t : elements)
					if (isValid(t))
						selected.add(t);
					else
						invalid.add(t);
				if (selected.size() > 0)
					setSelected(ListUtil.toArray(clazz, selected));

				if (elements.size() > 0)
					firePropertyChange(PROPERTY_SELECTION_CHANGED, true, false);
				else
					firePropertyChange(PROPERTY_EMPTY_ADD, true, false);

				if (invalid.size() > 0)
					firePropertyChange(PROPERTY_TRY_ADDING_INVALID, null, invalid);
			}
		});
		remButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				selfUpdateSelection = true;
				boolean selected = false;
				for (Object elem : selectList.getSelectedValues())
				{
					selectListModel.removeElement(elem);
					selected = true;
				}
				updateHighlight();
				selfUpdateSelection = false;
				if (selected)
					firePropertyChange(PROPERTY_SELECTION_CHANGED, true, false);
				else
					firePropertyChange(PROPERTY_EMPTY_REMOVE, true, false);
			}
		});
	}

	@SuppressWarnings("unchecked")
	private void updateHighlight()
	{
		T oldHighlight = highlightedElement;
		String oldHighlight2 = highlightedCategory;

		highlightedElement = null;
		if (searchTree.getSelectionPath() != null)
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) searchTree.getSelectionPath().getLastPathComponent();
			if (clazz.isInstance(node.getUserObject()))
				highlightedElement = (T) ((DefaultMutableTreeNode) searchTree.getSelectionPath().getLastPathComponent())
						.getUserObject();
		}
		else if (selectList.getSelectedIndex() != -1)
			highlightedElement = (T) selectList.getSelectedValue();

		highlightedCategory = null;
		if (searchTree.getSelectionPath() != null)
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) searchTree.getSelectionPath().getLastPathComponent();
			if (node == root)
				highlightedCategory = root.getUserObject().toString();
			else if (Category.class.isInstance(node.getUserObject()))
				highlightedCategory = ((DefaultMutableTreeNode) searchTree.getSelectionPath().getLastPathComponent())
						.getUserObject().toString();
		}

		if (oldHighlight != highlightedElement || oldHighlight2 != highlightedCategory)
			firePropertyChange(PROPERTY_HIGHLIGHTING_CHANGED, oldHighlight, highlightedElement);
	}

	public T getHighlightedElement()
	{
		return highlightedElement;
	}

	public String getHighlightedCategory()
	{
		return highlightedCategory;
	}

	@SuppressWarnings("unchecked")
	public void setCategorySelected(String name, boolean fireAddEvent)
	{
		Category c = new Category(name);
		DefaultMutableTreeNode parent = TreeUtil.getChild(root, c);
		if (parent == null)
			return;
		List<DefaultMutableTreeNode> leafs = TreeUtil.getLeafs(parent);
		for (DefaultMutableTreeNode node : leafs)
			if (isValid((T) node.getUserObject()) && selectListModel.indexOf(node.getUserObject()) == -1)
				selectListModel.addElement(node.getUserObject());
		if (fireAddEvent)
			firePropertyChange(PROPERTY_SELECTION_CHANGED, true, false);
	}

	public void setSelected(T elem)
	{
		setSelected(elem, false);
	}

	private void setSelected(T elem, boolean skipMatchTest)
	{
		boolean match = false;
		if (skipMatchTest)
			match = true;
		else
		{
			for (DefaultMutableTreeNode node : TreeUtil.getLeafs(root))
			{
				if (node.getUserObject().equals(elem))
				{
					match = true;
					break;
				}
			}
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
		Category c = new Category(name);
		DefaultMutableTreeNode parent = TreeUtil.getChild(root, c);
		if (parent == null)
			return null;
		List<DefaultMutableTreeNode> leafs = TreeUtil.getLeafs(parent);

		List<T> selected = new ArrayList<T>();
		for (int i = 0; i < selectListModel.getSize(); i++)
		{
			boolean match = false;
			for (DefaultMutableTreeNode node : leafs)
			{
				if (node.getUserObject().equals(selectListModel.getElementAt(i)))
				{
					match = true;
					break;
				}
			}
			if (match)
				selected.add((T) selectListModel.getElementAt(i));
		}
		T a[] = (T[]) Array.newInstance(clazz, selected.size());
		return selected.toArray(a);
	}

	private static void addNodeToDefaultTreeModel(DefaultTreeModel treeModel, DefaultMutableTreeNode parentNode,
			DefaultMutableTreeNode node)
	{
		DefaultMutableTreeNode alreadyExists = TreeUtil.getChild(parentNode, node.getUserObject());
		if (alreadyExists == null)
		{
			treeModel.insertNodeInto(node, parentNode, parentNode.getChildCount());
			if (parentNode == treeModel.getRoot())
				treeModel.nodeStructureChanged((TreeNode) treeModel.getRoot());
		}
	}

	public void clearElements()
	{
		root.removeAllChildren();
		selectListModel.removeAllElements();
	}

	public void addElements(String name, T... elements)
	{
		addElementList(new String[] { name }, elements);
	}

	public void addElements(String name[], T... elements)
	{
		addElementList(name, elements);
	}

	public void addElementList(String name, T[] elements)
	{
		addElementList(new String[] { name }, elements);
	}

	public void addElementList(String name[], T[] elements)
	{
		Vector<T> v = new Vector<T>();
		for (T t : elements)
			v.add(t);

		DefaultMutableTreeNode parent = root;
		for (String n : name)
		{
			Category c = new Category(n);
			DefaultMutableTreeNode alreadyExists = TreeUtil.getChild(parent, c);
			if (alreadyExists == null)
			{
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(c);
				addNodeToDefaultTreeModel(searchTreeModel, parent, node);
				parent = node;
			}
			else
				parent = alreadyExists;
		}
		for (T elem : elements)
			addNodeToDefaultTreeModel(searchTreeModel, parent, new DefaultMutableTreeNode(elem));
	}

	public static void main(String args[])
	{
		final Selector<String> sel = new Selector<String>(String.class, "Tiere", 12)
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

			@Override
			public String getString(String elem)
			{
				return elem;
			}
		};
		sel.addPropertyChangeListener(Selector.PROPERTY_HIGHLIGHTING_CHANGED, new PropertyChangeListener()
		{
			@Override
			public void propertyChange(PropertyChangeEvent evt)
			{
				System.out.println("new highlighting: " + sel.getHighlightedElement() + " - "
						+ sel.getHighlightedCategory());
			}
		});
		sel.addElements("Säugetiere", "Hund", "Katze", "Maus", "Nicht-hinzufügbar");
		sel.addElements("Fische", "Hai", "Kabeljau");
		sel.addElements("Vögel", "Spatz", "Adler", "Strauß", "Amsel");
		sel.addElements("Unmögliche Tiere");
		sel.addElements(new String[] { "Übergruppe", "Untergruppe" }, "Herdentier 1", "Herdentier 2");
		sel.addElements(new String[] { "Übergruppe", "Untergruppe2" }, "Herdentier 3");
		sel.addElements("Insekten", "Ameise1", "Ameise2", "Ameise3", "Ameise4", "Ameise5", "Ameise6", "Ameise7");
		sel.addKeyListener(new KeyAdapter()
		{
			public void keyTyped(KeyEvent e)
			{
				System.out.println("tpyed: " + e.getKeyChar());
			}

		});
		sel.setCategorySelected("Säugetiere", false);
		SwingUtil.showInDialog(sel);
		System.exit(0);
	}

	public void repaintSelector()
	{
		for (DefaultMutableTreeNode node : TreeUtil.getLeafs(root))
			searchTreeModel.nodeChanged(node);
		repaint();
	}

}
