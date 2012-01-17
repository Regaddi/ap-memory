package de.cbrn35.apmemory;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class HttpAsyncTask extends AsyncTask<Void, Void, JSONObject> {
	private Context ctx;
	private HttpGet get;
	private ProgressDialog pd;
	private Intent rIn;
	
	public HttpAsyncTask(HttpGet get, Context ctx, Intent resultIntent) {
		this.get = get;
		this.ctx = ctx;
		this.rIn = resultIntent;
	}
	protected void onPreExecute() {
		pd = new ProgressDialog(ctx);
		pd.setMessage(ctx.getString(R.string.prg_loading));
		pd.show();
	}
	
	protected JSONObject doInBackground(Void... params) {
		String rpStr = "";
		JSONObject response = null;
		try {
			HttpClient cl = new DefaultHttpClient();
			HttpResponse rp = cl.execute(get);
			Log.i(C.LOGTAG, get.getRequestLine().toString());
			if(rp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
    			rpStr = EntityUtils.toString(rp.getEntity());
    			Log.i(C.LOGTAG, rpStr);
    			response = new JSONObject(rpStr);
    		} else {
    			Log.i(C.LOGTAG, rp.getStatusLine().getReasonPhrase());
    			JSONObject error = new JSONObject();
    			error.put("error", 1);
    			error.put("error_msg", ctx.getString(R.string.err_no_connection));
    			return error;
    		}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return response;
	}
	
	protected void onPostExecute(JSONObject result) {
		pd.dismiss();
		try {
			if(result.getInt("error") == 1) {
				Toast.makeText(ctx, result.getString("error_msg"), Toast.LENGTH_LONG).show();
			} else {
				if(result.has("response")) {
					Toast.makeText(ctx,  result.getString("response"), Toast.LENGTH_LONG).show();
				}
				if(rIn != null) {
					if(result.has("data")) {
						rIn.putExtra("data", result.toString());
					}
					ctx.startActivity(rIn);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
