package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.net.URI;

import javax.swing.JEditorPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;

import util.SwingUtil;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class TextPanel extends JPanel
{
	protected JEditorPane editorPane;
	private String body = "";
	private int preferredWith = -1;

	public TextPanel()
	{
		this(null);
	}

	public TextPanel(String paragraph)
	{
		editorPane = new JEditorPane()
		{
			public Dimension getPreferredSize()
			{
				// HACK part 2 (see part 1 below) because of bug, preferred with has to be replaced
				Dimension dim = super.getPreferredSize();
				return new Dimension(preferredWith, dim.height);
			}
		};
		editorPane.setContentType("text/html");
		Font font = UIManager.getFont("Label.font");
		String bodyRule = "body { font-family: " + font.getFamily() + "; " + "font-size: " + font.getSize() + "pt; }";
		((HTMLDocument) editorPane.getDocument()).getStyleSheet().addRule(bodyRule);
		editorPane.addHyperlinkListener(new HyperlinkListener()
		{
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e)
			{
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
				{
					try
					{
						Desktop.getDesktop().browse(new URI(e.getURL().toString()));
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}
				}
			}
		});

		editorPane.setOpaque(false);
		editorPane.setEditable(false);
		if (paragraph != null)
			addParagraph(paragraph);
		buildLayout();
	}

	protected void buildLayout()
	{
		setLayout(new BorderLayout());
		add(editorPane, BorderLayout.NORTH);
	}

	public TextPanel cloneTextPanel()
	{
		TextPanel t = new TextPanel();
		t.body = body;
		t.update();
		return t;
	}

	private void update()
	{
		editorPane.setText("<html><body>" + body + "</body></html>");

		if (preferredWith != -1)
		{
			// HACK part 1, reset preferred size and set with (see part 2 above)
			editorPane.setPreferredSize(null);
			editorPane.setSize(new Dimension(preferredWith, Integer.MAX_VALUE));
		}
	}

	public void clear()
	{
		body = "";
		update();
	}

	public void addParagraph(String text)
	{
		addParagraph(text, true);
	}

	public void addParagraph(String text, boolean parseForLinks)
	{
		if (parseForLinks)
			text = parseForLinks(text);
		body += "<p>" + text.trim().replaceAll("\n", "<br>") + "</p>";
		update();
	}

	public void addHeading(String heading)
	{
		body += "<h3>" + heading + "</h3>";
		update();
	}

	public void addTable(String[][] table)
	{
		String t = "<table>";
		for (String[] strings : table)
		{
			t += "<tr>";
			for (String string : strings)
				t += "<td>" + parseForLinks(string) + "</td>";
			t += "</tr>";
		}
		t += "</table>";
		body += t;
		update();
	}

	private static String parseForLinks(String text)
	{
		return text.replaceAll("(?i)(http(s?)://[^\\r\\n\\s\\)\\,]*)", "<a href=$0>$0</a>");
	}

	public void setPreferredWith(int w)
	{
		preferredWith = w;
		update();
	}

	public static void main(String args[])
	{
		TextPanel t = new TextPanel();
		t.addHeading("Che-S Mapper");
		t.addParagraph("Che-S Mapper (Chemical Space Mapper) is a 3D-viewer for chemical datasets with small compounds.\n"
				+ "\n"
				+ " Mapper (Chemical Space Mapper) is a 3D-viewer for chemical datas Mapper (Chemical Space Mapper) is a 3D-viewer for chemical datas\n"
				+ "It is an open-source Java application, based on the Java libraries Jmol, CDK, WEKA, and utilizes OpenBabel and R.");
		t.addParagraph("The link is http://opentox.informatik.uni-freiburg.de/ches-mapper have a look");
		t.addTable(new String[][] { { "Ene:", "mene" }, { "Mu:", "und weg bist du" } });
		t.setPreferredWith(200);
		SwingUtil.setDebugBorder(t);
		//		SwingUtil.showInDialog(new JScrollPane(t));

		//		JPanel p = new JPanel(new BorderLayout());
		//		p.add(t, BorderLayout.CENTER);
		//		JButton l = new JButton("end");
		//		p.add(l, BorderLayout.SOUTH);
		//		//		SwingUtil.showInDialog(p);
		//

		JList lab = new JList();
		SwingUtil.setDebugBorder(lab, Color.GREEN);
		lab.setPreferredSize(new Dimension(100, 300));

		JPanel pp = new JPanel(new FormLayout("fill:p:grow", "top:100:grow,fill:p:grow"));
		CellConstraints cc = new CellConstraints();
		pp.add(new JScrollPane(t), cc.xy(1, 1));

		DefaultFormBuilder b = new DefaultFormBuilder(new FormLayout("fill:p:grow"));
		b.append(lab);
		pp.add(b.getPanel(), cc.xy(1, 2));

		//		DefaultFormBuilder b = new DefaultFormBuilder(new FormLayout("fill:p:grow"));
		//		b.append(new JScrollPane(t));
		//		b.getLayout().setRowSpec(1, new RowSpec(RowSpec.TOP, Sizes.pixel(100), RowSpec.DEFAULT_GROW));
		//		b.append(lab);
		//		b.getLayout().setRowSpec(2, new RowSpec(RowSpec.TOP, Sizes.PREFERRED, RowSpec.NO_GROW));
		//		b.setRowGroupingEnabled(false);
		//		b.setParagraphGapSize(Sizes.ZERO);
		//		JPanel pp = b.getPanel();

		//		SwingUtil.showInDialog(p);

		//		JPanel pusher = new JPanel();
		//		pusher.setPreferredSize(new Dimension(100, 3000));
		//		SwingUtil.setDebugBorder(pusher, Color.YELLOW);
		//		pp.add(pusher, cc.xy(1, 3));
		//		JPanel pusher2 = new JPanel();
		//		pusher2.setPreferredSize(new Dimension(100, 3000));
		//		SwingUtil.setDebugBorder(pusher2, Color.ORANGE);
		//		pp.add(pusher2, cc.xy(1, 4));

		SwingUtil.showInDialog(pp, new Dimension(400, 200));

		System.exit(0);
	}
}
