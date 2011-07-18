package gui;

import javax.swing.JOptionPane;

import com.sun.org.apache.xalan.internal.xsltc.cmdline.getopt.GetOpt;

public class InfoDialog
{
	public static void main(String[] args)
	{
		// args = new String[] { "-m", "hallo leucin", "-i", "1", "-t", "testnachricht" };

		String usage = "shows info window.\n" + "usage:\n" + "-t <title> (optional, default is Nachricht)\n"
				+ "-m <message>\n" + "-i <message-type> 0:info, 1:error, 2:warning (default is 0)\n" + "";
		GetOpt opt = new GetOpt(args, "t:m:i:");

		String title = "Nachricht";
		String message = null;
		int type = 0;

		try
		{
			if (args.length == 0)
				throw new IllegalStateException("param missing");

			int o = -1;
			while ((o = opt.getNextOption()) != -1)
			{
				if (o == 't')
				{
					title = opt.getOptionArg();
				}
				else if (o == 'm')
				{
					message = opt.getOptionArg();
				}
				else if (o == 'i')
				{
					type = Integer.parseInt(opt.getOptionArg());
				}
				else
					throw new IllegalStateException("illegal param");
			}

			if (message == null || type < 0 || type > 2)
				throw new IllegalStateException("illegal params");

		}
		catch (Exception e)
		{
			System.err.println(e.getMessage());
			System.err.println();
			System.err.println(usage);
			System.exit(1);
		}

		message = message.replace("\\n", "\n");
		JOptionPane.showMessageDialog(null, message, title, type == 0 ? JOptionPane.INFORMATION_MESSAGE
				: type == 2 ? JOptionPane.WARNING_MESSAGE : JOptionPane.ERROR_MESSAGE);
	}

}
