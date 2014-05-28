package br.com.casadocodigo.boaviagem;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;

public class LoginActivity extends ActionBarActivity {

    /**
     * A placeholder fragment containing a simple view.
     */
        /*

            private EditText usuario;
    private EditText senha;
    private CheckBox manterConectado;

    private SharedPreferences preference_header;
    private GoogleAccountManager accountManager;
    private Account conta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        usuario = (EditText) findViewById(R.id.usuario);
        senha = (EditText) findViewById(R.id.senha);
        manterConectado = (CheckBox) findViewById(R.id.manterConectado);
        accountManager = new GoogleAccountManager(this);

        preference_header = getPreferences(MODE_PRIVATE);

        if(preference_header.getBoolean(Constantes.MANTER_CONECTADO, false)){
            iniciarDashboard();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void entrarOnClick(View view) {
        String usuarioInformado = usuario.getText().toString();
        String senhaInformada = senha.getText().toString();

        autenticar(usuarioInformado, senhaInformada);



    }

    private void autenticar(final String usuario, String senha) {
        conta = accountManager.getAccountByName(usuario);

        if (conta == null) {
            Toast.makeText(this, getString(R.string.conta_inexistente), Toast.LENGTH_SHORT).show();
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putString(AccountManager.KEY_ACCOUNT_NAME, usuario);
        bundle.putString(AccountManager.KEY_PASSWORD, senha);

        accountManager.getAccountManager().confirmCredentials(conta, bundle, this, new AutenticacaoCallback(), null);

    }

    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_login, container, false);
            return rootView;
        }
    }

    private void solicitarAutorizacao(){
        accountManager.getAccountManager().getAuthToken(conta, Constantes.AUTH_TOKEN_TYPE,
                null, this, new AutorizacaoCallback(), null);
    }

    private void gravarTokenAcesso(String nomeConta, String tokenAcesso) {
        SharedPreferences.Editor editor = preference_header.edit();
        editor.putString(Constantes.NOME_CONTA, nomeConta);
        editor.putString(Constantes.TOKEN_ACESSO, tokenAcesso);
        editor.commit();
    }


    private class AutenticacaoCallback implements AccountManagerCallback<Bundle> {
        @Override
        public void run(AccountManagerFuture<Bundle> future) {
            try{
                Bundle bundle = future.getResult();
                if(bundle.getBoolean(AccountManager.KEY_BOOLEAN_RESULT)){
                    solicitarAutorizacao();
                }else {
                    Toast.makeText(getBaseContext(), getString(R.string.erro_autenticacao),
                            Toast.LENGTH_LONG).show();
                }



            } catch (AuthenticatorException e) {
                e.printStackTrace();
            } catch (OperationCanceledException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void iniciarDashboard() {
        startActivity(new Intent (this, DashboardActivity.class));
    }
    private class AutorizacaoCallback
            implements AccountManagerCallback<Bundle> {
        @Override
        public void run(AccountManagerFuture<Bundle> future) {
            try {
                Bundle bundle = future.getResult();
                String nomeConta =
                        bundle.getString(AccountManager.KEY_ACCOUNT_NAME);
                String tokenAcesso =
                        bundle.getString(AccountManager.KEY_AUTHTOKEN);
                gravarTokenAcesso(nomeConta, tokenAcesso);
                iniciarDashboard();

            } catch (OperationCanceledException e) {
                // usuário cancelou a operação
            } catch (AuthenticatorException e) {
            } catch (IOException e) {
                // possível problema de comunicação
            }
        }
    }


 */

}
