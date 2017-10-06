package com.lancius.palle2patnam.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.lancius.palle2patnam.R;


public class OrderSuccessActivity extends AppCompatActivity {

    String conform_order_id, slot, price, items;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_success);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Bundle b = getIntent().getExtras();
        conform_order_id = b.getString("OrderId");
        price = b.getString("Amount");
        items = b.getString("Items");

        TextView order_id_tx = (TextView) findViewById(R.id.conformation_order_no);
        order_id_tx.setText("Your Order Id is : " + conform_order_id);
//        TextView slot_tx = (TextView) findViewById(R.id.conformation_date);
//        slot_tx.setText(slot);
        TextView price_tx = (TextView) findViewById(R.id.conformation_items_total);
        price_tx.setText("Total Amount :  Rs. " + price);
        TextView items_tx = (TextView) findViewById(R.id.conformation_items);
        items_tx.setText("Number of Items : " + items + "");

    }

    public void ok(View v) {

        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);

        finish();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();


        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);

        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.

                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);

                finish();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
