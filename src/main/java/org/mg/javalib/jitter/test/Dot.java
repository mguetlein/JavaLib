package org.mg.javalib.jitter.test;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.vecmath.Vector3f;

public class Dot extends Vector3f// Jitterable
{
	//	Vector3f pos = new Vector3f(0, 0, 0);
	Color col = Color.BLACK;
	int size = 5;

	public Dot(float x, float y, Color col)
	{
		super(x, y, 0.0f);
		//		pos.x = x;
		//		pos.y = y;
		this.col = col;
	}

	//	@Override
	//	public Vector3f getPosition()
	//	{
	//		return pos;
	//	}
	//
	//	@Override
	//	public Vector3f[] getOffsets()
	//	{
	//		return new Vector3f[] { new Vector3f(0, 0, 0) };
	//	}

	public void draw(Graphics2D g)
	{
		g.setColor(col);
		g.fillOval((int) (x - size / 2.0), (int) (y - size / 2.0), size, size);
	}

}
