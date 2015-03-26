package org.mg.javalib.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.HashMap;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import org.mg.javalib.util.SwingUtil;

public class DescriptionListCellRenderer extends DefaultListCellRenderer
{
	HashMap<Integer, String> descriptions = new HashMap<Integer, String>();

	Color descriptionForeground = getForeground();
	int descriptionSizeOffset = -2;
	int descriptionSpaceTop = 0;
	int descriptionFontStyle = Font.ITALIC;

	public DescriptionListCellRenderer()
	{
	}

	public void addDescription(int index, String description)
	{
		descriptions.put(index, description);
	}

	public void clearDescriptions()
	{
		descriptions.clear();
	}

	public void setDescriptionForeground(Color descriptionForeground)
	{
		this.descriptionForeground = descriptionForeground;
	}

	public void setDescriptionSizeOffset(int offset)
	{
		descriptionSizeOffset = offset;
	}

	public void setDescriptionSpaceTop(int space)
	{
		descriptionSpaceTop = space;
	}

	public void setDescriptionFontStyle(int style)
	{
		descriptionFontStyle = style;
	}

	public ImageIcon getIcon(Object value)
	{
		return null;
	}

	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
			boolean cellHasFocus)
	{
		if (descriptions.containsKey(index))
		{
			JPanel p = new JPanel(new BorderLayout());
			p.setOpaque(false);
			JLabel l = new JLabel(descriptions.get(index));
			l.setBorder(new MatteBorder(0, 0, 1, 0, descriptionForeground));
			l.setForeground(descriptionForeground);
			l.setFont(this.getFont().deriveFont(descriptionFontStyle)
					.deriveFont((float) (this.getFont().getSize() + descriptionSizeOffset)));
			p.add(l, BorderLayout.NORTH);
			if (index == 0)
				p.setBorder(null);
			else
				p.setBorder(new EmptyBorder(descriptionSpaceTop, 0, 0, 0));
			JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			label.setIcon(getIcon(value));
			p.add(label);
			return p;
		}
		else
		{
			JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			label.setIcon(getIcon(value));
			return label;
		}
	}

	public static void main(String args[])
	{
		JComboBox b = new JComboBox(new String[] { "ene", "mene", "miste" });
		DescriptionListCellRenderer r = new DescriptionListCellRenderer();
		r.addDescription(1, "some usefull info:");

		b.setRenderer(r);

		SwingUtil.showInDialog(b);
	}

}
