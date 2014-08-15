package jitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.vecmath.Vector3f;

import util.ArrayUtil;
import util.Vector3fUtil;

public class Jittering
{
	public static boolean DEBUG = false;

	Jitterable[] objects;
	float minDist;
	float stepWidth;
	Random r;
	float dist[][];
	int neighbors[];
	Vector3f dirs[];
	float minMinDistInData;
	float maxMinDistInData;
	static boolean ignoreOffsets = false;

	public Jittering(Jitterable[] objects, Random r)
	{
		this(objects, -1, r);
	}

	public Jittering(Jitterable[] objects, float minDist, Random r)
	{
		if (DEBUG)
		{
			System.out.println("before jittering");
			for (Jitterable jitterable : objects)
				System.out.println(Vector3fUtil.toNiceString(jitterable.getPosition()));
		}
		this.objects = objects;
		this.minDist = minDist;
		this.r = r;
		this.stepWidth = minDist / 10.0f;
		dist = new float[objects.length][objects.length];
		for (int i = 0; i < objects.length; i++)
			dist[i][i] = Float.MAX_VALUE;
		neighbors = new int[objects.length];
		dirs = new Vector3f[objects.length];
		computeDist();
	}

	public Vector3f getPosition(int i)
	{
		return objects[i].getPosition();
	}

	public float getMinMinDist()
	{
		return minMinDistInData;
	}

	public float getMaxMinDist()
	{
		return maxMinDistInData;
	}

	public void setMinDist(float minDist)
	{
		this.minDist = minDist;
		this.stepWidth = minDist / 10.0f;
	}

	public boolean isJitteringDiscouraged()
	{
		return minMinDistInData / maxMinDistInData > 0.66;
	}

	private boolean computeDist()
	{
		boolean jitteringRequired = false;
		minMinDistInData = Float.MAX_VALUE;
		maxMinDistInData = 0;
		for (int i = 0; i < objects.length - 1; i++)
		{
			for (int j = i + 1; j < objects.length; j++)
			{
				float d = Vector3fUtil.dist(objects[i].getPosition(), objects[j].getPosition());
				dist[i][j] = d;
				dist[j][i] = d;
			}
		}
		for (int i = 0; i < neighbors.length; i++)
		{
			int idx = ArrayUtil.getMinIndex(dist[i]);
			if (dist[i][idx] < minMinDistInData)
				minMinDistInData = dist[i][idx];
			if (dist[i][idx] > maxMinDistInData)
				maxMinDistInData = dist[i][idx];
			neighbors[i] = idx;
			dirs[i] = null;
			if (dist[i][idx] < minDist)
				jitteringRequired = true;
		}
		if (DEBUG)
			System.out.println("ratio: " + minMinDistInData / maxMinDistInData + " min:" + minMinDistInData + " max:"
					+ maxMinDistInData);
		return jitteringRequired;
	}

	public boolean jitterStep()
	{
		if (minDist <= 0)
			throw new IllegalStateException("set min dist");

		for (int i = 0; i < dirs.length; i++)
		{
			int neighbor = neighbors[i];
			Vector3f dir;
			if (dist[i][neighbor] >= minDist)
			{
				dir = null;
			}
			else if (dirs[neighbor] != null && neighbors[neighbor] == i)
			{
				dir = Vector3fUtil.negate(dirs[neighbor]);
			}
			else if (objects[i].getPosition().equals(objects[neighbor].getPosition()))
			{
				dir = Vector3fUtil.randomVector(1.0F, r);
				if (objects[i].getPosition().y == 0 && objects[neighbor].getPosition().y == 0)
					dir.y = 0;
				if (objects[i].getPosition().z == 0 && objects[neighbor].getPosition().z == 0)
					dir.z = 0;
				Vector3fUtil.normalize(dir, stepWidth);
			}
			else
			{
				if (ignoreOffsets)
				{
					dir = new Vector3f(objects[i].getPosition());
					dir.sub(objects[neighbor].getPosition());
				}
				else
				{
					List<Vector3f> v1 = new ArrayList<Vector3f>();
					for (Vector3f v : objects[i].getOffsets())
						v1.add(Vector3fUtil.sum(v, objects[i].getPosition()));
					List<Vector3f> v2 = new ArrayList<Vector3f>();
					for (Vector3f v : objects[neighbor].getOffsets())
						v2.add(Vector3fUtil.sum(v, objects[neighbor].getPosition()));

					dir = new Vector3f(0, 0, 0);
					for (int j1 = 0; j1 < v1.size(); j1++)
						for (int j2 = 0; j2 < v2.size(); j2++)
						{
							Vector3f v = Vector3fUtil.direction(v1.get(j1), v2.get(j2));
							//							System.out.println("v1 " + v1.get(j1));
							//							System.out.println("v2 " + v2.get(j2));
							//							System.out.println("v " + v + " " + v.length());
							Vector3fUtil.normalize(v, dist[i][neighbor] / v.length());
							//							System.out.println("v " + v + " " + v.length());
							dir.add(v);
						}
				}
				//				System.out.println("d " + dir);
				Vector3fUtil.normalize(dir, stepWidth);
				//System.out.println("d " + dir);
			}
			if (DEBUG && dir != null)
				System.out.println("move " + i + " " + Vector3fUtil.toNiceString(dir) + " (l:" + dir.length() + ")");
			dirs[i] = dir;
		}
		for (int i = 0; i < dirs.length; i++)
		{
			if (dirs[i] != null)
			{
				objects[i].getPosition().add(dirs[i]);
			}
		}
		return !computeDist();
	}

	public void jitter()
	{
		if (DEBUG)
			System.out.println("minDist: " + minDist + " steps: " + stepWidth);
		while (!jitterStep())
		{
			//do nothing;
		}
		if (DEBUG)
		{
			System.out.println("after jittering");
			for (Jitterable jitterable : objects)
				System.out.println(Vector3fUtil.toNiceString(jitterable.getPosition()));
		}
	}

}
