package org.mg.javalib.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.mg.javalib.util.ArrayUtil;
import org.mg.javalib.util.SelectionModel;
import org.mg.javalib.util.StringUtil;
import org.mg.javalib.util.SwingUtil;

public class ClickMouseOverTable extends MouseOverTable
{
	protected SelectionModel clickSelectionModel;

	public ClickMouseOverTable()
	{
		super();
	}

	public ClickMouseOverTable(DefaultTableModel model)
	{
		super(model);
	}

	static final Color defaultForeground = UIManager.getColor("Table.foreground");
	static final Color defaultSelectedForeground = UIManager.getColor("Table.selectionForeground");
	static final Color defaultBackground = UIManager.getColor("Table.background");
	static final Color defaultSelectedBackground = UIManager.getColor("Table.selectionBackground");

	public static class ClickMouseOverRenderer extends DefaultTableCellRenderer
	{
		ClickMouseOverTable table;
		Color background = defaultBackground;
		public Color clickSelectedBackground = defaultSelectedBackground.darker();
		public Color mouseOverSelectedBackground = defaultSelectedBackground;
		Color foreground = defaultForeground;
		Color clickSelectedForeground = defaultSelectedForeground;
		Color mouseOverSelectedForeground = defaultSelectedForeground;

		public ClickMouseOverRenderer(ClickMouseOverTable table)
		{
			this.table = table;
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (this.table.getClickSelectionModel().isSelected(row))
			{
				setBackground(clickSelectedBackground);
				setForeground(clickSelectedForeground);
			}
			else if (isSelected)
			{
				setBackground(mouseOverSelectedBackground);
				setForeground(mouseOverSelectedForeground);
			}
			else
			{
				setBackground(background);
				setForeground(foreground);
			}
			return this;
		}

	}

	@Override
	protected void init()
	{
		super.init();

		clickSelectionModel = new SelectionModel(true);

		setDefaultRenderer(Object.class, new ClickMouseOverRenderer(this));

		clickSelectionModel.addListener(new PropertyChangeListener()
		{
			@Override
			public void propertyChange(PropertyChangeEvent evt)
			{
				ClickMouseOverTable.this.repaint();
			}
		});

		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent me)
			{
				Point p = new Point(me.getX(), me.getY());
				int idx = rowAtPoint(p);
				if (me.isControlDown())
				{
					clickSelectionModel.setSelectedInverted(idx);
				}
				else if (me.isShiftDown() && clickSelectionModel.getSelected() != -1
						&& clickSelectionModel.getSelected() != idx)
				{
					int minSel, maxSel;
					if (clickSelectionModel.getSelected() < idx)
					{
						minSel = clickSelectionModel.getSelected() + 1;
						maxSel = idx;
					}
					else
					{
						minSel = idx;
						maxSel = clickSelectionModel.getSelected() - 1;
					}
					int newSel[] = new int[1 + maxSel - minSel];
					for (int i = 0; i < newSel.length; i++)
						newSel[i] = minSel + i;
					clickSelectionModel.setSelectedIndices(newSel, false);
				}
				else
				{
					if (clickSelectionModel.isSelected(idx))
						clickSelectionModel.clearSelection();
					else
						clickSelectionModel.setSelected(idx);
				}
				//				System.out.println("is selected " + idx + " " + clickSelectionModel.isSelected(idx));
			}
		});
	}

	public SelectionModel getClickSelectionModel()
	{
		return clickSelectionModel;
	}

	public static void main(String args[])
	{
		DefaultTableModel model = new DefaultTableModel();
		final ClickMouseOverTable table = new ClickMouseOverTable(model);
		model.addColumn("Column A");
		model.addColumn("Second Column");
		model.addRow(new String[] { "1", "2" });
		model.addRow(new String[] { "frg", "aasdf asdfo" });
		for (int i = 0; i < 10; i++)
			model.addRow(new String[] { StringUtil.randomString(), StringUtil.randomString() });

		table.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				System.out.println("mouse over : " + ArrayUtil.toString(table.getSelectedRows()));
			}
		});
		table.getClickSelectionModel().addListener(new PropertyChangeListener()
		{

			@Override
			public void propertyChange(PropertyChangeEvent evt)
			{
				System.out.println("selected : "
						+ ArrayUtil.toString(table.getClickSelectionModel().getSelectedIndices()));
			}
		});

		JDialog d = new JDialog();
		JPanel p = new JPanel(new BorderLayout());
		p.add(new JLabel("Mouse over Table:"), BorderLayout.NORTH);
		p.add(new JScrollPane(table));
		d.add(p);
		d.pack();
		d.setLocationRelativeTo(null);
		d.setVisible(true);
		SwingUtil.waitWhileVisible(d);
		System.exit(1);
	}
}
