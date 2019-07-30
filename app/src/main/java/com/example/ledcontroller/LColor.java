package com.example.ledcontroller;

import android.annotation.TargetApi;
import android.graphics.Color;

import java.text.DecimalFormat;

@TargetApi(26)
public class LColor {
    private int[] rgbColor;
    private int hexColor;
    private int alphalessHex;
    private float[] hsvColor;
    private int brightness;

    private Color color;

    private String rgbString, hexString, hsvString;

    public LColor() {}

    public LColor(int hex) {

        // Set Hex
        hexColor = Color.valueOf(hex).toArgb();
        hexString = "#" + Integer.toHexString(hex);
        alphalessHex = (int) (Long.parseLong(hexString.substring(1), 16) - 0xFF000000);

        // Set RGB
        rgbColor = HexToRGB(hex);
        rgbString = "RGB(" + rgbColor[0] + ", "
                + rgbColor[1] + ", "
                + rgbColor[2] + ")";

        // Set HSV
        hsvColor = HexToHSV(hex);
        hsvString = HSVToString(hsvColor);

        brightness = 100;
    }

    public LColor(int hex, int brightness) {
        // Set Hex
        hexColor = Color.valueOf(hex).toArgb();
        hexString = "#" + Integer.toHexString(hex);
        alphalessHex = (int) (Long.parseLong(hexString.substring(1), 16) - 0xFF000000);

        // Set RGB
        rgbColor = HexToRGB(hex);
        rgbString = "RGB(" + rgbColor[0] + ", "
                + rgbColor[1] + ", "
                + rgbColor[2] + ")";

        // Set HSV
        hsvColor = HexToHSV(hex);
        hsvString = HSVToString(hsvColor);

        this.brightness = brightness;
    }



    /**
     * Gets RGB values from hexColor
     * @return integer array with red, green, and blue values, respectively
     */
    private int[] HexToRGB(int hex) {
        int[] rgb = new int[3];
        rgb[0] = Color.red(hex);
        rgb[1] = Color.green(hex);
        rgb[2] = Color.blue(hex);

        return rgb;
    }

    /**
     * Returns an array with HSV values
     * hue: hsv[0]
     * saturation: hsv[1]
     * brightness: hsv[2]
     * @return float[3]
     */
    private float[] HexToHSV(int hex) {
        float[] hsv = new float[3];
        Color.colorToHSV(hex, hsv);
        return hsv;
    }

    /**
     * Returns the string HSV value: "HSV(##0.00, ##0.00, ##0.00)"
     * @return String
     */
    public String HSVToString(float[] hsv) {
        DecimalFormat decimalFormat = new DecimalFormat("##0.00");
        return "HSV(" + decimalFormat.format(hsv[0]) + ", " +
                decimalFormat.format(hsv[1]) + ", " +
                decimalFormat.format(hsv[2]) + ")";
    }

    public int getHex() {
        return hexColor;
    }

    public String getHexString() { return hexString; }

    public int getAlphalessHex() { return alphalessHex; }

    public int[] getRgb() {
        return rgbColor;
    }

    public String getRgbString() { return rgbString; }

    public float[] getHsv() {
        return hsvColor;
    }

    public String getHsvString() { return hsvString; }

    public int getBrightness() {
        return (int)(hsvColor[2] * 100);
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }

    /**
     * Returns color integer based on object's brightness value
     * (Brightness and Color(hex)integer are kept separate to retain
     *      original color value, as brightness alters the color entirely)
     * @return color value as integer
     */
    public int getColor() {
        hsvColor[2] = brightness/(float)100;
        return Color.HSVToColor(hsvColor);
    }

    public void setByRGB(String s) {
        int[] rgb = new int[3];
        rgb[0] = Integer.parseInt(s.substring(0,2));
        rgb[1] = Integer.parseInt(s.substring(4,5));
        rgb[2] = Integer.parseInt(s.substring(7,9));
    }

}
