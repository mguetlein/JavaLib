package gui.binloc;

import java.util.List;
import java.util.Map;

public class BinaryLocator
{
	public static boolean locate(List<Binary> binaries)
	{
		Map<String, String> env = System.getenv();
		String pathDirs[] = env.get("PATH").split(":");
		boolean allFound = true;
		for (Binary bin : binaries)
		{
			// may already be found in earlier call
			if (bin.isFound())
				continue;

			// check envName
			if (bin.getEnvName() != null)
			{
				if (env.get(bin.getEnvName()) != null)
					bin.setLocation(env.get(bin.getEnvName()));
				if (bin.isFound())
					continue;
			}

			// check Path
			for (String dir : pathDirs)
			{
				bin.setParent(dir);
				if (bin.isFound())
					break;
			}
			if (bin.isFound())
				continue;

			allFound = false;
		}
		return allFound;
	}
}
