package jitter.test;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.vecmath.Vector3f;

import jitter.Jitterable;

public class Dot implements Jitterable
{
	Vector3f pos = new Vector3f(0, 0, 0);
	Color col = Color.BLACK;
	int size = 16;

	public Dot(float x, float y, Color col)
	{
		pos.x = x;
		pos.y = y;
		this.col = col;
	}

	@Override
	public Vector3f getPosition()
	{
		return pos;
	}

	@Override
	public Vector3f[] getOffsets()
	{
		return new Vector3f[] { new Vector3f(0, 0, 0) };
	}

	public void draw(Graphics2D g)
	{
		g.setColor(col);
		g.fillOval((int) (pos.x - size / 2.0), (int) (pos.y - size / 2.0), size, size);
	}

}
