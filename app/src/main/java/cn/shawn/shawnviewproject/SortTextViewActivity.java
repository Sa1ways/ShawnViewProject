package cn.shawn.shawnviewproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import cn.shawn.view.view.SortTextView;

public class SortTextViewActivity extends AppCompatActivity {

    SortTextView tvPrice;
    SortTextView tvSale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvPrice = (SortTextView) findViewById(R.id.tv_price);
        tvSale = (SortTextView) findViewById(R.id.tv_sale);
        tvPrice.setSortStateListener(priceListener);
        tvSale.setSortStateListener(saleListener);
    }

    SortTextView.OnSortStateChangeListener priceListener= new SortTextView.OnSortStateChangeListener() {
        @Override
        public void onStateChanged(SortTextView.SortState state) {
            tvSale.resetSort();
            switch (state ){
                case STATE_UP:
                    showToast("price up");
                    break;
                case STATE_DOWN:
                    showToast("price down");
                    break;
            }
        }
    };

    SortTextView.OnSortStateChangeListener saleListener= new SortTextView.OnSortStateChangeListener() {
        @Override
        public void onStateChanged(SortTextView.SortState state) {
            tvPrice.resetSort();
            switch (state ){
                case STATE_UP:
                    showToast("sale up");
                    break;
                case STATE_DOWN:
                    showToast("sale down");
                    break;
            }
        }
    };

    private void showToast(String text){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
