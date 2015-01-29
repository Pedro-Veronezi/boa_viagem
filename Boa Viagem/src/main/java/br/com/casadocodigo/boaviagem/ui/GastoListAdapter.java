package br.com.casadocodigo.boaviagem.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import br.com.casadocodigo.boaviagem.R;
import br.com.casadocodigo.boaviagem.bean.Gasto;
import br.com.casadocodigo.boaviagem.bean.Viagem;
import br.com.casadocodigo.boaviagem.bo.BoaViagemBO;

/**
 * Created by veronezi on 09/04/14.
 */
public class GastoListAdapter extends ArrayAdapter<Gasto> {
    private SimpleDateFormat dateFormat;
    private String dataAnterior;

    public GastoListAdapter(Context context, int resource, List<Gasto> gastos) {
        super(context, resource, gastos);
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dataAnterior = "";
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.lista_gasto, null);
            if (v == null) return v;
        }

        Gasto gasto = getItem(position);

        TextView data = (TextView) v.findViewById(R.id.data);
        TextView descricao = (TextView) v.findViewById(R.id.descricao);
        TextView valor = (TextView) v.findViewById(R.id.valor);

        if (!dataAnterior.equals(dateFormat.format(gasto.getData()))){
            dataAnterior = dateFormat.format(gasto.getData());
            data.setText(dataAnterior);
        }else{
            data.setVisibility(View.GONE);
        }
        descricao.setText(gasto.getDescricao());
        valor.setText("R$ "+ gasto.getValor());
        return v;
    }
}