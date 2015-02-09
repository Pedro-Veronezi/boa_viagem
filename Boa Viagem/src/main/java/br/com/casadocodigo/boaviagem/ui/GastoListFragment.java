package br.com.casadocodigo.boaviagem.ui;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import br.com.casadocodigo.boaviagem.Constantes;
import br.com.casadocodigo.boaviagem.R;
import br.com.casadocodigo.boaviagem.bean.Gasto;
import br.com.casadocodigo.boaviagem.bo.BoaViagemBO;

/**
 * Created by veronezi on 07/01/14.
 */
public class GastoListFragment extends ListFragment implements AdapterView.OnItemClickListener{
    private static final String TAG = "GastoListFragment";
    private List<Gasto> gastos;
    private String dataAnterior = "";
    private BoaViagemBO boaViagemBO;
    private FloatingActionButton fab;
    private GastoListAdapter gastoListAdapter;
    private long idViagem;
    private OnGastoSelectedListener listener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnGastoSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnGastoSelectedListener");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        boaViagemBO = new BoaViagemBO(getActivity());

        idViagem = getActivity().getIntent().getLongExtra(Constantes.VIAGEM_ID, 0);

        if (0 != idViagem){
            gastos = boaViagemBO.listarGastos(idViagem);
        }else {
            gastos = new ArrayList<Gasto>();
        }


        gastoListAdapter = new GastoListAdapter(getActivity(), R.layout.row_list_gasto, gastos);

        setListAdapter(gastoListAdapter);
        getListView().setOnItemClickListener(this);

        // registro do menu de contexto
        registerForContextMenu(getListView());

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "fab.setOnClickListener");
                Intent intent = new Intent(getActivity(), GastoActivity_.class);
                intent.putExtra(Constantes.VIAGEM_ID, idViagem);
                startActivity(intent);

            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_gasto, container, false);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);

        return view;
    }


    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        gastoListAdapter.clear();
        gastoListAdapter.addAll(boaViagemBO.listarGastos(idViagem));
        gastoListAdapter.notifyDataSetChanged();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view,
                            int position, long id) {
        listener.onGastoSelected(gastos.get(position).getId());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.gasto_menu, menu);
    }

    //TODO Adicionar a confirmação de exclusão, adicionar editar
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

    // Container Activity must implement this interface
    public interface OnGastoSelectedListener {
        public void onGastoSelected(long id);
    }


}
