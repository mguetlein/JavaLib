package org.mg.javalib.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

import com.jgoodies.forms.factories.ButtonBarFactory;

public class SwingUtil
{
	public static boolean isMouseInside(Component comp)
	{
		Point p = MouseInfo.getPointerInfo().getLocation();
		Point p2 = comp.getLocationOnScreen();
		return (p.x >= p2.x && p.y >= p2.y && p.x <= p2.x + comp.getWidth()
				&& p.y <= p2.y + comp.getHeight());
	}

	public static void waitWhileVisible(Window f)
	{
		if (SwingUtilities.isEventDispatchThread())
			throw new Error("do not wait in awt event thread");
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

	public static void waitWhileWindowsVisible()
	{
		if (SwingUtilities.isEventDispatchThread())
			throw new Error("do not wait in awt event thread");
		boolean viz = true;
		while (viz)
		{
			viz = false;
			for (Window w : Window.getWindows())
				if (w.isVisible())
					viz = true;
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

	public static void showTable(String title, List<String> names, List<Vector<Object>> values,
			int sortColumn, SortOrder sortOrder, boolean numericSort,
			final ActionListener actionListener)
	{
		showTable(title, names, values, sortColumn, sortOrder, numericSort, actionListener, null,
				null);
	}

	public static interface BackgroundPainter
	{
		public Color getColor(Object firstColumnVal, int row);
	}

	public static void showTable(String title, List<String> names, List<Vector<Object>> values,
			int sortColumn, SortOrder sortOrder, boolean numericSort,
			final ActionListener actionListener, String additionalInfo,
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
				public Component getTableCellRendererComponent(JTable table, Object value,
						boolean isSelected, boolean hasFocus, int row, int column)
				{
					if (value instanceof Double)
						value = StringUtil.formatDouble(((Double) value).doubleValue());
					return super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
							row, column);
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
						actionListener.actionPerformed(
								new ActionEvent(t.getValueAt(t.getSelectedRow(), 0), -1, ""));
				}
			}
		});
		f.setVisible(true);
		// SwingUtil.waitWhileVisible(f);
	}

	public static <T> T selectFromListWithDialog(List<T> list, T selected, String titel,
			JFrame owner)
	{
		return selectFromListWithDialog(list, selected, titel, owner, null);
	}

	@SuppressWarnings("unchecked")
	public static <T> T selectFromListWithDialog(List<T> list, T selected, String titel,
			JFrame owner, ListCellRenderer renderer)
	{
		final JDialog d = new JDialog(owner, titel);
		d.setModal(owner != null);
		DefaultListModel m = new DefaultListModel();
		final JList l = new JList(m);
		for (T t : list)
			m.addElement(t);
		if (renderer != null)
			l.setCellRenderer(renderer);
		l.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane s = new JScrollPane(l);
		JPanel p = new JPanel(new BorderLayout(10, 10));
		p.add(s);
		final JButton ok = new JButton("Select");
		JButton cancel = new JButton("Cancel");
		ActionListener al = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (e.getSource() != ok)
					l.clearSelection();
				d.setVisible(false);
			}
		};
		l.setSelectedValue(selected, true);
		l.addListSelectionListener(new ListSelectionListener()
		{
			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				ok.setEnabled(l.getSelectedIndex() != -1);
			}
		});
		ok.addActionListener(al);
		ok.setEnabled(l.getSelectedIndex() != -1);
		cancel.addActionListener(al);
		p.add(ButtonBarFactory.buildOKCancelBar(ok, cancel), BorderLayout.SOUTH);
		p.setBorder(new EmptyBorder(10, 10, 10, 10));
		d.getContentPane().add(p);
		d.pack();
		d.setLocationRelativeTo(owner);
		d.setVisible(true);
		if (!d.isModal())
			waitWhileVisible(d);
		return (T) l.getSelectedValue();
	}

	public static String toTmpFile(JComponent c)
	{
		return toTmpFile(c, null);
	}

	public static String toTmpFile(final JComponent c, Dimension dim)
	{
		try
		{
			final File file = File.createTempFile("pic", "png");
			return toFile(file.getPath(), c, dim);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static BufferedImage thresholdImage(BufferedImage image, int threshold)
	{
		BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(),
				BufferedImage.TYPE_BYTE_GRAY);
		result.getGraphics().drawImage(image, 0, 0, null);
		WritableRaster raster = result.getRaster();
		int[] pixels = new int[image.getWidth()];
		for (int y = 0; y < image.getHeight(); y++)
		{
			raster.getPixels(0, y, image.getWidth(), 1, pixels);
			for (int i = 0; i < pixels.length; i++)
			{
				if (pixels[i] < threshold)
					pixels[i] = 0;
				else
					pixels[i] = 255;
			}
			raster.setPixels(0, y, image.getWidth(), 1, pixels);
		}
		return result;
	}

	public static String toFile(final String file, final JComponent c, final Dimension dim)
	{
		final JFrame f = new JFrame();
		f.add(c);
		if (dim != null)
		{
			c.setPreferredSize(dim);
			c.setSize(dim);
		}
		f.pack();
		f.pack();
		f.setVisible(true);
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				BufferedImage bi = new BufferedImage(dim.width, dim.height,
						BufferedImage.TYPE_INT_ARGB);
				//				bi = thresholdImage(bi, 100);
				Graphics g = bi.createGraphics();
				c.paint(g);
				g.dispose();
				try
				{
					ImageIO.write(bi, "png", new File(file));
					//						System.out.println("image stored at " + file);
					//						Thread.sleep(30000);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				f.setVisible(false);
			}
		});
		waitWhileVisible(f);
		return file;
	}

	public static JFrame showInFrame(final JComponent c)
	{
		return showInFrame(c, "title");
	}

	public static JFrame showInFrame(final JComponent c, Dimension dim)
	{
		return showInFrame(c, "title", true, dim);
	}

	public static JFrame showInFrame(final JComponent c, String title)
	{
		return showInFrame(c, title, true);
	}

	public static JFrame showInFrame(final JComponent c, String title, boolean wait)
	{
		return showInFrame(c, title, wait, null);
	}

	public static JFrame showInFrame(final JComponent c, String title, boolean wait, Dimension dim)
	{
		final JFrame f = new JFrame(title);
		JPanel p = new JPanel(new BorderLayout(10, 10));
		p.add(c);
		p.setBorder(new EmptyBorder(10, 10, 10, 10));
		f.getContentPane().add(p);
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
		if (dim != null)
			f.setSize(dim.width, dim.height);
		else
		{
			f.pack();
			f.pack();
		}
		//		f.setLocationRelativeTo(null);
		ScreenUtil.centerOnScreen(f, ScreenUtil.getLargestScreen());
		f.setVisible(true);
		if (wait)
			waitWhileVisible(f);
		return f;
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
		showInDialog(c, title, dim, null);
	}

	public static void showInDialog(JComponent c, String title, Dimension dim,
			final Runnable runAfterVisible)
	{
		showInDialog(c, title, dim, runAfterVisible, null);
	}

	public static void showInDialog(JComponent c, String title, Dimension dim,
			final Runnable runAfterVisible, JFrame owner)
	{
		showInDialog(c, title, dim, runAfterVisible, owner, -1);
	}

	public static void showInDialog(JComponent c, String title, Dimension dim,
			final Runnable runAfterVisible, JFrame owner, int screenIndex)
	{
		final JDialog f = new JDialog(owner);
		f.setModal(true);
		f.setTitle(title);
		JPanel p = new JPanel(new BorderLayout(10, 10));
		p.add(c);
		p.setBorder(new EmptyBorder(10, 10, 10, 10));
		f.getContentPane().add(p);
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
		if (dim == null)
			f.pack();
		else
			f.setSize(dim);
		if (owner != null)
			f.setLocationRelativeTo(owner);
		else if (screenIndex != -1)
			ScreenUtil.centerOnScreen(f, screenIndex);
		else
			ScreenUtil.centerOnScreen(f, ScreenUtil.getLargestScreen());
		if (runAfterVisible != null)
		{
			f.addWindowListener(new WindowAdapter()
			{
				@Override
				public void windowOpened(WindowEvent e)
				{
					Thread th = new Thread(runAfterVisible);
					th.start();
				}
			});
		}
		f.setVisible(true);
	}

	private static HashMap<Component, MouseListener[]> ml = new HashMap<Component, MouseListener[]>();
	private static HashMap<Component, MouseWheelListener[]> mw = new HashMap<Component, MouseWheelListener[]>();
	private static HashMap<Component, MouseMotionListener[]> mm = new HashMap<Component, MouseMotionListener[]>();

	public static void removeMouseListeners(Component c, boolean removeFromChildren)
	{
		ml.put(c, c.getMouseListeners());
		mm.put(c, c.getMouseMotionListeners());
		mw.put(c, c.getMouseWheelListeners());
		for (MouseListener l : c.getMouseListeners())
			c.removeMouseListener(l);
		for (MouseMotionListener l : c.getMouseMotionListeners())
			c.removeMouseMotionListener(l);
		for (MouseWheelListener l : c.getMouseWheelListeners())
			c.removeMouseWheelListener(l);
		if (removeFromChildren && c instanceof Container)
			for (int i = 0; i < ((Container) c).getComponentCount(); i++)
				removeMouseListeners(((Container) c).getComponent(i), true);
	}

	public static void restoreMouseListeners(Component c, boolean restoreInChildren)
	{
		if (ml.containsKey(c))
			for (MouseListener l : ml.get(c))
				c.addMouseListener(l);
		if (mm.containsKey(c))
			for (MouseMotionListener l : mm.get(c))
				c.addMouseMotionListener(l);
		if (mw.containsKey(c))
			for (MouseWheelListener l : mw.get(c))
				c.addMouseWheelListener(l);
		if (restoreInChildren && c instanceof Container)
			for (int i = 0; i < ((Container) c).getComponentCount(); i++)
				restoreMouseListeners(((Container) c).getComponent(i), true);
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

		//		JLabel s = new JLabel();
		//		loadingLabel(s);
		//		showInDialog(s);

		JScrollPane s = new JScrollPane(new JList<String>(new String[] { "ene", "mene", "miste" }));
		setDebugBorder(s);
		removeDebugBorder(s);
		showInDialog(s);

		//		String s[] = { "ene", "mene", "miste" };
		//		System.out.println(selectFromListWithDialog(ArrayUtil.toList(s), null, "select please", null, null));
		System.exit(0);
	}

	public static void setDebugBorder(JComponent comp)
	{
		setDebugBorder(comp, Color.RED);
	}

	public static void setDebugBorder(JComponent comp, Color col)
	{
		comp.setBorder(new CompoundBorder(new LineBorder(col, 3), comp.getBorder()));
	}

	public static void removeDebugBorder(JComponent comp)
	{
		comp.setBorder(((CompoundBorder) comp.getBorder()).getInsideBorder());
	}

	public static void waitForAWTEventThread()
	{
		waitForAWTEventThread(100);
	}

	public static void waitForAWTEventThread(final long sleep)
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
				Thread.sleep(sleep);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	public static void invokeAndWait(Runnable r)
	{
		if (SwingUtilities.isEventDispatchThread())
			r.run();
		else
		{
			try
			{
				SwingUtilities.invokeAndWait(r);
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (InvocationTargetException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void checkNoAWTEventThread() throws IllegalStateException
	{
		if (SwingUtilities.isEventDispatchThread())
			throw new IllegalStateException(
					"not allowed in awt-event thread : " + Thread.currentThread().getName());
	}

	public static void checkIsAWTEventThread() throws IllegalStateException
	{
		if (!SwingUtilities.isEventDispatchThread())
			throw new IllegalStateException(
					"only allowed in awt-event thread : " + Thread.currentThread().getName());
	}
}
