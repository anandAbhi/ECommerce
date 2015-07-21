package live.Abhinav.ecommerce.app;

import android.app.ProgressDialog;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import com.android.volley.*;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import live.Abhinav.ecommerce.extras.AppConfig;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static live.Abhinav.ecommerce.extras.Keys.EndpointBoxOffice.*;

public class BuyActivity extends ActionBarActivity {
    String[] items;

    Spinner categorySpinner;
    Spinner subCategorySpinner;
    Spinner productSpinner;
    private static final String TAG = BuyActivity.class.getSimpleName();
    private AppController volleySingleton;
    private RequestQueue requestQueue;
    private ProgressDialog pDialog;
    ArrayList<String> arrayList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);

        volleySingleton = AppController.getInstance();
        requestQueue = volleySingleton.getRequestQueue();

        items = new String[]{""};

        //Progress Dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        sendCategoryRequest(AppConfig.URL_CATEGORIES);

        //Find all the spinners
        categorySpinner = (Spinner) findViewById(R.id.spinnerCategory);
        subCategorySpinner = (Spinner) findViewById(R.id.spinnerSubCategory);
        productSpinner = (Spinner) findViewById(R.id.spinnerProduct);


        //Set listener for all  the 3 spinners
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = (String) parent.getSelectedItem();
                sendSubCatRequest(selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        subCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedSubCategory = (String) parent.getSelectedItem();
                sendProductRequest(selectedSubCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        productSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedProduct = (String) parent.getSelectedItem();
                fetchProductsByName(selectedProduct);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void fetchProductsByName(String selectedProduct) {
        Log.d(TAG, "Fetch products by name" + AppConfig.URL_PRODUCTS);
        showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                AppConfig.URL_PRODUCTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }
        };
    }

    public void sendCategoryRequest(String url) {
        pDialog.setMessage("Fetching Categories...");
        showDialog();
        JsonArrayRequest catRequest = new JsonArrayRequest(Request.Method.GET,
                url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        Log.d(TAG, jsonArray + "");
                        //Parse categories list returned
                        arrayList = parseJSON(jsonArray, KEY_CATEGORY_NAME, KEY_CATEGORY_ID);
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(BuyActivity.this, android.R.layout.simple_spinner_item, arrayList);
                        categorySpinner.setAdapter(adapter);
                        hideDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
//                if (volleyError.getMessage() != null)
                Log.d(TAG, volleyError + " ");
                hideDialog();
            }
        });
        requestQueue.add(catRequest);
    }

    public void sendSubCatRequest(final String selectedCategory) {
        Log.d(TAG, "sendSubCatRequest " + AppConfig.URL_SUBCATEGORIES + selectedCategory);
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_SUBCATEGORIES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "response: " + response);
                        hideDialog();

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            arrayList = parseJSON(jsonArray, KEY_SUBCATEGORY_NAME, KEY_SUBCATEGORY_ID);
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(BuyActivity.this, android.R.layout.simple_spinner_item, arrayList);
                            subCategorySpinner.setAdapter(adapter);
                            hideDialog();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "Registration error: " + volleyError.getMessage());
                Toast.makeText(getApplicationContext(), volleyError.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("category", selectedCategory);
                return params;
            }
        };
        requestQueue.add(strReq);
    }

    private void sendProductRequest(final String selectedSubCategory) {
        Log.d(TAG, "sendSubCatRequest " + AppConfig.URL_SUBCATEGORIES + selectedSubCategory);
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_PRODUCTS_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Product response: " + response);
                        hideDialog();

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            arrayList = parseJSON(jsonArray, KEY_SUBCATEGORY_NAME, KEY_SUBCATEGORY_ID);
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(BuyActivity.this, android.R.layout.simple_spinner_item, arrayList);
                            productSpinner.setAdapter(adapter);
                            hideDialog();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "Registration error: " + volleyError.getMessage());
                Toast.makeText(getApplicationContext(), volleyError.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("subcategory", selectedSubCategory);
                return params;
            }
        };
        requestQueue.add(strReq);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public ArrayList<String> parseJSON(JSONArray response, String KEY_FIELD_NAME, String KEY_FIELD_ID) {
        ArrayList<String> categoryList = new ArrayList<String>();
        if (response != null && response.length() > 0) {
            try {
                StringBuilder data = new StringBuilder();
                JSONArray jsonArray = response;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String categoryName = jsonObject.getString(KEY_FIELD_NAME);
                    String categoryId = jsonObject.getString(KEY_FIELD_ID);

                    categoryList.add(categoryName);
                    data.append(categoryId + " " + categoryName);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return categoryList;
    }
}