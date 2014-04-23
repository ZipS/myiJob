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
			map.put("company", "��ɽ��ѧ" + i);
			map.put("peoplefocus", "��ע����");
			map.put("job", "������λ");
			map.put("workplace", "�����ص�");
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
				// TODO �Զ����ɵķ������
				
			}
		});

		searchButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO �Զ����ɵķ������

			}
		});

		chooseButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO �Զ����ɵķ������

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
		Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
		String current_timeString = formatter.format(curDate);
		mListView.setRefreshTime(current_timeString);
	}
	
	@Override
	public void onRefresh() {
		// TODO �Զ����ɵķ������
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				DownloadRunnable runnable = new DownloadRunnable();
				new Thread(runnable).start();//��ȡ����Ϣ
				
				xlistItemAdapter.notifyDataSetChanged();
				onLoad();
			}
		}, 1000);
	}

	@Override
	public void onLoadMore() {
		mHandler.postDelayed(new Runnable() {
			// TODO �Զ����ɵķ������
			@Override
			public void run() {
				//getInformation();//��ȡ����Ϣ
				//xlistItemAdapter.notifyDataSetChanged();
			}
		}, 1000);
	}
	public String HTTPGetInfo() {
		String uri = URL + start + ".json";
		String result = "";
		HttpGet httpGet = new HttpGet(uri);// �������ڵ�ַ�д���
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

	// ��̨�߳̽������ص�json��ʽ�ļ�
	class DownloadRunnable implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			String httpresponse = new String();
			httpresponse = HTTPGetInfo();
			try {
				JSONparser(httpresponse);
			} catch (JSONException e) {
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			}
		}

		public void JSONparser(String result) throws JSONException {
			if (result.length() > 100) {
				Log.i("1_mdatalistlength", ""+mDataList.size());
				JSONObject jsonObject = new JSONObject(result);
				JSONArray jsonArray = jsonObject.getJSONArray("job"); 
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", jsonObject.getString("id"));//��Ϣ��ID
				map.put("message_title", jsonObject.getString("message_title"));//��Ϣ����
				map.put("company", jsonObject.getString("company"));//��˾
				map.put("location", jsonObject.getString("location"));//��˾�ص�
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject2 = (JSONObject)jsonArray.opt(i);
					map.put("job_id", jsonObject2.getString("jid"));//��λID
					map.put("job_type", jsonObject2.getString("job_type"));//ʵϰ����ְ��ȫְ
					map.put("job", jsonObject2.getString("job_type2"));//�������ͣ�IT������...��
					map.put("workplace", jsonObject2.getString("job_area"));//�����ص�
					map.put("job_responsibilities", jsonObject2.getString("job_responsibilities"));//������λ����
					map.put("job_requirements", jsonObject2.getString("job_requirements"));//������������
					map.put("peoplefocus", "�����˹�ע");
				}
				mDataList.add(0,map);
				Log.i("2_mdatalistlength", ""+mDataList.size());
				start ++;
			}
			if (result == null) {
				Log.i("���ص�JSON�ļ�", "�յ�");
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
