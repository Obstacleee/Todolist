public enum Category {
    PERSO("Perso", "\u001B[32m"),   // Vert
    BOULOT("Boulot", "\u001B[34m"),  // Bleu
    FAMILLE("Famille", "\u001B[35m"); // Magenta

    private final String name;
    private final String color;

    Category(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
