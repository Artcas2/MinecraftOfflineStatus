package fr.artcas2.mincraftofflinestatus;

public class TextComponent {
    private final String text;
    private final String color;

    public TextComponent(String text) {
        this(text, null);
    }

    public TextComponent(String text, String color) {
        this.text = text;
        this.color = color;
    }

    public String getText() {
        return text;
    }

    public String getColor() {
        return color;
    }
}
