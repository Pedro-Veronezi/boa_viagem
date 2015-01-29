package br.com.casadocodigo.boaviagem.ui;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import java.util.List;

import br.com.casadocodigo.boaviagem.R;

public class ConfiguracoesActivity extends PreferenceActivity {
    public ConfiguracoesActivity(){
        super();
    }
    @Override
    public void onBuildHeaders(List<PreferenceActivity.Header> target) {
        loadHeadersFromResource(R.xml.preference_header, target);
    }

    public static class SettingsPreferenceFragment extends PreferenceFragment {
        public SettingsPreferenceFragment(){
             super();
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.settings_preference);
        }
    }
}
