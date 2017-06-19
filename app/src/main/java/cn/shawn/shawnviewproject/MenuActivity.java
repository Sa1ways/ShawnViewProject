package cn.shawn.shawnviewproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MenuActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView mLv;

    Class[] mActivities = {SortTextViewActivity.class, SubmitButtonActivity.class
                , ProgressViewActivity.class};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        mLv = (ListView) findViewById(R.id.lv);
        mLv.setAdapter(new SimpleAdapter());
        mLv.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        startActivity(new Intent(this, mActivities[position]));
    }

    class SimpleAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mActivities.length;
        }

        @Override
        public Object getItem(int position) {
            return mActivities[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // no need to recycle view
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_menu_item,
                    parent, false);
            ((TextView)itemView.findViewById(R.id.tv_title)).setText(mActivities[position].getSimpleName());
            return itemView;
        }
    }
}
