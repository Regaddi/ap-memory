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

/**
 * performs a HTTP-request by using a given HttpGet-Object
 * shows a loading-dialog, if showLoading = true
 * starts a new activity, if an intent is given
 */
public class HttpAsyncTask extends AsyncTask<Void, Void, JSONObject> {
	private Context ctx;
	private HttpGet get;
	private ProgressDialog pd;
	private Intent rIn;
	private boolean showLoading;
	
	public HttpAsyncTask(HttpGet get, Context ctx, Intent resultIntent, boolean showLoading) {
		this.get = get;
		this.ctx = ctx;
		this.rIn = resultIntent;
		this.showLoading = showLoading;
	}
	@Override
	protected void onPreExecute() {
		// show loading dialog, if showLoading = true
		if(this.showLoading) {
			pd = new ProgressDialog(ctx);
			pd.setMessage(ctx.getString(R.string.prg_loading));
			pd.show();
		}
	}
	
	@Override
	protected JSONObject doInBackground(Void... params) {
		String rpStr = "";
		JSONObject response = null;
		try {
			HttpClient cl = new DefaultHttpClient();
			// perform HTTP-request
			HttpResponse rp = cl.execute(get);
			Log.i(C.LOGTAG, get.getRequestLine().toString());
			if(rp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				// request was successfull, return JSONObject
    			rpStr = EntityUtils.toString(rp.getEntity());
    			Log.i(C.LOGTAG, rpStr);
    			response = new JSONObject(rpStr);
    			return response;
    		} else {
    			// request was not successful
    			Log.i(C.LOGTAG, rp.getStatusLine().getReasonPhrase());
    			// generate JSONObject containing an error
    			JSONObject error = new JSONObject();
    			error.put("error", 1);
    			error.put("error_msg", ctx.getString(R.string.err_no_connection));
    			return error;
    		}
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	protected void onPostExecute(JSONObject result) {
		try {
			if(result.getInt("error") == 1) {
				// show toast, if an error occured
				Toast.makeText(ctx, result.getString("error_msg"), Toast.LENGTH_SHORT).show();
			} else {
				if(result.has("response")) {
					// show response message, if set
					Toast.makeText(ctx,  result.getString("response"), Toast.LENGTH_LONG).show();
				}
				if(rIn != null) {
					// if activity shall be started afterwards, put data into it, if available
					if(result.has("data")) {
						rIn.putExtra("data", result.toString());
					}
					// start activity
					ctx.startActivity(rIn);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(this.showLoading) {
				pd.dismiss();
			}
		}
	}
}
