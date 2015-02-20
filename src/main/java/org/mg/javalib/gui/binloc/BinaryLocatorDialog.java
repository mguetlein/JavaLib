package org.mg.javalib.gui.binloc;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import org.mg.javalib.util.ImageLoader;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;

public class BinaryLocatorDialog extends JDialog
{
	List<Binary> binaries;
	DefaultListModel model;
	JList list;
	JButton close;
	JButton relocate;

	public BinaryLocatorDialog(Window owner, String title, String hostProgramTitle, List<Binary> binaries, Binary select)
	{
		super(owner, title);
		setModal(true);

		this.binaries = binaries;
		BinaryLocator.locate(binaries);

		buildLayout(hostProgramTitle);
		if (select != null)
			list.setSelectedValue(select, true);
		addListeners();

		pack();
		setLocationRelativeTo(owner);
		setVisible(true);
	}

	private void addListeners()
	{
		close.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				BinaryLocatorDialog.this.setVisible(false);
			}
		});

		relocate.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				BinaryLocator.locate(binaries);
				list.repaint();
			}
		});

		list.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent e)
			{
				if (e.getClickCount() > 1 && list.getSelectedIndex() != -1)
				{
					final Binary b = (Binary) list.getSelectedValue();

					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setFileFilter(new FileFilter()
					{
						@Override
						public String getDescription()
						{
							return b.getOSCommand();
						}

						@Override
						public boolean accept(File f)
						{
							return f.isDirectory() || f.getAbsolutePath().endsWith(b.getOSCommand());
						}
					});
					int res = fileChooser.showOpenDialog(BinaryLocatorDialog.this);
					if (res == JFileChooser.APPROVE_OPTION)
					{
						b.setLocation(fileChooser.getSelectedFile().getAbsolutePath());
						list.repaint();
					}
				}
			}
		});
	}

	private void buildLayout(String hostProgramTitle)
	{
		DefaultFormBuilder b = new DefaultFormBuilder(new FormLayout("fill:p:grow"));

		b.append("<html>The following programs are needed for full functionality of "
				+ hostProgramTitle
				+ ".<br>"
				+ "Install the external program first, then click 'relocate'.<br>"
				+ "If the program is installed but cannot be found, double-click on the program to assign its location manually.</html>");

		model = new DefaultListModel();
		for (Binary bin : binaries)
			model.addElement(bin);
		list = new JList(model);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setFont(list.getFont().deriveFont(Font.PLAIN));
		list.setVisibleRowCount(4);
		list.setCellRenderer(new DefaultListCellRenderer()
		{
			@Override
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
					boolean cellHasFocus)
			{
				JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				Binary b = (Binary) value;
				String found = "";
				if (b.isFound())
				{
					l.setIcon(ImageLoader.getImage(ImageLoader.Image.ok));
					found = b.getLocation();
				}
				else
				{
					l.setIcon(ImageLoader.getImage(ImageLoader.Image.error));
					found = "<b>Not found</b>";
				}
				l.setText("<html><b>" + b.getDescription() + "</b><br><table><tr><td>Program:</td><td>"
						+ b.getCommand() + "</td></tr><tr><td>Location:</td><td>" + found + "</td></tr></html>");

				l.setBorder(new EmptyBorder(5, 0, 5, 0));

				return l;

			}
		});
		b.append(new JScrollPane(list));

		close = new JButton("Close");
		relocate = new JButton("Relocate program automatically.");
		JPanel buttons = ButtonBarFactory.buildRightAlignedBar(relocate, close);
		b.append(buttons);

		b.setBorder(new EmptyBorder(10, 10, 10, 10));

		setLayout(new BorderLayout());
		add(b.getPanel());
	}

	public static void main(String args[])
	{
		Binary b = new Binary("babel", null,
				"alösdfj aslökf jalöksdjf alöskd jflöaskjdflöaksd jflöaksjd flöaksj flöaks jdflöakjs dflöaks jdf");

		List<Binary> l = new ArrayList<Binary>();
		l.add(b);

		new BinaryLocatorDialog(null, "Find binaries", "Test", l, null);
		System.exit(0);
	}

}
