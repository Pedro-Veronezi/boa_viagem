package br.com.casadocodigo.boaviagem;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.casadocodigo.boaviagem.bean.Viagem;
import br.com.casadocodigo.boaviagem.bo.BoaViagemBO;
import br.com.casadocodigo.boaviagem.dao.BoaViagemDAO;

/**
 * Created by pcampos on 07/01/14.
 */


public class ViagemListActivity extends ListActivity implements AdapterView.OnItemClickListener, DialogInterface.OnClickListener, SimpleAdapter.ViewBinder{

    private static final String TAG = "ViagemListActivity";
    private AlertDialog alertDialog;
    private int viagemSelecionada;
    private AlertDialog dialogConfirmacao;
    private Double valorLimite;
    private BoaViagemBO boaViagemBO;
    private List<Viagem> viagens;
    private ViagemListAdapter viagemListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        boaViagemBO = new BoaViagemBO(this);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        String valor = pref.getString("valor_limite", "-1");
        valorLimite = Double.valueOf(valor);

        viagens = boaViagemBO.listarViagens();
        viagemListAdapter =  new ViagemListAdapter(this,R.layout.lista_viagem, viagens);

        setListAdapter(viagemListAdapter);
        getListView().setOnItemClickListener(this);

        this.alertDialog = criarAlertDialog();
        this.dialogConfirmacao = criarDialogConfirmacao();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onResume() {
        super.onResume();
        viagemListAdapter.clear();
        viagemListAdapter.addAll(boaViagemBO.listarViagens());
        viagemListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //Map<String, Object> map = viagens.get(i);
        this.viagemSelecionada = i;
        alertDialog.show();

    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        Log.d(TAG, "void onClick(DialogInterface dialogInterface, int i)");
        Intent intent;
        Long id = viagens.get(viagemSelecionada).getId();
        Log.d(TAG, "void onClick(DialogInterface dialogInterface, int i) - id: "+ id);

        switch (i){
            case 0:
                intent = new Intent(this, ViagemActivity_.class);
                intent.putExtra(Constantes.VIAGEM_ID, id);
                startActivity(intent);
                break;
            case 1:
                intent = new Intent(this, GastoActivity.class);
                intent.putExtra(Constantes.VIAGEM_ID, id);
                startActivity(intent);
                break;
            case 2:
                intent = new Intent(this, GastoListActivity.class);
                intent.putExtra(Constantes.VIAGEM_ID, id);
                startActivity(intent);
                break;
            case 3:
                dialogConfirmacao.show();
                break;
            case DialogInterface.BUTTON_POSITIVE:
                viagens.remove(viagemSelecionada);
                boaViagemBO.removerViagem(id);
                getListView().invalidateViews();
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                dialogConfirmacao.dismiss();
                break;
        }
    }

    private AlertDialog criarAlertDialog() {
        final CharSequence[] itens = {
                getString(R.string.editar),
                getString(R.string.novo_gasto),
                getString(R.string.gastos_realizados),
                getString(R.string.remover)};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.opcoes);
        builder.setItems(itens, this);

        return builder.create();

    }

    private AlertDialog criarDialogConfirmacao() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirmacao_exclusao_viagem);
        builder.setPositiveButton(getString(R.string.sim), this);
        builder.setNegativeButton(getString(R.string.nao), this);
        return builder.create();

    }

    @Override
    public boolean setViewValue(View view, Object o, String s) {
        if (view.getId() == R.id.barraProgresso) {
            Double[] valores = (Double[]) o;
            ProgressBar pb = (ProgressBar) view;
            pb.setMax(valores[0].intValue());
            pb.setSecondaryProgress(valores[1].intValue());
            pb.setProgress(valores[2].intValue());
            return true;
        }

        return false;
    }
    @Override
    protected void onDestroy() {
        boaViagemBO.closeDao();
        super.onDestroy();
    }
}
