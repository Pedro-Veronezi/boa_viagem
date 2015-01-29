package br.com.casadocodigo.boaviagem.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import br.com.casadocodigo.boaviagem.Constantes;
import br.com.casadocodigo.boaviagem.R;
import br.com.casadocodigo.boaviagem.bean.Viagem;
import br.com.casadocodigo.boaviagem.bo.BoaViagemBO;
import br.com.casadocodigo.boaviagem.ui.GastoActivity;

/**
 * Created by veronezi on 02/12/13.
 */
@EActivity(R.layout.activity_viagem)
public class ViagemActivity extends ActionBarActivity {
    private static String TAG = "ViagemActivity";

    @ViewById
     Button dataChegadaButton;

    @ViewById
     Button dataSaidaButton;

    @ViewById
     EditText destino;

    @ViewById
     EditText quantidadePessoas;

    @ViewById
     EditText orcamento;

    @ViewById
     RadioGroup tipoViagem;

     Calendar dataChegada, dataSaida;

    @Extra(Constantes.VIAGEM_ID)
     long id = -1;

    BoaViagemBO bo;

    private DatePickerDialog.OnDateSetListener dataChegadaDialog;

    private DatePickerDialog.OnDateSetListener dataSaidaDialog;


    @AfterViews
    void init() {

        bo = new BoaViagemBO(this);

        dataChegada =Calendar.getInstance();
        dataSaida =Calendar.getInstance();

        dataChegadaButton.setText(dataChegada.get(Calendar.DAY_OF_MONTH)+ "/"+ (dataChegada.get(Calendar.MONTH) + 1) + "/" + dataChegada.get(Calendar.YEAR));
        dataSaidaButton.setText(dataSaida.get(Calendar.DAY_OF_MONTH)+ "/"+ (dataSaida.get(Calendar.MONTH) + 1) + "/" + dataSaida.get(Calendar.YEAR));

        if (id > 0){
            prepararEdicao();
        }

        dataChegadaDialog = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                dataChegada.set(year, month, day);
                dataChegadaButton.setText(day + "/" + (month+1) + "/"+ year);
            }
        };

        dataSaidaDialog = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                dataSaida.set(year, month, day);
                dataSaidaButton.setText(day + "/" + (month+1) + "/"+ year);
            }
        };

    }

    private void prepararEdicao() {
        Log.d(TAG, "prepararEdicao()");

        Viagem viagem = bo.buscarViagemPorId(id);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        if(viagem.getTipoViagem() == Constantes.VIAGEM_LAZER){
            tipoViagem.check(R.id.lazer);
        }else{
            tipoViagem.check(R.id.negocios);
        }

        destino.setText(viagem.getDestino());
        dataChegadaButton.setText(dateFormat.format(dataChegada.getTime()));
        dataSaidaButton.setText(dateFormat.format(dataSaida.getTime()));
        quantidadePessoas.setText(viagem.getQuantidadePessoas().toString());
        orcamento.setText(String.valueOf(viagem.getOrcamento()));
    }

    @Click(R.id.salvarViagemButton)
    public void salvarViagem(View view) {
        Log.d(TAG, "salvarViagem(View view)");

        if (destino.getText().toString().length() < 1 ||
                orcamento.getText().toString().length() < 1 ||
                quantidadePessoas.getText().toString().length() < 1) {
            Toast.makeText(this, getString(R.string.preencher_campos), Toast.LENGTH_LONG).show();
            return;
        }

        Viagem viagem = new Viagem();
        viagem.setDestino(destino.getText().toString());
        viagem.setDataChegada(dataChegada.getTime());
        viagem.setDataSaida(dataSaida.getTime());
        viagem.setOrcamento(
                Double.valueOf(orcamento.getText().toString()));
        viagem.setQuantidadePessoas(
                Integer.valueOf(quantidadePessoas.getText().toString()));

        int tipo = tipoViagem.getCheckedRadioButtonId();

        if(tipo == R.id.lazer){
            viagem.setTipoViagem(Constantes.VIAGEM_LAZER);
        }else{
            viagem.setTipoViagem(Constantes.VIAGEM_NEGOCIOS);
        }

        long resultado;

        if(id < 0){
            resultado = bo.inserirViagem(viagem);
        }else{
            viagem.setId(id);
            resultado = bo.atualizarViagem(viagem);
        }

        if(resultado != -1 ){
            Toast.makeText(this, getString(R.string.registro_salvo), Toast.LENGTH_LONG).show();
            finish();
        }else{
            Toast.makeText(this, getString(R.string.erro_salvar), Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.viagem_menu, menu);
        return true;
    }
/*

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()){

            case R.id.novo_gasto:
                startActivity(new Intent(this, GastoActivity.class));
                return true;
            case R.id.remover:
                removerViagem(id);
                finish();
                return true;
            default:
                return super.onMenuItemSelected(featureId, item);
        }
    }
*/

    private void removerViagem(Long id) {
        bo.removerViagem(id);
        bo.removerGastosViagem(id);
    }

    @Click({R.id.dataChegadaButton, R.id.dataSaidaButton})
    public void selecionarData(View view) {
        showDialog(view.getId());
    }

    @Override
    protected Dialog onCreateDialog(int id){
        if(R.id.dataChegadaButton == id){
            return new DatePickerDialog(this, dataChegadaDialog, dataChegada.get(Calendar.YEAR), dataChegada.get(Calendar.MONTH), dataChegada.get(Calendar.DAY_OF_MONTH));
        }else if(R.id.dataSaidaButton == id){
            return new DatePickerDialog(this, dataSaidaDialog, dataSaida.get(Calendar.YEAR), dataSaida.get(Calendar.MONTH), dataSaida.get(Calendar.DAY_OF_MONTH));
        }
        return null;

    }



    @Override
    protected void onDestroy() {
        bo.closeDb();
        super.onDestroy();
    }
}