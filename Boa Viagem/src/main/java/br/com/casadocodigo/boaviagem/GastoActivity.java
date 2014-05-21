package br.com.casadocodigo.boaviagem;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Calendar;

import br.com.casadocodigo.boaviagem.bean.Gasto;
import br.com.casadocodigo.boaviagem.bo.BoaViagemBO;

public class GastoActivity extends ActionBarActivity {
    private static final String TAG = "GastoActivity";
    private Button dataGastoButon;
    private Spinner categoria;
    private Calendar dataGasto;
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gasto);

        //configura o icone do action bar pra voltar ao dashboard
        // getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        // Configurações do calendario
        dataGasto = Calendar.getInstance();
        dataGastoButon = (Button) findViewById(R.id.data);
        dataGastoButon.setText(dataGasto.get(Calendar.DAY_OF_MONTH) + "/" + (dataGasto.get(Calendar.MONTH) + 1) + "/" + dataGasto.get(Calendar.YEAR));

        //Configurações do Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categoria_gasto, android.R.layout.simple_spinner_item);
        categoria = (Spinner) findViewById(R.id.categoria);
        categoria.setAdapter(adapter);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.novo_gasto, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void registrarGasto(View view) {
        Log.i(TAG, "void registrarGasto(View view)");
        EditText valor = (EditText) findViewById(R.id.valor);
        EditText descricao = (EditText) findViewById(R.id.descricao);
        EditText local = (EditText) findViewById(R.id.local);


        Gasto gasto = new Gasto();
        gasto.setCategoria(categoria.getSelectedItem().toString());
        gasto.setData(dataGasto.getTime());
        gasto.setDescricao(descricao.getText().toString());
        gasto.setLocal(local.getText().toString());
        gasto.setValor(Double.valueOf(valor.getText().toString()));

        long idTemp = getIntent().getLongExtra(Constantes.VIAGEM_ID, 0);
        Log.i(TAG, "void registrarGasto(View view) - Viagem id: "+ idTemp);
        if (0 != idTemp){
            gasto.setViagemId(idTemp);
        }

        long resultado = new BoaViagemBO(this).inserirGasto(gasto);


        if(resultado != -1 ){
            Toast.makeText(this, getString(R.string.registro_salvo), Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, getString(R.string.erro_salvar), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_gasto, container, false);
            return rootView;
        }
    }

    public void selecionarData(View view) {
        showDialog(view.getId());
    }

    @Override
    protected Dialog onCreateDialog(int id){
        if(R.id.data == id){
            return new DatePickerDialog(this, dataGastoDialog, dataGasto.get(Calendar.YEAR), dataGasto.get(Calendar.MONTH), dataGasto.get(Calendar.DAY_OF_MONTH));
        }
        return null;

    }

    private DatePickerDialog.OnDateSetListener dataGastoDialog = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            dataGastoButon.setText(day + "/" + (month+1) + "/"+ year);
            dataGasto.set(year, month, day);
        }
    };

}
