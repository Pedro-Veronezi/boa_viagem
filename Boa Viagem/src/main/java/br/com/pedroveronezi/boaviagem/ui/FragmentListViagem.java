package br.com.pedroveronezi.boaviagem.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;


import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.List;

import br.com.pedroveronezi.boaviagem.Constantes;
import br.com.pedroveronezi.boaviagem.R;
import br.com.pedroveronezi.boaviagem.bean.Viagem;
import br.com.pedroveronezi.boaviagem.bo.BoaViagemBO;

/**
 * Created by pcampos on 07/01/14.
 */


public class FragmentListViagem extends ListFragment implements AdapterView.OnItemClickListener, SimpleAdapter.ViewBinder {

    private static final String TAG = "ViagemListFragment";
    private Double valorLimite;
    private BoaViagemBO boaViagemBO;
    private List<Viagem> viagens;
    private ViagemListAdapter viagemListAdapter;
    private FloatingActionButton fab;
    private OnViagemSelectedListener listener;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnViagemSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnViagemSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_viagem, container, false);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        boaViagemBO = new BoaViagemBO(getActivity());
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String valor = pref.getString("valor_limite", "-1");
        valorLimite = Double.valueOf(valor);

        viagens = boaViagemBO.listarViagens();
        viagemListAdapter = new ViagemListAdapter(getActivity(), R.layout.row_list_viagem, viagens);

        setListAdapter(viagemListAdapter);
        getListView().setOnItemClickListener(this);

        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "fab.setOnClickListener");
                    startActivity(new Intent(getActivity(), ViagemActivity_.class));

                }
            });
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        registerForContextMenu(getListView());
    }


    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        viagemListAdapter.clear();
        viagemListAdapter.addAll(boaViagemBO.listarViagens());
        viagemListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d(TAG, "onItemClick");
        listener.onViagemSelected(viagens.get(i).getId());

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.gasto_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.remover:
                showDialog(info.position);
                break;
            case R.id.editar:
                Intent intent = new Intent(getActivity(), ViagemActivity_.class);
                intent.putExtra(Constantes.VIAGEM_ID, viagens.get(info.position).getId());
                startActivity(intent);
                break;
        }

        return super.onContextItemSelected(item);
    }


    private void showDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.confirmacao_exclusao_viagem);
        builder.setPositiveButton(getString(R.string.sim), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                boaViagemBO.removerViagem(viagens.get(position).getId());
                viagens.remove(position);
                getListView().invalidateViews();
                viagemListAdapter.notifyDataSetChanged();

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

    @Override
    public boolean setViewValue(View view, Object o, String s) {
        Log.d(TAG, "setViewValue");
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
    public void onDestroyView() {
        Log.d(TAG, "onDestroy");
        boaViagemBO.closeDb();
        super.onDestroyView();
    }

    // Container Activity must implement this interface
    public interface OnViagemSelectedListener {
        public void onViagemSelected(long id);
    }
}
