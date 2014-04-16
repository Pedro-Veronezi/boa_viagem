package br.com.casadocodigo.boaviagem.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import br.com.casadocodigo.boaviagem.DatabaseHelper;

/**
 * Created by veronezi on 15/04/14.
 */
public class BoaViagemProvider extends ContentProvider{

    private static final int VIAGENS = 1;
    private static final int VIAGEM_ID = 2;
    private static final int GASTOS = 3;
    private static final int GASTO_ID = 4;
    private static final int GASTOS_VIAGEM_ID = 5;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private DatabaseHelper helper;

    static{
        uriMatcher.addURI(AUTHORITY, VIAGEM_PATH, VIAGENS);
        uriMatcher.addURI(AUTHORITY, VIAGEM_PATH + "/#", VIAGEM_ID);
        uriMatcher.addURI(AUTHORITY, GASTO_PATH, GASTOS);
        uriMatcher.addURI(AUTHORITY, GASTO_PATH + "/#", GASTO_ID);
        uriMatcher.addURI(AUTHORITY,
                GASTO_PATH + "/"+ VIAGEM_PATH + "/#", GASTOS_VIAGEM_ID);
    }

    @Override
    public boolean onCreate() {
        helper = new DatabaseHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder){
        SQLiteDatabase database = helper.getReadableDatabase();

        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
