package br.com.casadocodigo.boaviagem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by veronezi on 01/12/13.
 */
public class DashboardActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
    }

    public void selecionarOpcao(View view) {
        switch (view.getId()) {
            case R.id.nova_viagem:
                startActivity(new Intent(this, ViagemActivity.class));
                break;

        }


    }
}