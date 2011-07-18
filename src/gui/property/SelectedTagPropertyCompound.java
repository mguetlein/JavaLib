package gui.property;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;

import weka.core.SelectedTag;
import weka.core.Tag;

public class SelectedTagPropertyCompound extends JComboBox implements PropertyCompound
{
	SelectedTagProperty property;

	public SelectedTagPropertyCompound(SelectedTagProperty property)
	{
		super(property.value.getTags());
		setSelectedItem(property.value.getSelectedTag());

		setRenderer(new DefaultListCellRenderer()
		{
			@Override
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
					boolean cellHasFocus)
			{
				return super.getListCellRendererComponent(list, ((Tag) value).getReadable(), index, isSelected,
						cellHasFocus);
			}
		});

		addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (getSelectedItem() != SelectedTagPropertyCompound.this.property.value.getSelectedTag())
				{
					SelectedTagPropertyCompound.this.property.value = new SelectedTag(getSelectedIndex(),
							SelectedTagPropertyCompound.this.property.value.getTags());
				}
			}
		});

		this.property = property;

	}
}