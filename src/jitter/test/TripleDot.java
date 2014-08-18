package jitter.test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

import javax.vecmath.Vector3f;

import util.Vector3fUtil;

public class TripleDot //implements Jitterable
{
	Vector3f pos = new Vector3f(0, 0, 0);
	Vector3f offsets[];

	Color col = Color.BLACK;

	int size = 4;
	int offsetSize = 8;
	int offsetLength = 12;

	public TripleDot(float x, float y, Color col, Random r)
	{
		pos.x = x;
		pos.y = y;
		this.col = col;

		offsets = new Vector3f[2];
		offsets[0] = new Vector3f(r.nextFloat() * (r.nextBoolean() ? -1 : 1), r.nextFloat()
				* (r.nextBoolean() ? -1 : 1), 0);
		Vector3fUtil.normalize(offsets[0], offsetLength);
		offsets[1] = Vector3fUtil.negate(offsets[0]);

		//		offsets = new Vector3f[2 + r.nextInt(3)];
		//		for (int i = 0; i < offsets.length; i++)
		//		{
		//			offsets[i] = new Vector3f(r.nextFloat() * (r.nextBoolean() ? -1 : 1), r.nextFloat()
		//					* (r.nextBoolean() ? -1 : 1), 0);
		//			Vector3fUtil.normalize(offsets[i], (float) (offsetLength / 2.0 + r.nextFloat() * offsetLength / 2.0));
		//		}

		//		}
	}

	//	@Override
	public Vector3f getPosition()
	{
		return pos;
	}

	//	@Override
	public Vector3f[] getOffsets()
	{
		return offsets;
	}

	public void draw(Graphics2D g)
	{
		g.setColor(col);
		g.fillOval((int) (pos.x - size / 2.0), (int) (pos.y - size / 2.0), size, size);
		for (int i = 0; i < offsets.length; i++)
		{
			g.fillOval((int) (pos.x + offsets[i].x - offsetSize / 2.0),
					(int) (pos.y + offsets[i].y - offsetSize / 2.0), offsetSize, offsetSize);
			g.drawLine((int) (pos.x + offsets[i].x), (int) (pos.y + offsets[i].y), (int) (pos.x), (int) (pos.y));
		}

	}
}
