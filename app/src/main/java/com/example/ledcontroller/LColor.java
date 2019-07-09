package com.example.ledcontroller;

import android.graphics.Color;

import java.text.DecimalFormat;

public class LColor {
    private int[] rgbColor;
    private int hexColor;
    private float[] hsvColor;

    private String rgbString, hexString, hsvString;

    public LColor(int[] rgb, int hex, float[] hsv) {
        rgbColor = rgb;
        hexColor = hex;
        hsvColor = hsv;

        rgbString = getHexString();
        hexString = getHexString();
        hsvString = getHSVString();
    }

    public LColor(int hex) {
        hexColor = hex;
        rgbColor = getRGB();
        hsvColor = getHSV();

        rgbString = getRGBString();
        hexString = getHexString();
        hsvString = getHSVString();
    }

    /**
     * Gets RGB values from hexColor
     * @return integer array with red, green, and blue values, respectively
     */
    public int[] getRGB() {
        int[] rgb = new int[3];
        rgb[0] = Color.red(hexColor);
        rgb[1] = Color.green(hexColor);
        rgb[2] = Color.blue(hexColor);

        return rgb;
    }

    /**
     * Returns the hex string value for a color: #RRGGBB
     * @return String of hex value
     */
    public String getHexString() {
        return "#" + Integer.toHexString(hexColor);
    }

    /**
     * Returns the int hex color
     * @return int
     */
    public int getHex() {
        return hexColor;
    }


    /**
     * Returns the string HSV value: "HSV(##0.00, ##0.00, ##0.00)"
     * @return String
     */
    public String getHSVString() {
        DecimalFormat decimalFormat = new DecimalFormat("##0.00");
        return "HSV(" + decimalFormat.format(hsvColor[0]) + ", " +
                decimalFormat.format(hsvColor[1]) + ", " +
                decimalFormat.format(hsvColor[2]) + ")";
    }

    /**
     * Returns an array with HSV values
     * hue: hsv[0]
     * saturation: hsv[1]
     * brightness: hsv[2]
     * @return float[3]
     */
    public float[] getHSV() {
        float[] hsv = new float[3];
        Color.colorToHSV(hexColor, hsv);
        return hsv;
    }

    /**
     * Returns the string RGB value: "RGB(RRR, GGG, BBB)"
     * @return String
     */
    public String getRGBString() {
        return "RGB(" + rgbColor[0] + ", "
                + rgbColor[1] + ", "
                + rgbColor[2] + ")";
    }

    /**
     * returns the current brightness of color
     * @return int
     */
    public int getBrightness() {
        return (int)(hsvColor[2] * 100);
    }

    public int modifyColorByBrightness(int brightness) {
        hsvColor[2] = brightness/(float)100;
        return Color.HSVToColor(hsvColor);
    }

    /**
     * Sets the color of the object to hex
     * @param hex integer color value
     */
    public void setColor(int hex) {
        hexColor = hex;
        rgbColor = getRGB();
        hsvColor = getHSV();
    }

}
