package foodbook.thinmint.activities.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import foodbook.thinmint.R;
import foodbook.thinmint.models.domain.Note;
import foodbook.thinmint.models.domain.User;

/**
 * Created by ZachS on 5/16/2017.
 */

public class UsersListAdapter extends ArrayAdapter<User> {
    public UsersListAdapter(Context context, ArrayList<User> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        User user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
        }
        // Lookup view for data population
        TextView userName = (TextView) convertView.findViewById(R.id.user_name);
        // Populate the data into the template view using the data object
        userName.setText(user.getUsername());
        // Return the completed view to render on screen
        return convertView;
    }
}
