package gui;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import util.ArrayUtil;

public class MouseOverTable extends JTable
{
	public MouseOverTable()
	{
		super();
		init();
	}

	public MouseOverTable(TableModel m)
	{
		super(m);
		init();
	}

	boolean clearOnExit = true;
	int lastSelectedIndex = -1;

	public void setClearOnExit(boolean b)
	{
		clearOnExit = b;
	}

	/**
	 * != -1
	 * 
	 * @return
	 */
	public int getLastSelectedIndex()
	{
		return lastSelectedIndex;
	}

	@Override
	public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend)
	{
		if (!toggle) // disables deselection with CTRL
			super.changeSelection(rowIndex, columnIndex, toggle, extend);
	}

	protected void init()
	{
		//setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{

			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				int index = MouseOverTable.this.getSelectedRow();
				if (index >= 0)
					lastSelectedIndex = index;
			}
		});

		addMouseMotionListener(new MouseAdapter()
		{
			public void mouseMoved(MouseEvent me)
			{
				Point p = new Point(me.getX(), me.getY());
				int idx = rowAtPoint(p);
				MouseOverTable.this.setRowSelectionInterval(idx, idx);
			}
		});

		addMouseListener(new MouseAdapter()
		{
			public void mouseExited(MouseEvent e)
			{
				if (clearOnExit)
					MouseOverTable.this.clearSelection();
			}
		});
		setFocusable(false);
	}

	public static void main(String args[])
	{

		DefaultTableModel model = new DefaultTableModel();
		final MouseOverTable table = new MouseOverTable(model);
		model.addColumn("Column A");
		model.addColumn("Second Column");
		model.addRow(new String[] { "1", "2" });
		model.addRow(new String[] { "frg", "aöeio" });
		model.addRow(new String[] { "wer", "xc" });

		table.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				System.out.println("mouse over : " + ArrayUtil.toString(table.getSelectedRows()));
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
	}
}
