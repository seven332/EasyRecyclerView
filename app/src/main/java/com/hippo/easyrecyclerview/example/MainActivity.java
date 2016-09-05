package com.hippo.easyrecyclerview.example;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hippo.easyrecyclerview.EasyRecyclerView;
import com.hippo.ripple.Ripple;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EasyRecyclerView view = (EasyRecyclerView) findViewById(R.id.recycler_view);
        view.setLayoutManager(new LinearLayoutManager(this));
        view.setAdapter(new RecyclerView.Adapter<SimpleHolder>() {
            @Override
            public SimpleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new SimpleHolder(LayoutInflater.from(MainActivity.this)
                        .inflate(R.layout.item_simple, parent, false));
            }

            @Override
            public void onBindViewHolder(SimpleHolder holder, int position) {
                holder.mTextView.setText("AAAAAAAAAAAAA");
            }

            @Override
            public int getItemCount() {
                return 100;
            }
        });
        view.setSelector(Ripple.generateRippleDrawable(this, true));
        view.setDrawSelectorOnTop(true);
    }

    private static class SimpleHolder extends RecyclerView.ViewHolder {

        private final TextView mTextView;

        public SimpleHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.text);
        }
    }
}
