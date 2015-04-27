package org.mg.javalib.util;

import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;

public class ImageConvertUtil
{
    public static void distortPerspective(String source, String dest, double ratio, boolean parallel)
            throws IOException
    {
        ImageIcon img = new ImageIcon(source);

        int w = img.getIconWidth();
        int h = img.getIconHeight();

        int offset = (int) (w * ratio / 2.0);
        int yTopRight = offset;
        int yBottomLeft, yBottomRight;
        if (parallel)
        {
            yBottomLeft = h - offset;
            yBottomRight = h;
        }
        else
        {
            yBottomLeft = h;
            yBottomRight = h - offset;
        }

        String cmd[] = { "convert", source, "-matte", "-virtual-pixel", "transparent", "-distort", "Perspective", // 
                "0,0 0,0 "//top left
                        + w + ",0 " + w + "," + yTopRight + " "//top right
                        + w + "," + h + " " + w + "," + yBottomRight + "  " //bottom right
                        + "0," + h + " 0," + yBottomLeft//bottom left 
                , dest };
        System.out.println(ArrayUtil.toString(cmd, " ", "", "", ""));
        Runtime.getRuntime().exec(cmd);
    }

    public static void distortPerspectiveInFolder(String srcFolder, String dstFolder, double ratio) throws IOException
    {
        for (String f : new File(srcFolder).list())
        {
            if (f.endsWith("png"))
            {
                System.out.println(f);
                distortPerspective(srcFolder + "/" + f, dstFolder + "/" + f, ratio, true);
            }
        }
    }

    public static void main(String[] args) throws IOException
    {
        distortPerspectiveInFolder("/home/martin/documents/ecfps/latex/pics/",
                "/home/martin/documents/ecfps/latex/pics/distorted", 0.1);

        //        distortPerspective("/home/martin/documents/ecfps/latex/pics/prediction.png",
        //                "/home/martin/documents/ecfps/latex/pics/prediction_distorted.png", 0.2);
    }
}
