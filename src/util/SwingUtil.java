package util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

import com.jgoodies.forms.factories.ButtonBarFactory;

public class SwingUtil
{
	public static void waitWhileVisible(Window f)
	{
		while (f.isVisible())
		{
			try
			{
				Thread.sleep(100);
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void loadingLabel(final JLabel label)
	{
		final String loading[] = { "Loading... ", "Loading.. .", "Loading. ..", "Loading ..." };
		label.setText(loading[0]);
		Thread th = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				int index = 0;
				while (label.getText().startsWith("Loading"))
				{
					index++;
					if (index == loading.length)
						index = 0;
					label.setText(loading[index]);

					try
					{
						Thread.sleep(500);
					}
					catch (InterruptedException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		th.start();
	}

	public static void addClickLink(JComponent compound, final String uri)
	{
		compound.setToolTipText(uri);
		compound.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				try
				{
					Desktop.getDesktop().browse(new URI(uri));
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
		});
	}

	public static void showTable(String title, List<String> names, List<Vector<Object>> values, int sortColumn,
			SortOrder sortOrder, boolean numericSort, final ActionListener actionListener)
	{
		showTable(title, names, values, sortColumn, sortOrder, numericSort, actionListener, null, null);
	}

	public static interface BackgroundPainter
	{
		public Color getColor(Object firstColumnVal, int row);
	}

	public static void showTable(String title, List<String> names, List<Vector<Object>> values, int sortColumn,
			SortOrder sortOrder, boolean numericSort, final ActionListener actionListener, String additionalInfo,
			TableCellRenderer cellRenderer)
	{
		DefaultTableModel m = new DefaultTableModel()
		{
			public boolean isCellEditable(int row, int col)
			{
				return false;
			}
		};
		for (String n : names)
			m.addColumn(n);
		for (Vector<Object> v : values)
			m.addRow(v);
		final JTable t = new JTable(m);
		if (cellRenderer == null)
			cellRenderer = new DefaultTableCellRenderer()
			{
				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
						boolean hasFocus, int row, int column)
				{
					if (value instanceof Double)
						value = StringUtil.formatDouble(((Double) value).doubleValue());
					return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				}
			};
		t.setDefaultRenderer(Object.class, cellRenderer);

		if (sortColumn != -1)
		{
			TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>(m);
			if (numericSort)
				sorter.setComparator(sortColumn, new Comparator<Double>()
				{
					@Override
					public int compare(Double arg0, Double arg1)
					{
						return arg0.compareTo(arg1);
					}
				});
			List<RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
			sortKeys.add(new RowSorter.SortKey(sortColumn, sortOrder));
			sorter.setSortKeys(sortKeys);
			t.setRowSorter(sorter);
		}
		final JDialog f = new JDialog();
		f.setModal(true);
		f.setTitle(title);
		JPanel p = new JPanel(new BorderLayout(5, 5));
		p.setBorder(new EmptyBorder(5, 5, 5, 5));
		if (additionalInfo != null)
		{
			JLabel l = new JLabel(additionalInfo);
			l.setFont(l.getFont().deriveFont(Font.PLAIN));
			p.add(l, BorderLayout.NORTH);
		}
		p.add(new JScrollPane(t));
		f.getContentPane().add(p);
		f.pack();
		f.setSize(f.getWidth() + 300, f.getHeight() + 300);
		// f.setLocationRelativeTo(null);
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		// f.setVisible(true);
		t.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent arg0)
			{
				if (arg0.getClickCount() > 1)
				{
					if (actionListener != null)
						actionListener.actionPerformed(new ActionEvent(t.getValueAt(t.getSelectedRow(), 0), -1, ""));
				}
			}
		});
		f.setVisible(true);
		// SwingUtil.waitWhileVisible(f);
	}

	public static void showInDialog(JComponent c)
	{
		showInDialog(c, "test dialog", null);
	}

	public static void showInDialog(JComponent c, String title)
	{
		showInDialog(c, title, null);
	}

	public static void showInDialog(JComponent c, Dimension dim)
	{
		showInDialog(c, "test dialog", dim);
	}

	public static void showInDialog(JComponent c, String title, Dimension dim)
	{
		final JDialog f = new JDialog();
		f.setModal(true);
		f.setTitle(title);
		JPanel p = new JPanel(new BorderLayout(10, 10));
		p.add(c);
		p.setBorder(new EmptyBorder(10, 10, 10, 10));
		f.getContentPane().add(p);
		if (dim == null)
			f.pack();
		else
			f.setSize(dim);
		f.setLocationRelativeTo(null);
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JButton close = new JButton("Close");
		close.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				f.setVisible(false);
			}
		});
		p.add(ButtonBarFactory.buildCloseBar(close), BorderLayout.SOUTH);
		f.setVisible(true);
	}

	public static void main(String args[])
	{
		//		List<String> names = new ArrayList<String>();
		//		names.add("col1");
		//		names.add("col2");
		//		List<Vector<Object>> values = new ArrayList<Vector<Object>>();
		//		Vector<Object> v1 = new Vector<Object>();
		//		v1.add("a");
		//		v1.add("b");
		//		values.add(v1);
		//		Vector<Object> v2 = new Vector<Object>();
		//		v2.add("1");
		//		v2.add("2");
		//		values.add(v2);
		//		SwingUtil.showTable("test", names, values, -1, null, false, null);

		JLabel s = new JLabel();
		loadingLabel(s);
		showInDialog(s);

	}

	public static void setDebugBorder(JComponent comp)
	{
		setDebugBorder(comp, Color.RED);
	}

	public static void setDebugBorder(JComponent comp, Color col)
	{
		comp.setBorder(new CompoundBorder(new LineBorder(col, 3), comp.getBorder()));

	}

	public static void waitForAWTEventThread()
	{
		final StringBuffer clear = new StringBuffer();
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				clear.append("clear");
			}
		});
		while (!clear.toString().equals("clear"))
		{
			try
			{
				Thread.sleep(100);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
