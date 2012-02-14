package de.cbrn35.apmemory.lobby;

import de.cbrn35.apmemory.R;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter4x4 extends BaseAdapter {
	 private Context mContext;
	 //GridView myGridView;
	 public ImageAdapter4x4(Context c) {
	        mContext = c;
	        //myGridView = new GridView(c);
	        //myGridView.setNumColumns(4);
			
	    }
	
	public int getCount() {
		return mThumbIds.length;
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
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            //imageView.setLayoutParams(New GridView.)
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(4, 4, 4, 4);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(mThumbIds[position]);
        return imageView;
	}
	
	private Integer[] mThumbIds = {
			R.drawable.game_dummy1,R.drawable.game_dummy2,
			R.drawable.game_dummy3,R.drawable.game_dummy4,
			R.drawable.game_dummy5,R.drawable.game_dummy6,
			R.drawable.game_dummy7,R.drawable.game_dummy8,
			R.drawable.game_dummy1,R.drawable.game_dummy2,
			R.drawable.game_dummy3,R.drawable.game_dummy4,
			R.drawable.game_dummy5,R.drawable.game_dummy6,
			R.drawable.game_dummy7,R.drawable.game_dummy8
    };

}
