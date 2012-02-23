package de.cbrn35.apmemory.lobby;

import java.util.ArrayList;

import de.cbrn35.apmemory.C;
import de.cbrn35.apmemory.Game;
import de.cbrn35.apmemory.R;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * 
 * manages the open game process
 *
 */
public class OpenGameAdapter extends ArrayAdapter<Game> {
	Context context;
    int layoutResourceId;   
    ArrayList<Game> data = null;
   
    public OpenGameAdapter(Context context, int layoutResourceId, ArrayList<Game> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	//prepare a string for creating a new game
        View row = convertView;
        GameHolder holder = null;
       
        if(row == null)
        {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
           
            holder = new GameHolder();
            holder.id = (TextView)row.findViewById(R.id.open_game_id);
            holder.name = (TextView)row.findViewById(R.id.open_game_name);
            holder.creator = (TextView)row.findViewById(R.id.open_game_creator);
            holder.maxPlayers = (TextView)row.findViewById(R.id.open_game_max_players);
            holder.size = (ImageView)row.findViewById(R.id.open_game_size_image);
            holder.isPrivate = (ImageView)row.findViewById(R.id.open_game_private);
           
            row.setTag(holder);
        }
        else
        {
            holder = (GameHolder)row.getTag();
        }
       
        Game game = data.get(position);
        holder.id.setText(game.id+"");
        holder.name.setText(game.name);
        holder.creator.setText(game.creator.username);
        holder.maxPlayers.setText(game.maxPlayers+"");
        switch(game.gameSize) {
        case 8:
        	holder.size.setImageResource(R.drawable.game_size_8);
        	break;
        case 16:
        	holder.size.setImageResource(R.drawable.game_size_16);
        	break;
        case 24:
        	holder.size.setImageResource(R.drawable.game_size_24);
        	break;
        case 32:
        	holder.size.setImageResource(R.drawable.game_size_32);
        	break;
        }
        
        if(game.isPrivate) {
        	holder.isPrivate.setVisibility(View.VISIBLE);
        } else {
        	holder.isPrivate.setVisibility(View.INVISIBLE);
        }
       
        return row;
    }
   
    static class GameHolder
    {
    	TextView id;
        TextView name;
        TextView creator;
        TextView maxPlayers;
        ImageView size;
        ImageView isPrivate;
        
        @Override
		public String toString() {
        	return "GameHolder:[id:"+this.id.getText().toString()+"; "
        			+"name:"+this.name.getText().toString()+"; "
        			+"creator:"+this.creator.getText().toString()+"; "
        			+"size:"+this.size.getDrawable().toString()+"]";
        }
    }
}
