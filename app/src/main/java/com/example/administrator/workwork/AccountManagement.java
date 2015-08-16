package com.example.administrator.workwork;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

/**
 * Created by Sunny on 8/16/2015.
 */
public class AccountManagement extends Activity{

    Spinner plan_type;
    Button MakePayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accounts_management);
        plan_type = (Spinner) findViewById(R.id.plan_categories);
        MakePayment = (Button) findViewById(R.id.make_payment);
        MakePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), com.example.administrator.workwork.MakePayment.class);
                i.putExtra("type",""+plan_type.getSelectedItemPosition());
                startActivity(i);
            }
        });
    }
}
