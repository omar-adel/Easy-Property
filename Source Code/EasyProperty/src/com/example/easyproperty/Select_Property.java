package com.example.easyproperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class Select_Property extends ListActivity {
	public String Agent_ID;
	public String Agent_Name;
	public boolean Caller;
	private static final String EXTRA_ID="ID";
	private static final String EXTRA_NAME="NAME";
	private ProgressDialog pDialog;
	private static String TAG_ID="properties";
	private static String TAG_ID2="Product_ID";
	private String givenArea;
	JSONParser jParser = new JSONParser();
	private static final String url_Get_Results = "http://easyproperty.tk.hostinghood.com/agent.php";
	private static final String TAG_SUCCESS="success";

	JSONArray Properties = null;
	ArrayList<HashMap<String, String>> PropertyList;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    Intent e = getIntent();
		Agent_ID = e.getStringExtra(EXTRA_ID).toString();
		Agent_Name = e.getStringExtra(EXTRA_NAME);
		Caller = e.getBooleanExtra("Edit", false);
	    setContentView(R.layout.all_products);
	    new GetData().execute();
	    PropertyList = new ArrayList<HashMap<String,String>>();
	    // TODO Auto-generated method stub
	    
	    ListView lv = getListView();

		// on seleting single product
		// launching Edit Product Screen
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// getting values from selected ListItem
				String ID = ((TextView) view.findViewById(R.id.pid)).getText()
						.toString();
				Log.d("ID",ID);
				// Starting new intent
				Intent in;
				if(Caller){
				 in = new Intent(getApplicationContext(),
						Edit_property.class);
				}
				else{
					 in = new Intent(getApplicationContext(),GetProperty.class);
				}
				// sending pid to next activity
				in.putExtra(TAG_ID2, ID);
				
				// starting new activity and expecting some response back
				startActivity(in);
			}
		});
	}
	class GetData extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Select_Property.this);
			pDialog.setMessage("Retrieving Properties ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * Saving product
		 * */
		@Override
		protected String doInBackground(String... args) {

			// Building Parameters
			//Log.d("Given",givenArea);
		
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("id", Agent_ID));
			// sending modified data through http request
			// Notice that update product url accepts POST method
			JSONObject json = jParser.makeHttpRequest(url_Get_Results,
					"GET", params);
			Log.d("TRY", "Before Try");
			// check json success tag
			try {
				int success = json.getInt(TAG_SUCCESS);
				
				if (success == 1) {
					Log.d("Success", "Inside Success");
					Properties= json.getJSONArray("properties");
					for (int i = 0; i < Properties.length(); i++) {
						JSONObject c = Properties.getJSONObject(i);

						// Storing each json item in variable
						String id = c.getString("id");
						String name = c.getString("name");
						String description = c.getString("descrpt");

						// creating new HashMap
						HashMap<String, String> map = new HashMap<String, String>();

						// adding each child node to HashMap key => value
						map.put("Property_ID", id);
						map.put("Property_Name", name);
						map.put("Description", description);

						// adding HashList to ArrayList
						PropertyList.add(map);
					}
				} else {
					// failed to update product
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}


		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once product uupdated
			pDialog.dismiss();
			
			ListAdapter adapter = new SimpleAdapter(
					Select_Property.this, PropertyList,
					R.layout.list_item, new String[] { "Property_ID",
							"Property_Name","Description"},
					new int[] { R.id.pid, R.id.name,R.id.Description });
			// updating listview
			setListAdapter(adapter);
		}

	}
}
