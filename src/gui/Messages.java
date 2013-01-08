package gui;

import java.util.HashMap;

public class Messages
{
	private HashMap<MessageType, Message> map = new HashMap<MessageType, Message>();

	public Messages()
	{
	}

	private Messages(Message msg)
	{
		add(msg);
	}

	public static Messages infoMessage(String string)
	{
		return new Messages(Message.infoMessage(string));
	}

	public static Messages slowMessage(String string)
	{
		return new Messages(Message.slowMessage(string));
	}

	public static Messages slowMessage(String string, String url, String urlText)
	{
		return new Messages(Message.slowMessage(string, url, urlText));
	}

	public static Messages warningMessage(String string)
	{
		return new Messages(Message.warningMessage(string));
	}

	public static Messages errorMessage(String string)
	{
		return new Messages(Message.errorMessage(string));
	}

	public void add(Message message)
	{
		if (!map.containsKey(message.type))
			map.put(message.type, message);
		else
			map.get(message.type).string += "\n" + message.string;
	}

	public boolean containsError()
	{
		return map.containsKey(MessageType.Error);
	}

	public boolean containsWarning()
	{
		return map.containsKey(MessageType.Warning);
	}

	public boolean containsSlow()
	{
		return map.containsKey(MessageType.Slow);
	}

	public Message getMostUrgentMessage()
	{
		if (containsError())
			return map.get(MessageType.Error);
		if (containsWarning())
			return map.get(MessageType.Warning);
		if (containsSlow())
			return map.get(MessageType.Slow);
		return map.get(MessageType.Info);
	}

	public int getSize()
	{
		return map.size();
	}

	public void clear()
	{
		map.clear();
	}

	public Message getMessage(MessageType type)
	{
		return map.get(type);
	}
}
