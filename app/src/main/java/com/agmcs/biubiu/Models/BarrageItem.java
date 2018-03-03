package com.agmcs.biubiu.Models;

/**
 * Created by agmcs on 2015/6/12.
 */
public class BarrageItem {
    String text;
    int color;
    int x;
    int y;
    int TextLength;
    int line;

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getTextLength() {
        return TextLength;
    }

    public void setTextLength(int textLength) {
        TextLength = textLength;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public BarrageItem() {
    }
}
