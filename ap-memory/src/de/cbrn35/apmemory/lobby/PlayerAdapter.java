package de.cbrn35.apmemory.lobby;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.apache.http.client.methods.HttpGet;
import org.json.JSONException;
import org.json.JSONObject;

import de.cbrn35.apmemory.C;
import de.cbrn35.apmemory.Game;
import de.cbrn35.apmemory.HttpAsyncTask;
import de.cbrn35.apmemory.Player;
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

public class PlayerAdapter extends ArrayAdapter<Player> {
	Context context;
    int layoutResourceId;   
    ArrayList<Player> data = null;
   
    public PlayerAdapter(Context context, int layoutResourceId, ArrayList<Player> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        PlayerHolder holder = null;
       
        if(row == null)
        {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
           
            holder = new PlayerHolder();
            holder.id = (TextView)row.findViewById(R.id.player_id);
            holder.username = (TextView)row.findViewById(R.id.player_username);
            holder.isCreator = (ImageView)row.findViewById(R.id.player_is_creator);
           
            row.setTag(holder);
        }
        else
        {
            holder = (PlayerHolder)row.getTag();
            Log.i(C.LOGTAG, ((PlayerHolder)row.getTag()).toString());
        }
       
        Player player = data.get(position);
        holder.id.setText(player.id+"");
        holder.username.setText(player.username);
        
        HttpGet getGame = new HttpGet(C.URL + "?action=get_game&gameid=" + player.currentGameId);
        HttpAsyncTask gameTask = new HttpAsyncTask(getGame, context, null, false);
        gameTask.execute();
        JSONObject result;
		try {
			result = gameTask.get();
			if(result.has("data")) {
	        	Game game = new Game(result.getJSONObject("data"));
	        	if(game.creator.id == player.id) {
	        		holder.isCreator.setVisibility(View.VISIBLE);
	        	}
	        }
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return row;
    }
   
    static class PlayerHolder
    {
    	TextView id;
        TextView username;
        ImageView isCreator;
        
        public String toString() {
        	return "PlayerHolder:[id:"+this.id.getText().toString()+"; "
        			+"username:"+this.username.getText().toString()+"]";
        }
    }
}
