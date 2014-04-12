package br.com.casadocodigo.boaviagem;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.SyncStateContract;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import br.com.casadocodigo.boaviagem.bean.Viagem;
import br.com.casadocodigo.boaviagem.dao.BoaViagemDAO;

/**
 * Created by veronezi on 02/12/13.
 */
public class ViagemActivity extends Activity {
    private Button dataChegadaButton, dataSaidaButton;
    private EditText destino, quantidadePessoas, orcamento;
    private RadioGroup radioGroup;
    private Calendar dataChegada, dataSaida;
    private Long id;
    private BoaViagemDAO dao;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viagem);
        dataChegada =Calendar.getInstance();
        dataSaida =Calendar.getInstance();

        dataChegadaButton = (Button)findViewById(R.id.dataChegada);
        dataChegadaButton.setText(dataChegada.get(Calendar.DAY_OF_MONTH)+ "/"+ (dataChegada.get(Calendar.MONTH) + 1) + "/" + dataChegada.get(Calendar.YEAR));

        dataSaidaButton = (Button)findViewById(R.id.dataSaida);
        dataSaidaButton.setText(dataSaida.get(Calendar.DAY_OF_MONTH)+ "/"+ (dataSaida.get(Calendar.MONTH) + 1) + "/" + dataSaida.get(Calendar.YEAR));

        destino = (EditText) findViewById(R.id.destino);
        quantidadePessoas = (EditText) findViewById(R.id.quantidadePessoas);
        orcamento = (EditText) findViewById(R.id.orcamento);
        radioGroup = (RadioGroup) findViewById(R.id.tipoViagem);

        dao = new BoaViagemDAO(this);

        String idTemp = getIntent().getStringExtra(Constantes.VIAGEM_ID);

        if (null != idTemp){
            id = Long.valueOf(idTemp);
            prepararEdicao();
        }else{
            id = new Long(-1);
        }

    }

    private void prepararEdicao() {
        Viagem viagem = dao.buscarViagemPorId(id);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        if(viagem.getTipoViagem() == Constantes.VIAGEM_LAZER){
            radioGroup.check(R.id.lazer);
        }else{
            radioGroup.check(R.id.negocios);
        }

        destino.setText(viagem.getDestino());
        dataChegadaButton.setText(dateFormat.format(dataChegada.getTime()));
        dataSaidaButton.setText(dateFormat.format(dataSaida.getTime()));
        dataChegadaButton.setText(dateFormat.format(dataChegada));
        dataSaidaButton.setText(dateFormat.format(dataSaida));
        quantidadePessoas.setText(viagem.getQuantidadePessoas().toString());
        orcamento.setText(viagem.getOrcamento().toString());
    }


    public void salvarViagem(View view) {
        Viagem viagem = new Viagem();
        viagem.setDestino(destino.getText().toString());
        viagem.setDataChegada(dataChegada.getTime());
        viagem.setDataSaida(dataSaida.getTime());
        viagem.setOrcamento(
                Double.valueOf(orcamento.getText().toString()));
        viagem.setQuantidadePessoas(
                Integer.valueOf(quantidadePessoas.getText().toString()));

        int tipo = radioGroup.getCheckedRadioButtonId();

        if(tipo == R.id.lazer){
            viagem.setTipoViagem(Constantes.VIAGEM_LAZER);
        }else{
            viagem.setTipoViagem(Constantes.VIAGEM_NEGOCIOS);
        }

        long resultado;

        if(id == -1){
            resultado = dao.inserir(viagem);
        }else{
            resultado = dao.atualizar(viagem);
        }

        if(resultado != -1 ){
            Toast.makeText(this, getString(R.string.registro_salvo), Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, getString(R.string.erro_salvar), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.viagem_menu, menu);
        return true;
    }

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

    private void removerViagem(Long id) {
        dao.removerViagem(id);
        dao.removerGastosViagem(id);
    }

    public void selecionarData(View view) {
        showDialog(view.getId());
    }

    @Override
    protected Dialog onCreateDialog(int id){
        if(R.id.dataChegada == id){
            return new DatePickerDialog(this, dataChegadaDialog, dataChegada.get(Calendar.YEAR), dataChegada.get(Calendar.MONTH), dataChegada.get(Calendar.DAY_OF_MONTH));
        }else if(R.id.dataSaida == id){
            return new DatePickerDialog(this, dataSaidaDialog, dataSaida.get(Calendar.YEAR), dataSaida.get(Calendar.MONTH), dataSaida.get(Calendar.DAY_OF_MONTH));
        }
        return null;

    }

    private DatePickerDialog.OnDateSetListener dataChegadaDialog = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            dataChegada.set(year, month, day);
            dataChegadaButton.setText(day + "/" + (month+1) + "/"+ year);
        }
    };

    private DatePickerDialog.OnDateSetListener dataSaidaDialog = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            dataSaida.set(year, month, day);
            dataSaidaButton.setText(day + "/" + (month+1) + "/"+ year);
        }
    };

    @Override
    protected void onDestroy() {
        dao.close();
        super.onDestroy();
    }
}