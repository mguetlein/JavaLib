package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.HashMap;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

import util.SwingUtil;

import com.lowagie.text.Font;

public class DescriptionListCellRenderer extends DefaultListCellRenderer
{
	HashMap<Integer, String> descriptions = new HashMap<Integer, String>();

	Color descriptionForeground = getForeground();

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
			l.setFont(this.getFont().deriveFont(Font.ITALIC).deriveFont((float) (this.getFont().getSize() - 2)));
			p.add(l, BorderLayout.NORTH);
			p.add(super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus));

			return p;
		}
		else
			return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
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
