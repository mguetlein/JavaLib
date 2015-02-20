package org.mg.javalib.jitter;

import java.util.Random;

import javax.vecmath.Vector3f;

import org.mg.javalib.util.ArrayUtil;
import org.mg.javalib.util.FileUtil;
import org.mg.javalib.util.MathUtil;
import org.mg.javalib.util.StopWatchUtil;
import org.mg.javalib.util.Vector3fUtil;

public class Jittering
{
	public static boolean DEBUG = false;

	Vector3f[] v;
	float minDist;
	float stepWidth;
	Random r;
	Vector3f dirs[];
	public static int STEPS = 10;
	int steps = STEPS;
	NNComputer nn;

	public Jittering(Vector3f[] v, float minDist, Random r)
	{
		if (DEBUG)
		{
			System.out.println("before jittering");
			for (Vector3f vec : v)
				System.out.println(Vector3fUtil.toNiceString(vec));
		}
		this.v = v;
		this.minDist = minDist;
		this.r = r;
		dirs = new Vector3f[v.length];
		computeDist();
	}

	public Vector3f getPosition(int i)
	{
		return v[i];
	}

	/**
	 * returns true if neighbors found < min dist
	 */
	private Boolean computeDist()
	{
		nn = new NNComputer(v, minDist);
		nn.computeFast();
		return nn.isNeighborFound();
	}

	/**
	 * returns true if finished (no neighbors found < min dist)
	 */
	public boolean jitterStep()
	{
		if (stepWidth == 0)
			this.stepWidth = minDist / (float) steps;

		if (minDist <= 0)
			throw new IllegalStateException("set min dist");

		for (int i = 0; i < v.length; i++)
			dirs[i] = null;
		for (int i = 0; i < v.length; i++)
		{
			int neighbor = nn.getNeigbohrs()[i];
			Vector3f dir;
			if (neighbor == -1)
			{
				dir = null;
			}
			else if (dirs[neighbor] != null && nn.getNeigbohrs()[neighbor] == i)
			{
				dir = Vector3fUtil.negate(dirs[neighbor]);
			}
			else if (v[i].equals(v[neighbor]))
			{
				dir = Vector3fUtil.randomVector(1.0F, r);
				if (v[i].y == 0 && v[neighbor].y == 0)
					dir.y = 0;
				if (v[i].z == 0 && v[neighbor].z == 0)
					dir.z = 0;
				Vector3fUtil.normalize(dir, stepWidth);
			}
			else
			{
				dir = Vector3fUtil.direction(v[i], v[neighbor]);
				//					List<Vector3f> v1 = new ArrayList<Vector3f>();
				//					for (Vector3f v : objects[i].getOffsets())
				//						v1.add(Vector3fUtil.sum(v, objects[i].getPosition()));
				//					List<Vector3f> v2 = new ArrayList<Vector3f>();
				//					for (Vector3f v : objects[neighbor].getOffsets())
				//						v2.add(Vector3fUtil.sum(v, objects[neighbor].getPosition()));
				//
				//					dir = new Vector3f(0, 0, 0);
				//					for (int j1 = 0; j1 < v1.size(); j1++)
				//						for (int j2 = 0; j2 < v2.size(); j2++)
				//						{
				//							Vector3f v = Vector3fUtil.direction(v1.get(j1), v2.get(j2));
				//							Vector3fUtil.normalize(v, dist[i][neighbor] / v.length());
				//							dir.add(v);
				//						}
				Vector3fUtil.normalize(dir, stepWidth);
			}
			if (DEBUG && dir != null)
				System.out.println("move " + i + " " + Vector3fUtil.toNiceString(dir) + " (l:" + dir.length() + ")");
			dirs[i] = dir;
		}
		for (int i = 0; i < dirs.length; i++)
		{
			if (dirs[i] != null)
			{
				v[i].add(dirs[i]);
			}
		}
		return !computeDist();
	}

	public void jitter()
	{
		if (DEBUG)
			System.out.println("minDist: " + minDist + " steps: " + stepWidth);
		long start = System.currentTimeMillis();
		long reduceStepWidth = 2000; // reduce step-width if running longer than 2 seconds
		this.stepWidth = minDist / (float) steps;

		int i = 0;
		while (!jitterStep())
		{
			i++;
			if (steps > 3 && System.currentTimeMillis() - start > reduceStepWidth)
			{
				reduceStepWidth += 1000;
				steps = Math.max(3, steps - 1);
				this.stepWidth = minDist / (float) steps;
				System.out.println("jitter-iteration: " + i + " reducing step-width to " + steps);
			}
		}
		if (DEBUG)
		{
			System.out.println("after jittering");
			for (Vector3f vec : v)
				System.out.println(Vector3fUtil.toNiceString(vec));
		}
	}

	public static void main(String[] args)
	{
		try
		{
			String s = FileUtil
					.readStringFromFile("/home/martin/.ches-mapper/cache/TOX21S_v2a_8193_22Mar2012.ob_cleaned.c7be2e0e0f777fbed181e80b4f8273ea.0fde2d61225215e571ce44063f762178.embed.pos");
			Vector3f[] v = new Vector3f[8165];
			int i = 0;
			for (String strings : s.split(","))
				v[i++] = Vector3fUtil.deserialize(strings);

			StopWatchUtil.start("brute");
			NNComputer nn = new NNComputer(v);
			nn.computeNaive();
			StopWatchUtil.stop("brute");
			float dist = nn.getMinMinDist();
			float add = (nn.getMaxMinDist() - nn.getMinMinDist()) * 0.5f;
			//		Jittering j = create(c.getCompounds(), null, 1);
			//		float dist = j.getMinMinDist();
			//		float add = (j.getMaxMinDist() - j.getMinMinDist()) * 0.5f;
			double log[] = ArrayUtil.toPrimitiveDoubleArray(MathUtil.logBinning(10, 1.2));
			float minDistances[] = new float[11];
			for (int j = 1; j <= 10; j++)
				minDistances[j] = dist + add * (float) log[j];
			System.out.println(ArrayUtil.toNiceString(minDistances));

			Jittering.STEPS = 10;
			Jittering j = new Jittering(v, minDistances[1], new Random());
			j.jitter();
			StopWatchUtil.print();

		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
