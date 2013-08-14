package gui;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import util.ArrayUtil;

import com.jgoodies.forms.factories.ButtonBarFactory;

public class CheckBoxSelectDialog extends JDialog
{
	protected boolean okPressed = false;

	CheckBoxSelectPanel panel;

	private CheckBoxSelectDialog(Window owner, String title, String description, Object[] values, boolean allSelected)
	{
		super(owner, title);
		boolean selection[] = new boolean[values.length];
		Arrays.fill(selection, allSelected);
		init(owner, description, values, selection);
	}

	private CheckBoxSelectDialog(Window owner, String title, String description, Object[] values, boolean selection[])
	{
		super(owner, title);
		init(owner, description, values, selection);
	}

	private void init(Window owner, String description, Object[] values, boolean selection[])
	{
		panel = new CheckBoxSelectPanel(description, values, selection);
		buildLayout();
		setModal(true);
		pack();
		setLocationRelativeTo(owner);
		setVisible(true);
	}

	private void buildLayout()
	{
		final JButton ok = new JButton("OK");
		JButton cancel = new JButton("Cancel");
		ActionListener l = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				okPressed = (e.getSource() == ok);
				CheckBoxSelectDialog.this.setVisible(false);
			}
		};
		ok.addActionListener(l);
		cancel.addActionListener(l);
		JPanel buttons = ButtonBarFactory.buildOKCancelBar(ok, cancel);

		JPanel p = new JPanel(new BorderLayout(0, 10));
		p.add(panel);
		p.add(buttons, BorderLayout.SOUTH);
		p.setBorder(new EmptyBorder(10, 10, 10, 10));
		getContentPane().add(p);
	}

	public static Object[] select(final Window owner, final String title, final String info, final Object[] values,
			final boolean allSelected)
	{
		CheckBoxSelectDialog d = new CheckBoxSelectDialog(owner, title, info, values, allSelected);
		return d.selected();
	}

	public static Object[] select(Window owner, String title, String info, Object[] values, boolean selected[])
	{
		CheckBoxSelectDialog d = new CheckBoxSelectDialog(owner, title, info, values, selected);
		return d.selected();
	}

	private Object[] selected()
	{
		if (okPressed)
		{
			Object o[] = panel.getSelectedValues();
			if (o == null)
				return new Object[0];// return length 0 if empty selection
			else
				return o;
		}
		else
			return null;
	}

	public static int[] selectIndices(Window owner, String title, String info, Object[] values, boolean allSelected)
	{
		CheckBoxSelectDialog d = new CheckBoxSelectDialog(owner, title, info, values, allSelected);
		return d.selectedIndices();
	}

	public static int[] selectIndices(Window owner, String title, String info, Object[] values, boolean selected[])
	{
		CheckBoxSelectDialog d = new CheckBoxSelectDialog(owner, title, info, values, selected);
		return d.selectedIndices();
	}

	private int[] selectedIndices()
	{
		if (okPressed)
			return panel.getSelectedIndices();
		else
			return null;
	}

	public static void main(String args[])
	{
		String s[] = { "ene", "mene", "miste" };
		int ss[] = CheckBoxSelectDialog.selectIndices(null, "test-select-dialog", "description text", s, true);
		if (ss != null)
			System.out.println(ArrayUtil.toString(ss));
		else
			System.out.println("nothing");
	}
}
