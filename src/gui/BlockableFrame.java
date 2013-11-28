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

import util.SwingUtil;

public class BlockableFrame extends JFrame implements Blockable
{
	JPanel glass;
	JPanel coverPanel;
	private static boolean DEBUG = false;
	public boolean strictWithAWTBlocking = false;
	List<String> block = new ArrayList<String>();

	public BlockableFrame(boolean strictWithAWTBlocking)
	{
		super();
		this.strictWithAWTBlocking = strictWithAWTBlocking;

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

	public BlockableFrame(String title, boolean strictWithAWTBlocking)
	{
		this(strictWithAWTBlocking);
		setTitle(title);
	}

	public void setStrictWithAWTBlocking(boolean strictWithAWTBlocking)
	{
		this.strictWithAWTBlocking = strictWithAWTBlocking;
	}

	@Override
	public void block(String blocker)
	{
		if (strictWithAWTBlocking)
			SwingUtil.checkIsAWTEventThread();
		synchronized (block)
		{
			if (block.contains(blocker))
				throw new Error("already blocking for: " + blocker);
			block.add(blocker);
			if (DEBUG)
				System.out.println(this.hashCode() + " BLOCK (" + hashCode() + " " + block.size() + ") '" + blocker
						+ "' ------------------");
			coverPanel.setVisible(true);
			coverPanel.requestFocus();
			firePropertyChange(BLOCKED, null, blocker);
		}
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
	public void unblock(final String blocker)
	{
		if (strictWithAWTBlocking)
			SwingUtil.checkIsAWTEventThread();
		synchronized (block)
		{
			if (!block.contains(blocker))
				throw new Error("use block first for " + blocker);
			if (DEBUG)
				System.out.println(this.hashCode() + " UNBLOCK (" + hashCode() + " " + block.size() + ") '" + blocker
						+ "'");
			block.remove(blocker);
			if (block.size() > 0)
				firePropertyChange(UN_BLOCKED, null, blocker);
			else
				SwingUtilities.invokeLater(new Runnable()
				{
					@Override
					public void run()
					{
						synchronized (block)
						{
							if (block.size() == 0)
							{
								coverPanel.setVisible(false);
								firePropertyChange(UN_BLOCKED, null, blocker);
								if (DEBUG)
									System.out.println(BlockableFrame.this.hashCode() + " ---------------- UNBLOCKED");
							}
							else
								firePropertyChange(UN_BLOCKED, null, blocker);
						}
					}
				});
		}
	}

}
