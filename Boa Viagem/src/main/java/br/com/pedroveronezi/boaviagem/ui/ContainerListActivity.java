package br.com.pedroveronezi.boaviagem.ui;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import br.com.pedroveronezi.boaviagem.Constantes;
import br.com.pedroveronezi.boaviagem.R;

/**
 * Created by pveronezi on 29/01/15.
 */
public class ContainerListActivity extends ActionBarActivity
        implements FragmentListGasto.OnGastoSelectedListener,
        FragmentListViagem.OnViagemSelectedListener {
    public static final String TAG = "ContainerListActivity";

    private boolean tablet = true;
    private FragmentListGasto fragmentListGasto;
    private FloatingActionsMenu fab;
    private FloatingActionButton fabViagem;
    private FloatingActionButton fabGasto;
    private long idViagemSelecionada;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.container_list_viagem);

        View view = findViewById(R.id.fragment_container);

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (view != null) {
            Log.d(TAG, "Celular ou tablet vertical");
            tablet = false;

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            /*if (savedInstanceState != null) {
                Log.d(TAG, "(savedInstanceState != null)");
                return;
            }*/

            // Create a new Fragment to be placed in the activity layout
            FragmentListViagem viagemFragment = new FragmentListViagem();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            viagemFragment.setArguments(getIntent().getExtras());

            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.fragment_container, viagemFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }else {
            Log.d(TAG, "Tablet land");
            tablet = true;
            fragmentListGasto  = (FragmentListGasto) getFragmentManager()
                    .findFragmentById(R.id.fragment_gastos);
            fabViagem = (FloatingActionButton) findViewById(R.id.fab_nova_viagem);
            fabGasto = (FloatingActionButton) findViewById(R.id.fab_novo_gasto);

            fabViagem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "fabViagem.onClick");
                    startActivity(new Intent(ContainerListActivity.this, ViagemActivity_.class));
                }
            });

            fabGasto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "fabGasto.onClick");
                    if (idViagemSelecionada<1){
                        Toast.makeText(ContainerListActivity.this, getString(R.string.Selecione_antes_uma_viagem), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent intent = new Intent(ContainerListActivity.this, GastoActivity_.class);
                    intent.putExtra(Constantes.VIAGEM_ID, idViagemSelecionada);
                    startActivity(intent);
                }
            });
        }
    }



    @Override
    public void onViagemSelected(long id) {
        Log.i(TAG, "onViagemSelected");

        if (!tablet){
            fragmentListGasto = new FragmentListGasto();
            Bundle args = new Bundle();
            args.putLong(Constantes.VIAGEM_ID, id);
            fragmentListGasto.setArguments(args);

            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragmentListGasto);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }else {
            idViagemSelecionada = id;
            fragmentListGasto.updateGastos(id);
        }

    }

    @Override
    public void onGastoSelected(long id) {
        Log.i(TAG, "onGastoSelected");
        Toast.makeText(this, "Gasto selecionada: " + id, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        Log.i(TAG, "onBackPressed");
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else {
            getFragmentManager().popBackStack();
        }
    }
}
