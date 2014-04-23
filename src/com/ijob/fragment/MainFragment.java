package com.ijob.fragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.ijob.R;
import com.ijob.listview.XListView;
import com.ijob.listview.XListView.IXListViewListener;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;

public class MainFragment extends Fragment implements IXListViewListener{
	private Handler mHandler;
	private int start = 111;
	private SimpleAdapter xlistItemAdapter;
	private List<Map<String, Object>> mDataList = new ArrayList<Map<String, Object>>();
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	private String URL = "http://1.iters.sinaapp.com/messages/getMessageById/";
	private XListView mListView;
	public MainFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// inflater the layout
		View view = inflater.inflate(R.layout.main_listview, null);
		mListView = (XListView) view.findViewById(R.id.main_xListView1);
		Button searchButton = (Button) view.findViewById(R.id.main_search);
		Button chooseButton = (Button) view.findViewById(R.id.main_choose);
		EditText editText = (EditText)view.findViewById(R.id.main_editText1);
		
		for (int i = 5; i > 0; i--) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("company", "中山大学" + i);
			map.put("peoplefocus", "关注人数");
			map.put("job", "工作岗位");
			map.put("workplace", "工作地点");
			map.put("id", ""+i);
			map.put("detail", "no more details!");
			mDataList.add(0,map);
		}
		mListView.setPullLoadEnable(true);
		xlistItemAdapter = new SimpleAdapter(view.getContext(), mDataList,
				R.layout.list_item, new String[] { "company", "peoplefocus",
						"job", "workplace" }, new int[] { R.id.company,
						R.id.peoplefocus, R.id.job, R.id.workplace });
		mListView.setAdapter(xlistItemAdapter);
		mListView.setXListViewListener(this);
		mHandler = new Handler();
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO 自动生成的方法存根
				
			}
		});

		searchButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根

			}
		});

		chooseButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根

			}
		});
		return view;
	}


	@Override
	public void onStop() {
		super.onStop();
	}

	private void onLoad() {
		mListView.stopRefresh();
		mListView.stopLoadMore();
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String current_timeString = formatter.format(curDate);
		mListView.setRefreshTime(current_timeString);
	}
	
	@Override
	public void onRefresh() {
		// TODO 自动生成的方法存根
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				DownloadRunnable runnable = new DownloadRunnable();
				new Thread(runnable).start();//获取新信息
				
				xlistItemAdapter.notifyDataSetChanged();
				onLoad();
			}
		}, 1000);
	}

	@Override
	public void onLoadMore() {
		mHandler.postDelayed(new Runnable() {
			// TODO 自动生成的方法存根
			@Override
			public void run() {
				//getInformation();//获取旧信息
				//xlistItemAdapter.notifyDataSetChanged();
			}
		}, 1000);
	}
	public String HTTPGetInfo() {
		String uri = URL + start + ".json";
		String result = "";
		HttpGet httpGet = new HttpGet(uri);// 将参数在地址中传递
		Log.i("URL = ", uri);
		try {
			HttpResponse response = new DefaultHttpClient().execute(httpGet);
			//Log.i("response state = ", ""+ response.getStatusLine().getStatusCode());
			if (response.getStatusLine().getStatusCode() == 200) {
				StringBuffer buffer = new StringBuffer();
				BufferedReader bufferedReader = new BufferedReader(new 
						InputStreamReader(response.getEntity().getContent()));
				String data = "";
				while ((data = bufferedReader.readLine()) != null) {
					buffer.append(data);
				}
				result = buffer.toString();
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	// 后台线程解析下载的json格式文件
	class DownloadRunnable implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			String httpresponse = new String();
			httpresponse = HTTPGetInfo();
			try {
				JSONparser(httpresponse);
			} catch (JSONException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		}

		public void JSONparser(String result) throws JSONException {
			if (result.length() > 100) {
				Log.i("1_mdatalistlength", ""+mDataList.size());
				JSONObject jsonObject = new JSONObject(result);
				JSONArray jsonArray = jsonObject.getJSONArray("job"); 
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", jsonObject.getString("id"));//信息的ID
				map.put("message_title", jsonObject.getString("message_title"));//信息标题
				map.put("company", jsonObject.getString("company"));//公司
				map.put("location", jsonObject.getString("location"));//公司地点
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject2 = (JSONObject)jsonArray.opt(i);
					map.put("job_id", jsonObject2.getString("jid"));//岗位ID
					map.put("job_type", jsonObject2.getString("job_type"));//实习，兼职，全职
					map.put("job", jsonObject2.getString("job_type2"));//工作类型（IT，金融...）
					map.put("workplace", jsonObject2.getString("job_area"));//工作地点
					map.put("job_responsibilities", jsonObject2.getString("job_responsibilities"));//工作岗位描述
					map.put("job_requirements", jsonObject2.getString("job_requirements"));//工作技能需求
					map.put("peoplefocus", "多少人关注");
				}
				mDataList.add(0,map);
				Log.i("2_mdatalistlength", ""+mDataList.size());
				start ++;
			}
			if (result == null) {
				Log.i("下载的JSON文件", "空的");
			}
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

}
