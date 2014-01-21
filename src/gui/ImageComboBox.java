package gui;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;

import util.ArrayUtil;

public class ImageComboBox extends JComboBox
{
	DefaultComboBoxModel model = new DefaultComboBoxModel();
	ImageIcon icons[];
	Object elements[];

	public ImageComboBox()
	{
		setModel(model);
		setRenderer(new DefaultListCellRenderer()
		{
			@Override
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
					boolean cellHasFocus)
			{
				JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (index < 0)
				{
					l.setText("");
					l.setIcon(icons[ArrayUtil.indexOf(elements, value)]);
				}
				else
				{
					l.setText(value + "");
					l.setIcon(icons[index]);
				}
				return l;
			}
		});

	}

	public void addContent(Object elements[], ImageIcon icons[])
	{
		this.icons = icons;
		this.elements = elements;
		model.removeAllElements();
		maxStringWidth = 0;

		for (Object e : elements)
		{
			model.addElement(e);
			maxStringWidth = Math.max(maxStringWidth, getFontMetrics(getFont()).stringWidth(e + ""));
		}

	}

	private boolean layingOut = false;
	private int maxStringWidth = 0;

	public void doLayout()
	{
		try
		{
			layingOut = true;
			super.doLayout();
		}
		finally
		{
			layingOut = false;
		}
	}

	public Dimension getSize()
	{
		if (layingOut)
			return super.getSize();
		else
		{
			// for popup
			Dimension dim = super.getSize();
			dim.width = dim.width + maxStringWidth;
			return dim;
		}
	}

	public static void main(String[] args)
	{
		//		ImageComboBox box = new ImageComboBox();
		//		box.addContent(new String[] { "ene", "mene", "miste-miste-miste" }, new ImageIcon[] { ImageLoader.FILTER,
		//				ImageLoader.FILTER_WHITE, ImageLoader.WARNING });
		//		JPanel p = new JPanel(new BorderLayout());
		//		p.add(box, BorderLayout.WEST);
		//		SwingUtil.showInDialog(p);
		//		System.exit(0);
	}
}
