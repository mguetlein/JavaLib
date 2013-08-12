package gui.property;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.UUID;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.jgoodies.forms.factories.ButtonBarFactory;

public class ColorGradientChooser
{
	public static ColorGradient show(Window owner, String title, ColorGradient col)
	{
		ColorGradientProperty prop = new ColorGradientProperty("Color gradient", "Color gradient"
				+ UUID.randomUUID().toString(), col);
		PropertyPanel p = new PropertyPanel(prop);
		final JDialog d = new JDialog(owner, title);
		d.setModal(true);
		final JButton ok = new JButton("OK");
		final JButton cancel = new JButton("Cancel");
		final StringBuffer b = new StringBuffer();
		ActionListener l = new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				b.append(((JButton) e.getSource()).getText());
				d.setVisible(false);
			}
		};
		ok.addActionListener(l);
		cancel.addActionListener(l);
		JPanel panel = new JPanel(new BorderLayout(10, 10));
		panel.add(p, BorderLayout.CENTER);
		panel.add(ButtonBarFactory.buildOKCancelBar(ok, cancel), BorderLayout.SOUTH);
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));
		d.add(panel);
		d.pack();
		d.setLocationRelativeTo(owner);
		d.setVisible(true);
		if (b.toString().equals(ok.getText()))
			return prop.getValue();
		else
			return null;
	}

	public static void main(String[] args)
	{
		System.out.println(ColorGradientChooser.show(null, "Select plz", new ColorGradient()));
		System.exit(1);
	}
}
