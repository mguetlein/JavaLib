package jitter.test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

import javax.swing.JPanel;

import jitter.Jittering;
import util.ColorUtil;
import util.SwingUtil;
import util.ThreadUtil;

public class DotTest
{

	static long seed = -2551159139782621652L;
	static
	{
		seed = new Random().nextLong();
		System.out.println(seed);
	}
	Random r = new Random(seed);
	int width = 400;
	int height = 400;
	int margin = 150;
	int numDots = 20;
	int minDist = 10;
	TripleDot[] dots = new TripleDot[numDots];

	public DotTest()
	{
		float x = 0;
		float y = 0;
		for (int i = 0; i < dots.length; i++)
		{
			if (i == 0 || r.nextDouble() > 0.1)
			{
				x = margin + r.nextFloat() * (width - margin * 2);
				y = margin + r.nextFloat() * (height - margin * 2);
			}
			dots[i] = new TripleDot(x, y, ColorUtil.getRandomColor(r), r);//new Random(789));
		}
		final JPanel p = new JPanel()
		{
			@Override
			public void paint(Graphics g)
			{
				((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setColor(Color.WHITE);
				g.fillRect(0, 0, width, height);
				for (int i = 0; i < dots.length; i++)
					dots[i].draw((Graphics2D) g);
			}
		};
		p.setPreferredSize(new Dimension(width, height));

		p.addKeyListener(new KeyAdapter()
		{
			int minD = -1;

			public void keyPressed(KeyEvent e)
			{
				if (minD == -1)
					minD = minDist;
				Thread th = new Thread(new Runnable()
				{
					public void run()
					{
						Jittering j = new Jittering(dots, minD, r);
						while (!j.jitterStep())
						{
							ThreadUtil.sleep(20);
							System.out.println("jittering");
							SwingUtil.invokeAndWait(new Runnable()
							{

								@Override
								public void run()
								{
									p.repaint();
								}
							});
						}
						System.out.println("jittering done");
						p.repaint();
						minD += minDist;
					}
				});
				th.start();
			}

		});
		SwingUtil.showInDialog(p, null, null, new Runnable()
		{
			@Override
			public void run()
			{
				p.requestFocus();
				SwingUtil.waitWhileVisible((Window) p.getTopLevelAncestor());
				System.exit(0);
			}
		}, null, 1);
	}

	public static void main(String[] args)
	{
		new DotTest();
	}
}
