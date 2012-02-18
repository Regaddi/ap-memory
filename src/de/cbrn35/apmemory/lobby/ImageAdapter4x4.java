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
            imageView.setLayoutParams(new GridView.LayoutParams(98, 98));
            //imageView.setLayoutParams(New GridView.)
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            imageView.setPadding(4, 4, 4, 4);
            //imageView.
            
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(mThumbIds[position]);
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
			R.drawable.meme_img14,R.drawable.meme_img15
    };

}
