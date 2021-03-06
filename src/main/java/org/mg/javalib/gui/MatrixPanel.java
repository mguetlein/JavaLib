package org.mg.javalib.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.MatteBorder;

import org.mg.javalib.util.ArrayUtil;
import org.mg.javalib.util.CorrelationMatrix;
import org.mg.javalib.util.FileUtil;
import org.mg.javalib.util.StringUtil;
import org.mg.javalib.util.SwingUtil;
import org.mg.javalib.util.CorrelationMatrix.PearsonBooleanCorrelationMatrix;
import org.mg.javalib.util.CorrelationMatrix.PearsonDoubleCorrelationMatrix;
import org.mg.javalib.util.CorrelationMatrix.SpearmanDoubleCorrelationMatrix;
import org.mg.javalib.util.FileUtil.CSVFile;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class MatrixPanel extends JPanel
{
	private double infoTextSmaller = 0.9;
	private int minNumValues = 2;
	private String titleString = "Matrix";
	private String subtitleString = null;
	private Font titleFont = null;

	public MatrixPanel()
	{
		setFont(new Font("sans", Font.PLAIN, 12));
		titleFont = getFont().deriveFont(getFont().getSize() + 4.0F).deriveFont(Font.BOLD);
	}

	public void fill(Double d[][], String l[])
	{
		fill(d, l, null);
	}

	public void fill(Double d[][], String l[], Color c[][])
	{
		fill(d, l, l, c, null, null, null);
	}

	private String infoText(String text, String info)
	{
		return infoText(text, info, infoTextSmaller);
	}

	private String infoText(String text, String info, Double smaller)
	{
		return "<html><center>" + text + "<br><div style=\"font-size:" + smaller + "em;font-style:italic\">" + info
				+ "</div></html>";
	}

	public void fill(Double d[][], String colNames[], String rowNames[], Color c[][], String dInfo[][],
			String colInfo[], String rowInfo[])
	{
		//setLayout(new GridLayout(d.length + 1, d[0].length + 1, 1, 1));
		ColumnSpec[] cols = new ColumnSpec[d.length + 1];
		cols[0] = ColumnSpec.decode("fill:p:grow");
		for (int i = 0; i < d.length; i++)
			cols[i + 1] = ColumnSpec.decode("fill:p:grow");
		RowSpec[] rows = new RowSpec[d.length + 1];
		rows[0] = RowSpec.decode("fill:p:grow");
		for (int i = 0; i < d[0].length; i++)
			rows[i + 1] = RowSpec.decode("fill:p:grow");

		JPanel p = new JPanel(new FormLayout(cols, rows));
		p.setOpaque(false);
		CellConstraints cc = new CellConstraints();

		p.setBorder(new MatteBorder(1, 1, 0, 0, getBackground()));
		for (int i = 0; i < d.length + 1; i++)
		{
			for (int j = 0; j < d[0].length + 1; j++)
			{
				JLabel la = new JLabel()
				{
					public void paintComponent(final Graphics g)
					{
						final Graphics2D g2d = (Graphics2D) g;
						g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
						super.paintComponent(g2d);
					}
				};
				la.setFont(getFont());
				Color col = null;
				String text = "";
				if (i == 0 && j > 0)
				{
					text = colNames[j - 1];
					if (colInfo != null && colInfo[j - 1] != null)
						text = infoText(text, colInfo[j - 1]);
				}
				else if (i > 0 && j == 0)
				{
					text = rowNames[i - 1];
					if (text.length() > 5)
						text = text.substring(0, 3) + "..";
					if (rowInfo != null && rowInfo[i - 1] != null)
						text = infoText(text, rowInfo[i - 1]);
				}
				else if (i > 0 && j > 0)
				{
					Double v = d[i - 1][j - 1];
					if (v != null)
						text = StringUtil.formatDouble(v);
					else
						text = "-";
					if (dInfo != null && dInfo[i - 1][j - 1] != null)
						text = infoText(text, dInfo[i - 1][j - 1]);
					if (c != null)
						col = c[i - 1][j - 1];
				}
				la.setText(text);
				la.setHorizontalAlignment(SwingConstants.CENTER);
				la.setOpaque(col != null);
				la.setBorder(new MatteBorder(0, 0, 1, 1, getBackground()));
				if (col != null)
					la.setBackground(col);
				p.add(la, cc.xy(i + 1, j + 1));
			}
		}
		setLayout(new BorderLayout(5, 5));
		String text = titleString;
		if (subtitleString != null)
			text = infoText(text, subtitleString, 0.9);
		JLabel title = new JLabel(text);
		title.setFont(titleFont);
		title.setHorizontalAlignment(JLabel.CENTER);
		add(title, BorderLayout.NORTH);
		add(p, BorderLayout.CENTER);
	}

	public void fill(CorrelationMatrix<?> m, String l[])
	{
		fill(m.getMatrix(), l, l, m.getColor(), m.getCellInfo(), m.getRowInfo(), m.getRowInfo());
	}

	public void booleanCorrelationMatrix(List<Boolean[]> b, String l[])
	{
		PearsonBooleanCorrelationMatrix m = new PearsonBooleanCorrelationMatrix();
		m.setMinNumValues(minNumValues);
		m.computeMatrix(b);
		fill(m, l);
	}

	public void doubleCorrelationMatrixPearson(List<Double[]> b, String l[])
	{
		PearsonDoubleCorrelationMatrix m = new PearsonDoubleCorrelationMatrix();
		m.setMinNumValues(minNumValues);
		m.computeMatrix(b);
		fill(m, l);
	}

	public void doubleCorrelationMatrixSpearman(List<Double[]> b, String l[])
	{
		SpearmanDoubleCorrelationMatrix m = new SpearmanDoubleCorrelationMatrix();
		m.setMinNumValues(minNumValues);
		m.computeMatrix(b);
		fill(m, l);
	}

	//	private static double computeMCC(boolean[] b1, boolean[] b2)
	//	{
	//		if (b1.length == 0)
	//			return Double.NaN;
	//		int tp = 0, tn = 0, fp = 0, fn = 0;
	//		for (int i = 0; i < b2.length; i++)
	//		{
	//			if (b1[i] == b2[i])
	//			{
	//				if (b1[i])
	//					tp++;
	//				else
	//					tn++;
	//			}
	//			else
	//			{
	//				if (b1[i])
	//					fp++;
	//				else
	//					fn++;
	//			}
	//		}
	//		if (fp + fn == 0)
	//			return 1;
	//		if (tp + tn == 0)
	//			return 0;
	//		double mcc = (tp * tn - fp * fn) / Math.sqrt((tp + fp) * (tp + fn) * (tn + fp) * (tn * fn));
	//		if (Double.isInfinite(mcc) || Double.isNaN(mcc))
	//			return 0;
	//		return mcc;
	//	}
	//
	//	private static double computeCCC(double[] d1, double[] d2)
	//	{
	//		double m1_mean = 0;
	//		double m2_mean = 0;
	//		int count = 0;
	//		for (int i = 0; i < d1.length; i++)
	//		{
	//			m1_mean += d1[i];
	//			m2_mean += d2[i];
	//			count++;
	//		}
	//		m1_mean /= (double) count;
	//		m2_mean /= (double) count;
	//
	//		double numerator = 0;
	//		for (int i = 0; i < d1.length; i++)
	//			numerator += (d1[i] - m1_mean) * (d2[i] - m2_mean);
	//		numerator *= 2;
	//
	//		double m1_ss = 0;
	//		double m2_ss = 0;
	//		for (int i = 0; i < d1.length; i++)
	//		{
	//			m1_ss += Math.pow((d1[i] - m1_mean), 2);
	//			m2_ss += Math.pow((d2[i] - m2_mean), 2);
	//		}
	//
	//		double denominator = m1_ss + m2_ss;
	//		denominator += count * Math.pow((m1_mean - m2_mean), 2);
	//		double ccc = numerator / denominator;
	//		if (Double.isInfinite(ccc) || Double.isNaN(ccc))
	//			return 0;
	//		return ccc;
	//	}

	public double getInfoTextSmaller()
	{
		return infoTextSmaller;
	}

	public void setInfoTextSmaller(double infoTextSmaller)
	{
		this.infoTextSmaller = infoTextSmaller;
	}

	public int getMinNumValues()
	{
		return minNumValues;
	}

	public void setMinNumValues(int minNumValues)
	{
		this.minNumValues = minNumValues;
	}

	public String getTitleString()
	{
		return titleString;
	}

	public void setTitleString(String titleString)
	{
		this.titleString = titleString;
	}

	public String getSubtitleString()
	{
		return subtitleString;
	}

	public void setSubtitleString(String subtitleString)
	{
		this.subtitleString = subtitleString;
	}

	public static void fromCSV(String csvFile)
	{
		CSVFile csv = FileUtil.readCSV(csvFile);

		MatrixPanel p = new MatrixPanel();
		p.setTitleString(FileUtil.getFilename(csvFile));
		p.setSubtitleString(csv.content.get(0)[0]);
		p.setBackground(Color.WHITE);
		p.fill(new Double[][] { { 1.0, 1.0 }, { 0.5, 0.5 }, { 0.6, 0.6 } }, new String[] { "a", "b" }, new String[] {
				"e", "f", "g" }, null, null, new String[] { "aa", "bb" }, null);

		JPanel pp = new JPanel();
		pp.add(p);
		pp.setPreferredSize(new Dimension(1000, 600));
		SwingUtil.showInDialog(pp);

	}

	public static void testFromCsv()
	{
		String csv[][] = { { "Das ist der Untertitel", "Spalte 1", "Spalte 2", "Spalte 3" },
				{ "Zeile 1", "0.1", "0.1", "0.1" }, { "Zeile 2", "0.1", "0.1", "0.1" },
				{ "Zeile 3", "0.1", "0.1", "0.1" }, { "Zeile 4", "0.1", "0.1", "0.1" } };
		String s = "";
		for (String[] line : csv)
			s += ArrayUtil.toCSVString(line) + "\n";
		FileUtil.writeStringToFile("/tmp/test.csv", s);
		fromCSV("/tmp/test.csv");
		System.exit(0);
	}

	public static void main(String args[])
	{
		testFromCsv();

		JPanel pp = new JPanel();

		for (Boolean b : new Boolean[] { true, false })
		{

			List<Double[]> values = new ArrayList<Double[]>();
			values.add(new Double[] { 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, b ? 7.0 : 70.0 });
			values.add(new Double[] { 1.0, 2.0, 3.0, 3.0, 3.0, 2.0, 7.0 });
			values.add(new Double[] { 1.0, 2.0, 1.0, 1.0, null, 1.0, 1.0 });
			values.add(new Double[] { 7.0, 6.0, 5.0, 4.0, 2.0, 2.0, 1.0 });

			MatrixPanel p = new MatrixPanel();
			p.setTitleString("This is the Title - Pearson - " + b);
			p.setSubtitleString("and this is some subtitle that is a bit longer and less important");
			p.setBackground(Color.WHITE);
			p.doubleCorrelationMatrixPearson(values, new String[] { "asterix", "b", "c", "this-is-a-very-long-name" });

			MatrixPanel p2 = new MatrixPanel();
			p2.setTitleString("This is the Title - Spearman - " + b);
			p2.setSubtitleString("and this is some subtitle that is a bit longer and less important");
			p2.doubleCorrelationMatrixSpearman(values, new String[] { "asterix", "b", "c", "this-is-a-very-long-name" });

			//		List<Boolean[]> values = new ArrayList<Boolean[]>();
			//		values.add(new Boolean[] { true, false, true, true, true, false });
			//		values.add(new Boolean[] { true, false, true, false, false, true });
			//		values.add(new Boolean[] { true, true, true, false, false, true });
			//		values.add(new Boolean[] { true, false, true, null, false, true });
			//		JPanel p = MatrixPanel.correlationMatrixBool(values, new String[] { "asterix", "b", "this-is-a-very-long-name",
			//				"d" });

			pp.add(p);
			pp.add(p2);

		}
		pp.setPreferredSize(new Dimension(1000, 600));
		SwingUtil.showInDialog(pp);
		System.exit(0);
	}

	public Font getTitleFont()
	{
		return titleFont;
	}

	public void setTitleFont(Font titleFont)
	{
		this.titleFont = titleFont;
	}

}
