package br.com.casadocodigo.boaviagem;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pcampos on 07/01/14.
 */
public class ViagemListActivity extends ListActivity implements AdapterView.OnItemClickListener, DialogInterface.OnClickListener, SimpleAdapter.ViewBinder{
    private List<Map<String, Object>> viagens;
    private AlertDialog alertDialog;
    private int viagemSelecionada;
    private AlertDialog dialogConfirmacao;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        String[] de = {"imagem", "destino", "data", "total", "barraProgresso"};
        int[] para = {R.id.tipoViagem, R.id.destino, R.id.data, R.id.valor, R.id.barraProgresso};

        SimpleAdapter adapter;
        adapter = new SimpleAdapter(this, listarViagens(), R.layout.lista_viagem, de, para);
        adapter.setViewBinder(this);
        setListAdapter(adapter);

        getListView().setOnItemClickListener(this);

        this.alertDialog = criarAlertDialog();
        this.dialogConfirmacao = criarDialogConfirmacao();
    }

    private List<Map<String, Object>> listarViagens() {
        viagens = new ArrayList<Map<String,Object>>();
        Map<String, Object> item = new HashMap<String, Object>();
        item.put("imagem", R.drawable.negocios);
        item.put("destino", "São Paulo");
        item.put("data","02/02/2012 a 04/02/2012");
        item.put("total","Gasto total R$ 314,98");
        item.put("barraProgresso", new Double[]{500.0, 450.0, 314.98});

        viagens.add(item);
        item = new HashMap<String, Object>();
        item.put("imagem", R.drawable.lazer);
        item.put("destino", "Maceió");
        item.put("data","14/05/2012 a 22/05/2012");
        item.put("total","Gasto total R$ 25834,67");
        item.put("barraProgresso", new Double[]{800.0, 750.0, 300.0});
        viagens.add(item);
        return viagens;
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Map<String, Object> map = viagens.get(i);
        this.viagemSelecionada = i;
        alertDialog.show();

    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        switch (i){
            case 0:
                startActivity(new Intent(this, ViagemActivity.class));
                break;
            case 1:
                startActivity(new Intent(this, GastoActivity.class));
                break;
            case 2:
                startActivity(new Intent(this, GastoListActivity.class));
                break;
            case 3:
                dialogConfirmacao.show();
                break;
            case DialogInterface.BUTTON_POSITIVE:
                viagens.remove(viagemSelecionada);
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
}
