package org.mg.javalib.io;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.mg.javalib.gui.TextPanel;
import org.mg.javalib.util.StringUtil;

import com.jgoodies.forms.factories.ButtonBarFactory;

public class Logger
{
	String logfile;
	BufferedWriter fileWriter;
	boolean logging = false;
	boolean printToSystemOut;
	StringBuffer stringBuffer;
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final PrintStream origStdErr = System.err;
	private static final PrintStream origStdOut = System.out;
	private static final String lineSeparator = System.getProperty("line.separator");

	public void logFromStdOut()
	{
		logFromStd(false);
	}

	public void logFromStdErr()
	{
		logFromStd(true);
	}

	private void logFromStd(final boolean err)
	{
		PrintStream ps = new PrintStream(new ByteArrayOutputStream()
		{
			@Override
			public void flush() throws IOException
			{
				String record;
				synchronized (this)
				{
					super.flush();
					record = this.toString();
					super.reset();
					if (record.length() == 0 || record.equals(lineSeparator))
						return;
					if (logging)
					{
						if (err)
							origStdErr.println(record);
						else
							origStdOut.println(record);
					}
					else
						println(record, err ? Status.error : Status.info);
				}
			}
		}, true);
		if (err)
			System.setErr(ps);
		else
			System.setOut(ps);

	}

	public Logger(String logfile, boolean printToSystemOut)
	{
		try
		{
			if (logfile != null)
			{
				this.logfile = logfile;
				fileWriter = new BufferedWriter(new FileWriter(new File(logfile), true));
			}

		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		stringBuffer = new StringBuffer();
		this.printToSystemOut = printToSystemOut;
	}

	public String getText()
	{
		return stringBuffer.toString();
	}

	public synchronized void error(Throwable error)
	{
		println(error.getMessage(), Status.error);
		if (printToSystemOut)
			error.printStackTrace();
		if (fileWriter != null)
		{
			try
			{
				FileWriter fw = new FileWriter(new File(logfile), true);
				PrintWriter pw = new PrintWriter(fw);
				error.printStackTrace(pw);
				pw.close();
				fw.close();
			}
			catch (IOException e)
			{
				System.err.println("could not access logfile '" + logfile + "'");
				e.printStackTrace();
			}
		}
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		error.printStackTrace(pw);
		pw.flush();
		sw.flush();
		stringBuffer.append(sw.toString());
		pw.close();
	}

	public void info(Object o)
	{
		info(String.valueOf(o));
	}

	public void info()
	{
		info("");
	}

	public void info(String msg)
	{
		println(msg, Status.info);
	}

	public void debug(String msg)
	{
		println(msg, Status.debug);
	}

	public void warn(String msg)
	{
		println(msg, Status.warn);
	}

	public void error(String msg)
	{
		println(msg, Status.error);
	}

	enum Status
	{
		info, debug, warn, error;

		String getChar()
		{
			switch (this)
			{
				case info:
					return "I";
				case debug:
					return "D";
				case warn:
					return "W";
				case error:
					return "E";
			}
			return null;
		}
	}

	private synchronized void println(String msg, Status status)
	{
		logging = true;
		String logMsg = dateFormat.format(new Date()) + " - " + status.getChar() + " - " + msg;
		stringBuffer.append(logMsg);
		stringBuffer.append("\n");
		try
		{
			if (fileWriter != null)
			{
				fileWriter.write(logMsg);
				fileWriter.write("\n");
				fileWriter.flush();
			}
		}
		catch (IOException e)
		{
			System.err.println("could not log to logfile '" + logfile + "'");
			e.printStackTrace();
		}
		if (printToSystemOut)
		{
			if (status == Status.info || status == Status.debug)
				System.out.println(msg);
			else
				System.err.println(msg);
		}
		logging = false;
	}

	public void showDialog(Window owner)
	{
		TextPanel text = new TextPanel();
		text.addParagraph(getText());
		text.setPreferredWith(500);

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout(10, 10));
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));
		JScrollPane scroll = new JScrollPane(text);
		panel.add(scroll);

		final JDialog dialog = new JDialog(owner);

		JButton copy = new JButton("Copy to clipboard");
		copy.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
						clipboard.setContents(new StringSelection(Logger.this.getText()), new ClipboardOwner()
						{
							@Override
							public void lostOwnership(Clipboard clipboard, Transferable contents)
							{
							}
						});
					}
				});
			}
		});
		JButton close = new JButton("Close");
		close.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				dialog.setVisible(false);
			}
		});
		panel.add(ButtonBarFactory.buildHelpCloseBar(copy, close), BorderLayout.SOUTH);

		dialog.setModal(true);
		dialog.setTitle("CheS-Mapper Log");
		dialog.getContentPane().add(panel);
		dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		dialog.pack();
		if (dialog.getWidth() > 600 || dialog.getHeight() > 800)
			dialog.setSize(new Dimension(600, 800));
		dialog.setLocationRelativeTo(owner);
		dialog.setVisible(true);
	}

	public static void main(String args[]) throws IOException
	{
		Logger l = new Logger("/tmp/logfile", true);
		l.logFromStd(true);
		l.logFromStd(false);

		l.info("hallo leucin");
		Random r = new Random();
		for (int i = 0; i < 1; i++)
			l.info(StringUtil.randomString(0, 100, r));
		l.info("test 1");
		l.error(new IllegalStateException("bla"));
		l.info("info after error");
		//System.out.println(">>> logged >>>\n" + l.getText());

		new IllegalArgumentException("osterhase").printStackTrace();
		System.out.println("weihnachtsmann");

		l.showDialog(null);
		System.exit(0);
	}
}
