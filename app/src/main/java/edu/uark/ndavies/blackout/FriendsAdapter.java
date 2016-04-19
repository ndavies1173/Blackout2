package edu.uark.ndavies.blackout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class FriendsAdapter extends ArrayAdapter{

    List list = new ArrayList();

    public FriendsAdapter(Context context, int resource) {
        super(context, resource);
    }

    public void add(Friends object){
        super.add(object);
        list.add(object);
    }

    @Override
    public int getCount(){
        return list.size();
    }

    @Override
    public Object getItem(int position){
        return list.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        View row;
        row = convertView;
        FriendHolder friendHolder;
        if(row == null){
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.row_layout, parent, false);
            friendHolder = new FriendHolder();
            friendHolder.tx_name = (TextView) row.findViewById(R.id.tx_name);
            row.setTag(friendHolder);
        }
        else {
            friendHolder = (FriendHolder) row.getTag();
        }

        Friends friends = (Friends) this.getItem(position);
        friendHolder.tx_name.setText(friends.getName());

        return row;
    }

    static class FriendHolder{
        TextView tx_name;
    }
}
