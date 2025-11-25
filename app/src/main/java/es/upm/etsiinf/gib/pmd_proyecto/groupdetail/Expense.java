package es.upm.etsiinf.gib.pmd_proyecto.groupdetail;

public class Expense {

    private String emoji;        // 💶 or 🍔 etc.
    private String title;        // "Bar à jeux"
    private String payer;        // "Baptiste"
    private double amount;       // 25.00 €

    public Expense(String emoji, String title, String payer, double amount) {
        this.emoji = emoji;
        this.title = title;
        this.payer = payer;
        this.amount = amount;
    }

    public String getEmoji() { return emoji; }
    public String getTitle() { return title; }
    public String getPayer() { return payer; }
    public double getAmount() { return amount; }
}
