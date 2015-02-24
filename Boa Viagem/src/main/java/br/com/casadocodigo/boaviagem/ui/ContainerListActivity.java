package br.com.casadocodigo.boaviagem.ui;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import br.com.casadocodigo.boaviagem.Constantes;
import br.com.casadocodigo.boaviagem.R;

/**
 * Created by pveronezi on 29/01/15.
 */
public class ContainerListActivity extends ActionBarActivity
        implements GastoListFragment.OnGastoSelectedListener,
        ViagemListFragment.OnViagemSelectedListener {
    public static final String TAG = "ContainerListActivity";

    private boolean tablet = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.container_list_viagem);

        View view = findViewById(R.id.fragment_container);

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (view != null) {
            tablet = false;

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            ViagemListFragment viagemFragment = new ViagemListFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            viagemFragment.setArguments(getIntent().getExtras());

            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.fragment_container, viagemFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }



    @Override
    public void onViagemSelected(long id) {
        Log.i(TAG, "onViagemSelected");
        if (!tablet){
            GastoListFragment gastoListFragment = new GastoListFragment();
            Bundle args = new Bundle();
            args.putLong(Constantes.VIAGEM_ID, id);
            gastoListFragment.setArguments(args);

            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, gastoListFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
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
