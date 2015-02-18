package org.mg.javalib.freechart;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.mg.javalib.gui.Message;
import org.mg.javalib.gui.MessageType;
import org.mg.javalib.gui.Messages;

public class MessageChartPanel extends ChartPanel
{
	Messages msg = new Messages();

	public MessageChartPanel(JFreeChart chart)
	{
		super(chart);
	}

	public String getWarning()
	{
		return msg.getMessage(MessageType.Warning).getString();
	}

	public void setWarning(String string)
	{
		msg.add(Message.warningMessage(string));
	}

}
