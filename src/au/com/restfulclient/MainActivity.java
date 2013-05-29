package au.com.restfulclient;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

public class MainActivity extends Activity {

	private final String URL_ERROR = "http://www.cheesejedi.com/rest_services/get_big_cheese";
	private final String URL_SUCCESS = "http://developer.yahooapis.com/TimeService/V1/getTime";

	private Button callService;
	private Button callServiceSuccess;
	private TextView resultTxt;

	private JSONObject jsonObj;
	private RequestQueue volleyRequestQueue;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		callService = (Button) findViewById(R.id.callServiceError);
		callService.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MainActivity.this.getService(URL_ERROR);
			}
		});

		callServiceSuccess = (Button) findViewById(R.id.callServiceSuccess);
		callServiceSuccess.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MainActivity.this.getService(URL_SUCCESS);
			}
		});

		resultTxt = (TextView) findViewById(R.id.appResponse);
		resultTxt.setMovementMethod(new ScrollingMovementMethod());
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	private void getService(String url) {
//		JSONObject reqBody = new JSONObject();
		Map<String,String> params = new HashMap<String,String>();
		if (url.equals(URL_SUCCESS)) {
			try {
				params.put("appid", "YahooDemo");
				params.put("output", "json");
				url = addParamsToUrl(url, params);
//				reqBody.put("appid", "YahooDemo");
//				reqBody.put("output", "json");
				volleyRequestQueue = Volley.newRequestQueue(this);
				// GET
				volleyRequestQueue.add(new JsonObjectRequest(Method.GET, url, null, new MyResponseListener(), new MyErrorListener()));
				
				// POST 
				// Need to comment at least the line 86 where url receive the par1ameters and uncomment lines 79, 87 and 88
				// However this url doesn't seems accept post, you need to try a different URL or create your own for test purpose
//				volleyRequestQueue.add(new JsonObjectRequest(Method.POST, url, reqBody, new MyResponseListener(), new MyErrorListener()));
			} catch (Exception e) {
				Log.e("ErrorListener", e.getLocalizedMessage());
			}
		} else {
			params.put("level", "1");
			url = addParamsToUrl(url, params);
			volleyRequestQueue = Volley.newRequestQueue(this);
			volleyRequestQueue.add(new JsonObjectRequest(Method.GET, url, null, new MyResponseListener(), new MyErrorListener()));
		}
	}


	private String addParamsToUrl(String url, Map<String, String> paramsToAdd) {
		StringBuffer urlWithParams = new StringBuffer(url);
		for (Map.Entry<String, String> entry : paramsToAdd.entrySet()) {
			if (urlWithParams.toString().contains("?")) {
				urlWithParams.append("&");
			} else {
				urlWithParams.append("?");
			}
			urlWithParams.append(entry.getKey()).append("=").append(entry.getValue());
		}
		return urlWithParams.toString();
	}
	
	
	@Override
	protected void onStop() {
		if (volleyRequestQueue != null)
			volleyRequestQueue.cancelAll(this);
		super.onStop();
	}

	class MyResponseListener implements Listener<JSONObject> {

		@Override
		public void onResponse(JSONObject response) {
			MainActivity.this.jsonObj = response;
			try {
				JSONObject result = MainActivity.this.jsonObj.getJSONObject("Result");
				String timestamp = result.getString("Timestamp");
				MainActivity.this.resultTxt.setText(new Date(Long.parseLong(timestamp.trim())).toString());
			} catch (Exception e) {
				Log.e("ErrorListener", e.getLocalizedMessage());
			}
		}
	}

	class MyErrorListener implements ErrorListener {

		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e("ErrorListener", error.getCause() + "");
			if (error.getCause() != null) {
				Log.e("ErrorListener", error.getLocalizedMessage());
			}
			MainActivity.this.resultTxt.setText(error.toString());
			volleyRequestQueue.cancelAll(this);
		}
	}

}
