package gui;

import javax.swing.Action;

public class Message
{
	String string;
	MessageType type;
	Action action;

	public Message(String string, MessageType type)
	{
		this.string = string;
		this.type = type;
	}

	public static Message infoMessage(String string)
	{
		return new Message(string, MessageType.Info);
	}

	public static Message slowMessage(String string)
	{
		return new Message(string, MessageType.Slow);
	}

	public static Message slowMessage(String string, Action action)
	{
		Message m = new Message(string, MessageType.Slow);
		m.action = action;
		return m;
	}

	public static Message warningMessage(String string)
	{
		return new Message(string, MessageType.Warning);
	}

	public static Message errorMessage(String string)
	{
		return new Message(string, MessageType.Error);
	}

	public String getString()
	{
		return string;
	}

	public MessageType getType()
	{
		return type;
	}

	public Action getAction()
	{
		return action;
	}

}
