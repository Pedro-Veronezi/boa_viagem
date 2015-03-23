package br.com.pedroveronezi.boaviagem.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.Calendar;

import br.com.pedroveronezi.boaviagem.Constantes;
import br.com.pedroveronezi.boaviagem.R;
import br.com.pedroveronezi.boaviagem.bean.Gasto;
import br.com.pedroveronezi.boaviagem.bean.Viagem;
import br.com.pedroveronezi.boaviagem.bo.BoaViagemBO;

@EActivity(R.layout.activity_gasto)
public class GastoActivity extends ActionBarActivity {
    private static final String TAG = "GastoActivity";

    @ViewById
    Button dataGastoButton;

    @ViewById
    Spinner categoria;

    @Extra(Constantes.VIAGEM_ID)
    long idViagem = 0;

    /**
     * Viagem ativa
     */
    private Viagem viagem;

    private Calendar dataGasto;


    private DatePickerDialog.OnDateSetListener dataGastoDialog;

    @AfterViews
    void init() {
        Log.i(TAG, "init");
        //configura o icone do action bar pra voltar ao dashboard
        // getActionBar().setHomeButtonEnabled(true);
        // getActionBar().setDisplayHomeAsUpEnabled(true);

        // Configurações do calendario
        dataGasto = Calendar.getInstance();
        dataGastoButton.setText(dataGasto.get(Calendar.DAY_OF_MONTH) + "/" + (dataGasto.get(Calendar.MONTH) + 1) + "/" + dataGasto.get(Calendar.YEAR));
        dataGastoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "dataGastoButton.onClick");
                showDialog(v.getId());
            }
        });

        dataGastoDialog = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                dataGasto.set(year, month, day);
                dataGastoButton.setText(day + "/" + (month + 1) + "/" + year);
            }
        };
        //Configurações do Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categoria_gasto, android.R.layout.simple_spinner_dropdown_item);
        categoria.setAdapter(adapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "onCreateOptionsMenu");

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.novo_gasto, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "onOptionsItemSelected");
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Click
    public void registrarGasto(View view) {
        Log.i(TAG, "void registrarGasto(View view)");
        EditText valor = (EditText) findViewById(R.id.valor);
        EditText descricao = (EditText) findViewById(R.id.descricao);
        EditText local = (EditText) findViewById(R.id.local);

        if (descricao.getText().toString().length() < 1 ||
                local.getText().toString().length() < 1 ||
                valor.getText().toString().length() < 1) {
            Toast.makeText(this, getString(R.string.preencher_campos), Toast.LENGTH_LONG).show();
            return;
        }

        Gasto gasto = new Gasto();
        gasto.setCategoria(categoria.getSelectedItem().toString());
        gasto.setData(dataGasto.getTime());

        gasto.setDescricao(descricao.getText().toString());
        gasto.setLocal(local.getText().toString());
        gasto.setValor(Double.valueOf(valor.getText().toString()));

            gasto.setViagemId(idViagem);

        long resultado = new BoaViagemBO(this).inserirGasto(gasto);


        if (resultado != -1) {
            Toast.makeText(this, getString(R.string.registro_salvo), Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, getString(R.string.erro_salvar), Toast.LENGTH_SHORT).show();
        }
    }


    public void selecionarData(View view) {
        showDialog(view.getId());
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (R.id.dataGastoButton == id) {
            return new DatePickerDialog(this, R.style.AppTheme, dataGastoDialog, dataGasto.get(Calendar.YEAR), dataGasto.get(Calendar.MONTH), dataGasto.get(Calendar.DAY_OF_MONTH));
        }
        return null;

    }



}
