package gui;

import java.awt.Cursor;
import java.awt.LayoutManager;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.OverlayLayout;
import javax.swing.SwingUtilities;

public class BlockableFrame extends JFrame implements Blockable
{
	JPanel glass;
	JPanel coverPanel;
	private static boolean DEBUG = false;
	List<String> block = new ArrayList<String>();

	public BlockableFrame()
	{
		super();
		glass = new JPanel();
		LayoutManager layout = new OverlayLayout(glass);
		glass.setLayout(layout);

		coverPanel = new JPanel();
		coverPanel.setOpaque(false);
		MouseAdapter listener = new MouseAdapter()
		{
		};
		coverPanel.addMouseListener(listener);
		coverPanel.addMouseMotionListener(listener);
		coverPanel.setVisible(false);
		coverPanel.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		glass.add(coverPanel);

		setGlassPane(glass);
		glass.setVisible(true);
		glass.setOpaque(false);
	}

	public BlockableFrame(String title)
	{
		this();
		setTitle(title);
	}

	@Override
	public synchronized void block(String blocker)
	{
		if (block.contains(blocker))
			throw new Error("already blocking for: " + blocker);
		block.add(blocker);
		if (DEBUG)
			System.out.println("BLOCK (" + block.size() + ") '" + blocker + "' ------------------");
		coverPanel.setVisible(true);
		coverPanel.requestFocus();
	}

	@Override
	public boolean isBlocked()
	{
		if (isVisible())
			return coverPanel.isVisible();
		else
			return block.size() > 0;
	}

	@Override
	public synchronized void unblock(String blocker)
	{
		if (!block.contains(blocker))
			throw new Error("use block first for " + blocker);
		block.remove(blocker);
		if (block.size() == 0)
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override
				public void run()
				{
					coverPanel.setVisible(false);
					if (DEBUG)
						System.out.println("---------------- UNBLOCK");
				}
			});
	}

}
