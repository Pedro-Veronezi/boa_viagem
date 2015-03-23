package br.com.pedroveronezi.boaviagem.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
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

import br.com.pedroveronezi.boaviagem.Constantes;
import br.com.pedroveronezi.boaviagem.R;
import br.com.pedroveronezi.boaviagem.bean.Viagem;
import br.com.pedroveronezi.boaviagem.bo.BoaViagemBO;

/**
 * Created by veronezi on 09/04/14.
 */
public class ViagemListAdapter extends ArrayAdapter<Viagem> {
    private SimpleDateFormat dateFormat;
    private BoaViagemBO boaViagemBO;

    public ViagemListAdapter(Context context, int resource, List<Viagem> viagens) {
        super(context, resource, viagens);
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        boaViagemBO = new BoaViagemBO(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.row_list_viagem, null);
        }

        Viagem viagem = getItem(position);

        if (v != null) {

            ImageView tipoViagem = (ImageView) v.findViewById(R.id.tipoViagem);

            TextView destino = (TextView) v.findViewById(R.id.destino);
            TextView data = (TextView) v.findViewById(R.id.data);
            TextView orcamento = (TextView) v.findViewById(R.id.valor);
            ProgressBar barraProgresso = (ProgressBar) v.findViewById(R.id.barraProgresso);
            Drawable tipoViagemIcone;

            if(viagem.getTipoViagem() == Constantes.VIAGEM_LAZER){
                tipoViagemIcone = getContext().getResources().getDrawable(R.drawable.ic_martini_white_24dp);
            }else{
                tipoViagemIcone = getContext().getResources().getDrawable(R.drawable.ic_currency_usd_white_24dp);
            }

            tipoViagemIcone.setColorFilter(getContext().getResources().getColor(R.color.accent), PorterDuff.Mode.MULTIPLY);
            tipoViagem.setImageDrawable(tipoViagemIcone);

            destino.setText(viagem.getDestino());
            String periodo = dateFormat.format(viagem.getDataChegada()) + " a "
                    + dateFormat.format(viagem.getDataSaida());
            data.setText(periodo);

            double totalGasto = boaViagemBO.calcularTotalGasto(viagem);

            orcamento.setText("Gasto total: R$ " + totalGasto + " de R$ " + viagem.getOrcamento());

            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
            int valorLimite = Integer.valueOf(pref.getString("valor_limite", "80"));
            int alerta = (int) (viagem.getOrcamento() * valorLimite / 100);

            barraProgresso.setMax((int)viagem.getOrcamento());
            barraProgresso.setSecondaryProgress(alerta);
            barraProgresso.setProgress((int) totalGasto);


            if (alerta < totalGasto) {
                Drawable drawable = barraProgresso.getProgressDrawable();
                drawable.setColorFilter(new LightingColorFilter(Color.RED, Color.RED));
                orcamento.setTextColor(Color.RED);
            }
        }


        return v;

    }
}