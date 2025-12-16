package es.upm.etsiinf.gib.pmd_proyecto.groupdetail.Currency;

public class CurrencyItem {
    private final String code;
    private final double rate;
    private final int iconResId;

    public CurrencyItem(String code, double rate, int iconResId) {
        this.code = code;
        this.rate = rate;
        this.iconResId = iconResId;
    }

    public String getCode() { return code; }
    public double getRate() { return rate; }
    public int getIconResId() { return iconResId; }
}
