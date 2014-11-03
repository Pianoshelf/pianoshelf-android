package com.pianoshelf.joey.pianoshelf;

import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.PagerAdapter;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by joey on 24/10/14.
 * Page/Activity for viewing sheet music
 * Goal: Swipe left/right to move pages (intuitive)
 * Goal: Auto-Hiding navigation buttons after some time {Left, Right, Page Number}
 * Goal: Intuitive back button
 */
public class SheetView extends FragmentActivity {
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private int pageTotal;
    private String sheetMusicUrl;
    private String[] sheetUrls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheetview);

        // fetches the url of the sheet music JSON object
        Intent intent = getIntent();
        sheetMusicUrl = intent.getStringExtra("sheetMusicUrl");

        // Fetch the JSON object from the URL
        // Create the request object
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, sheetMusicUrl, null, new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonSheetMusicArray = response.getJSONArray("images");
                            // Assign the total number of pages here
                            pageTotal = jsonSheetMusicArray.length();
                            sheetUrls = new String[pageTotal];
                            for (int i=0; i<pageTotal; i++) {
                                JSONObject jsonSheet = jsonSheetMusicArray.getJSONObject(i);

                                String sheetUniqueKey = jsonSheet.getString("unique_key");
                                sheetUrls[i] = parseSheetUniqueKey(sheetUniqueKey);
                            }

                            viewPager = (ViewPager) findViewById(R.id.sheetViewPager);
                            pagerAdapter = new SheetViewPagerAdapter(getSupportFragmentManager());
                            // TODO BeginTransaction ?
                            viewPager.setAdapter(pagerAdapter);
                        } catch (JSONException ex) {
                            // TODO Maybe do something else here?
                            throw new RuntimeException(ex);
                        }
                    }
                }, new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        //TODO popup error message
                    }
                });
        // Make the actual request
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

        // Instantiate a ViewPager and a PagerAdapter.

        // TODO get current pageTotal via API calls
        /*
        // ony instantiate the fragment if this is not a state restore
        if (savedInstanceState == null) {
            // 42 will be replaced with the location of the sheet music page
            Sheet sheetOne = Sheet.newInstance(42);
            //(getSupportFragmentManager().beginTransaction()).add(sheetOne).commit();

        }*/
    }

    private String parseSheetUniqueKey(String sheetUniqueKey) {
        return "http://static.classicpianosolos.com/sheetmusicimg/" + sheetUniqueKey + ".jpg";
    }

    private class SheetViewPagerAdapter extends FragmentStatePagerAdapter {
        public SheetViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            return Sheet.newInstance(sheetUrls[position]);
        }

        @Override
        public int getCount() {
            return pageTotal;
        }

    }
}
