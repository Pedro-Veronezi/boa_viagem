package br.com.casadocodigo.boaviagem.bo;

import android.content.Context;
import java.util.List;

import br.com.casadocodigo.boaviagem.bean.Gasto;
import br.com.casadocodigo.boaviagem.bean.Viagem;
import br.com.casadocodigo.boaviagem.dao.BoaViagemDAO;

/**
 * Created by veronezi on 09/04/14.
 */
public class BoaViagemBO {
    /**
     * Contexto da Activity que instanciou o BO.
     */
    private Context context;
    private BoaViagemDAO boaViagemDAO;

    public BoaViagemBO(Context context) {
        this.context = context;
        boaViagemDAO = new BoaViagemDAO(context);
    }

    public double calcularTotalGasto(Viagem viagem){
        return boaViagemDAO.calcularTotalGasto(viagem);
    }

    public List<Viagem> listarViagens(){
        return boaViagemDAO.listarViagens();
    }

    public boolean removerViagem(Long id) {
        return boaViagemDAO.removerViagem(id);
    }

    public void closeDao() {
        boaViagemDAO.close();
    }

    public long inserirGasto(Gasto gasto) {
        return boaViagemDAO.inserir(gasto);
    }

    public List<Gasto> listarGastos(long id) {
        Viagem v = new Viagem();
        v.setId(id);
        return boaViagemDAO.listarGastos(v);
    }

    public List<Gasto> listarGastos(Viagem v) {
        return boaViagemDAO.listarGastos(v);
    }
}
