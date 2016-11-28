package org.mg.javalib.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import org.mg.imagelib.ImageLoader;
import org.mg.javalib.util.ArrayUtil;
import org.mg.javalib.util.SwingUtil;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

public class ColorSequenceEditor extends JPanel
{
	JScrollPane scroll;

	JLabel labels[];
	Color colors[];
	JButton buttons[];

	public ColorSequenceEditor(Color col[])
	{
		this(col, null);
	}

	public ColorSequenceEditor(Color col[], String labelText[])
	{
		this.colors = new Color[col.length];
		for (int i = 0; i < col.length; i++)
			this.colors[i] = new Color(col[i].getRed(), col[i].getGreen(), col[i].getBlue());
		buildLayout(labelText);
	}

	class UpDownActionListener implements ActionListener
	{
		int idx;
		boolean up;

		public UpDownActionListener(int idx, boolean up)
		{
			this.idx = idx;
			this.up = up;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			int idx1 = idx;
			int idx2;
			if (up)
				idx2 = (idx1 == 0) ? (colors.length - 1) : (idx1 - 1);
			else
				idx2 = (idx1 == colors.length - 1) ? 0 : (idx1 + 1);
			Color tmp = colors[idx1];
			colors[idx1] = colors[idx2];
			colors[idx2] = tmp;
			buttons[idx1].setBackground(colors[idx1]);
			buttons[idx2].setBackground(colors[idx2]);
		}
	}

	public void updateLabels(String[] labelText)
	{
		if (labelText == null)
			labelText = new String[0];
		for (int i = 0; i < labelText.length; i++)
			labels[i].setText(labelText[i]);
		for (int i = labelText.length; i < labels.length; i++)
			labels[i].setText("");
	}

	private void buildLayout(String labelText[])
	{
		DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout("p,5px,p,10px,p,10px,p"));
		buttons = new JButton[colors.length];
		labels = new JLabel[colors.length];
		int count = 0;
		for (Color c : colors)
		{
			JLabel label = new JLabel();
			if (labelText != null && labelText.length > count && labelText[count] != null)
				label.setText(labelText[count]);
			else
				label.setText("");
			labels[count] = label;

			JButton colorButton = new JButton("          ");
			colorButton.setFocusable(false);
			colorButton.setBackground(c);
			final int idx = count;
			buttons[idx] = colorButton;
			ActionListener l = new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					Color c = JColorChooser.showDialog(ColorSequenceEditor.this.getTopLevelAncestor(), "Select Color",
							((JButton) e.getSource()).getBackground());
					if (c != null && !((JButton) e.getSource()).getBackground().equals(c))
					{
						((JButton) e.getSource()).setBackground(c);
						colors[idx] = c;
					}
				}
			};
			colorButton.addActionListener(l);

			JButton up = new JButton(ImageLoader.getImage(ImageLoader.Image.up14));
			up.setMargin(new Insets(2, 2, 2, 2));
			up.addActionListener(new UpDownActionListener(count, true));
			JButton down = new JButton(ImageLoader.getImage(ImageLoader.Image.down14));
			down.setMargin(new Insets(2, 2, 2, 2));
			down.addActionListener(new UpDownActionListener(count, false));

			builder.append(up);
			builder.append(down);
			builder.append(colorButton);
			builder.append(label);
			builder.nextLine();

			count++;
		}
		builder.setBorder(new EmptyBorder(10, 10, 10, 10));
		scroll = new JScrollPane(builder.getPanel());
		setLayout(new BorderLayout());
		add(scroll);
	}

	public Color[] getSequence()
	{
		return colors;
	}

	public static void main(String[] args)
	{
		Color col[] = new Color[] { Color.RED, Color.BLACK, Color.CYAN, Color.GRAY, Color.GREEN, Color.YELLOW };
		col = ArrayUtil.concat(col, col, col, col);
		ColorSequenceEditor edit = new ColorSequenceEditor(col);
		edit.setPreferredSize(new Dimension(edit.getPreferredSize().width + 30, 400));
		SwingUtil.showInDialog(edit);
		System.exit(0);
	}

}
