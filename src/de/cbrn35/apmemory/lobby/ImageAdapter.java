package de.cbrn35.apmemory.lobby;

import java.util.ArrayList;
import java.util.HashMap;

import de.cbrn35.apmemory.*;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;


public class ImageAdapter extends BaseAdapter {
	 private Context mContext;
	 private GameField gf;
	 private HashMap<Integer, Integer> posCardRelations = new HashMap<Integer, Integer>();
	 private Integer hiddenCard = R.drawable.card_unknown;
	 //GridView myGridView;
	 public ImageAdapter(Context c, GameField gf) {
	        mContext = c;
	        this.gf = gf;
	        
	        ArrayList<Integer> usedPics = new ArrayList<Integer>();
	        
	        for(Card card : this.gf.cards) {
	        	Integer pos;
	        	do {
	        		pos = mThumbIds[(int)Math.abs(Math.random()*mThumbIds.length)];
	        	} while(usedPics.contains(pos)); 
	        	
	        	Log.i(C.LOGTAG, "generating image: "+pos+" with card "+card);
	        	
	        	posCardRelations.put(card.pos1, pos);
	        	posCardRelations.put(card.pos2, pos);
	        }
	        
	        //myGridView = new GridView(c);
	        //myGridView.setNumColumns(4);
			
	    }
	
	public int getCount() {
		return posCardRelations.size();
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.WRAP_CONTENT, GridView.LayoutParams.WRAP_CONTENT));
            //imageView.setLayoutParams(New GridView.)
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            imageView.setPadding(4, 4, 4, 4);
            imageView.setAdjustViewBounds(true);
            
            
        } else {
            imageView = (ImageView) convertView;
        }
        Card card = gf.findCard(position);
        if(card != null) {
        	if(card.pos1 == position) {
        		if(card.visible1) {
        			imageView.setImageResource(posCardRelations.get(position));
        		} else {
        			imageView.setImageResource(hiddenCard);
        		}
        	}
        	if(card.pos2 == position) {
        		if(card.visible2) {
        			imageView.setImageResource(posCardRelations.get(position));
        		} else {
        			imageView.setImageResource(hiddenCard);
        		}
        	}
        } else {
        	Log.e(C.LOGTAG, "Card not found at position "+position);
        }
        return imageView;
	}
	
	private Integer[] mThumbIds = {
			R.drawable.meme_img00,R.drawable.meme_img01,
			R.drawable.meme_img02,R.drawable.meme_img03,
			R.drawable.meme_img04,R.drawable.meme_img05,
			R.drawable.meme_img06,R.drawable.meme_img07,
			R.drawable.meme_img08,R.drawable.meme_img09,
			R.drawable.meme_img10,R.drawable.meme_img11,
			R.drawable.meme_img12,R.drawable.meme_img13,
			R.drawable.meme_img14,R.drawable.meme_img15,
    };

}
