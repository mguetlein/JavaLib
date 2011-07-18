package gui;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import util.ArrayUtil;

import com.jgoodies.forms.factories.ButtonBarFactory;

public class CheckBoxSelectDialog extends JDialog
{
	protected boolean okPressed = false;

	MouseOverCheckBoxList list;
	DefaultListModel listModel;
	MouseOverCheckBoxListComponent listPanel;

	private CheckBoxSelectDialog(Window owner, String title, String description, Object[] values, boolean allSelected)
	{
		super(owner, title);
		setModal(true);
		buildLayout(description);
		pack();

		for (Object o : values)
			listModel.addElement(o);
		if (allSelected)
		{
			int indices[] = new int[values.length];
			for (int i = 0; i < indices.length; i++)
				indices[i] = i;
			list.getCheckBoxSelection().setSelectedIndices(indices);
		}
		setLocationRelativeTo(owner);
		setVisible(true);
	}

	private void buildLayout(String description)
	{
		JTextArea d = new JTextArea(description);
		d.setEditable(false);
		d.setOpaque(false);
		d.setBorder(null);
		d.setWrapStyleWord(true);
		d.setLineWrap(true);

		listModel = new DefaultListModel();
		list = new MouseOverCheckBoxList(listModel);
		list.setClearOnExit(false);
		listPanel = new MouseOverCheckBoxListComponent(list);

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
		p.add(d, BorderLayout.NORTH);
		p.add(listPanel);
		p.add(buttons, BorderLayout.SOUTH);
		p.setBorder(new EmptyBorder(10, 10, 10, 10));
		getContentPane().add(p);
	}

	public static int[] select(Window owner, String title, String info, Object[] values, boolean allSelected)
	{
		CheckBoxSelectDialog d = new CheckBoxSelectDialog(owner, title, info, values, allSelected);
		if (d.okPressed)
			return d.list.getCheckBoxSelection().getSelectedIndices();
		else
			return null;
	}

	public static void main(String args[])
	{
		String s[] = { "ene", "mene", "miste" };
		int ss[] = CheckBoxSelectDialog.select(null, "test-select-dialog", "description text", s, true);
		if (ss != null)
			System.out.println(ArrayUtil.toString(ss));
	}
}
