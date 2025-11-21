package es.upm.etsiinf.gib.pmd_proyecto.grouplist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;

import es.upm.etsiinf.gib.pmd_proyecto.R;

public class GroupAdapter extends BaseAdapter {

    private Context context;
    private List<Group> groups;

    public GroupAdapter(Context context, List<Group> groups) {
        this.context = context;
        this.groups = groups;
    }

    @Override
    public int getCount() {
        return groups.size();
    }

    @Override
    public Object getItem(int position) {
        return groups.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        if (row == null) {
            row = LayoutInflater.from(context)
                    .inflate(R.layout.grouplist_item, parent, false);
        }

        TextView emojiView = row.findViewById(R.id.groupEmoji);
        TextView nameView = row.findViewById(R.id.groupName);

        Group group = groups.get(position);

        emojiView.setText(group.getEmoji());
        nameView.setText(group.getName());

        return row;
    }
}

