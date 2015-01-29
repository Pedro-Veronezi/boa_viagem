package br.com.casadocodigo.boaviagem.ui;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.casadocodigo.boaviagem.Constantes;
import br.com.casadocodigo.boaviagem.R;
import br.com.casadocodigo.boaviagem.bean.Gasto;
import br.com.casadocodigo.boaviagem.bo.BoaViagemBO;

/**
 * Created by veronezi on 07/01/14.
 */
public class GastoListActivity extends ListActivity implements AdapterView.OnItemClickListener{
    private List<Gasto> gastos;
    private String dataAnterior = "";
    private BoaViagemBO boaViagemBO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boaViagemBO = new BoaViagemBO(this);
        long idTemp = getIntent().getLongExtra(Constantes.VIAGEM_ID, 0);

        if (0 != idTemp){
            gastos = boaViagemBO.listarGastos(idTemp);
        }else {
            gastos = new ArrayList<Gasto>();
        }

        GastoListAdapter gastoListAdapter = new GastoListAdapter(this, R.layout.lista_gasto, gastos);

        setListAdapter(gastoListAdapter);
        getListView().setOnItemClickListener(this);

        // registro do menu de contexto
        registerForContextMenu(getListView());

    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view,
                            int position, long id) {
        String mensagem = "Gasto selecionada: " + gastos.get(position).getDescricao();
        Toast.makeText(this, mensagem,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.gasto_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.remover){
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            gastos.remove(info.position);
            getListView().invalidateViews();
            dataAnterior = "";
            // remover do banco
            return true;
        }
        return super.onContextItemSelected(item);
    }

    private class GastoViewBinder implements SimpleAdapter.ViewBinder{


        @Override
        public boolean setViewValue(View view, Object data, String textRepresentation) {
            if(view.getId() == R.id.data){
                if(!dataAnterior.equals(data)){
                    TextView textView = (TextView) view;
                    textView.setText(textRepresentation);
                    dataAnterior = textRepresentation;
                    view.setVisibility(View.VISIBLE);
                } else {
                    view.setVisibility(View.GONE);
                }
                return true;
            }
            if(view.getId() == R.id.categoria){
                Integer id = (Integer) data;
                view.setBackgroundColor(getResources().getColor(id));
                return true;
            }
            return false;

                }
    }




}
