package br.com.pedroveronezi.boaviagem.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.DialogInterface;
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


import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.List;

import br.com.pedroveronezi.boaviagem.Constantes;
import br.com.pedroveronezi.boaviagem.R;
import br.com.pedroveronezi.boaviagem.bean.Gasto;
import br.com.pedroveronezi.boaviagem.bo.BoaViagemBO;

/**
 * Created by veronezi on 07/01/14.
 */
public class FragmentListGasto extends ListFragment implements AdapterView.OnItemClickListener {
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

        Bundle bundle = this.getArguments();

        idViagem = -1;
        if (bundle != null) {
            idViagem = bundle.getLong(Constantes.VIAGEM_ID, -1);
        }

        atualizarLista(idViagem);
    }

    private void showDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.confirmacao_exclusao_gasto);
        builder.setPositiveButton(getString(R.string.sim), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                boaViagemBO.removerGasto(gastos.get(position).getId());
                gastos.remove(position);
                getListView().invalidateViews();
                gastoListAdapter.notifyDataSetChanged();
                dataAnterior = "";
            }
        });
        builder.setNegativeButton(getString(R.string.nao), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();

    }

    private void atualizarLista(long id) {
        idViagem = id;

        gastos = boaViagemBO.listarGastos(idViagem);

        gastoListAdapter = new GastoListAdapter(getActivity(), R.layout.row_list_gasto, gastos);
        setListAdapter(gastoListAdapter);
        getListView().setOnItemClickListener(this);

        // registro do menu de contexto
        registerForContextMenu(getListView());
        if (fab != null) {
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
        getListView().invalidate();
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

    //TODO adicionar editar
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.remover:
                showDialog(info.position);
                break;
            case R.id.editar:
                break;

        }
        return super.onContextItemSelected(item);
    }

    public void updateGastos(long id) {
        atualizarLista(id);

    }


    // Container Activity must implement this interface
    public interface OnGastoSelectedListener {
        public void onGastoSelected(long id);
    }


}
