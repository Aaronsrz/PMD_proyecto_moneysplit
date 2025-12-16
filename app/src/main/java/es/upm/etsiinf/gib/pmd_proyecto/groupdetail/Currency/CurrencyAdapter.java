package es.upm.etsiinf.gib.pmd_proyecto.groupdetail.Currency;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import es.upm.etsiinf.gib.pmd_proyecto.R;

public class CurrencyAdapter extends BaseAdapter {

    private final Context context;
    private final List<CurrencyItem> items;
    private final LayoutInflater inflater;

    public CurrencyAdapter(Context context, List<CurrencyItem> items) {
        this.context = context;
        this.items = items;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() { return items.size(); }

    @Override
    public Object getItem(int position) { return items.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    static class ViewHolder {
        ImageView imgCurrency;
        TextView txtCode;
        TextView txtRate;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            row = inflater.inflate(R.layout.item_currency, parent, false);
            holder = new ViewHolder();
            holder.imgCurrency = row.findViewById(R.id.imgCurrency);
            holder.txtCode = row.findViewById(R.id.txtCurrencyCode);
            holder.txtRate = row.findViewById(R.id.txtCurrencyRate);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        CurrencyItem item = items.get(position);
        holder.imgCurrency.setImageResource(item.getIconResId());
        holder.txtCode.setText(item.getCode());
        holder.txtRate.setText(
                String.format(Locale.US, "1 EUR = %.4f %s", item.getRate(), item.getCode())
        );

        return row;
    }
}

