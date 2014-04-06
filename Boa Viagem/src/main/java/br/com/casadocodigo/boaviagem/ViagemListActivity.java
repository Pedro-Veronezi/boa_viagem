package br.com.casadocodigo.boaviagem;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

/**
 * Created by pcampos on 07/01/14.
 */
public class ViagemListActivity extends ListActivity implements AdapterView.OnItemClickListener, DialogInterface.OnClickListener, SimpleAdapter.ViewBinder{
    private List<Map<String, Object>> viagens;
    private AlertDialog alertDialog;
    private int viagemSelecionada;
    private AlertDialog dialogConfirmacao;

    private DatabaseHelper helper;
    private SimpleDateFormat dateFormat;
    private Double valorLimite;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        helper = new DatabaseHelper(this);
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        String valor = pref.getString("valor_limite", "-1");
        valorLimite = Double.valueOf(valor);

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
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor c = db.rawQuery("select _id, tipo_viagem, destino, " +
                "data_chegada, data_saida, orcamento from viagem", null);

        c.moveToFirst();



        viagens = new ArrayList<Map<String,Object>>();
        Map<String, Object> item = null;

        for(int i = 0; i< c.getCount(); i++) {
            item = new HashMap<String, Object>();

            String id = c.getString(0);
            int tipoViagem = c.getInt(1);
            String destino = c.getString(2);
            long dataChegada = c.getLong(3);
            long dataSaida = c.getLong(4);
            double orcamento = c.getDouble(5);

            item.put("id", id);
            item.put("imagem", tipoViagem == Constantes.VIAGEM_LAZER? R.drawable.lazer : R.drawable.negocios);
            item.put("destino", destino);
            Date dataChegadaDate = new Date(dataChegada);
            Date dataSaidaDate = new Date(dataSaida);
            item.put("data", dateFormat.format(dataChegadaDate) + " a "
                    + dateFormat.format(dataSaidaDate));
            double totalGasto = calcularTotalGasto(db, id);

            item.put("total", "Gasto total R$ " + totalGasto);
            double alerta = orcamento * valorLimite / 100;
            Double [] valores =
                    new Double[] { orcamento, alerta, totalGasto };
            item.put("barraProgresso", valores);

            viagens.add(item);

            c.moveToNext();
        }

        c.close();
        return viagens;
    }

    private double calcularTotalGasto(SQLiteDatabase db, String id) {
        Cursor cursor = db.rawQuery(
                "SELECT SUM(valor) FROM gasto WHERE viagem_id = ?",
                new String[]{ id });
        cursor.moveToFirst();
        double total = cursor.getDouble(0);
        cursor.close();
        return total;
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
